package se.lth.cs.nlp.mediawiki;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.sweble.wikitext.engine.EngineException;
import org.sweble.wikitext.parser.parser.LinkTargetException;
import se.lth.cs.nlp.mediawiki.model.Header;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.Siteinfo;
import se.lth.cs.nlp.mediawiki.parser.SinglestreamXmlDumpParser;
import se.lth.cs.nlp.pipeline.PipelineBuilder;
import se.lth.cs.nlp.pipeline.Sink;
import se.lth.cs.nlp.wikipedia.lang.SwedishConfig;
import se.lth.cs.nlp.wikipedia.parser.SwebleWikimarkupToText;
import se.lth.cs.nlp.wikipedia.parser.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Unit tests
 */
public class AppTest extends TestCase
{

    public AppTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public String readArticle(String name){
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("testdata/" + name + ".wiki")));
            sb = new StringBuilder();
            String line;
            while ((line = reader.readLine() ) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        return sb.toString();
    }

    public Page readAlfredArticle() {
        Page page = new Page(null, 1L, "Alfred Nobel", readArticle("alfred_nobel"), 123L, 0, "text/x-wiki");
        return page;
    }

    public Page readHelsingborgArticle() {
        Page page = new Page(null, 1L, "Helsingborg", readArticle("helsingborg"), 123L, 0, "text/x-wiki");
        return page;
    }

    public void testAlfredArticle() {
        SwebleWikimarkupToText swebleWikimarkupToText = new SwebleWikimarkupToText(new SwedishConfig());

        String text = swebleWikimarkupToText.parse(readAlfredArticle()).getText();
        System.out.println(text);
    }

    public void testStructuredAlfred() {
        AnnotationParser<Void,Void> parser = new AnnotationParser<Void,Void>() {

            @Override
            public void startDocument(AnnotationContext<Void> context) {

            }

            @Override
            public Void endDocument(AnnotationContext<Void> context) {
                System.out.println("Final rendered text: ");
                System.out.println(context.getText());
                return null;
            }

            @Override
            public void category(AnnotationContext<Void> context, String title, String category, int start) {
                System.out.println("Found category: " + category);
            }

            @Override
            public void template(AnnotationContext<Void> context, Template template, int start) {
                System.out.println("Found template: " + template.path);
                for (TemplateArgument templateArgument : template.parse()) {
                    System.out.println("Found template argument for " + template.path + " at " + templateArgument.getPath() + " with argument " + templateArgument.getArgument());
                    try {
                        templateArgument.parse(context.getModel(),this);
                    } catch (EngineException e) {
                        e.printStackTrace();
                    } catch (LinkTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void table(AnnotationContext<Void> context, Table table, int start) {

            }

            @Override
            public void tableCellStart(AnnotationContext<Void> context, int row, int col, int start) {

            }

            @Override
            public void tableCellEnd(AnnotationContext<Void> context, int row, int col, int start, int end) {

            }

            @Override
            public void startTemplateArgument(AnnotationContext<Void> context, int start) {

            }

            @Override
            public void endTemplateArgument(AnnotationContext<Void> context, int start, int end) {
                System.out.println("Template argument parsed: " + context.getText());
            }

            @Override
            public void anchor(AnnotationContext<Void> context, String target, String fragmentIdentifier, boolean internal, int start, int end) {
                System.out.println("Found anchor with target " + target + " at (" + start + ", " + end + ")");
            }

            @Override
            public void header(AnnotationContext<Void> context, int level, String title, Collection<String> headerPath, int start) {
                System.out.println("Found header with title " + title + " at level " + level);
            }

            @Override
            public void textAbstract(AnnotationContext<Void> context, int start, int end) {
                System.out.println("Found abstract at " + start + ", " + end);
            }

            @Override
            public void paragraph(AnnotationContext<Void> context, String currentHeading, int start, int end) {
                System.out.println("Found paragraph with heading: " + currentHeading + " at (" + start + ", " + end + ")" );
            }

            @Override
            public void startList(AnnotationContext<Void> context, int id, int start, boolean ordered) {
                System.out.println("List ordered = " + ordered + " + starts at " + start);
            }

            @Override
            public void startListItem(AnnotationContext<Void> context, int listId, int itemId, int start) {
                System.out.println("List Item Start "  + start);
            }

            @Override
            public void endListItem(AnnotationContext<Void> context, int listId, int itemId, int start, int end) {
                System.out.println("List Item End "  + start + "," + end);
            }

            @Override
            public void endList(AnnotationContext<Void> context, int id, int start, int end, boolean ordered) {
                System.out.println("List ordered = " + ordered + " ends. List range " + start + ", " + end);
            }
        };

        SwebleAnnotationParser<Void,Void> annotationParser = new SwebleAnnotationParser<Void,Void>(new SwedishConfig(), parser);

        Page article = readHelsingborgArticle();
        annotationParser.parse(article);
    }

    public void testPipeline() {
        final ArrayList<Page> pages = new ArrayList<Page>();
        PipelineBuilder.input(new SinglestreamXmlDumpParser(new File("testdata/svminidump.xml")))
                       .pipe(new Sink<Page>() {
                           @Override
                           public void process(List<Page> batch) {
                               pages.addAll(batch);
                           }
                       })
                       .run();

        assertEquals(1, pages.size());
        Page page = pages.get(0);

        assertEquals(1, page.getId());
        assertEquals("Amager", page.getTitle());
        assertEquals("text/x-wiki", page.getFormat());
        assertEquals(0L, page.getNamespace());
        Header header = page.getHeader();

        assertEquals("0.9", header.getVersion());
        assertEquals("sv", header.getLang());

        Siteinfo siteinfo = header.getSiteinfo();
        assertEquals("Wikipedia", siteinfo.getSitename());
        assertEquals("svwiki", siteinfo.getDbname());
        assertEquals("http://sv.wikipedia.org/wiki/Portal:Huvudsida", siteinfo.getBase());

        assertEquals("Media", siteinfo.getNamespaces().get(-2));
        assertEquals("Special", siteinfo.getNamespaces().get(-1));
        assertEquals("", siteinfo.getNamespaces().get(0));
        assertEquals("Diskussion", siteinfo.getNamespaces().get(1));
        assertEquals("Användare", siteinfo.getNamespaces().get(2));
        assertEquals("Användardiskussion", siteinfo.getNamespaces().get(3));
        assertEquals("Wikipedia", siteinfo.getNamespaces().get(4));
        assertEquals("Wikipediadiskussion", siteinfo.getNamespaces().get(5));
        assertEquals("Fil", siteinfo.getNamespaces().get(6));
        assertEquals("Fildiskussion", siteinfo.getNamespaces().get(7));
        assertEquals("MediaWiki", siteinfo.getNamespaces().get(8));
        assertEquals("MediaWiki-diskussion", siteinfo.getNamespaces().get(9));
        assertEquals("Mall", siteinfo.getNamespaces().get(10));
        assertEquals("Malldiskussion", siteinfo.getNamespaces().get(11));
        assertEquals("Hjälp", siteinfo.getNamespaces().get(12));
        assertEquals("Hjälpdiskussion", siteinfo.getNamespaces().get(13));
        assertEquals("Kategori", siteinfo.getNamespaces().get(14));
        assertEquals("Kategoridiskussion", siteinfo.getNamespaces().get(15));
        assertEquals("Portal", siteinfo.getNamespaces().get(100));
        assertEquals("Portaldiskussion", siteinfo.getNamespaces().get(101));
        assertEquals("Utbildningsprogram", siteinfo.getNamespaces().get(446));
        assertEquals("Utbildningsprogramsdiskussion", siteinfo.getNamespaces().get(447));
        assertEquals("Modul", siteinfo.getNamespaces().get(828));
        assertEquals("Moduldiskussion", siteinfo.getNamespaces().get(829));
    }
}
