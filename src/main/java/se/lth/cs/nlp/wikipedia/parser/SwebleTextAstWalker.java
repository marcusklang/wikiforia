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
package se.lth.cs.nlp.wikipedia.parser;

import de.fau.cs.osr.ptk.common.AstVisitor;
import org.apache.commons.lang.StringUtils;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngPage;
import org.sweble.wikitext.parser.nodes.*;
import org.sweble.wikitext.parser.parser.LinkTargetException;

/**
 * A Sweble AST walker that extracts text content from the parser AST tree.
 */
public class SwebleTextAstWalker extends AstVisitor<WtNode> {
    private final WikiConfig config;

    private StringBuilder sb;

    private String currentSectionTitle;

    private boolean filterOutput;
    private boolean expectSectionTitle;


    // =========================================================================

    public SwebleTextAstWalker(WikiConfig config)
    {
        this.config = config;
    }

    @Override
    protected boolean before(WtNode node)
    {
        // This method is called by go() before visitation starts
        sb = new StringBuilder();
        currentSectionTitle = "";
        expectSectionTitle = false;
        return super.before(node);
    }

    @Override
    protected Object after(WtNode node, Object result)
    {
        return sb.toString();
    }

    // =========================================================================

    public void visit(WtNode n)
    {

    }

    public void visit(WtNodeList n)
    {
        iterate(n);
    }

    public void visit(WtUnorderedList e)
    {
        //int start = sb.length();
        iterate(e);
        //int end = sb.length();
        sb.append("\n\n");
    }

    public void visit(WtOrderedList e)
    {
        //int start = sb.length();
        iterate(e);
        //int end = sb.length();
        sb.append("\n\n");
    }

    public void visit(WtListItem item)
    {
        int start = sb.length();
        iterate(item);
        int end = sb.length();

        if(start != end) {
            String endpart = StringUtils.trim(sb.substring(start,end));
            if(endpart.length() > 0 && !endpart.endsWith("."))
                sb.append(". ");
            else
                sb.append(" ");
        }
    }

    public void visit(EngPage p)
    {
        iterate(p);
    }

    public void visit(WtText text)
    {
        if(expectSectionTitle) {
            if(!text.getContent().trim().equalsIgnoreCase("")) {
                currentSectionTitle = text.getContent().trim();
            }
            expectSectionTitle = false;
        }

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
    }

    public void visit(WtInternalLink link)
    {
        try
        {
            if (link.getTarget().isResolved())
            {
                PageTitle page = PageTitle.make(config, link.getTarget().getAsString());
                if (page.getNamespace().equals(config.getNamespace("Category"))) {
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

        //int start = sb.length();

        if(!isInsideFilteredSection()) {
            sb.append(link.getPrefix());
        }

        if (!link.hasTitle())
        {
            iterate(link.getTarget());
        }
        else
        {
            iterate(link.getTitle());
        }

        if(!isInsideFilteredSection()) {
            sb.append(link.getPostfix());
        }

        //int end = sb.length();
    }

    public void visit(WtSection s)
    {
        //int start = sb.length();

        filterOutput = true;
        expectSectionTitle = true;
        WtHeading heading = s.getHeading();

        iterate(s.getHeading());
        String title = currentSectionTitle;

        expectSectionTitle = false;
        filterOutput = false;

        if(!isInsideFilteredSection()) {
            if(sb.length() > 0) {
                if(sb.charAt(sb.length() -1) != '\n') {
                    sb.append("\n\n");
                }
            }
        }

        iterate(s.getBody());

        //int end = sb.length();

        //for(int i = sb.length()-1; i > 0 && sb.charAt(i) == '\n'; i--, end--);
    }

    public void visit(WtParagraph p)
    {
        //int start = sb.length();
        iterate(p);
        if(!isInsideFilteredSection()) {
            if(sb.length() > 0) {
                if(sb.charAt(sb.length() -1) != '\n') {
                    sb.append("\n\n");
                }
            }
        }
        //int end = sb.length();

        //for(int i = sb.length()-1; i > 0 && sb.charAt(i) == '\n'; i--, end--);
    }

    public void visit(WtHorizontalRule hr)
    {
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

    public void visit(WtImageLink n)
    {
    }

    public void visit(WtIllegalCodePoint n)
    {
    }

    public void visit(WtXmlComment n)
    {
    }

    public void visit(WtTemplate n)
    {
        if(!isInsideFilteredSection()) {
            if(n.toString().equalsIgnoreCase("WtTemplate([0] = WtName[WtText(\"spaced ndash\")], [1] = WtTemplateArguments[])")) {
                sb.append(" - ");
            }
        }
    }

    public void visit(WtTemplateArgument n)
    {
    }

    public void visit(WtTemplateParameter n)
    {
    }

    public void visit(WtTagExtension n)
    {
    }

    public void visit(WtPageSwitch n)
    {
    }

    // =========================================================================

    private boolean isInsideFilteredSection() {
        if(filterOutput) {
            return true;
        }

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