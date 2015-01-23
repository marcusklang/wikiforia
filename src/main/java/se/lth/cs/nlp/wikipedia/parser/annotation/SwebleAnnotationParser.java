package se.lth.cs.nlp.wikipedia.parser.annotation;

import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.wikipedia.lang.TemplateConfig;
import se.lth.cs.nlp.wikipedia.parser.SwebleParserUtil;
import se.lth.cs.nlp.wikipedia.parser.SwebleWikimarkupParserBase;

/**
 * Sweble wikimarkup extended parser to extract text and structured data from the page.
 */
public class SwebleAnnotationParser<T,Out> extends SwebleWikimarkupParserBase<Out> {

    private AnnotationParser<T,Out> useableParser;

    /**
     * Default constructor
     * @param config the wikipedia configuration
     */
    public SwebleAnnotationParser(TemplateConfig config) {
        super(config);
        this.useableParser = null;
    }

    /**
     * Parser constructor, supply one parse to be used throughout the execution of the program
     * @param config the wikipedia configuration
     */
    public SwebleAnnotationParser(TemplateConfig config, AnnotationParser<T,Out> parser) {
        super(config);
        this.useableParser = parser;
    }

    @Override
    public Out parse(Page page) {
        try{
            EngProcessedPage eng = SwebleParserUtil.parsePage(config, page.getTitle(), page.getRevision(), page.getContent());

            TextParser<T,Out> parser = new TextParser<T,Out>(this, page);
            parser.parser.startDocument(parser.context);
            parser.go(eng.getPage());
            return parser.parser.endDocument(parser.context);
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Construct a new parser
     * @param page the current page
     * @remarks override this method to create a new parser instance if needed per parse pass.
     */
    protected AnnotationParser<T,Out> newParser(AnnotationContext<T> context, Page page) {
        return useableParser;
    }
}
