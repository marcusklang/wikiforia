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
package se.lth.cs.nlp.wikiforia;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lth.cs.nlp.io.OneLineWikipediaPageWriter;
import se.lth.cs.nlp.io.PlainTextWikipediaPageWriter;
import se.lth.cs.nlp.io.SimpleHadoopTextWriter;
import se.lth.cs.nlp.io.XmlWikipediaPageWriter;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.mediawiki.parser.MultistreamBzip2XmlDumpParser;
import se.lth.cs.nlp.mediawiki.parser.SinglestreamXmlDumpParser;
import se.lth.cs.nlp.pipeline.Filter;
import se.lth.cs.nlp.pipeline.Sink;
import se.lth.cs.nlp.pipeline.Source;
import se.lth.cs.nlp.wikipedia.lang.EnglishConfig;
import se.lth.cs.nlp.wikipedia.lang.LangFactory;
import se.lth.cs.nlp.wikipedia.lang.TemplateConfig;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class that contains the entrypoint for the CLI interface
 */
public class App
{
    @SuppressWarnings("static-access")
    private static final Option index = OptionBuilder.withLongOpt("index")
            .withDescription("index file (bzip2 format)")
            .hasArg(true)
            .withArgName("path")
            .create("index");

    @SuppressWarnings("static-access")
    private static final Option pages = OptionBuilder.withLongOpt("pages")
            .withDescription("page file (bzip2 or plain xml), only multistreamed bzip2 has support for threading.")
            .hasArg(true)
            .withArgName("path")
            .isRequired()
            .create("pages");

    @SuppressWarnings("static-access")
    private static final Option threads = OptionBuilder.withLongOpt("num-threads")
            .hasArg()
            .withDescription("set the number of threads to use, defaults to available processors as given by Runtime.getRuntime()")
            .create("t");

    @SuppressWarnings("static-access")
    private static final Option batch = OptionBuilder.withLongOpt("batch-size")
            .hasArg()
            .withDescription("set the size of a batch, defaults to 100")
            .create("batchsize");

    @SuppressWarnings("static-access")
    private static final Option hadoop = OptionBuilder.withLongOpt("use-hadoop-format")
            .withDescription("use splits and a simple but hadoop friendly output format")
            .create("hadoop");

    @SuppressWarnings("static-access")
    private static final Option gzip = OptionBuilder.withLongOpt("gzip-compression")
            .withDescription("enable gzip compression of output (uses fast compression)")
            .create("gzip");

    @SuppressWarnings("static-access")
    private static final Option filterNs = OptionBuilder.withLongOpt("filter-by-namespace-id")
                                                    .hasArg()
                                                    .withDescription("only include namespaces that match given numbers, separate by ',' for more than 1 filter")
                                                    .create("fns");

    @SuppressWarnings("static-access")
    private static final Option splitsize = OptionBuilder.withLongOpt("split-size")
                                                    .hasArg()
                                                    .withDescription("set split size (defaults to 64 M UTF-8 chars), only applicable with hadoop, max value = 2 G")
                                                    .create("splitsize");

    @SuppressWarnings("static-access")
    private static final Option testDecompression = OptionBuilder.withLongOpt("test")
                                                                 .create("test");

    @SuppressWarnings("static-access")
    private static final Option output = OptionBuilder.withLongOpt("output")
            .withDescription("output filepath")
            .hasArg()
            .isRequired()
            .withArgName("path")
            .create("o");

    @SuppressWarnings("static-access")
    private static final Option lang = OptionBuilder.withLongOpt("language")
            .withDescription("2 letter iso-639 code, en = english, sv = swedish, ...")
            .hasArg()
            .withArgName("language")
            .create("lang");

    @SuppressWarnings("static-access")
    private static final Option outputFormatOption = OptionBuilder.withLongOpt("output-format")
            .withDescription("Output format : xml, plain-text or one-line")
            .hasArg()
            .withArgName("outputformat")
            .create("of");

    private static final String OUTPUT_FORMAT_XML = "xml";
    private static final String OUTPUT_FORMAT_PLAIN_TEXT = "plain-text";
    private static final String OUTPUT_FORMAT_ONE_LINE = "one-line";
    private static final String OUTPUT_FORMAT_DEFAULT = OUTPUT_FORMAT_XML;

    /**
     * Used to invoke the hadoop conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     * @param gzip use gzip compression
     * @param splitsize the size of a split in chars
     */
    public static void hadoopConvert(
            TemplateConfig config,
            File indexPath,
            File pagesPath,
            File outputPath,
            int numThreads,
            int batchsize,
            int splitsize,
            boolean gzip)
    {
        Source<Page,Void> source;

        if(indexPath == null)
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        else
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);

        Pipeline pipeline = new Pipeline(source, new SimpleHadoopTextWriter(outputPath, splitsize, numThreads, gzip), config);
        pipeline.run();
    }

    /**
     * Used to invoke the hadoop conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     * @param gzip use gzip compression
     * @param splitsize the size of a split in chars
     * @param filters all filters to append
     */
    public static void hadoopConvert(TemplateConfig config,
                                     File indexPath,
                                     File pagesPath,
                                     File outputPath,
                                     int numThreads,
                                     int batchsize,
                                     int splitsize,
                                     boolean gzip,
                                     ArrayList<Filter<WikipediaPage>> filters)
    {
        Source<Page,Void> source;

        if(indexPath == null)
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        else
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);

        Pipeline pipeline = new Pipeline(source, new SimpleHadoopTextWriter(outputPath, splitsize, numThreads, gzip), config);
        pipeline.appendAllFilters(filters);
        pipeline.run();
    }

    /**
     * Used to invoke the conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     */
    public static void convert(
            TemplateConfig config,
            File indexPath,
            File pagesPath,
            File outputPath,
            int numThreads,
            int batchsize)
    {
        convert(config, indexPath, pagesPath, outputPath, numThreads, batchsize, OUTPUT_FORMAT_DEFAULT);
    }

    /**
     * Used to invoke the conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     * @param outputFormat format of output i.e. xml or plain-text
     */
    public static void convert(
            TemplateConfig config,
            File indexPath,
            File pagesPath,
            File outputPath,
            int numThreads,
            int batchsize,
            String outputFormat)
    {
        Source<Page,Void> source;

        if(indexPath == null)
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        else
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);

        Pipeline pipeline = new Pipeline(source, getSink(outputFormat, outputPath), config);
        pipeline.run();
    }

    /**
     * @param outputFormat output format
     * @param outputPath output path
     * @return Sink
     */
    private static Sink<WikipediaPage> getSink(String outputFormat, File outputPath) {
        if (outputFormat != null) {
            if (outputFormat.trim().equalsIgnoreCase(OUTPUT_FORMAT_PLAIN_TEXT))
                    return new PlainTextWikipediaPageWriter(outputPath);
            if (outputFormat.trim().equalsIgnoreCase(OUTPUT_FORMAT_ONE_LINE))
                return new OneLineWikipediaPageWriter(outputPath);
        }
        return new XmlWikipediaPageWriter(outputPath);
    }

    /**
     * Used to invoke the conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     * @param filters All filters to append
     */
    public static void convert(
            TemplateConfig config,
            File indexPath,
            File pagesPath,
            File outputPath,
            int numThreads,
            int batchsize,
            ArrayList<Filter<WikipediaPage>> filters)
    {
        convert(config, indexPath, pagesPath, outputPath, numThreads, batchsize, filters, OUTPUT_FORMAT_DEFAULT);
    }

    /**
     * Used to invoke the conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     * @param filters All filters to append
     * @param outputFormat format of output i.e. xml, plain-text or one-line
     */
    public static void convert(
            TemplateConfig config,
            File indexPath,
            File pagesPath,
            File outputPath,
            int numThreads,
            int batchsize,
            ArrayList<Filter<WikipediaPage>> filters,
            String outputFormat)
    {
        Source<Page,Void> source;

        if(indexPath == null)
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        else
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);

        Pipeline pipeline = new Pipeline(source, getSink(outputFormat, outputPath), config);
        pipeline.appendAllFilters(filters);
        pipeline.run();
    }

    public static void test(TemplateConfig config,
                            File indexPath,
                            File pagesPath,
                            int numThreads,
                            int batchsize) {
        Source<Page,Void> source;

        if(indexPath == null)
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        else
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);

        Pipeline pipeline = new Pipeline(source, null, config, true);
        pipeline.run();
    }

    /**
     * Application entrypoint
     * @param args input arguments
     */
    public static void main( String[] args )
    {
        Logger logger = LoggerFactory.getLogger(App.class);

        logger.info("Wikiforia v1.2.1 by Marcus Klang");

        Options options = new Options();
        options.addOption(index);
        options.addOption(pages);
        options.addOption(threads);
        options.addOption(batch);
        options.addOption(output);
        options.addOption(lang);
        options.addOption(hadoop);
        options.addOption(gzip);
        options.addOption(testDecompression);
        options.addOption(filterNs);
        options.addOption(outputFormatOption);

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmdline = parser.parse(options, args);

            File indexPath = null, pagesPath, outputPath;
            int batchsize = 100;
            int numThreads = Runtime.getRuntime().availableProcessors();
            String outputFormat = OUTPUT_FORMAT_DEFAULT;

            //Read batch size
            if(cmdline.hasOption(batch.getOpt())) {
                batchsize = Integer.parseInt(cmdline.getOptionValue(batch.getOpt()));
            }

            //Read num threads
            if(cmdline.hasOption(threads.getOpt())) {
                numThreads = Integer.parseInt(cmdline.getOptionValue(threads.getOpt()));
            }

            //Output format
            if(cmdline.hasOption(outputFormatOption.getOpt())) {
                outputFormat = cmdline.getOptionValue(outputFormatOption.getOpt());
            }

            //Read required paths
            pagesPath = new File(cmdline.getOptionValue(pages.getOpt()));
            outputPath = new File(cmdline.getOptionValue(output.getOpt()));

            //Create output directories if they do not exist
            if(!outputPath.getAbsoluteFile().getParentFile().getAbsoluteFile().exists()) {
                if (!outputPath.getParentFile().getAbsoluteFile().mkdirs()) {
                    throw new IOError(new IOException("Failed to create directories for " + outputPath.getParentFile().getAbsolutePath()));
                }
            }

            //To to automatically select an index file if it does not exits
            if(!cmdline.hasOption(index.getOpt()))
            {
                //try to automatically identify if there is an index file
                if(pagesPath.getAbsolutePath().toLowerCase().endsWith("-multistream.xml.bz2")) {
                    int pos = pagesPath.getAbsolutePath().lastIndexOf("-multistream.xml.bz2");
                    indexPath = new File(pagesPath.getAbsolutePath().substring(0,pos) + "-multistream-index.txt.bz2");
                    if(!indexPath.exists())
                        indexPath = null;
                }
            }
            else {
                indexPath = new File(cmdline.getOptionValue(index.getOpt()));
            }

            //Validation
            if(!pagesPath.exists()) {
                logger.error("pages with absolute filepath {} could not be found.", pagesPath.getAbsolutePath());
                return;
            }

            if(indexPath != null && !indexPath.exists())
            {
                logger.error("Could not find index file {}.", indexPath.getAbsolutePath());
                logger.error("Skipping index and continuing with singlestream parsing (no threaded decompression)");
                indexPath = null;
            }

            String langId;
            if(cmdline.hasOption(lang.getOpt()))
            {
                langId = cmdline.getOptionValue(lang.getOpt());
            }
            else
            {
                Pattern langmatcher = Pattern.compile("([a-z]{2})wiki-");
                Matcher matcher = langmatcher.matcher(pagesPath.getName());
                if(matcher.find()) {
                    langId = matcher.group(1).toLowerCase();
                }
                else {
                    logger.error("Could not find a suitable language, will default to English");
                    langId = "en";
                }
            }

            ArrayList<Filter<WikipediaPage>> filters = new ArrayList<Filter<WikipediaPage>>();
            if(cmdline.hasOption(filterNs.getOpt())) {
                String optionValue = cmdline.getOptionValue(filterNs.getOpt());
                final TreeSet<Integer> ns = new TreeSet<Integer>();
                for (String s : optionValue.split(",")) {
                    ns.add(Integer.parseInt(s));
                }

                if(ns.size() > 0) {
                    filters.add(new Filter<WikipediaPage>() {
                        @Override
                        protected boolean accept(WikipediaPage item) {
                            return ns.contains(item.getNamespace());
                        }

                        @Override
                        public String toString() {
                            return String.format("Namespace filter { namespaces: %s }", StringUtils.join(ns, ","));
                        }
                    });
                }
            }

            TemplateConfig config;
            Class<? extends TemplateConfig> configClazz = LangFactory.get(langId);
            if(configClazz != null) {
                try {
                    config = configClazz.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                config = new EnglishConfig();
                logger.error("language {} is not yet supported and will be defaulted to a English setting for Sweble.", langId);
                langId = "en";
            }

            if(cmdline.hasOption(hadoop.getOpt())) {
                if(outputPath.exists()) {
                    logger.error("The target location already exists, please remove before using the tool!");
                    System.exit(1);
                }
                else {
                    int splitsize = 64000000;
                    if(cmdline.hasOption(App.splitsize.getOpt())) {
                        splitsize = Integer.parseInt(cmdline.getOptionValue(App.splitsize.getOpt()));
                    }

                    hadoopConvert(config, indexPath, pagesPath, outputPath, numThreads, batchsize, splitsize, cmdline.hasOption(gzip.getOpt()), filters);
                }
            }
            else {
                if(cmdline.hasOption(testDecompression.getOpt())) {
                    test(config, indexPath, pagesPath, numThreads, batchsize);
                }
                else {
                    convert(config,indexPath,pagesPath, outputPath, numThreads, batchsize, filters, outputFormat);
                }
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter writer = new HelpFormatter();
            writer.printHelp("wikiforia", options);
        }
    }
}
