package se.lth.cs.nlp.wikipedia.parser.annotation;

import java.util.List;

/**
 * Template
 */
public abstract class Template {
    public String path;

    public Template(String path) {
        this.path = path;
    }

    /**
     * Parse the template and find all arguments
     * @return list of arguments.
     */
    public abstract List<TemplateArgument> parse();
}
