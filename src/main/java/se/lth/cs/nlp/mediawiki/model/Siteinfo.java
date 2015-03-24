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
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents siteinfo present in the mediawiki dump header.
 */
public class Siteinfo implements Serializable {
    private String sitename;
    private String dbname;
    private String base;
    private String generator;
    private Map<Integer,String> namespaces;

    protected Siteinfo() {

    }

    public Siteinfo(String sitename, String dbname, String base, String generator, Map<Integer, String> namespaces) {
        this.sitename = sitename;
        this.dbname = dbname;
        this.base = base;
        this.generator = generator;
        this.namespaces = Collections.unmodifiableMap(new TreeMap<Integer, String>(namespaces));
    }

    public String getSitename() {
        return sitename;
    }

    public String getDbname() {
        return dbname;
    }

    public String getBase() {
        return base;
    }

    public String getGenerator() {
        return generator;
    }

    public Map<Integer, String> getNamespaces() {
        return namespaces;
    }
}
