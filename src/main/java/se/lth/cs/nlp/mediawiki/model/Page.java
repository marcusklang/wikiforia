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

import java.io.Serializable;

/**
 * Represents a MediaWiki page
 */
public class Page implements Serializable {
    private Header header;
    private long id;
    private String title;
    private String content;
    private long revision;
    private int namespace;
    private String format;

    protected Page() {

    }

    public Page(Header head, long id, String title, String content, long revision, int namespace, String format) {
        this.header = head;
        this.id = id;
        this.title = title;
        this.content = content;
        this.revision = revision;
        this.namespace = namespace;
        this.format = format;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getRevision() {
        return revision;
    }

    public String getFormat() {
        return format;
    }

    public int getNamespace() {
        return namespace;
    }

    public Header getHeader() {
        return header;
    }
}
