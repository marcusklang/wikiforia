package se.lth.cs.nlp.io;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.pipeline.Sink;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hadoop text writer which writes to a directory
 *
 * Uses the format:
 *  [title]\t[escaped text using commons StringEscapeUtils.escapeJava(text)]
 */
public class SimpleHadoopTextWriter implements Sink<WikipediaPage> {

    private final File basepath;
    private final int limit;
    private final int maxWriters;
    private final boolean gzip;
    private ParallelSplitWriter<String> writer;
    private final AtomicLong written = new AtomicLong(0);
    private final Logger logger = LoggerFactory.getLogger(SimpleHadoopTextWriter.class);

    /**
     * Construct a hadoop text writer
     * @param basepath the target location (directory)
     * @param limit the size limit of a split
     * @param maxWriters the maximum number of concurrent writers
     */
    public SimpleHadoopTextWriter(File basepath, int limit, int maxWriters, boolean gzip) {
        this.basepath = basepath;
        this.limit = limit;
        this.maxWriters = maxWriters;
        this.gzip = gzip;
        if(basepath.exists())
            throw new IllegalArgumentException("Safety feature: basepath must not exist prior to output");

        if(gzip)
            this.writer = LineWriter.openFastGzipParallelWriter(basepath, limit, maxWriters);
        else
            this.writer = LineWriter.openParallelWriter(basepath, limit, maxWriters);
    }

    @Override
    public void process(List<WikipediaPage> batch) {
        if(batch.size() == 0) {
            //end signal
            logger.info("End of dump reached.");
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setGroupingUsed(true);
            logger.info("Total written: {}", nf.format(written.get()));
            writer.close();
            writer = null; //nothing should call after this point.
        } else {
            ArrayList<String> pages = new ArrayList<String>();
            for (WikipediaPage wikipediaPage : batch) {
                if(wikipediaPage.getText().length() > 0) {
                    StringBuilder sb = new StringBuilder();

                    sb.append(wikipediaPage.getTitle())
                      .append("\t")
                      .append(wikipediaPage.getText().replaceAll("\n", " \t"));

                    pages.add(sb.toString());
                }
            }

            written.addAndGet(pages.size());
            writer.write(pages);
        }
    }

    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(true);
        return String.format("Hadoop Split Writer { \n * Split limit: %s, \n * Max writers: %s, \n * Use Gzip: %s, \n * Basepath: %s \n}", nf.format(limit), nf.format(maxWriters), gzip ? "Yes" : "No", basepath.getAbsolutePath());
    }
}
