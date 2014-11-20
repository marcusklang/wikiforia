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

import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.wikipedia.lang.TemplateConfig;

import java.util.regex.Pattern;

/**
 * Sweble Wikimarkup to text
 */
public class SwebleWikimarkupToText extends SwebleWikimarkupParserBase<WikipediaPage> {

    public SwebleWikimarkupToText(TemplateConfig config) {
        super(config);
    }

    private final Pattern trimLineStartFix = Pattern.compile("^[\\t ]+", Pattern.MULTILINE);
    private final Pattern trimLineEndFix = Pattern.compile("[\\t ]+$", Pattern.MULTILINE);

    @Override
    protected WikipediaPage extract(Page page, EngProcessedPage cp) {
        SwebleTextAstWalker walker = new SwebleTextAstWalker(config);
        String text = (String)walker.go(cp.getPage());
        text = text.replaceAll(" {2,}", " ");
        text = text.replaceAll("\n{2,}", "\n\n");
        text = trimLineStartFix.matcher(text).replaceAll("");
        text = trimLineEndFix.matcher(text).replaceAll("");
        text = text.trim();

        return new WikipediaPage(page, text);
    }
}
