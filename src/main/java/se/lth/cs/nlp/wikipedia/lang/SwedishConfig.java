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
