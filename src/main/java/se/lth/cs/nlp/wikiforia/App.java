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
import se.lth.cs.nlp.io.XmlWikipediaPageWriter;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.mediawiki.parser.MultistreamBzip2XmlDumpParser;
import se.lth.cs.nlp.mediawiki.parser.SinglestreamXmlDumpParser;
import se.lth.cs.nlp.pipeline.PipelineBuilder;
import se.lth.cs.nlp.pipeline.Sink;
import se.lth.cs.nlp.pipeline.Source;
import se.lth.cs.nlp.wikipedia.lang.EnglishConfig;
import se.lth.cs.nlp.wikipedia.lang.SwedishConfig;
import se.lth.cs.nlp.wikipedia.lang.TemplateConfig;
import se.lth.cs.nlp.wikipedia.parser.SwebleWikimarkupToText;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
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
            .withDescription("set the number of threads to use, defaults to available processors as given by Runtime.getRuntime()")
            .create("t");

    @SuppressWarnings("static-access")
    private static final Option batch = OptionBuilder.withLongOpt("batch-size")
            .hasArg()
            .withDescription("set the size of a batch, defaults to 100")
            .create("batchsize");

    @SuppressWarnings("static-access")
    private static final Option output = OptionBuilder.withLongOpt("output")
            .withDescription("xml output filepath")
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

    /**
     * Used to invoke the conversion internally
     * @param config the language config
     * @param indexPath the index path (might be null)
     * @param pagesPath the pages path (must never be null)
     * @param outputPath the output path (must never be null)
     * @param numThreads the number of threads to use
     * @param batchsize the size of a batch
     */
    public static void convert(TemplateConfig config, File indexPath, File pagesPath, File outputPath, int numThreads, int batchsize) {
        Source<Page,Void> source;

        if(index == null)
        {
            //Singlestream
            source = new SinglestreamXmlDumpParser(pagesPath, batchsize);
        }
        else if(index != null)
        {
            source = new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, batchsize, numThreads);
        }

        System.out.println("");

        System.out.println("Using " + numThreads + " threads with a batch-size of " + batchsize + ". Using language " + config.getIso639() + " settings.");
        System.out.println("Index path: " + (indexPath == null ? "[not used]" : indexPath.getAbsolutePath()));
        System.out.println("Pages path: " + pagesPath.getAbsolutePath());
        System.out.println("Output path: " + outputPath.getAbsolutePath());

        System.out.println("");
        System.out.println("Conversion has started.");
        final AtomicLong count = new AtomicLong();
        final AtomicLong failcount = new AtomicLong();
        final AtomicLong addcount = new AtomicLong();

        long start = System.currentTimeMillis();

        PipelineBuilder.input(new MultistreamBzip2XmlDumpParser(indexPath, pagesPath, 100))
                .pipe(new SwebleWikimarkupToText(config))
                .sendLog(new Sink<String>() {
                    @Override
                    public void process(List<String> batch) {
                        for(String str : batch) {
                            System.out.println(str);
                        }
                    }
                })
                .sendError(new Sink<Page>() {
                    @Override
                    public void process(List<Page> batch) {
                        for (Page page : batch) {
                            System.out.println("Failed to parse " + page.getTitle());
                        }

                        count.addAndGet(batch.size());
                        failcount.addAndGet(batch.size());
                    }
                })
                .sendOutput(new Sink<WikipediaPage>() {
                    private final AtomicLong futureTime = new AtomicLong(System.currentTimeMillis() + 1000);

                    @Override
                    public void process(List<WikipediaPage> batch) {
                        long added = count.addAndGet(batch.size());
                        addcount.addAndGet(batch.size());

                        if (System.currentTimeMillis() >= futureTime.get()) {
                            futureTime.set(System.currentTimeMillis() + 1000);
                            System.out.println(" Read " + added);
                        }
                    }
                })
                .pipe(new XmlWikipediaPageWriter(outputPath))
                .run();

        long end = System.currentTimeMillis();

        System.out.println("Done.");
        System.out.println();
        System.out.println("Total count: " + count.get());
        System.out.println("Total added: " + addcount.get());
        System.out.println("Total failed: " + failcount.get());
        System.out.println();
        System.out.println("Completed in " + (end - start) / 1000.0 + " sec.");
    }

    /**
     * Application entrypoint
     * @param args input arguments
     */
    public static void main( String[] args )
    {
        System.out.println("Wikiforia v1.0 by Marcus Klang");

        Options options = new Options();
        options.addOption(index);
        options.addOption(pages);
        options.addOption(threads);
        options.addOption(batch);
        options.addOption(output);
        options.addOption(lang);

        CommandLineParser parser = new PosixParser();
        try {
            CommandLine cmdline = parser.parse(options, args);

            File indexPath = null, pagesPath, outputPath;
            int batchsize = 100;
            int numThreads = Runtime.getRuntime().availableProcessors();

            //Read batch size
            if(cmdline.hasOption(batch.getOpt())) {
                batchsize = Integer.parseInt(cmdline.getOptionValue(batch.getOpt()));
            }

            //Read num threads
            if(cmdline.hasOption(threads.getOpt())) {
                numThreads = Integer.parseInt(cmdline.getOptionValue(threads.getOpt()));
            }

            //Read required paths
            pagesPath = new File(cmdline.getOptionValue(pages.getOpt()));
            outputPath = new File(cmdline.getOptionValue(output.getOpt()));

            //Create output directories if they do not exist
            if(!outputPath.getParentFile().getAbsoluteFile().exists()) {
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
                System.out.println("pages with absolute filepath '" + pagesPath.getAbsolutePath() + "' could not be found.");
                return;
            }

            if(indexPath != null && !indexPath.exists())
            {
                System.out.println("Could not find index file '" + indexPath.getAbsolutePath() + "'.");
                System.out.println("Skipping index and continuing with singlestream parsing (no threaded decompression)");
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
                    System.out.println("Could not find a suitable language, will default to English");
                    langId = "en";
                }
            }

            TemplateConfig config;
            if(langId.equals("sv")) {
                config = new SwedishConfig();
            }
            else if(langId.equals("en")) {
                config = new EnglishConfig();
            }
            else {
                config = new EnglishConfig();
                System.out.println("language " + langId + " is not yet supported and will be defaulted to a English setting for Sweble.");
                langId = "en";
            }

            convert(config,indexPath,pagesPath, outputPath, numThreads, batchsize);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            HelpFormatter writer = new HelpFormatter();
            writer.printHelp("wikiforia", options);
        }
    }
}
