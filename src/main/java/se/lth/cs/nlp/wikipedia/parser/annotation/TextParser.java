package se.lth.cs.nlp.wikipedia.parser.annotation;

import de.fau.cs.osr.ptk.common.AstVisitor;
import org.apache.commons.lang.StringUtils;
import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngPage;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.parser.nodes.*;
import org.sweble.wikitext.parser.parser.LinkTargetException;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.wikipedia.parser.SwebleParserUtil;
import se.lth.cs.nlp.wikipedia.parser.SwebleTextAstWalker;

import java.util.*;

/**
 * The main text parser that is hookable.
 */
public class TextParser<T,Out> extends AstVisitor<WtNode> {
    protected FilteringStringBuilder sb;

    private String currentSectionTitle = "@Abstract";

    private int currentDepth = 1;
    private TreeMap<Integer,String> headers = new TreeMap<Integer, String>();

    private boolean hasNotReadAbstract = true;

    protected final WikiConfig config;
    protected AnnotationParser<T,Out> parser;

    protected AnnotationContext<T> context;
    protected final Page page;

    public AnnotationContext<T> getContext() {
        return context;
    }

    protected TextParser(WikiConfig config, Page page){
        sb = new FilteringStringBuilder();

        this.page = page;
        this.config = config;
        this.context = new AnnotationContext<T>() {
            @Override
            public String getText() {
                return sb.toString();
            }
        };
    }

    public TextParser(SwebleAnnotationParser<T,Out> parent, Page page) {
        this(parent.getConfig(), page);
        this.parser = parent.newParser(this.context, page);
    }

    public void visit(WtNode n)
    {

    }

    public void visit(WtNodeList n)
    {
        iterate(n);
    }

    private int listIdCounter = 0;
    private int listItemIdCounter = 0;
    private ArrayDeque<Integer> currentList = new ArrayDeque<Integer>();

    protected void appendParagraph(int start, int end) {
        sb.append("\n\n");
    }

    public void visit(WtUnorderedList e)
    {
        int listId = ++listIdCounter;
        currentList.push(listId);

        int start = sb.length();
        parser.startList(context, listId, start, false);

        iterate(e);
        int end = sb.length();
        currentList.pop();

        start = trimStart(start);
        end = trimEnd(end);

        if(start < end) {
            parser.endList(context, listId, start, end, false);
            parser.paragraph(context, currentSectionTitle, start, end);
        }

        appendParagraph(start, end);
    }

    public void visit(WtOrderedList e)
    {
        int listId = ++listIdCounter;
        currentList.push(listId);

        int start = sb.length();
        parser.startList(context, listId, start, true);

        iterate(e);
        int end = sb.length();
        currentList.pop();

        start = trimStart(start);
        end = trimEnd(end);

        if(start < end) {
            parser.endList(context, listId, start, end, true);
            parser.paragraph(context, currentSectionTitle, start, end);
        }

        appendParagraph(start, end);
    }

    public void visit(WtListItem item)
    {
        int itemId = ++listItemIdCounter;
        int start = sb.length();

        parser.startListItem(context, currentList.peek(), itemId, start);
        iterate(item);
        sb.flush();
        int end = sb.length();

        start = trimStart(start);
        end = trimEnd(end);

        if(start < end) {
            String endpart = StringUtils.trim(sb.substring(start, end));
            if(endpart.length() > 0 && !endpart.endsWith(".")) {
                sb.append(". ");
                end = sb.length() - 1;
            }
            else
                sb.append(" ");


            parser.endListItem(context, currentList.peek(), itemId, start, end);
        }
    }

    public void visit(EngPage p)
    {
        iterate(p);
    }

    public void visit(WtText text)
    {
        if(!isInsideFilteredSection() && !text.getContent().replaceAll("[\n\r]", "").equalsIgnoreCase("")) {
            sb.append(text.getContent());
        }
    }

    public void visit(WtWhitespace w)
    {
        if(!isInsideFilteredSection()) {
            sb.append(" ");
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

    public void visit(WtXmlCharRef cr)
    {
        if(!isInsideFilteredSection()) {
            sb.append(Character.toChars(cr.getCodePoint()));
        }
    }

    public void visit(WtXmlEntityRef er)
    {
        String ch = er.getResolved();
        if (ch == null)
        {
            if(!isInsideFilteredSection()) {
                sb.append('&' + er.getName() + ';');
            }
        }
        else
        {
            if(!isInsideFilteredSection()) {
                sb.append(ch);
            }
        }

    }

    public void visit(WtUrl wtUrl)
    {
        if (!wtUrl.getProtocol().isEmpty())
        {
            if(!isInsideFilteredSection()) {
                sb.append(wtUrl.getProtocol() + ':');
            }
        }
        if(!isInsideFilteredSection()) {
            sb.append(wtUrl.getPath());
        }
    }

    public void visit(WtExternalLink link)
    {
        iterate(link.getTitle());
    }

    public void visit(WtInternalLink link)
    {
        try
        {
            if (link.getTarget().isResolved())
            {
                PageTitle page = PageTitle.make(config, link.getTarget().getAsString());
                if (page.getNamespace().equals(config.getNamespace("Category"))) {
                    sb.flush();
                    parser.category(context, link.getTarget().getAsString(), page.getTitle().replace('_', ' '), sb.length());
                    return;
                }
                else if(page.getNamespace().isFileNs() || page.getNamespace().isMediaNs()) {
                    return;
                }
            }
        }
        catch (LinkTargetException e)
        {
        }

        sb.flush();
        int start = sb.length();

        if(!isInsideFilteredSection()) {
            sb.append(link.getPrefix());
        }

        if (!link.hasTitle())
            iterate(link.getTarget());
        else
            iterate(link.getTitle());

        if(!isInsideFilteredSection()) {
            sb.append(link.getPostfix());
        }

        sb.flush();
        int end = sb.length();

        start = trimStart(start);
        end = trimEnd(end);

        if(start < end) {
            String target = link.getTarget().getAsString();
            if(target.startsWith("#")) {
                parser.anchor(context, page.getTitle(), target.substring(1), true, start, end);
            } else {
                int hashIndex = target.lastIndexOf('#');
                if(hashIndex == -1) {
                    parser.anchor(context, link.getTarget().getAsString(), null, true, start, end);
                } else {
                    parser.anchor(context, target.substring(0,hashIndex), target.substring(hashIndex+1), true, start, end);
                }
            }
        }
    }

    public void visit(WtSection s)
    {
        if (hasNotReadAbstract && sb.length() > 0) {
            parser.textAbstract(context, 0 , sb.length());
            hasNotReadAbstract = false;
        }

        int start = sb.length();
        WtHeading heading = s.getHeading();

        SwebleTextAstWalker textAstWalker = new SwebleTextAstWalker(config);
        currentSectionTitle = ((String)textAstWalker.go(s.getHeading())).trim();

        if(heading.getRtd().getFields().length >= 1 && heading.getRtd().getFields()[0].length >= 1) {
            String startText = heading.getRtd().getFields()[0][0].toString();

            int depth = startText.length();
            if(currentDepth > depth) {
                ArrayList<Integer> deleteThese = new ArrayList<Integer>();
                for(Map.Entry<Integer,String> entry : headers.tailMap(depth, false).entrySet()) {
                    deleteThese.add(entry.getKey());
                }

                for(Integer entry : deleteThese)
                    headers.remove(entry);
            }

            currentDepth = depth;
            String currentHeading = currentSectionTitle;
            headers.put(depth, currentHeading);

            parser.header(context, depth, currentHeading, headers.values(), start);
        }

        iterate(s.getBody());
    }

    public void visit(final WtTable table) {
        parser.table(context, new Table() {
            @Override
            public List<List<TableCell>> parse() {
                TableParser parser = new TableParser();
                parser.go(table);

                List<List<TableCell>> rows = new ArrayList<List<TableCell>>();

                for (int row = 0; row < parser.cells.size(); row++) {
                    List<WtTableCell> cols = parser.cells.get(row);
                    ArrayList<TableCell> output_cols = new ArrayList<TableCell>();

                    for (int col = 0; col < cols.size(); col++) {
                        final WtTableCell tableCell = cols.get(col);

                        output_cols.add(new TableCell(row, col) {
                            @Override
                            public <T2, Out> void parse(T2 model, AnnotationParser<T2, Out> parser) {
                                TextParser<T2,Out> textParser = new TextParser<T2,Out>(config, page);
                                textParser.parser = parser;
                                textParser.context.setModel(model);

                                parser.tableCellStart(textParser.context, row, col, 0);
                                textParser.go(tableCell.getBody());
                                textParser.sb.flush();
                                parser.tableCellEnd(textParser.context, row, col, 0, textParser.sb.length());
                            }

                            @Override
                            public void parse() {
                                int start = TextParser.this.sb.length();
                                TextParser.this.parser.tableCellStart(context, row, col, start);
                                iterate(tableCell.getBody());
                                int end = TextParser.this.sb.length();

                                start = trimStart(start);
                                end = trimEnd(end);

                                TextParser.this.parser.tableCellEnd(context, row, col, start, end);
                            }
                        });
                    }

                    rows.add(output_cols);
                }

                return rows;
            }
        }, sb.length());
    }

    private final int trimStart(int start) {
        for(; start < sb.length() && (sb.charAt(start) == '\n' ||sb.charAt(start) == ' '); start++);
        return start;
    }

    private final int trimEnd(int end) {
        end = end - 1;
        for(; end > 0 && (sb.charAt(end) == '\n' ||sb.charAt(end) == ' '); end--);
        return end + 1;
    }

    public void visit(WtParagraph p)
    {
        int start = sb.length();

        iterate(p);

        if(!isInsideFilteredSection()) {
            if(sb.length() > 0) {
                sb.append("\n\n");
            }
        }
        int end = sb.length();

        start = trimStart(start);
        end = trimEnd(end);

        if(end > start)
        {
            parser.paragraph(context, currentSectionTitle, start, end);
        }
    }

    public void visit(WtHorizontalRule hr)
    {
        sb.append("\n\n");
    }

    public void visit(WtXmlElement e)
    {
        if (e.getName().equalsIgnoreCase("br") || e.getName().equalsIgnoreCase("gallery") || e.getName().equalsIgnoreCase("imagemap"))
        {
        }
        else
        {
            iterate(e.getBody());
        }
    }

    // =========================================================================
    // Stuff we want to hide

    public void visit(final WtTemplate n)
    {
        boolean handled = false;

        if(!isInsideFilteredSection()) {
            if (n.toString().equalsIgnoreCase("WtTemplate([0] = WtName[WtText(\"spaced ndash\")], [1] = WtTemplateArguments[])")) {
                sb.append(" - ");
                handled = true;
            }
        }

        if(!handled) {
            if(n.getName().isResolved()) {
                String value = n.getName().getAsString().trim();
                parser.template(context, new Template(value) {

                    @Override
                    public List<TemplateArgument> parse() {
                        TemplateParser templateParser = new TemplateParser(config);
                        templateParser.go(n);

                        ArrayList<TemplateArgument> arguments = new ArrayList<TemplateArgument>();

                        //Got all arguments, now it is time to run through each argument
                        for (final TemplateParser.TemplateProperty templateProperty : templateParser.getTemplateProperties()) {
                            arguments.add(new TemplateArgument(templateProperty.path, templateProperty.argument, templateProperty.content) {

                                @Override
                                public void parse() throws EngineException, LinkTargetException {
                                    int start = sb.length();
                                    parser.startTemplateArgument(context, start);
                                    EngProcessedPage cp =
                                            SwebleParserUtil.parsePage(
                                                    config,
                                                    page.getTitle(),
                                                    page.getRevision(),
                                                    templateProperty.content
                                            );

                                    go(cp.getPage());

                                    int end = sb.length();
                                    start = trimStart(start);
                                    end = trimEnd(end);

                                    parser.endTemplateArgument(context, start, end);
                                }

                                @Override
                                public <M2,Out> void parse(M2 model, AnnotationParser<M2,Out> parser) throws EngineException, LinkTargetException {
                                    TextParser<M2,Out> textParser = new TextParser<M2,Out>(config, page);
                                    textParser.parser = parser;
                                    textParser.context.setModel(model);

                                    //We do not want abstracts in a template parsing.
                                    textParser.hasNotReadAbstract = false;
                                    textParser.currentSectionTitle = "";

                                    parser.startTemplateArgument(textParser.context,0);
                                    EngProcessedPage cp =
                                            SwebleParserUtil.parsePage(
                                                    config,
                                                    page.getTitle(),
                                                    page.getRevision(),
                                                    templateProperty.content
                                            );

                                    textParser.go(cp.getPage());
                                    textParser.sb.flush();
                                    parser.endTemplateArgument(textParser.context,0,textParser.sb.length());
                                }
                            });
                        }

                        return arguments;
                    }
                }, sb.length());
            }
        }
    }

    // =========================================================================

    private boolean isInsideFilteredSection() {
        if(this.currentSectionTitle.equalsIgnoreCase("See also") ||
                this.currentSectionTitle.equalsIgnoreCase("Notes") ||
                this.currentSectionTitle.equalsIgnoreCase("Writings") ||
                this.currentSectionTitle.equalsIgnoreCase("References") ||
                this.currentSectionTitle.equalsIgnoreCase("Publications") ||
                this.currentSectionTitle.equalsIgnoreCase("Bibliography") ||
                this.currentSectionTitle.equalsIgnoreCase("Bibliografi") ||
                this.currentSectionTitle.equalsIgnoreCase("Further reading") ||
                this.currentSectionTitle.equalsIgnoreCase("External links") ||
                this.currentSectionTitle.equalsIgnoreCase("Se även") ||
                this.currentSectionTitle.equalsIgnoreCase("Källor") ||
                this.currentSectionTitle.equalsIgnoreCase("Externa länkar") ||
                this.currentSectionTitle.equalsIgnoreCase("Referenser") ||
                this.currentSectionTitle.equalsIgnoreCase("Litteratur") ||
                this.currentSectionTitle.equalsIgnoreCase("参见") ||
                this.currentSectionTitle.equalsIgnoreCase("相关条目") ||
                this.currentSectionTitle.equalsIgnoreCase("注释") ||
                this.currentSectionTitle.equalsIgnoreCase("参考资料") ||
                this.currentSectionTitle.equalsIgnoreCase("外部链接") ||
                this.currentSectionTitle.equalsIgnoreCase("参看")) {
            return true;
        } else {
            return false;
        }

    }
}
