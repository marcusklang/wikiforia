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
package se.lth.cs.nlp.mediawiki.model;

/**
 * Represents a parsed page that is assumed to belong to a wikipedia dump.
 */
public class WikipediaPage extends Page {
    private final String text;

    public WikipediaPage(Page page, String text) {
        super(page.getHeader(), page.getId(), page.getTitle(), page.getContent(), page.getRevision(), page.getNamespace(), page.getFormat());
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getSignature() { return "{{page:"+this.getId()+"}}"; }
}
