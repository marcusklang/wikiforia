package se.lth.cs.nlp.wikipedia.parser.annotation;

import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.parser.parser.LinkTargetException;

/**
 * Template argument
 */
public abstract class TemplateArgument {
    private String path;
    private String argument;
    private String content;

    public TemplateArgument(String path, String argument, String content) {
        this.path = path;
        this.argument = argument;
    }

    /**
     * Parse custom
     * @param model the model for the context
     * @param parser the parser to use
     * @param <T> model type
     * @throws EngineException
     * @throws LinkTargetException
     * @remarks It throws exceptions because the arguments needs to be recursively parsed which
     * means that the engine is invoked again, and this can fail. Total failure is undesirable;
     * this design allows the user to handle the error.
     */
    public abstract <T,Out> void parse(T model, AnnotationParser<T,Out> parser) throws EngineException, LinkTargetException;

    /**
     * Parse as text, append result to current parser.
     * @throws EngineException
     * @throws LinkTargetException
     * @remarks It throws exceptions because the arguments needs to be recursively parsed which
     * means that the engine is invoked again, and this can fail. Total failure is undesirable;
     * this design allows the user to handle the error.
     */
    public abstract void parse() throws EngineException, LinkTargetException;

    /**
     * Get raw content
     * @return text
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the path
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the argument
     * @return
     */
    public String getArgument() {
        return argument;
    }
}
