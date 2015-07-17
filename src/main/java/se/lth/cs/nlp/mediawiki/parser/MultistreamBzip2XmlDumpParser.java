/**
 * This file is part of Wikiforia.
 *
 * Wikiforia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Wikiforia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.lth.cs.nlp.mediawiki.parser;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import se.lth.cs.nlp.mediawiki.model.Header;
import se.lth.cs.nlp.pipeline.AbstractEmitter;
import se.lth.cs.nlp.pipeline.Source;
import se.lth.cs.nlp.mediawiki.model.Page;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multistream Mediawiki bzip2 xml dump parser (supporting parallel decompression and xml parsing)
 */
public class MultistreamBzip2XmlDumpParser extends AbstractEmitter<Page,Void> implements Source<Page,Void> {

    /**
     * Represents a page block
     */
    private final static class PageBlock {
        private final Block block;
        private final byte[] buffer;

        public PageBlock(Block block, byte[] buffer) {
            this.block = block;
            this.buffer = buffer;
        }

        public Block getBlock() {
            return block;
        }

        public final byte[] getBuffer() {
            return buffer;
        }

        public final boolean isEof() {
            return buffer == null;
        }
    }

    private final ArrayBlockingQueue<PageBlock> blocks;
    private final PageReader pageReader;
    private final int batchsize;
    private final Worker[] workers;
    private final File indexFile, pageFile;

    /**
     * Default constructor (batchsize = 100, numThreads = available processors)
     * @param index the index file containing all the indicies of blocks
     * @param pages the page file contain all the multistreams
     */
    public MultistreamBzip2XmlDumpParser(File index, File pages) {
        this(index, pages, 100, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Batchsize constructor (numThreads = available processors)
     * @param index the index file containing all the indicies of blocks
     * @param pages the page file contain all the multistreams
     * @param batchsize the size of a batch
     */
    public MultistreamBzip2XmlDumpParser(File index, File pages, int batchsize) {
        this(index, pages, batchsize, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Full constructor
     * @param index the index file containing all the indicies of blocks
     * @param pages the page file contain all the multistreams
     * @param numThreads the number of threads to use above the current
     * @param batchsize the size of a batch
     */
    public MultistreamBzip2XmlDumpParser(File index, File pages, int batchsize, int numThreads) {
        this.indexFile = index;
        this.pageFile = pages;
        this.workers = new Worker[numThreads];
        this.blocks = new ArrayBlockingQueue<PageBlock>(numThreads*3);
        this.pageReader = new PageReader(new IndexReader(index, pages, numThreads * 3), pages);
        this.batchsize = batchsize;
    }

    private static class Block {
        public final long start;
        public final int size;

        public Block(long start, int size) {
            this.start = start;
            this.size = size;
        }
    }

    /**
     * The index reader
     */
    private static class IndexReader {
        private BufferedReader indexReader;
        private final long pageFileSize;
        private final int bufferAhead;
        private final ArrayDeque<Block> buffer;

        public IndexReader(File indexFile, File pageFile, int bufferAhead) {
            try {
                this.pageFileSize = pageFile.length();
                this.buffer = new ArrayDeque<Block>();
                this.bufferAhead = bufferAhead;
                this.indexReader =
                        new BufferedReader(
                            new InputStreamReader(
                                new BZip2CompressorInputStream(
                                        new BufferedInputStream(
                                                new FileInputStream(indexFile)))));
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        private long start = 0;

        private void fillBuffer() {
            if(indexReader == null)
                return;

            try {
                int counter = 0;
                String line;
                while( (line = indexReader.readLine()) != null && counter < bufferAhead) {
                    if(line.isEmpty())
                        continue;

                    int pos = line.indexOf(':');
                    long current = Long.parseLong(line.substring(0, pos));
                    if(start != current) {
                        long diff = current - start;
                        buffer.addLast(new Block(start, (int)diff));
                        start = current;

                        counter++;
                    }
                }

                if(counter != bufferAhead) {
                    buffer.addLast(new Block(start, (int)(pageFileSize - start)));
                    indexReader.close();
                    indexReader = null;
                }
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        public Block peek() {
            if(buffer.isEmpty())
                fillBuffer();

            return buffer.isEmpty() ? null : buffer.peekFirst();
        }

        /**
         * Get next block to read
         * @return -1 if no more blocks to read, integer > 0 if more blocks to read
         */
        public Block next() {
            if(buffer.isEmpty())
                fillBuffer();

            return buffer.isEmpty() ? null : buffer.removeFirst();
        }
    }

    /**
     * The compressed block reader
     */
    private static class PageReader {
        private final IndexReader indexReader;
        private final FileInputStream pageStream;
        private final Header header;

        public PageReader(IndexReader indexReader, File pages) {
            try {
                this.indexReader = indexReader;
                this.pageStream = new FileInputStream(pages);
                this.header = readHeader();
            } catch (IOException e) {
                throw new IOError(e);
            }
        }

        /**
         * Header parsing code
         * @param xml the header xml
         * @return true if match
         * @throws javax.xml.stream.XMLStreamException
         */
        public static Header parseHeader(String xml) throws XMLStreamException
        {
            XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory2.newInstance();
            factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
            BufferedReader buffreader = new BufferedReader(new StringReader(xml));

            XMLStreamReader2 xmlReader = (XMLStreamReader2)factory.createXMLStreamReader(buffreader);
            return XmlDumpParser.readHeader(xmlReader);
        }

        private Header readHeader() throws IOException {
            byte[] header = next().buffer;

            ByteArrayInputStream bais = new ByteArrayInputStream(header);
            BZip2CompressorInputStream bcis = new BZip2CompressorInputStream(bais);

            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(bcis,"UTF-8"));
            String line;
            while( (line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }

            sb.append("</mediawiki>");

            reader.close();

            try {
                return parseHeader(sb.toString());
            } catch (XMLStreamException e) {
                throw new IOError(e);
            }
        }

        public Header getHeader() {
            return header;
        }

        /**
         * Read page blocks
         * @return null if no more blocks, otherwise a byte[] with the block
         */
        public PageBlock next() {
            Block block = indexReader.next();
            if(block == null)
                return null;

            try
            {
                byte[] buffer = new byte[block.size];
                int left = block.size;
                while(left > 0) {
                    int read = pageStream.read(buffer, block.size - left, left);
                    if(read == -1)
                        throw new IOError(new EOFException("Unexpected end of file!"));

                    left -= read;
                }

                return new PageBlock(block, buffer);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }

    protected final ParallelDumpStream getStream() {
        return new ParallelDumpStream();
    }

    protected class ParallelDumpStream extends InputStream {

        private PageBlock lastBlock = null;
        private PageBlock currentBlock = null;

        public PageBlock getLastBlock() {
            return lastBlock;
        }

        public PageBlock getCurrentBlock() {
            return currentBlock;
        }

        private byte[] buffer = new byte[0];
        private int pos = 0;

        private boolean getNext() {
            PageBlock block;
            try {
                block = blocks.take();
                if(block.isEof()) {
                    pos = 0;
                    return false;
                }
                else {
                    pos = 0;
                    this.buffer = block.getBuffer();
                    this.lastBlock = this.currentBlock;
                    this.currentBlock = block;
                    return true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public final int read() throws IOException {
            if(pos == buffer.length) {
                if(!getNext())
                    return -1;
                else
                    return buffer[pos++] & 0xFF;
            }
            else
                return buffer[pos++] & 0xFF;
        }

        @Override
        public final int read(final byte[] b, final int off, final int len) throws IOException {
            int left = len;
            int current = off;

            while(left > 0) {
                if(pos == buffer.length) {
                    if(!getNext())
                        return len - left;
                }

                int possibleToRead = Math.min(buffer.length - pos, len);

                System.arraycopy(buffer, pos, b, current, possibleToRead);
                left -= possibleToRead;
                current += possibleToRead;
            }

            return len - left;
        }

    }

    public class Worker extends Thread {
        public void run()
        {
            try {
                ParallelDumpStream dumpStream = getStream();
                XmlDumpParser parser = new XmlDumpParser(pageReader.getHeader(), new BZip2CompressorInputStream(dumpStream, true));
                ArrayList<Page> batch = new ArrayList<Page>(batchsize);

                Page page;
                while((page = parser.next()) != null) {
                    batch.add(page);
                    if(batch.size() == batchsize)
                    {
                        try {
                            output(batch);
                        }
                        catch (Exception ex) {
                            //Save prev block, current block
                            PageBlock currentBlock = dumpStream.getCurrentBlock();
                            PageBlock prevBlock = dumpStream.getLastBlock();

                            if(currentBlock != null && currentBlock.block != null) {
                                File output = new File("stream-current-" + currentBlock.block.start + "-" + currentBlock.block.size + ".xml.bz2");
                                FileOutputStream outputStream = new FileOutputStream(output);
                                outputStream.write(currentBlock.buffer);
                                outputStream.flush();
                                outputStream.close();
                            }

                            if(prevBlock != null && prevBlock.block != null) {
                                File output = new File("stream-prev-" + prevBlock.block.start + "-" + prevBlock.block.size + ".xml.bz2");
                                FileOutputStream outputStream = new FileOutputStream(output);
                                outputStream.write(prevBlock.buffer);
                                outputStream.flush();
                                outputStream.close();
                            }

                            throw new IOError(ex);
                        }
                        batch = new ArrayList<Page>(batchsize);
                    }
                }

                if(batch.size() > 0)
                    output(batch);
            } catch (IOException e) {
                throw new IOError(e);
            }
        }
    }

    @Override
    public void run() {
        final AtomicBoolean terminate = new AtomicBoolean(false);
        final Logger logger = LoggerFactory.getLogger(MultistreamBzip2XmlDumpParser.class);
        //1. Start all worker threads
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker();
            workers[i].setName("Dump Worker " + i);
        }

        //Add an uncaught exception handler and allow for a graceful shutdown.
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                logger.error("Fatal error in thread {}, terminating...", th.getName(), ex);
                for (Worker worker : workers) {
                    worker.interrupt();
                }
                terminate.set(true);
            }
        };

        for (Worker worker : workers) {
            worker.setUncaughtExceptionHandler(h);
            worker.start();
        }

        //2. Seed them with data until there is no more
        PageBlock data;
        while((data = pageReader.next()) != null && !terminate.get()) {
            try {
                blocks.put(data);
            } catch (InterruptedException e) {
                logger.error("Data put interrupted", e);
                break;
            }
        }

        for (int i = 0; i < workers.length; i++) {
            try {
                blocks.put(new PageBlock(null, null));
            } catch (InterruptedException e) {
                logger.info("Termination interrupted", e);
                break;
            }
        }

        //3. Await termination of all workers
        for (Worker worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                logger.error("Worker {} thread interrupted.", worker.getName(), e);
            }
        }

        output(Collections.<Page>emptyList());
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(true);

        return String.format("Multistreamed Bzip2 XML Dump parser { \n * Threads: %s, \n * Batch size: %s, \n * Index: %s, \n * Pages: %s, \n * Basepath: %s \n}",
                             nf.format(workers.length),
                             nf.format(batchsize),
                             indexFile.getName(),
                             pageFile.getName(),
                             pageFile.getParentFile().getAbsolutePath());
    }
}
