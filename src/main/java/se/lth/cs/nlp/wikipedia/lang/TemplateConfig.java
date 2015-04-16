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
import org.sweble.wikitext.engine.config.*;
import org.sweble.wikitext.engine.utils.DefaultConfig;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.wikipedia.WikipediaPageType;

import java.util.*;

/**
 * Programatically generate a default configuration for that is similar to that
 * of the English Wikipedia.
 */
public abstract class TemplateConfig
        extends
        DefaultConfig
{

    public List<String> getNamespaceAliases(int ns) {
        TreeSet<String> list = nsAliases.get(ns);
        return list == null ? Collections.<String>emptyList() : new ArrayList<>(list);
    }

    protected abstract String getSiteName();
    protected abstract String getWikiUrl();
    public abstract String getIso639();

    protected void addNamespaceAlias(int ns, String...aliases) {
        if(aliases.length == 0)
            throw new IllegalArgumentException("aliases must have length > 0");

        TreeSet<String> list = nsAliases.get(ns);
        if(list == null) {
            list = new TreeSet<String>();
            nsAliases.put(ns, list);
        }

        for (String aliase : aliases) {
            list.add(aliase);
        }
    }

    public WikiConfigImpl get() {
        WikiConfigImpl c = new WikiConfigImpl();
        this.configureWiki(c);
        return c;
    }

    private final HashMap<Integer,TreeSet<String>> nsAliases = new HashMap<Integer, TreeSet<String>>();
    private final HashMap<String,TreeSet<String>> i18nAliases = new HashMap<String, TreeSet<String>>();
    private final HashMap<String,TreeSet<String>> i18nCIAliases = new HashMap<String, TreeSet<String>>();

    protected void addI18nAlias(String key, String...aliases) {
        if(aliases.length == 0)
            throw new IllegalArgumentException("aliases must have length > 0");

        TreeSet<String> list = i18nAliases.get(key);
        if(list == null) {
            list = new TreeSet<String>();
            i18nAliases.put(key, list);
        }

        for (String aliase : aliases) {
            list.add(aliase);
        }
    }

    protected void addI18nCIAlias(String key, String...aliases) {
        if(aliases.length == 0)
            throw new IllegalArgumentException("aliases must have length > 0");

        TreeSet<String> list = i18nCIAliases.get(key);
        if(list == null) {
            list = new TreeSet<String>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.toLowerCase().compareTo(o2.toLowerCase());
                }
            });

            i18nCIAliases.put(key, list);
        }

        for (String aliase : aliases) {
            list.add(aliase);
        }
    }

    public WikipediaPageType classifyPageType(Page page) {
        switch(page.getNamespace()) {
            case 0:
                String data = page.getContent();
                if(page.getContent().startsWith(" "))
                    data = data.trim();

                for (String redirect : getCI18nAlias("redirect")) {
                    if(StringUtils.startsWithIgnoreCase(data, redirect))
                        return WikipediaPageType.REDIRECT;
                }
                if(StringUtils.startsWithIgnoreCase(data, "#REDIRECT"))
                    return WikipediaPageType.REDIRECT;

                return WikipediaPageType.ARTICLE;
            case 10:
                return WikipediaPageType.TEMPLATE;
            case 14:
                return WikipediaPageType.CATEGORY;
            default:
                return WikipediaPageType.OTHER;
        }
    }

    public List<String> getI18nAlias(String key) {
        TreeSet<String> list = i18nAliases.get(key);
        return list == null ? Collections.<String>emptyList() : new ArrayList<>(list);
    }

    public List<String> getCI18nAlias(String key) {
        TreeSet<String> list = i18nCIAliases.get(key);
        return list == null ? Collections.<String>emptyList() : new ArrayList<>(list);
    }

    public TemplateConfig() {
        super();
        addI18nCIAlias("expr", "#expr:");
        addI18nCIAlias("if", "#if:");
        addI18nCIAlias("ifeq", "#ifeq:");
        addI18nCIAlias("ifexpr", "#ifexpr:");
        addI18nCIAlias("iferror", "#iferror:");
        addI18nCIAlias("switch", "#switch:");
        addI18nCIAlias("ifexist", "#ifexist:");
        addI18nCIAlias("time", "#time:");
        addI18nCIAlias("titleparts", "#titleparts:");
        addI18nCIAlias("redirect", "#REDIRECT");
        addI18nAlias("currentmonth", "CURRENTMONTH", "CURRENTMONTH2");
        addI18nAlias("currentday", "CURRENTDAY");
        addI18nAlias("currentyear", "CURRENTYEAR");
        addI18nAlias("pagename", "PAGENAME", "PAGENAME:");
        addI18nAlias("pagenamee", "PAGENAMEE", "PAGENAMEE:");
        addI18nAlias("namespace", "NAMESPACE", "NAMESPACE:");
        addI18nAlias("talkspace","TALKSPACE");
        addI18nAlias("subjectspace","SUBJECTSPACE", "ARTICLESPACE");
        addI18nAlias("fullpagename","FULLPAGENAME");
        addI18nAlias("fullpagenamee","FULLPAGENAMEE");
        addI18nAlias("basepagename","BASEPAGENAME");
        addI18nAlias("talkpagename","TALKPAGENAME", "TALKPAGENAME:");
        addI18nAlias("subjectpagename","SUBJECTPAGENAME", "ARTICLEPAGENAME");
        addI18nCIAlias("safesubst","SAFESUBST:");
        addI18nAlias("sitename","SITENAME");
        addI18nCIAlias("ns","NS:");
        addI18nCIAlias("fullurl","FULLURL:");
        addI18nCIAlias("lcfirst","LCFIRST:");
        addI18nCIAlias("ucfirst","UCFIRST:");
        addI18nCIAlias("lc","LC:");
        addI18nCIAlias("uc","UC:");
        addI18nCIAlias("urlencode","URLENCODE:");
        addI18nAlias("contentlanguage","CONTENTLANGUAGE", "CONTENTLANG");
        addI18nCIAlias("padleft","PADLEFT:");
        addI18nAlias("defaultsort","DEFAULTSORT:", "DEFAULTSORTKEY:", "DEFAULTCATEGORYSORT:");
        addI18nCIAlias("filepath","FILEPATH:");
        addI18nCIAlias("tag","#tag:");
        addI18nAlias("protectionlevel","PROTECTIONLEVEL:");
    }

    /*
    public static WikiConfigImpl generate()
    {
        WikiConfigImpl c = new WikiConfigImpl();
        new ().configureWiki(c);
        return c;
    }*/

    protected void configureSiteProperties(WikiConfigImpl c)
    {
        c.setSiteName(getSiteName());

        c.setWikiUrl(getWikiUrl());

        c.setContentLang(getIso639());

        c.setIwPrefix(getIso639());
    }

    protected ParserConfigImpl configureParser(WikiConfigImpl c)
    {
        ParserConfigImpl pc = super.configureParser(c);

        // --[ Link classification and parsing ]--

        pc.setInternalLinkPrefixPattern(null);
        pc.setInternalLinkPostfixPattern("[a-z]+");

        return pc;
    }

    protected void addNamespaces(WikiConfigImpl c)
    {
        c.addNamespace(new NamespaceImpl(
                -2,
                "Media",
                "Media",
                false,
                false,
                getNamespaceAliases(-2)));

        c.addNamespace(new NamespaceImpl(
                -1,
                "Special",
                "Special",
                false,
                false,
                getNamespaceAliases(-1)));

        c.addNamespace(new NamespaceImpl(
                0,
                "",
                "",
                false,
                false,
                getNamespaceAliases(0)));

        c.addNamespace(new NamespaceImpl(
                1,
                "Talk",
                "Talk",
                false,
                false,
                getNamespaceAliases(1)));

        c.addNamespace(new NamespaceImpl(
                2,
                "User",
                "User",
                false,
                false,
                getNamespaceAliases(2)));

        c.addNamespace(new NamespaceImpl(
                3,
                "User talk",
                "User talk",
                false,
                false,
                getNamespaceAliases(3)));

        c.addNamespace(new NamespaceImpl(
                4,
                "Wikipedia",
                "Project",
                false,
                false,
                getNamespaceAliases(4)));

        c.addNamespace(new NamespaceImpl(
                5,
                "Wikipedia talk",
                "Project talk",
                false,
                false,
                getNamespaceAliases(5)));

        c.addNamespace(new NamespaceImpl(
                6,
                "File",
                "File",
                false,
                true,
                getNamespaceAliases(6)));

        c.addNamespace(new NamespaceImpl(
                7,
                "File talk",
                "File talk",
                false,
                false,
                getNamespaceAliases(7)));

        c.addNamespace(new NamespaceImpl(
                8,
                "MediaWiki",
                "MediaWiki",
                false,
                false,
                getNamespaceAliases(8)));

        c.addNamespace(new NamespaceImpl(
                9,
                "MediaWiki talk",
                "MediaWiki talk",
                false,
                false,
                getNamespaceAliases(9)));

        c.addNamespace(new NamespaceImpl(
                10,
                "Template",
                "Template",
                false,
                false,
                getNamespaceAliases(10)));

        c.addNamespace(new NamespaceImpl(
                11,
                "Template talk",
                "Template talk",
                false,
                false,
                getNamespaceAliases(11)));

        c.addNamespace(new NamespaceImpl(
                12,
                "Help",
                "Help",
                false,
                false,
                getNamespaceAliases(12)));

        c.addNamespace(new NamespaceImpl(
                13,
                "Help talk",
                "Help talk",
                false,
                false,
                getNamespaceAliases(13)));

        c.addNamespace(new NamespaceImpl(
                14,
                "Category",
                "Category",
                false,
                false,
                getNamespaceAliases(14)));

        c.addNamespace(new NamespaceImpl(
                15,
                "Category talk",
                "Category talk",
                false,
                false,
                getNamespaceAliases(15)));

        c.addNamespace(new NamespaceImpl(
                100,
                "Portal",
                "Portal",
                false,
                false,
                getNamespaceAliases(100)));

        c.addNamespace(new NamespaceImpl(
                101,
                "Portal talk",
                "Portal talk",
                false,
                false,
                getNamespaceAliases(101)));

        c.addNamespace(new NamespaceImpl(
                108,
                "Book",
                "Book",
                false,
                false,
                getNamespaceAliases(108)));

        c.addNamespace(new NamespaceImpl(
                109,
                "Book talk",
                "Book talk",
                false,
                false,
                getNamespaceAliases(109)));

        c.setDefaultNamespace(c.getNamespace(0));
        c.setTemplateNamespace(c.getNamespace(10));
    }

    protected void addInterwikis(WikiConfigImpl c)
    {
        c.addInterwiki(new InterwikiImpl(
                "aa",
                "http://aa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ab",
                "http://ab.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "abbenormal",
                "http://ourpla.net/cgi/pikie?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "acronym",
                "http://www.acronymfinder.com/af-query.asp?String=exact&Acronym=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "advisory",
                "http://advisory.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "advogato",
                "http://www.advogato.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "aew",
                "http://wiki.arabeyes.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "af",
                "http://af.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "airwarfare",
                "http://airwarfare.com/mediawiki-1.4.5/index.php?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "aiwiki",
                "http://www.ifi.unizh.ch/ailab/aiwiki/aiw.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ak",
                "http://ak.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "allwiki",
                "http://allwiki.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "als",
                "http://als.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "am",
                "http://am.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "an",
                "http://an.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ang",
                "http://ang.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "appropedia",
                "http://www.appropedia.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "aquariumwiki",
                "http://www.theaquariumwiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ar",
                "http://ar.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "arc",
                "http://arc.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "arxiv",
                "http://arxiv.org/abs/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "arz",
                "http://arz.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "as",
                "http://as.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "aspienetwiki",
                "http://aspie.mela.de/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ast",
                "http://ast.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "atmwiki",
                "http://www.otterstedt.de/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "av",
                "http://av.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ay",
                "http://ay.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "az",
                "http://az.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "b",
                "http://en.wikibooks.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ba",
                "http://ba.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bar",
                "http://bar.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bat-smg",
                "http://bat-smg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "battlestarwiki",
                "http://en.battlestarwiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bcl",
                "http://bcl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "be",
                "http://be.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "be-x-old",
                "http://be-x-old.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bemi",
                "http://bemi.free.fr/vikio/index.php?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "benefitswiki",
                "http://www.benefitslink.com/cgi-bin/wiki.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "betawiki",
                "http://translatewiki.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "betawikiversity",
                "http://beta.wikiversity.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bg",
                "http://bg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bh",
                "http://bh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bi",
                "http://bi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "biblewiki",
                "http://bible.tmtm.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bluwiki",
                "http://www.bluwiki.org/go/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bm",
                "http://bm.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bn",
                "http://bn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bo",
                "http://bo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "botwiki",
                "http://botwiki.sno.cc/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "boxrec",
                "http://www.boxrec.com/media/index.php?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bpy",
                "http://bpy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "br",
                "http://br.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "brickwiki",
                "http://brickwiki.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bridgeswiki",
                "http://c2.com:8000/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bs",
                "http://bs.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bug",
                "http://bug.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bugzilla",
                "https://bugzilla.wikimedia.org/show_bug.cgi?id=$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "buzztard",
                "http://buzztard.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bxr",
                "http://bxr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "bytesmiths",
                "http://www.Bytesmiths.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "c2",
                "http://c2.com/cgi/wiki?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "c2find",
                "http://c2.com/cgi/wiki?FindPage&value=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ca",
                "http://ca.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cache",
                "http://www.google.com/search?q=cache:$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "canwiki",
                "http://www.can-wiki.info/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "canyonwiki",
                "http://www.canyonwiki.com/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cbk-zam",
                "http://cbk-zam.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cdo",
                "http://cdo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ce",
                "http://ce.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ceb",
                "http://ceb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cellwiki",
                "http://cell.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "centralwikia",
                "http://www.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ch",
                "http://ch.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "chapter",
                "http://en.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "chej",
                "http://esperanto.blahus.cz/cxej/vikio/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cho",
                "http://cho.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "choralwiki",
                "http://www.cpdl.org/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "chr",
                "http://chr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "chy",
                "http://chy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ciscavate",
                "http://ciscavate.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "citizendium",
                "http://en.citizendium.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ckwiss",
                "http://ck-wissen.de/ckwiki/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "closed-zh-tw",
                "http://closed-zh-tw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cndbname",
                "http://cndb.com/actor.html?name=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cndbtitle",
                "http://cndb.com/movie.html?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "co",
                "http://co.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "colab",
                "http://colab.info",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "comcom",
                "http://comcom.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "comixpedia",
                "http://www.comixpedia.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "commons",
                "http://commons.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "communityscheme",
                "http://community.schemewiki.org/?c=s&key=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "comune",
                "http://rete.comuni-italiani.it/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "consciousness",
                "http://teadvus.inspiral.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "corpknowpedia",
                "http://corpknowpedia.org/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cr",
                "http://cr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "crazyhacks",
                "http://www.crazy-hacks.org/wiki/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "creatureswiki",
                "http://creatures.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "crh",
                "http://crh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cs",
                "http://cs.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "csb",
                "http://csb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cu",
                "http://cu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cv",
                "http://cv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cxej",
                "http://esperanto.blahus.cz/cxej/vikio/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cy",
                "http://cy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "cz",
                "http://cz.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "da",
                "http://da.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dawiki",
                "http://www.dienstag-abend.de/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dbdump",
                "http://download.wikimedia.org/$1/latest/",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dcc",
                "http://www.dccwiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dcdatabase",
                "http://www.dcdatabaseproject.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dcma",
                "http://www.christian-morgenstern.de/dcma/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "de",
                "http://de.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dejanews",
                "http://www.deja.com/=dnc/getdoc.xp?AN=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "delicious",
                "http://del.icio.us/tag/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "demokraatia",
                "http://wiki.demokraatia.ee/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "devmo",
                "http://developer.mozilla.org/en/docs/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dict",
                "http://www.dict.org/bin/Dict?Database=*&Form=Dict1&Strategy=*&Query=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dictionary",
                "http://www.dict.org/bin/Dict?Database=*&Form=Dict1&Strategy=*&Query=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "diq",
                "http://diq.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "disinfopedia",
                "http://www.sourcewatch.org/wiki.phtml?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "distributedproofreaders",
                "http://www.pgdp.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "distributedproofreadersca",
                "http://www.pgdpcanada.net/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dk",
                "http://dk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dmoz",
                "http://www.dmoz.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dmozs",
                "http://www.dmoz.org/cgi-bin/search?search=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "docbook",
                "http://wiki.docbook.org/topic/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "doi",
                "http://dx.doi.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "doom_wiki",
                "http://doom.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "download",
                "http://download.wikimedia.org/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "drae",
                "http://buscon.rae.es/draeI/SrvltGUIBusUsual?LEMA=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dreamhost",
                "http://wiki.dreamhost.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "drumcorpswiki",
                "http://www.drumcorpswiki.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dsb",
                "http://dsb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dv",
                "http://dv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dwjwiki",
                "http://www.suberic.net/cgi-bin/dwj/wiki.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "dz",
                "http://dz.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "echei",
                "http://www.ikso.net/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ecoreality",
                "http://www.EcoReality.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ecxei",
                "http://www.ikso.net/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ee",
                "http://ee.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "efnetceewiki",
                "http://purl.net/wiki/c/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "efnetcppwiki",
                "http://purl.net/wiki/cpp/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "efnetpythonwiki",
                "http://purl.net/wiki/python/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "efnetxmlwiki",
                "http://purl.net/wiki/xml/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "el",
                "http://el.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "elibre",
                "http://enciclopedia.us.es/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "emacswiki",
                "http://www.emacswiki.org/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "eml",
                "http://eml.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "en",
                "http://en.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "energiewiki",
                "http://www.netzwerk-energieberater.de/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "eo",
                "http://eo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "eokulturcentro",
                "http://esperanto.toulouse.free.fr/nova/wikini/wakka.php?wiki=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "epo",
                "http://epo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "es",
                "http://es.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "et",
                "http://et.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ethnologue",
                "http://www.ethnologue.com/show_language.asp?code=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "eu",
                "http://eu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "evowiki",
                "http://wiki.cotch.net/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "exotica",
                "http://www.exotica.org.uk/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ext",
                "http://ext.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "e\u0109ei",
                "http://www.ikso.net/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fa",
                "http://fa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fanimutationwiki",
                "http://wiki.animutationportal.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ff",
                "http://ff.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fi",
                "http://fi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "finalempire",
                "http://final-empire.sourceforge.net/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "finalfantasy",
                "http://finalfantasy.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "finnix",
                "http://www.finnix.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fiu-vro",
                "http://fiu-vro.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fj",
                "http://fj.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "flickruser",
                "http://www.flickr.com/people/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "floralwiki",
                "http://www.floralwiki.co.uk/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "flyerwiki-de",
                "http://de.flyerwiki.net/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fo",
                "http://fo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "foldoc",
                "http://www.foldoc.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "forthfreak",
                "http://wiki.forthfreak.net/index.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "foundation",
                "http://wikimediafoundation.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "foxwiki",
                "http://fox.wikis.com/wc.dll?Wiki~$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fr",
                "http://fr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freebio",
                "http://freebiology.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freebsdman",
                "http://www.FreeBSD.org/cgi/man.cgi?apropos=1&query=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freeculturewiki",
                "http://wiki.freeculture.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freedomdefined",
                "http://freedomdefined.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freefeel",
                "http://freefeel.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "freekiwiki",
                "http://wiki.freegeek.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "frp",
                "http://frp.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fur",
                "http://fur.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "fy",
                "http://fy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ga",
                "http://ga.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gan",
                "http://gan.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ganfyd",
                "http://ganfyd.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gausswiki",
                "http://gauss.ffii.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gd",
                "http://gd.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gentoo-wiki",
                "http://gentoo-wiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "genwiki",
                "http://wiki.genealogy.net/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gl",
                "http://gl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "glk",
                "http://glk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "globalvoices",
                "http://cyber.law.harvard.edu/dyn/globalvoices/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "glossarwiki",
                "http://glossar.hs-augsburg.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "glossarywiki",
                "http://glossary.hs-augsburg.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gn",
                "http://gn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "golem",
                "http://golem.linux.it/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "google",
                "http://www.google.com/search?q=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "googledefine",
                "http://www.google.com/search?q=define:$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "googlegroups",
                "http://groups.google.com/groups?q=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "got",
                "http://got.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gotamac",
                "http://www.got-a-mac.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "greatlakeswiki",
                "http://greatlakeswiki.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gu",
                "http://gu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "guildwiki",
                "http://gw.gamewikis.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gutenberg",
                "http://www.gutenberg.org/etext/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gutenbergwiki",
                "http://www.gutenberg.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "gv",
                "http://gv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "h2wiki",
                "http://halowiki.net/p/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ha",
                "http://ha.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hak",
                "http://hak.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hammondwiki",
                "http://www.dairiki.org/HammondWiki/index.php3?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "haw",
                "http://haw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "he",
                "http://he.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "heroeswiki",
                "http://heroeswiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "herzkinderwiki",
                "http://www.herzkinderinfo.de/Mediawiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hi",
                "http://hi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hif",
                "http://hif.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hkmule",
                "http://www.hkmule.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ho",
                "http://ho.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "holshamtraders",
                "http://www.holsham-traders.de/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hr",
                "http://hr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hrfwiki",
                "http://fanstuff.hrwiki.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hrwiki",
                "http://www.hrwiki.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hsb",
                "http://hsb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ht",
                "http://ht.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hu",
                "http://hu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "humancell",
                "http://www.humancell.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hupwiki",
                "http://wiki.hup.hu/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hy",
                "http://hy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "hz",
                "http://hz.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ia",
                "http://ia.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "id",
                "http://id.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ie",
                "http://ie.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ig",
                "http://ig.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ii",
                "http://ii.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ik",
                "http://ik.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ilo",
                "http://ilo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "imdbcharacter",
                "http://www.imdb.com/character/ch$1/",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "imdbcompany",
                "http://www.imdb.com/company/co$1/",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "imdbname",
                "http://www.imdb.com/name/nm$1/",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "imdbtitle",
                "http://www.imdb.com/title/tt$1/",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "incubator",
                "http://incubator.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "infoanarchy",
                "http://www.infoanarchy.org/en/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "infosecpedia",
                "http://www.infosecpedia.org/pedia/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "infosphere",
                "http://theinfosphere.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "io",
                "http://io.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "is",
                "http://is.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "iso639-3",
                "http://www.sil.org/iso639-3/documentation.asp?id=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "it",
                "http://it.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "iu",
                "http://iu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "iuridictum",
                "http://iuridictum.pecina.cz/w/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ja",
                "http://ja.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jameshoward",
                "http://jameshoward.us/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "javanet",
                "http://wiki.java.net/bin/view/Main/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "javapedia",
                "http://wiki.java.net/bin/view/Javapedia/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jbo",
                "http://jbo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jefo",
                "http://esperanto-jeunes.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jiniwiki",
                "http://www.cdegroot.com/cgi-bin/jini?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jp",
                "http://jp.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jspwiki",
                "http://www.ecyrd.com/JSPWiki/Wiki.jsp?page=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jstor",
                "http://www.jstor.org/journals/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "jv",
                "http://jv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ka",
                "http://ka.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kaa",
                "http://kaa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kab",
                "http://kab.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kamelo",
                "http://kamelopedia.mormo.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "karlsruhe",
                "http://ka.stadtwiki.net/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kerimwiki",
                "http://wiki.oxus.net/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kg",
                "http://kg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ki",
                "http://ki.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kinowiki",
                "http://kino.skripov.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kj",
                "http://kj.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kk",
                "http://kk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kl",
                "http://kl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "km",
                "http://km.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kmwiki",
                "http://kmwiki.wikispaces.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kn",
                "http://kn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ko",
                "http://ko.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kontuwiki",
                "http://kontu.merri.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "koslarwiki",
                "http://wiki.koslar.de/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kpopwiki",
                "http://www.kpopwiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kr",
                "http://kr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ks",
                "http://ks.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ksh",
                "http://ksh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ku",
                "http://ku.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kv",
                "http://kv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "kw",
                "http://kw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ky",
                "http://ky.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "la",
                "http://la.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lad",
                "http://lad.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lb",
                "http://lb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lbe",
                "http://lbe.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lg",
                "http://lg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "li",
                "http://li.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lij",
                "http://lij.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "linguistlist",
                "http://linguistlist.org/forms/langs/LLDescription.cfm?code=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "linuxwiki",
                "http://www.linuxwiki.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "linuxwikide",
                "http://www.linuxwiki.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "liswiki",
                "http://liswiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "literateprograms",
                "http://en.literateprograms.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "livepedia",
                "http://www.livepedia.gr/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lmo",
                "http://lmo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ln",
                "http://ln.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lo",
                "http://lo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lojban",
                "http://www.lojban.org/tiki/tiki-index.php?page=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lostpedia",
                "http://lostpedia.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lqwiki",
                "http://wiki.linuxquestions.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lt",
                "http://lt.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lugkr",
                "http://lug-kr.sourceforge.net/cgi-bin/lugwiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "luxo",
                "http://toolserver.org/~luxo/contributions/contributions.php?user=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lv",
                "http://lv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "lyricwiki",
                "http://www.lyricwiki.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mail",
                "https://lists.wikimedia.org/mailman/listinfo/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mailarchive",
                "http://lists.wikimedia.org/pipermail/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "map-bms",
                "http://map-bms.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mariowiki",
                "http://www.mariowiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "marveldatabase",
                "http://www.marveldatabase.com/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mdf",
                "http://mdf.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "meatball",
                "http://www.usemod.com/cgi-bin/mb.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mediazilla",
                "https://bugzilla.wikimedia.org/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "memoryalpha",
                "http://memory-alpha.org/en/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "metawiki",
                "http://sunir.org/apps/meta.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "metawikipedia",
                "http://meta.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mg",
                "http://mg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mh",
                "http://mh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mi",
                "http://mi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mineralienatlas",
                "http://www.mineralienatlas.de/lexikon/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "minnan",
                "http://minnan.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mk",
                "http://mk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ml",
                "http://ml.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mn",
                "http://mn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mo",
                "http://mo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "moinmoin",
                "http://moinmo.in/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "monstropedia",
                "http://www.monstropedia.org/?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mosapedia",
                "http://mosapedia.de/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mozcom",
                "http://mozilla.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mozillawiki",
                "http://wiki.mozilla.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mozillazinekb",
                "http://kb.mozillazine.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mr",
                "http://mr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ms",
                "http://ms.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mt",
                "http://mt.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mus",
                "http://mus.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "musicbrainz",
                "http://wiki.musicbrainz.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mw",
                "http://www.mediawiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mwod",
                "http://www.merriam-webster.com/cgi-bin/dictionary?book=Dictionary&va=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mwot",
                "http://www.merriam-webster.com/cgi-bin/thesaurus?book=Thesaurus&va=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "my",
                "http://my.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "myv",
                "http://myv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "mzn",
                "http://mzn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "n",
                "http://en.wikinews.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "na",
                "http://na.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nah",
                "http://nah.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nan",
                "http://nan.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nap",
                "http://nap.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nb",
                "http://nb.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nds",
                "http://nds.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nds-nl",
                "http://nds-nl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ne",
                "http://ne.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "netvillage",
                "http://www.netbros.com/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "new",
                "http://new.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ng",
                "http://ng.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nkcells",
                "http://www.nkcells.info/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nl",
                "http://nl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nn",
                "http://nn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "no",
                "http://no.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nomcom",
                "http://nomcom.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nosmoke",
                "http://no-smok.net/nsmk/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nost",
                "http://nostalgia.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nov",
                "http://nov.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nrm",
                "http://nrm.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "nv",
                "http://nv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ny",
                "http://ny.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "oc",
                "http://oc.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "oeis",
                "http://www.research.att.com/~njas/sequences/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "oldwikisource",
                "http://wikisource.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "olpc",
                "http://wiki.laptop.org/go/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "om",
                "http://om.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "onelook",
                "http://www.onelook.com/?ls=b&w=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "openfacts",
                "http://openfacts.berlios.de/index.phtml?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "openstreetmap",
                "http://wiki.openstreetmap.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "openwetware",
                "http://openwetware.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "openwiki",
                "http://openwiki.com/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "opera7wiki",
                "http://operawiki.info/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "or",
                "http://or.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "organicdesign",
                "http://www.organicdesign.co.nz/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "orgpatterns",
                "http://www.bell-labs.com/cgi-user/OrgPatterns/OrgPatterns?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "orthodoxwiki",
                "http://orthodoxwiki.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "os",
                "http://os.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "osi reference model",
                "http://wiki.tigma.ee/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "otrs",
                "https://ticket.wikimedia.org/otrs/index.pl?Action=AgentTicketZoom&TicketID=$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "otrswiki",
                "http://otrs-wiki.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ourmedia",
                "http://www.socialtext.net/ourmedia/index.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pa",
                "http://pa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pag",
                "http://pag.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "paganwiki",
                "http://www.paganwiki.org/wiki/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pam",
                "http://pam.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "panawiki",
                "http://wiki.alairelibre.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pangalacticorg",
                "http://www.pangalactic.org/Wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pap",
                "http://pap.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "patwiki",
                "http://gauss.ffii.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pdc",
                "http://pdc.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "perlconfwiki",
                "http://perl.conf.hu/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "perlnet",
                "http://perl.net.au/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "personaltelco",
                "http://www.personaltelco.net/index.cgi/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "phpwiki",
                "http://phpwiki.sourceforge.net/phpwiki/index.php?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "phwiki",
                "http://wiki.pocketheaven.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pi",
                "http://pi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pih",
                "http://pih.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pl",
                "http://pl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "planetmath",
                "http://planetmath.org/?op=getobj&from=objects&id=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pmeg",
                "http://www.bertilow.com/pmeg/$1.php",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pms",
                "http://pms.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pmwiki",
                "http://old.porplemontage.com/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pnt",
                "http://pnt.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ps",
                "http://ps.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "psycle",
                "http://psycle.sourceforge.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pt",
                "http://pt.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "purlnet",
                "http://purl.oclc.org/NET/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pythoninfo",
                "http://www.python.org/cgi-bin/moinmoin/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pythonwiki",
                "http://www.pythonwiki.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "pywiki",
                "http://c2.com/cgi/wiki?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "q",
                "http://en.wikiquote.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "qcwiki",
                "http://wiki.quantumchemistry.net/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "qu",
                "http://qu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "quality",
                "http://quality.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "qwiki",
                "http://qwiki.caltech.edu/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "r3000",
                "http://prinsig.se/weekee/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "raec",
                "http://www.raec.clacso.edu.ar:8080/raec/Members/raecpedia/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rakwiki",
                "http://rakwiki.no-ip.info/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "reuterswiki",
                "http://glossary.reuters.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rev",
                "http://www.mediawiki.org/wiki/Special:Code/MediaWiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "revo",
                "http://purl.org/NET/voko/revo/art/$1.html",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rfc",
                "http://tools.ietf.org/html/rfc$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rheinneckar",
                "http://wiki.rhein-neckar.de/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rm",
                "http://rm.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rmy",
                "http://rmy.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rn",
                "http://rn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ro",
                "http://ro.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "roa-rup",
                "http://roa-rup.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "roa-tara",
                "http://roa-tara.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "robowiki",
                "http://robowiki.net/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rowiki",
                "http://wiki.rennkuckuck.de/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ru",
                "http://ru.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "rw",
                "http://rw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "s",
                "http://en.wikisource.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "s23wiki",
                "http://s23.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sa",
                "http://sa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sah",
                "http://sah.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sc",
                "http://sc.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "scholar",
                "http://scholar.google.com/scholar?q=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "schoolswp",
                "http://schools-wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "scn",
                "http://scn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sco",
                "http://sco.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "scores",
                "http://www.imslp.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "scoutwiki",
                "http://en.scoutwiki.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "scramble",
                "http://www.scramble.nl/wiki/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sd",
                "http://sd.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "se",
                "http://se.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "seapig",
                "http://www.seapig.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "seattlewiki",
                "http://seattlewiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "seattlewireless",
                "http://seattlewireless.net/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "senseislibrary",
                "http://senseis.xmp.net/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sg",
                "http://sg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sh",
                "http://sh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "shakti",
                "http://cgi.algonet.se/htbin/cgiwrap/pgd/ShaktiWiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "si",
                "http://si.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "silcode",
                "http://www.sil.org/iso639-3/documentation.asp?id=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "simple",
                "http://simple.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sk",
                "http://sk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sl",
                "http://sl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "slashdot",
                "http://slashdot.org/article.pl?sid=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "slwiki",
                "http://wiki.secondlife.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sm",
                "http://sm.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "smikipedia",
                "http://www.smiki.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sn",
                "http://sn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "so",
                "http://so.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sourceforge",
                "http://sourceforge.net/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "spcom",
                "http://spcom.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "species",
                "http://species.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sq",
                "http://sq.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "squeak",
                "http://wiki.squeak.org/squeak/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sr",
                "http://sr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "srn",
                "http://srn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ss",
                "http://ss.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "st",
                "http://st.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "stable",
                "http://stable.toolserver.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "stq",
                "http://stq.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "strategywiki",
                "http://strategywiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "su",
                "http://su.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sulutil",
                "http://toolserver.org/~vvv/sulutil.php?user=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "susning",
                "http://www.susning.nu/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sv",
                "http://sv.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "svgwiki",
                "http://www.protocol7.com/svg-wiki/default.asp?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "svn",
                "http://svn.wikimedia.org/viewvc/mediawiki/$1?view=log",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "sw",
                "http://sw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "swinbrain",
                "http://mercury.it.swin.edu.au/swinbrain/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "swingwiki",
                "http://www.swingwiki.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "swtrain",
                "http://train.spottingworld.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "szl",
                "http://szl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ta",
                "http://ta.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tabwiki",
                "http://www.tabwiki.com/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "takipedia",
                "http://www.takipedia.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tavi",
                "http://tavi.sourceforge.net/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tclerswiki",
                "http://wiki.tcl.tk/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "te",
                "http://te.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "technorati",
                "http://www.technorati.com/search/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tejo",
                "http://www.tejo.org/vikio/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tesoltaiwan",
                "http://www.tesol-taiwan.org/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "testwiki",
                "http://test.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tet",
                "http://tet.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tg",
                "http://tg.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "th",
                "http://th.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "thelemapedia",
                "http://www.thelemapedia.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "theopedia",
                "http://www.theopedia.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "theppn",
                "http://wiki.theppn.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "thinkwiki",
                "http://www.thinkwiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ti",
                "http://ti.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tibiawiki",
                "http://tibia.erig.net/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ticket",
                "https://ticket.wikimedia.org/otrs/index.pl?Action=AgentTicketZoom&TicketNumber=$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tk",
                "http://tk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tl",
                "http://tl.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tmbw",
                "http://tmbw.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tmnet",
                "http://www.technomanifestos.net/?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tmwiki",
                "http://www.EasyTopicMaps.com/?page=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tn",
                "http://tn.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "to",
                "http://to.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tokipona",
                "http://tokipona.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tokyonights",
                "http://wiki.tokyo-nights.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tools",
                "http://toolserver.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tp",
                "http://tp.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tpi",
                "http://tpi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tr",
                "http://tr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "translatewiki",
                "http://translatewiki.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "trash!italia",
                "http://trashware.linux.it/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ts",
                "http://ts.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tswiki",
                "http://wiki.toolserver.org/view/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tt",
                "http://tt.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tum",
                "http://tum.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "turismo",
                "http://www.tejo.org/turismo/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tviv",
                "http://tviv.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tvtropes",
                "http://www.tvtropes.org/pmwiki/pmwiki.php/Main/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tw",
                "http://tw.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "twiki",
                "http://twiki.org/cgi-bin/view/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "twistedwiki",
                "http://purl.net/wiki/twisted/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ty",
                "http://ty.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "tyvawiki",
                "http://www.tyvawiki.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "udm",
                "http://udm.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ug",
                "http://ug.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "uk",
                "http://uk.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "uncyclopedia",
                "http://uncyclopedia.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "unreal",
                "http://wiki.beyondunreal.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ur",
                "http://ur.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "urbandict",
                "http://www.urbandictionary.com/define.php?term=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "usej",
                "http://www.tejo.org/usej/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "usemod",
                "http://www.usemod.com/cgi-bin/wiki.pl?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "uz",
                "http://uz.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "v",
                "http://en.wikiversity.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "valuewiki",
                "http://www.valuewiki.com/w/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "ve",
                "http://ve.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vec",
                "http://vec.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "veropedia",
                "http://en.veropedia.com/a/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vi",
                "http://vi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vinismo",
                "http://vinismo.com/en/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vkol",
                "http://kol.coldfront.net/thekolwiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vlos",
                "http://www.thuvienkhoahoc.com/tusach/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vls",
                "http://vls.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "vo",
                "http://vo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "voipinfo",
                "http://www.voip-info.org/wiki/view/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wa",
                "http://wa.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "war",
                "http://war.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "warpedview",
                "http://www.warpedview.com/mediawiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "webdevwikinl",
                "http://www.promo-it.nl/WebDevWiki/index.php?page=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "webisodes",
                "http://www.webisodes.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "webseitzwiki",
                "http://webseitz.fluxent.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wg",
                "http://wg.en.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wiki",
                "http://c2.com/cgi/wiki?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikia",
                "http://www.wikia.com/wiki/c:$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikianso",
                "http://www.ansorena.de/mediawiki/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiasite",
                "http://www.wikia.com/wiki/c:$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikible",
                "http://wikible.org/en/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikibooks",
                "http://en.wikibooks.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikichat",
                "http://www.wikichat.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikichristian",
                "http://www.wikichristian.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikicities",
                "http://www.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikicity",
                "http://www.wikia.com/wiki/c:$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikif1",
                "http://www.wikif1.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikifur",
                "http://en.wikifur.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikihow",
                "http://www.wikihow.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiindex",
                "http://wikiindex.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikilemon",
                "http://wiki.illemonati.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikilivres",
                "http://wikilivres.info/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikimac-de",
                "http://apfelwiki.de/wiki/Main/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikimac-fr",
                "http://www.wikimac.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikimedia",
                "http://wikimediafoundation.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikinews",
                "http://en.wikinews.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikinfo",
                "http://www.wikinfo.org/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikinurse",
                "http://wikinurse.org/media/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikinvest",
                "http://www.wikinvest.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikipaltz",
                "http://www.wikipaltz.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikipediawikipedia",
                "http://en.wikipedia.org/wiki/Wikipedia:$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiquote",
                "http://en.wikiquote.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikireason",
                "http://wikireason.net/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikischool",
                "http://www.wikischool.de/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikisophia",
                "http://wikisophia.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikisource",
                "http://en.wikisource.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikispecies",
                "http://species.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikispot",
                "http://wikispot.org/?action=gotowikipage&v=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiti",
                "http://wikiti.denglend.net/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikitravel",
                "http://wikitravel.org/en/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikitree",
                "http://wikitree.org/index.php?title=$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiversity",
                "http://en.wikiversity.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikiwikiweb",
                "http://c2.com/cgi/wiki?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wikt",
                "http://en.wiktionary.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wiktionary",
                "http://en.wiktionary.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wipipedia",
                "http://www.londonfetishscene.com/wipi/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wlug",
                "http://www.wlug.org.nz/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2005",
                "http://wikimania2005.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2006",
                "http://wikimania2006.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2007",
                "http://wikimania2007.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2008",
                "http://wikimania2008.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2009",
                "http://wikimania2009.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wm2010",
                "http://wikimania2010.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wmania",
                "http://wikimania.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wmcz",
                "http://meta.wikimedia.org/wiki/Wikimedia_Czech_Republic/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wmf",
                "http://wikimediafoundation.org/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wmrs",
                "http://rs.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wmse",
                "http://se.wikimedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wo",
                "http://wo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wookieepedia",
                "http://starwars.wikia.com/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "world66",
                "http://www.world66.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wowwiki",
                "http://www.wowwiki.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wqy",
                "http://wqy.sourceforge.net/cgi-bin/index.cgi?$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wurmpedia",
                "http://www.wurmonline.com/wiki/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wuu",
                "http://wuu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "wznan",
                "http://www.wikiznanie.ru/wiki/article/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "xal",
                "http://xal.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "xboxic",
                "http://wiki.xboxic.com/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "xh",
                "http://xh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "yi",
                "http://yi.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "yo",
                "http://yo.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "za",
                "http://za.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zea",
                "http://zea.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh",
                "http://zh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-cfr",
                "http://zh-cfr.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-classical",
                "http://zh-classical.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-cn",
                "http://zh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-min-nan",
                "http://zh-min-nan.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-tw",
                "http://zh.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zh-yue",
                "http://zh-yue.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zrhwiki",
                "http://www.zrhwiki.ch/wiki/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zu",
                "http://zu.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zum",
                "http://wiki.zum.de/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zwiki",
                "http://www.zwiki.org/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "zzz wiki",
                "http://wiki.zzz.ee/index.php/$1",
                false,
                false));

        c.addInterwiki(new InterwikiImpl(
                "\u0108ej",
                "http://esperanto.blahus.cz/cxej/vikio/index.php/$1",
                false,
                false));

    }

    protected void addI18nAlises(WikiConfigImpl c)
    {
        for (Map.Entry<String, TreeSet<String>> stringArrayListEntry : i18nAliases.entrySet()) {
            c.addI18nAlias(new I18nAliasImpl(stringArrayListEntry.getKey(), true, stringArrayListEntry.getValue()));
        }

        for (Map.Entry<String, TreeSet<String>> stringArrayListEntry : i18nCIAliases.entrySet()) {
            c.addI18nAlias(new I18nAliasImpl(stringArrayListEntry.getKey(), false, stringArrayListEntry.getValue()));
        }
    }
}
