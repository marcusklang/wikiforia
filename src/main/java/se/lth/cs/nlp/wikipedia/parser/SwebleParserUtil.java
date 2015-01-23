package se.lth.cs.nlp.wikipedia.parser;

import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.parser.parser.LinkTargetException;

/**
 * Created by csz-mkg on 15-01-20.
 */
public class SwebleParserUtil {
    public static EngProcessedPage parseWikipage(WtEngineImpl engine, PageId pageId, String markup) throws EngineException {
        return engine.postprocess(pageId, markup, null);
    }

    public static EngProcessedPage parsePage(WikiConfig config, String title, long revision, String markup) throws EngineException, LinkTargetException {
        WtEngineImpl engine = new WtEngineImpl(config);

        PageTitle pageTitle = PageTitle.make(config, title);
        PageId pageId = new PageId(pageTitle, revision);

        return parseWikipage(engine, pageId, markup);
    }
}
