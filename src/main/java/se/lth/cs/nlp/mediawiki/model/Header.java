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
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Represents a Mediawiki dump header
 */
public class Header implements Serializable {
    private String lang;
    private String version;
    private Siteinfo siteinfo;

    protected Header() {

    }

    public Header(String lang, String version, Siteinfo siteinfo) {
        this.siteinfo = siteinfo;
        this.version = version;
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public String getVersion() {
        return version;
    }

    public Siteinfo getSiteinfo() {
        return siteinfo;
    }
}
