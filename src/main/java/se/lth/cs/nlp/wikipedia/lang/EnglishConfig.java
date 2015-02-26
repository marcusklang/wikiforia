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

import org.apache.commons.lang.StringUtils;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.wikipedia.WikipediaPageType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * English Wikipedia configuration
 */
public class EnglishConfig extends TemplateConfig {
    @Override
    protected String getSiteName() {
        return "Wikipedia";
    }

    @Override
    protected String getWikiUrl() {
        return "http://en.wikipedia.org/";
    }

    private static final Pattern stubTextPattern = Pattern.compile("\\{\\{\\s*?(.*?stub)\\s*?\\}\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern disambiguationTextPattern = Pattern.compile("\\{\\{\\.*?(disambiguation|disambig|disamb|geodis|hndis|dab)\\.*?\\}\\}", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern listTitlePattern = Pattern.compile("(^list\\s+of)", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL);
    private static final Pattern disambiguationTitlePattern = Pattern.compile(".*?\\((\\s)*disambiguation(\\s)*\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern templateContent = Pattern.compile("\\{\\{(.+)?\\}\\}");

    private static final String[] disambiguationParts = {"disambiguation", "disambig", "disamb", "geodis", "hndis", "dab"};

    public static boolean matchDisambiguation(String text) {
        Matcher matcher = templateContent.matcher(text);
        while(matcher.find()) {
            String templateContent = matcher.group(1).toLowerCase();
            for (String disambiguationPart : disambiguationParts) {
                if(templateContent.contains(disambiguationPart))
                    return true;
            }
        }

        return false;
    }

    public static boolean matchStub(String text) {
        Matcher matcher = templateContent.matcher(text);
        while(matcher.find()) {
            String templateContent = matcher.group(1);
            if(StringUtils.containsIgnoreCase("stub", text))
                return true;
        }

        return false;
    }

    @Override
    public WikipediaPageType classifyPageType(Page page) {
        WikipediaPageType type = super.classifyPageType(page);
        if(type == WikipediaPageType.ARTICLE) {
            //Matcher matcher = disambiguationTextPattern.matcher(page.getContent());
            if(matchDisambiguation(page.getContent()) || disambiguationTitlePattern.matcher(page.getTitle()).find() ) {
                return WikipediaPageType.DISAMBIGUATION;
            }

            //Matcher matcher = stubTextPattern.matcher(page.getContent());
            if(matchStub(page.getContent())) {
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
    public String getIso639() {
        return "en";
    }
}
