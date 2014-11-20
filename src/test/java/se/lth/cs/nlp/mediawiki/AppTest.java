package se.lth.cs.nlp.mediawiki;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import se.lth.cs.nlp.mediawiki.model.Header;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.Siteinfo;
import se.lth.cs.nlp.mediawiki.parser.SinglestreamXmlDumpParser;
import se.lth.cs.nlp.mediawiki.parser.XmlDumpParser;
import se.lth.cs.nlp.pipeline.PipelineBuilder;
import se.lth.cs.nlp.pipeline.Sink;
import se.lth.cs.nlp.wikipedia.lang.SwedishConfig;
import se.lth.cs.nlp.wikipedia.parser.SwebleWikimarkupToText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests
 */
public class AppTest 
    extends TestCase
{

    public AppTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testAlfredArticle() {
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("testdata/alfred_nobel.wiki")));
            sb = new StringBuilder();
            String line;
            while ((line = reader.readLine() ) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        SwebleWikimarkupToText swebleWikimarkupToText = new SwebleWikimarkupToText(new SwedishConfig());

        Page page = new Page(null, 1L, "Alfred Nobel", sb.toString(), 123L, 0, "text/x-wiki");
        String text = swebleWikimarkupToText.parse(page).getText();
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
