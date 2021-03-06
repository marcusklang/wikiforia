/**
 * This file is part of Wikiforia.
 *
 * Wikiforia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Wikiforia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Wikiforia. If not, see <http://www.gnu.org/licenses/>.
 */
 package se.lth.cs.nlp.wikipedia.lang;

//Autogenerated from Wikimedia sources at 2015-04-16T13:55:11+00:00

public class NbConfig extends TemplateConfig {
	public NbConfig() {
		addNamespaceAlias(-2, "Medium");
		addNamespaceAlias(-1, "Spesial");
		addNamespaceAlias(1, "Diskusjon");
		addNamespaceAlias(2, "Bruker");
		addNamespaceAlias(3, "Brukerdiskusjon");
		addNamespaceAlias(5, "Wikipedia-diskusjon");
		addNamespaceAlias(6, "Fil", "Bilde");
		addNamespaceAlias(7, "Fildiskusjon", "Bildediskusjon");
		addNamespaceAlias(8, "MediaWiki");
		addNamespaceAlias(9, "MediaWiki-diskusjon");
		addNamespaceAlias(10, "Mal");
		addNamespaceAlias(11, "Maldiskusjon");
		addNamespaceAlias(12, "Hjelp");
		addNamespaceAlias(13, "Hjelpdiskusjon");
		addNamespaceAlias(14, "Kategori");
		addNamespaceAlias(15, "Kategoridiskusjon");

		addI18nCIAlias("redirect", "#OMDIRIGERING", "#REDIRECT");
		addI18nCIAlias("notoc", "__INGENINNHOLDSFORTEGNELSE__", "__NOTOC__");
		addI18nCIAlias("nogallery", "__INTETGALLERI__", "__NOGALLERY__");
		addI18nCIAlias("forcetoc", "__TVINGINNHOLDSFORTEGNELSE__", "__FORCETOC__");
		addI18nCIAlias("toc", "__INNHOLDSFORTEGNELSE__", "__TOC__");
		addI18nCIAlias("noeditsection", "__INGENSEKSJONSREDIGERING__", "__NOEDITSECTION__");
		addI18nAlias("currentmonth", "NÅVÆRENDEMÅNED", "NÅVÆRENDEMÅNED2", "CURRENTMONTH", "CURRENTMONTH2");
		addI18nAlias("currentmonth1", "NÅVÆRENDEMÅNED1", "CURRENTMONTH1");
		addI18nAlias("currentmonthname", "NÅVÆRENDEMÅNEDSNAVN", "CURRENTMONTHNAME");
		addI18nAlias("currentmonthnamegen", "NÅVÆRENDEMÅNEDSNAVNGEN", "CURRENTMONTHNAMEGEN");
		addI18nAlias("currentmonthabbrev", "NÅVÆRENDEMÅNEDSNAVNKORT", "CURRENTMONTHABBREV");
		addI18nAlias("currentday", "NÅVÆRENDEDAG", "CURRENTDAY");
		addI18nAlias("currentday2", "NÅVÆRENDEDAG2", "CURRENTDAY2");
		addI18nAlias("currentdayname", "NÅVÆRENDEDAGSNAVN", "CURRENTDAYNAME");
		addI18nAlias("currentyear", "NÅVÆRENDEÅR", "CURRENTYEAR");
		addI18nAlias("currenttime", "NÅVÆRENDETID", "CURRENTTIME");
		addI18nAlias("currenthour", "NÅVÆRENDETIME", "CURRENTHOUR");
		addI18nAlias("localmonth", "LOKALMÅNED", "LOKALMÅNED2", "LOCALMONTH", "LOCALMONTH2");
		addI18nAlias("localmonth1", "LOKALMÅNED1", "LOCALMONTH1");
		addI18nAlias("localmonthname", "LOKALMÅNEDSNAVN", "LOCALMONTHNAME");
		addI18nAlias("localmonthnamegen", "LOKALMÅNEDSNAVNGEN", "LOCALMONTHNAMEGEN");
		addI18nAlias("localmonthabbrev", "LOKALMÅNEDSNAVNKORT", "LOCALMONTHABBREV");
		addI18nAlias("localday", "LOKALDAG", "LOCALDAY");
		addI18nAlias("localday2", "LOKALDAG2", "LOCALDAY2");
		addI18nAlias("localdayname", "LOKALDAGSNAVN", "LOCALDAYNAME");
		addI18nAlias("localyear", "LOKALTÅR", "LOCALYEAR");
		addI18nAlias("localtime", "LOKALTID", "LOCALTIME");
		addI18nAlias("localhour", "LOKALTIME", "LOCALHOUR");
		addI18nAlias("numberofpages", "ANTALLSIDER", "NUMBEROFPAGES");
		addI18nAlias("numberofarticles", "ANTALLARTIKLER", "NUMBEROFARTICLES");
		addI18nAlias("numberoffiles", "ANTALLFILER", "NUMBEROFFILES");
		addI18nAlias("numberofusers", "ANTALLBRUKERE", "NUMBEROFUSERS");
		addI18nAlias("numberofactiveusers", "ANTALLAKTIVEBRUKERE", "NUMBEROFACTIVEUSERS");
		addI18nAlias("numberofedits", "ANTALLREDIGERINGER", "NUMBEROFEDITS");
		addI18nAlias("numberofviews", "ANTALLVISNINGER", "NUMBEROFVIEWS");
		addI18nAlias("pagename", "SIDENAVN", "PAGENAME");
		addI18nAlias("pagenamee", "SIDENAVNE", "PAGENAMEE");
		addI18nAlias("namespace", "NAVNEROM", "NAMESPACE");
		addI18nAlias("namespacee", "NAVNEROME", "NAMESPACEE");
		addI18nAlias("talkspace", "DISKUSJONSROM", "TALKSPACE");
		addI18nAlias("talkspacee", "DISKUSJONSROME", "TALKSPACEE");
		addI18nAlias("subjectspace", "SUBJEKTROM", "ARTIKKELROM", "SUBJECTSPACE", "ARTICLESPACE");
		addI18nAlias("subjectspacee", "SUBJEKTROME", "ARTIKKELROME", "SUBJECTSPACEE", "ARTICLESPACEE");
		addI18nAlias("fullpagename", "FULLTSIDENAVN", "FULLPAGENAME");
		addI18nAlias("fullpagenamee", "FULLTSIDENAVNE", "FULLPAGENAMEE");
		addI18nAlias("subpagename", "UNDERSIDENAVN", "SUBPAGENAME");
		addI18nAlias("subpagenamee", "UNDERSIDENAVNE", "SUBPAGENAMEE");
		addI18nAlias("basepagename", "GRUNNSIDENAVN", "BASEPAGENAME");
		addI18nAlias("basepagenamee", "GRUNNSIDENAVNE", "BASEPAGENAMEE");
		addI18nAlias("talkpagename", "DISKUSJONSSIDENAVN", "TALKPAGENAME");
		addI18nAlias("talkpagenamee", "DISKUSJONSSIDENAVNE", "TALKPAGENAMEE");
		addI18nAlias("subjectpagename", "SUBJEKTSIDENAVN", "ARTIKKELSIDENAVN", "SUBJECTPAGENAME", "ARTICLEPAGENAME");
		addI18nAlias("subjectpagenamee", "SUBJEKTSIDENAVNE", "ARTIKKELSIDENAVNE", "SUBJECTPAGENAMEE", "ARTICLEPAGENAMEE");
		addI18nAlias("img_thumbnail", "miniatyr", "mini", "thumbnail", "thumb");
		addI18nAlias("img_manualthumb", "miniatyr=$1", "mini=$1", "thumbnail=$1", "thumb=$1");
		addI18nAlias("img_right", "høyre", "right");
		addI18nAlias("img_left", "venstre", "left");
		addI18nAlias("img_none", "ingen", "none");
		addI18nAlias("img_center", "sentrer", "senter", "midtstilt", "center", "centre");
		addI18nAlias("img_framed", "ramme", "framed", "enframed", "frame");
		addI18nAlias("img_frameless", "rammeløs", "ingenramme", "frameless");
		addI18nAlias("img_page", "side=$1", "side $1", "page=$1", "page $1");
		addI18nAlias("img_upright", "portrett", "portrett=$1", "portrett_$1", "upright", "upright=$1", "upright $1");
		addI18nAlias("img_baseline", "grunnlinje", "baseline");
		addI18nAlias("img_top", "topp", "top");
		addI18nAlias("img_middle", "midt", "middle");
		addI18nAlias("img_bottom", "bunn", "bottom");
		addI18nAlias("img_text_bottom", "tekst-bunn", "text-bottom");
		addI18nAlias("img_link", "lenke=$1", "link=$1");
		addI18nCIAlias("ns", "NR:", "NS:");
		addI18nCIAlias("localurl", "LOKALURL:", "LOCALURL:");
		addI18nCIAlias("localurle", "LOKALURLE:", "LOCALURLE:");
		addI18nCIAlias("articlepath", "ARTIKKELSTI", "ARTICLEPATH");
		addI18nCIAlias("server", "TJENER", "SERVER");
		addI18nCIAlias("servername", "TJENERNAVN", "SERVERNAME");
		addI18nCIAlias("scriptpath", "SKRIPTSTI", "SCRIPTPATH");
		addI18nCIAlias("stylepath", "STILSTI", "STYLEPATH");
		addI18nCIAlias("grammar", "GRAMMATIKK:", "GRAMMAR:");
		addI18nCIAlias("gender", "KJØNN:", "GENDER:");
		addI18nCIAlias("notitleconvert", "__INGENTITTELKONVERTERING__", "__NOTITLECONVERT__", "__NOTC__");
		addI18nCIAlias("nocontentconvert", "__INGENINNHOLDSKONVERTERING__", "__NOCONTENTCONVERT__", "__NOCC__");
		addI18nAlias("currentweek", "NÅVÆRENDEUKE", "CURRENTWEEK");
		addI18nAlias("currentdow", "NÅVÆRENDEUKEDAG", "CURRENTDOW");
		addI18nAlias("localweek", "LOKALUKE", "LOCALWEEK");
		addI18nAlias("localdow", "LOKALUKEDAG", "LOCALDOW");
		addI18nAlias("revisionid", "REVISJONSID", "REVISIONID");
		addI18nAlias("revisionday", "REVISJONSDAG", "REVISIONDAY");
		addI18nAlias("revisionday2", "REVISJONSDAG2", "REVISIONDAY2");
		addI18nAlias("revisionmonth", "REVISJONSMÅNED", "REVISIONMONTH");
		addI18nAlias("revisionmonth1", "REVISJONSMÅNED1", "REVISIONMONTH1");
		addI18nAlias("revisionyear", "REVISJONSÅR", "REVISIONYEAR");
		addI18nAlias("revisiontimestamp", "REVISJONSTIDSSTEMPEL", "REVISIONTIMESTAMP");
		addI18nAlias("revisionuser", "REVISJONSBRUKER", "REVISIONUSER");
		addI18nCIAlias("plural", "FLERTALL:", "PLURAL:");
		addI18nCIAlias("raw", "RÅ:", "RAW:");
		addI18nAlias("displaytitle", "VISTITTEL", "DISPLAYTITLE");
		addI18nAlias("newsectionlink", "__NYSEKSJONSLENKE__", "__NEWSECTIONLINK__");
		addI18nAlias("nonewsectionlink", "__INGENNYSEKSJONSLENKE__", "__NONEWSECTIONLINK__");
		addI18nAlias("currentversion", "NÅVÆRENDEVERSJON", "CURRENTVERSION");
		addI18nAlias("currenttimestamp", "NÅVÆRENDETIDSSTEMPEL", "CURRENTTIMESTAMP");
		addI18nAlias("localtimestamp", "LOKALTTIDSSTEMPEL", "LOCALTIMESTAMP");
		addI18nAlias("contentlanguage", "INNHOLDSSPRÅK", "CONTENTLANGUAGE", "CONTENTLANG");
		addI18nAlias("pagesinnamespace", "SIDERINAVNEROM:", "PAGESINNAMESPACE:", "PAGESINNS:");
		addI18nAlias("numberofadmins", "ANTALLADMINISTRATORER", "NUMBEROFADMINS");
		addI18nCIAlias("special", "spesial", "special");
		addI18nAlias("defaultsort", "STANDARDSORTERING", "DEFAULTSORT:", "DEFAULTSORTKEY:", "DEFAULTCATEGORYSORT:");
		addI18nCIAlias("filepath", "FILSTI:", "FILEPATH:");
		addI18nAlias("hiddencat", "__SKJULTKATEGORI__", "__HIDDENCAT__");
		addI18nAlias("pagesincategory", "SIDERIKATEGORI", "PAGESINCATEGORY", "PAGESINCAT");
		addI18nAlias("pagesize", "SIDESTØRRELSE", "PAGESIZE");
		addI18nAlias("index", "__INDEKSER__", "__INDEX__");
		addI18nAlias("noindex", "__INGENINDEKSERING__", "__NOINDEX__");
		addI18nAlias("numberingroup", "NUMMERIGRUPPE", "NUMBERINGROUP", "NUMINGROUP");
		addI18nAlias("staticredirect", "__STATISTOMDIRIGERING__", "__STATICREDIRECT__");
		addI18nAlias("protectionlevel", "BESKYTTELSESNIVÅ", "PROTECTIONLEVEL");
		addI18nCIAlias("formatdate", "datoformat", "formatdate", "dateformat");
		addI18nCIAlias("url_path", "STI", "PATH");
		addI18nCIAlias("url_query", "SPØRRING", "QUERY");
	}

	@Override
	protected String getSiteName() {
		return "Wikipedia";
	}

	@Override
	protected String getWikiUrl() {
		return "http://nb.wikipedia.org/";
	}

	@Override
	public String getIso639() {
		return "nb";
	}
}
