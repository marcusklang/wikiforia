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
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.pipeline.AbstractEmitter;
import se.lth.cs.nlp.pipeline.Source;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Singlestream Mediawiki xml dump parser (supporting uncompressed and bzip2 compressed)
 */
public class SinglestreamXmlDumpParser extends AbstractEmitter<Page,Void> implements Source<Page,Void> {

    private final InputStream input;
    private final XmlDumpParser parser;
    private final int batchsize;
    private final File pageInput;

    /**
     * File constructor
     * @param input file to read from
     */
    public SinglestreamXmlDumpParser(File input) {
        this(input, 100);
    }

    /**
     * Stream constructor
     * @param stream stream to read from
     */
    public SinglestreamXmlDumpParser(InputStream stream) {
        this(stream, 100);
    }

    /**
     * File constructor with batchsize
     * @param path file to read from
     * @param batchsize the size of a batch
     */
    public SinglestreamXmlDumpParser(File path, int batchsize) {
        this.pageInput = path;
        this.batchsize = batchsize;
        try {
            if(path.getAbsolutePath().toLowerCase().endsWith(".bz2")) {
                this.input = new BZip2CompressorInputStream(new FileInputStream(path), true);
            }
            else
            {
                this.input = new FileInputStream(path);
            }
        } catch (IOException e) {
            throw new IOError(e);
        }

        parser = new XmlDumpParser(input);

    }

    /**
     * Stream constructor with batchsize
     * @param stream stream to read from
     * @param batchsize the size of a batch
     */
    public SinglestreamXmlDumpParser(InputStream stream, int batchsize) {
        this.pageInput = null;
        this.input = stream;
        this.batchsize = batchsize;
        this.parser = new XmlDumpParser(input);
    }

    @Override
    public void run()
    {
        ArrayList<Page> pages = new ArrayList<Page>(batchsize);
        Page page;

        while( (page = parser.next()) != null) {
            pages.add(page);
            if(pages.size() == batchsize) {
                output(pages);
                pages = new ArrayList<Page>(batchsize);
            }
        }

        if(!pages.isEmpty())
            output(pages);

        output(Collections.<Page>emptyList());
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(true);

        return String.format("Singlestreamed XML Dump parser { \n * Batch size: %s, \n * Input: %s \n}",
                             nf.format(batchsize),
                             pageInput == null ? "[Inputstream]" : pageInput.getAbsolutePath());
    }
}
