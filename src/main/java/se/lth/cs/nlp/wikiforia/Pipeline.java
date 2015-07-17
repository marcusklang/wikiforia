package se.lth.cs.nlp.wikiforia;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.lth.cs.nlp.io.SimpleHadoopTextWriter;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.pipeline.*;
import se.lth.cs.nlp.wikipedia.lang.TemplateConfig;
import se.lth.cs.nlp.wikipedia.parser.SwebleWikimarkupToText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The pipline runner and monitor.
 */
public class Pipeline {
    protected final Logger logger = LoggerFactory.getLogger(Pipeline.class);

    protected final Source<Page,Void> source;
    protected final Sink<WikipediaPage> target;
    protected final ArrayList<Filter<WikipediaPage>> filters = new ArrayList<Filter<WikipediaPage>>();
    protected final TemplateConfig config;
    protected final boolean test;

    public Pipeline(Source<Page, Void> source, Sink<WikipediaPage> target, TemplateConfig config) {
        this(source,target,config,false);
    }

    public Pipeline(Source<Page, Void> source, Sink<WikipediaPage> target, TemplateConfig config, boolean test) {
        this.source = source;
        this.target = target;
        this.config = config;
        this.test = test;
    }

    public void appendFilter(Filter<WikipediaPage> filter) {
        this.filters.add(filter);
    }

    public <T extends Filter<WikipediaPage>> void appendAllFilters(Collection<T> filters) {
        this.filters.addAll(filters);
    }

    public static class MergedFilter extends Filter<WikipediaPage> {
        private final List<Filter<WikipediaPage>> pages;

        public MergedFilter(List<Filter<WikipediaPage>> pages) {
            this.pages = pages;
        }

        @Override
        protected boolean accept(WikipediaPage item) {
            boolean accept = true;

            Iterator<Filter<WikipediaPage>> iterator = pages.iterator();
            while(iterator.hasNext() && accept) {
                accept = accept && Filter.delegatedAccept(iterator.next(), item);
            }

            return accept;
        }
    }

    public void run() {
        logger.info("Source: {}", source.toString());
        logger.info("Target: {}", target == null ? " NULL " :  target.toString());
        logger.info("Filters: {}", filters.isEmpty() ? "None" : "{ " + StringUtils.join(filters, ", \n") + " }");

        logger.info("Pipeline started...");
        final AtomicLong count = new AtomicLong();
        final AtomicLong failcount = new AtomicLong();
        final AtomicLong addcount = new AtomicLong();

        long start = System.currentTimeMillis();

        final ThreadLocal<NumberFormat> nfs = new ThreadLocal<NumberFormat>() {
            @Override
            protected NumberFormat initialValue() {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setGroupingUsed(true);
                nf.setMaximumFractionDigits(0);
                return nf;
            }
        };

        Filter<WikipediaPage> filter;
        if(filters.size() > 0) {
            filter = new MergedFilter(filters);
        }
        else {
            filter = new IdentityFilter<WikipediaPage>();
        }

        if(test) {
            PipelineBuilder.input(source)
                    .sendOutput(new Sink<Page>() {
                        private final AtomicLong futureTime = new AtomicLong(System.currentTimeMillis() + 1000);

                        @Override
                        public void process(List<Page> batch) {
                            count.addAndGet(batch.size());

                            long added = addcount.addAndGet(batch.size());

                            if (System.currentTimeMillis() >= futureTime.get()) {
                                futureTime.set(System.currentTimeMillis() + 1000);
                                NumberFormat nf = nfs.get();
                                logger.info("Read {}, Added {}", nf.format(count.get()), nf.format(added));
                            }
                        }
                    })
                    .pipe(new Sink<Page>() {
                        @Override
                        public void process(List<Page> batch) {
                            if(batch.size() == 0) {
                                System.out.println("Found the end!");
                            }
                        }
                    })
                    .run();
        } else {
            PipelineBuilder.input(source)
                    .sendOutput(new Sink<Page>() {
                        @Override
                        public void process(List<Page> batch) {
                            count.addAndGet(batch.size());
                        }
                    })
                    .pipe(new SwebleWikimarkupToText(config))
                    .sendLog(new Sink<String>() {
                        @Override
                        public void process(List<String> batch) {
                            for (String str : batch) {
                                logger.info(str);
                            }
                        }
                    })
                    .sendError(new Sink<Page>() {
                        @Override
                        public void process(List<Page> batch) {
                            for (Page page : batch) {
                                logger.error("Failed to parse " + page.getTitle());
                            }

                            failcount.addAndGet(batch.size());
                        }
                    })
                    .pipe(filter)
                    .sendOutput(new Sink<WikipediaPage>() {
                        private final AtomicLong futureTime = new AtomicLong(System.currentTimeMillis() + 1000);

                        @Override
                        public void process(List<WikipediaPage> batch) {
                            long added = addcount.addAndGet(batch.size());

                            if (System.currentTimeMillis() >= futureTime.get()) {
                                futureTime.set(System.currentTimeMillis() + 1000);
                                NumberFormat nf = nfs.get();
                                logger.info("Read {}, Added {}", nf.format(count.get()), nf.format(added));
                            }
                        }
                    })
                    .pipe(target)
                    .run();
        }


        long end = System.currentTimeMillis();

        logger.info("Pipeline finished.");

        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(true);

        logger.info("Total read: {}", nf.format(count.get()));
        logger.info("Total ignored: {}", nf.format(count.get() - addcount.get() - failcount.get()));
        logger.info("Total added: {}", nf.format(addcount.get()));
        logger.info("Total failed: {}", nf.format(failcount.get()));
        logger.info("Time spent: {}", DurationFormatUtils.formatDurationHMS(end - start));
    }
}
