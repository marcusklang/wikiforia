package se.lth.cs.nlp.wikipedia.lang;

/**
 * Created by marcus on 2015-03-23.
 */
public class FrenchConfig extends TemplateConfig {

    public FrenchConfig() {
        addNamespaceAlias(-2, "Média");
        addNamespaceAlias(-1, "Spécial");
        addNamespaceAlias(1, "Discussion", "Discuter");
        addNamespaceAlias(2, "Utilisateur", "Utilisateur", "Utilisatrice");
        addNamespaceAlias(3, "Discussion_utilisateur", "Discussion_Utilisateur", "Discussion_utilisateur", "Discussion_utilisatrice");
        addNamespaceAlias(4, "Wikipedia");
        addNamespaceAlias(5, "Discussion_Wikipedia");
        addNamespaceAlias(6, "Fichier");
        addNamespaceAlias(7, "Discussion_fichier", "Discussion_Fichier", "Discussion_Image");
        addNamespaceAlias(8, "MediaWiki");
        addNamespaceAlias(9, "Discussion_MediaWiki");
        addNamespaceAlias(10, "Modèle");
        addNamespaceAlias(11, "Discussion_modèle", "Discussion_Modèle");
        addNamespaceAlias(12, "Aide");
        addNamespaceAlias(13, "Discussion_aide", "Discussion_Aide");
        addNamespaceAlias(14, "Catégorie");
        addNamespaceAlias(15, "Discussion_catégorie", "Discussion_Catégorie");

        addI18nCIAlias("redirect", "#REDIRECTION", "#REDIRECT");
        addI18nCIAlias("notoc", "__AUCUNSOMMAIRE__", "__AUCUNETDM__", "__NOTOC__");
        addI18nCIAlias("nogallery", "__AUCUNEGALERIE__", "__NOGALLERY__");
        addI18nCIAlias("forcetoc", "__FORCERSOMMAIRE__", "__FORCERTDM__", "__FORCETOC__");
        addI18nCIAlias("toc", "__SOMMAIRE__", "__TDM__", "__TOC__");
        addI18nCIAlias("noeditsection", "__SECTIONNONEDITABLE__", "__NOEDITSECTION__");
        addI18nAlias("currentmonth", "MOISACTUEL", "MOIS2ACTUEL", "CURRENTMONTH", "CURRENTMONTH2");
        addI18nAlias("currentmonth1", "MOIS1ACTUEL", "CURRENTMONTH1");
        addI18nAlias("currentmonthname", "NOMMOISACTUEL", "CURRENTMONTHNAME");
        addI18nAlias("currentmonthnamegen", "NOMGENMOISACTUEL", "CURRENTMONTHNAMEGEN");
        addI18nAlias("currentmonthabbrev", "ABREVMOISACTUEL", "CURRENTMONTHABBREV");
        addI18nAlias("currentday", "JOURACTUEL", "JOUR1ACTUEL", "CURRENTDAY");
        addI18nAlias("currentday2", "JOUR2ACTUEL", "CURRENTDAY2");
        addI18nAlias("currentdayname", "NOMJOURACTUEL", "CURRENTDAYNAME");
        addI18nAlias("currentyear", "ANNEEACTUELLE", "CURRENTYEAR");
        addI18nAlias("currenttime", "HORAIREACTUEL", "CURRENTTIME");
        addI18nAlias("currenthour", "HEUREACTUELLE", "CURRENTHOUR");
        addI18nAlias("localmonth", "MOISLOCAL", "MOIS2LOCAL", "LOCALMONTH", "LOCALMONTH2");
        addI18nAlias("localmonth1", "MOIS1LOCAL", "LOCALMONTH1");
        addI18nAlias("localmonthname", "NOMMOISLOCAL", "LOCALMONTHNAME");
        addI18nAlias("localmonthnamegen", "NOMGENMOISLOCAL", "LOCALMONTHNAMEGEN");
        addI18nAlias("localmonthabbrev", "ABREVMOISLOCAL", "LOCALMONTHABBREV");
        addI18nAlias("localday", "JOURLOCAL", "JOUR1LOCAL", "LOCALDAY");
        addI18nAlias("localday2", "JOUR2LOCAL", "LOCALDAY2");
        addI18nAlias("localdayname", "NOMJOURLOCAL", "LOCALDAYNAME");
        addI18nAlias("localyear", "ANNEELOCALE", "LOCALYEAR");
        addI18nAlias("localtime", "HORAIRELOCAL", "LOCALTIME");
        addI18nAlias("localhour", "HEURELOCALE", "LOCALHOUR");
        addI18nAlias("numberofpages", "NOMBREPAGES", "NUMBEROFPAGES");
        addI18nAlias("numberofarticles", "NOMBREARTICLES", "NUMBEROFARTICLES");
        addI18nAlias("numberoffiles", "NOMBREFICHIERS", "NUMBEROFFILES");
        addI18nAlias("numberofusers", "NOMBREUTILISATEURS", "NUMBEROFUSERS");
        addI18nAlias("numberofactiveusers", "NOMBREUTILISATEURSACTIFS", "NUMBEROFACTIVEUSERS");
        addI18nAlias("numberofedits", "NOMBREMODIFS", "NUMBEROFEDITS");
        addI18nAlias("numberofviews", "NOMBREVUES", "NUMBEROFVIEWS");
        addI18nAlias("pagename", "NOMPAGE", "PAGENAME");
        addI18nAlias("pagenamee", "NOMPAGEX", "PAGENAMEE");
        addI18nAlias("namespace", "ESPACENOMMAGE", "NAMESPACE");
        addI18nAlias("namespacee", "ESPACENOMMAGEX", "NAMESPACEE");
        addI18nAlias("namespacenumber", "NOMBREESPACENOMMAGE", "NAMESPACENUMBER");
        addI18nAlias("talkspace", "ESPACEDISCUSSION", "TALKSPACE");
        addI18nAlias("talkspacee", "ESPACEDISCUSSIONX", "TALKSPACEE");
        addI18nAlias("subjectspace", "ESPACESUJET", "ESPACEARTICLE", "SUBJECTSPACE", "ARTICLESPACE");
        addI18nAlias("subjectspacee", "ESPACESUJETX", "ESPACEARTICLEX", "SUBJECTSPACEE", "ARTICLESPACEE");
        addI18nAlias("fullpagename", "NOMPAGECOMPLET", "FULLPAGENAME");
        addI18nAlias("fullpagenamee", "NOMPAGECOMPLETX", "FULLPAGENAMEE");
        addI18nAlias("subpagename", "NOMSOUSPAGE", "SUBPAGENAME");
        addI18nAlias("subpagenamee", "NOMSOUSPAGEX", "SUBPAGENAMEE");
        addI18nAlias("rootpagename", "NOMPAGERACINE", "ROOTPAGENAME");
        addI18nAlias("rootpagenamee", "NOMPAGERACINEX", "ROOTPAGENAMEE");
        addI18nAlias("basepagename", "NOMBASEDEPAGE", "BASEPAGENAME");
        addI18nAlias("basepagenamee", "NOMBASEDEPAGEX", "BASEPAGENAMEE");
        addI18nAlias("talkpagename", "NOMPAGEDISCUSSION", "TALKPAGENAME");
        addI18nAlias("talkpagenamee", "NOMPAGEDISCUSSIONX", "TALKPAGENAMEE");
        addI18nAlias("subjectpagename", "NOMPAGESUJET", "NOMPAGEARTICLE", "SUBJECTPAGENAME", "ARTICLEPAGENAME");
        addI18nAlias("subjectpagenamee", "NOMPAGESUJETX", "NOMPAGEARTICLEX", "SUBJECTPAGENAMEE", "ARTICLEPAGENAMEE");
        addI18nAlias("img_thumbnail", "vignette", "thumbnail", "thumb");
        addI18nAlias("img_manualthumb", "vignette=$1", "thumbnail=$1", "thumb=$1");
        addI18nAlias("img_right", "droite", "right");
        addI18nAlias("img_left", "gauche", "left");
        addI18nAlias("img_none", "néant", "neant", "none");
        addI18nAlias("img_center", "centré", "center", "centre");
        addI18nAlias("img_framed", "cadre", "encadré", "encadre", "framed", "enframed", "frame");
        addI18nAlias("img_frameless", "sans_cadre", "non_encadré", "non_encadre", "frameless");
        addI18nAlias("img_lang", "langue=$1", "lang=$1");
        addI18nAlias("img_upright", "redresse", "redresse=$1", "redresse_$1", "upright", "upright=$1", "upright $1");
        addI18nAlias("img_border", "bordure", "border");
        addI18nAlias("img_baseline", "ligne-de-base", "base", "baseline");
        addI18nAlias("img_sub", "indice", "ind", "sub");
        addI18nAlias("img_super", "exposant", "exp", "super", "sup");
        addI18nAlias("img_top", "haut", "top");
        addI18nAlias("img_text_top", "haut-texte", "haut-txt", "text-top");
        addI18nAlias("img_middle", "milieu", "middle");
        addI18nAlias("img_bottom", "bas", "bottom");
        addI18nAlias("img_text_bottom", "bas-texte", "bas-txt", "text-bottom");
        addI18nAlias("img_link", "lien=$1", "link=$1");
        addI18nAlias("img_class", "classe=$1", "class=$1");
        addI18nAlias("sitename", "NOMSITE", "SITENAME");
        addI18nCIAlias("ns", "ESPACEN:", "NS:");
        addI18nCIAlias("nse", "ESPACENX:", "NSE:");
        addI18nCIAlias("localurl", "URLLOCALE:", "LOCALURL:");
        addI18nCIAlias("localurle", "URLLOCALEX:", "LOCALURLE:");
        addI18nCIAlias("articlepath", "CHEMINARTICLE", "ARTICLEPATH");
        addI18nCIAlias("pageid", "IDPAGE", "PAGEID");
        addI18nCIAlias("server", "SERVEUR", "SERVER");
        addI18nCIAlias("servername", "NOMSERVEUR", "SERVERNAME");
        addI18nCIAlias("scriptpath", "CHEMINSCRIPT", "SCRIPTPATH");
        addI18nCIAlias("stylepath", "CHEMINSTYLE", "STYLEPATH");
        addI18nCIAlias("grammar", "GRAMMAIRE:", "GRAMMAR:");
        addI18nCIAlias("gender", "GENRE:", "GENDER:");
        addI18nCIAlias("notitleconvert", "__SANSCONVERSIONTITRE__", "__SANSCT__", "__NOTITLECONVERT__", "__NOTC__");
        addI18nCIAlias("nocontentconvert", "__SANSCONVERSIONCONTENU__", "__SANSCC__", "__NOCONTENTCONVERT__", "__NOCC__");
        addI18nAlias("currentweek", "SEMAINEACTUELLE", "CURRENTWEEK");
        addI18nAlias("currentdow", "JDSACTUEL", "CURRENTDOW");
        addI18nAlias("localweek", "SEMAINELOCALE", "LOCALWEEK");
        addI18nAlias("localdow", "JDSLOCAL", "LOCALDOW");
        addI18nAlias("revisionid", "IDVERSION", "REVISIONID");
        addI18nAlias("revisionday", "JOURVERSION", "JOUR1VERSION", "REVISIONDAY");
        addI18nAlias("revisionday2", "JOUR2VERSION", "REVISIONDAY2");
        addI18nAlias("revisionmonth", "MOISVERSION", "REVISIONMONTH");
        addI18nAlias("revisionmonth1", "MOISVERSION1", "REVISIONMONTH1");
        addI18nAlias("revisionyear", "ANNEEVERSION", "REVISIONYEAR");
        addI18nAlias("revisiontimestamp", "INSTANTVERSION", "REVISIONTIMESTAMP");
        addI18nAlias("revisionuser", "UTILISATEURVERSION", "REVISIONUSER");
        addI18nCIAlias("plural", "PLURIEL:", "PLURAL:");
        addI18nCIAlias("fullurl", "URLCOMPLETE:", "FULLURL:");
        addI18nCIAlias("fullurle", "URLCOMPLETEX:", "FULLURLE:");
        addI18nCIAlias("canonicalurl", "URLCANONIQUE:", "CANONICALURL:");
        addI18nCIAlias("canonicalurle", "URLCANONIQUEX:", "CANONICALURLE:");
        addI18nCIAlias("lcfirst", "INITMINUS:", "LCFIRST:");
        addI18nCIAlias("ucfirst", "INITMAJUS:", "INITCAPIT:", "UCFIRST:");
        addI18nCIAlias("lc", "MINUS:", "LC:");
        addI18nCIAlias("uc", "MAJUS:", "CAPIT:", "UC:");
        addI18nCIAlias("raw", "BRUT:", "RAW:");
        addI18nAlias("displaytitle", "AFFICHERTITRE", "DISPLAYTITLE");
        addI18nAlias("rawsuffix", "BRUT", "B", "R");
        addI18nCIAlias("nocommafysuffix", "SANSSEP", "NOSEP");
        addI18nAlias("newsectionlink", "__LIENNOUVELLESECTION__", "__NEWSECTIONLINK__");
        addI18nAlias("nonewsectionlink", "__AUCUNLIENNOUVELLESECTION__", "__NONEWSECTIONLINK__");
        addI18nAlias("currentversion", "VERSIONACTUELLE", "CURRENTVERSION");
        addI18nCIAlias("urlencode", "ENCODEURL:", "URLENCODE:");
        addI18nCIAlias("anchorencode", "ENCODEANCRE", "ANCHORENCODE");
        addI18nAlias("currenttimestamp", "INSTANTACTUEL", "CURRENTTIMESTAMP");
        addI18nAlias("localtimestamp", "INSTANTLOCAL", "LOCALTIMESTAMP");
        addI18nAlias("directionmark", "MARQUEDIRECTION", "MARQUEDIR", "DIRECTIONMARK", "DIRMARK");
        addI18nCIAlias("language", "#LANGUE:", "#LANGUAGE:");
        addI18nAlias("contentlanguage", "LANGUECONTENU", "LANGCONTENU", "CONTENTLANGUAGE", "CONTENTLANG");
        addI18nAlias("pagesinnamespace", "PAGESDANSESPACE:", "PAGESINNAMESPACE:", "PAGESINNS:");
        addI18nAlias("numberofadmins", "NOMBREADMINS", "NUMBEROFADMINS");
        addI18nCIAlias("formatnum", "FORMATNOMBRE", "FORMATNUM");
        addI18nCIAlias("padleft", "BOURRAGEGAUCHE", "BOURREGAUCHE", "PADLEFT");
        addI18nCIAlias("padright", "BOURRAGEDROITE", "BOURREDROITE", "PADRIGHT");
        addI18nCIAlias("special", "spécial", "special");
        addI18nCIAlias("speciale", "spéciale", "speciale");
        addI18nAlias("defaultsort", "CLEFDETRI:", "CLEDETRI:", "DEFAULTSORT:", "DEFAULTSORTKEY:", "DEFAULTCATEGORYSORT:");
        addI18nCIAlias("filepath", "CHEMIN:", "FILEPATH:");
        addI18nCIAlias("tag", "balise", "tag");
        addI18nAlias("hiddencat", "__CATCACHEE__", "__HIDDENCAT__");
        addI18nAlias("pagesincategory", "PAGESDANSCAT", "PAGESINCATEGORY", "PAGESINCAT");
        addI18nAlias("pagesize", "TAILLEPAGE", "PAGESIZE");
        addI18nAlias("noindex", "__AUCUNINDEX__", "__NOINDEX__");
        addI18nAlias("numberingroup", "NOMBREDANSGROUPE", "NBDANSGROUPE", "NUMBERINGROUP", "NUMINGROUP");
        addI18nAlias("staticredirect", "__REDIRECTIONSTATIQUE__", "__STATICREDIRECT__");
        addI18nAlias("protectionlevel", "NIVEAUDEPROTECTION", "PROTECTIONLEVEL");
        addI18nCIAlias("url_path", "CHEMIN", "PATH");
        addI18nCIAlias("url_query", "QUESTION", "QUERY");
        addI18nCIAlias("defaultsort_noerror", "sanserreur", "noerror");
        addI18nCIAlias("defaultsort_noreplace", "sansremplacer", "noreplace");
        addI18nCIAlias("pagesincategory_all", "tous", "all");
        addI18nCIAlias("pagesincategory_subcats", "souscats", "subcats");
        addI18nCIAlias("pagesincategory_files", "fichiers", "files");
    }

    @Override
    protected String getSiteName() {
        return "Wikipedia";
    }

    @Override
    protected String getWikiUrl() {
        return "http://fr.wikipedia.org/";
    }

    @Override
    public String getIso639() {
        return "fr";
    }
}
