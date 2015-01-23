package se.lth.cs.nlp.wikipedia.parser.annotation;

import de.fau.cs.osr.ptk.common.AstVisitor;
import org.apache.commons.lang.StringUtils;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngPage;
import org.sweble.wikitext.parser.nodes.*;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Template AST visitor, finds all arguments for a template.
 */
public class TemplateParser extends AstVisitor<WtNode> {
    protected WikiConfig config;

    public TemplateParser(WikiConfig config) {
        this.config = config;
    }

    private ArrayDeque<String> template = new ArrayDeque<String>();
    private ArrayList<TemplateProperty> templateProperties = new ArrayList<TemplateProperty>();
    private String argument;

    public ArrayList<TemplateProperty> getTemplateProperties() {
        return templateProperties;
    }

    public static class TemplateProperty {
        public String path;
        public String argument;
        public String content;

        public TemplateProperty(String path, String argument, String content) {
            this.path = path;
            this.argument = argument;
            this.content = content;
        }
    }

    @Override
    protected boolean before(WtNode node)
    {
        // This method is called by go() before visitation starts
        return super.before(node);
    }

    @Override
    protected Object after(WtNode node, Object result)
    {
        return "";
    }

    public void visit(WtNode n)
    {
    }

    public void visit(WtNodeList n)
    {
        iterate(n);
    }

    public void visit(WtUnorderedList e)
    {
        iterate(e);
    }

    public void visit(WtOrderedList e)
    {
        iterate(e);
    }

    public void visit(WtListItem item)
    {
        iterate(item);
    }

    public void visit(EngPage p)
    {
        iterate(p);
    }

    public void visit(WtText text)
    {
        if(template.size() > 0) {
            String content = text.getContent().trim();
            if(content.length() > 0) {
                templateProperties.add(new TemplateProperty("[" + StringUtils.join(template, "].[") + "]", argument, content));
            }
        }
    }

    public void visit(WtBold b)
    {
        iterate(b);
    }

    public void visit(WtItalics i)
    {
        iterate(i);
    }

    public void visit(WtXmlElement e)
    {
        if (e.getName().equalsIgnoreCase("br"))
        {
        }
        else
        {
            iterate(e.getBody());
        }
    }

    public void visit(WtTemplate n)
    {
        if(n.getName().isResolved()) {
            String value = n.getName().getAsString().trim();
            template.add(value);
            iterate(n.getArgs());
            template.removeLast();
        }
    }

    public void visit(WtTemplateArgument n)
    {
        if(n.hasName() && n.getName().isResolved()) {
            String name = n.getName().getAsString().trim();
            argument = name;
            iterate(n.getValue());
        }
        else if(!n.hasName())
        {
            argument = "__VALUE__";
            iterate(n.getValue());
        }
    }

    public void visit(WtTemplateParameter n)
    {
        iterate(n);
    }
}
