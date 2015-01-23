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
package se.lth.cs.nlp.wikipedia.lang;

import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.wikipedia.WikipediaPageType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Swedish Wikipedia configuration
 */
public class SwedishConfig extends TemplateConfig {
    public SwedishConfig() {
        addNamespaceAlias(-2,"Media");
        addNamespaceAlias(-1,"Special");
        addNamespaceAlias(1,"Diskussion");
        addNamespaceAlias(2,"Användare");
        addNamespaceAlias(3,"Användardiskussion");
        addNamespaceAlias(4,"Wikipedia");
        addNamespaceAlias(5,"Wikipediadiskussion");
        addNamespaceAlias(6,"Fil", "Image", "Bild");
        addNamespaceAlias(7,"Fildiskussion");
        addNamespaceAlias(8,"MediaWiki");
        addNamespaceAlias(9,"MediaWiki-diskussion");
        addNamespaceAlias(10,"Mall");
        addNamespaceAlias(11,"Malldiskussion");
        addNamespaceAlias(12,"Hjälp");
        addNamespaceAlias(13,"Hjälpdiskussion");
        addNamespaceAlias(14,"Kategori");
        addNamespaceAlias(15,"Kategoridiskussion");
        addNamespaceAlias(100,"Portal");
        addNamespaceAlias(101,"Portaldiskussion");
        addNamespaceAlias(446,"Utbildningsprogram");
        addNamespaceAlias(447,"Utbildningsprogramsdiskussion");
        addNamespaceAlias(828,"Modul");
        addNamespaceAlias(829,"Moduldiskussion");

        addI18nAlias("redirect", "#OMDIRIGERING", "#Omdirigering");
        addI18nAlias("filepath", "SÖKVÄG:");
    }

    private static final Pattern stubTextPattern = Pattern.compile("\\{\\{\\s*?([a-zA-Z0-9åäöÅÄÖ\\ ]*?stub)\\s*?\\}\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern disambiguationTextPattern = Pattern.compile("\\{\\{\\s*?(förgrening|gren)\\s*?\\}\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern listTitlePattern = Pattern.compile("(^lista\\s+över)|(\\(lista\\))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    private static final Pattern disambiguationTitlePattern = Pattern.compile(".*?\\((\\s|[\\_])*olika(\\s|[\\_])*betydelser(\\s|[\\_])*\\)", Pattern.CASE_INSENSITIVE);

    @Override
    public WikipediaPageType classifyPageType(Page page) {
        WikipediaPageType type = super.classifyPageType(page);
        if(type == WikipediaPageType.ARTICLE) {
            Matcher matcher = disambiguationTextPattern.matcher(page.getContent());
            if(matcher.find() || disambiguationTitlePattern.matcher(page.getTitle()).find() ) {
                return WikipediaPageType.DISAMBIGUATION;
            }

            matcher = stubTextPattern.matcher(page.getContent());
            if(matcher.find()) {
                return WikipediaPageType.STUB;
            }

            if(listTitlePattern.matcher(page.getTitle()).find())
                return WikipediaPageType.LIST;

            return WikipediaPageType.ARTICLE;
        }
        else
            return type;
    }

    @Override
    protected String getSiteName() {
        return "Wikipedia";
    }

    @Override
    protected String getWikiUrl() {
        return "http://sv.wikipedia.org/";
    }

    @Override
    public String getIso639() {
        return "sv";
    }
}
