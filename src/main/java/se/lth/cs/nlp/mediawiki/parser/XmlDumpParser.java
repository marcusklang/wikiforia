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
package se.lth.cs.nlp.mediawiki.parser;

import com.ctc.wstx.api.WstxInputProperties;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import se.lth.cs.nlp.mediawiki.model.Header;
import se.lth.cs.nlp.mediawiki.model.Page;
import se.lth.cs.nlp.mediawiki.model.Siteinfo;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.TreeMap;

/**
 * The XML parser for a MediaWiki XML dump.
 */
public class XmlDumpParser
{
    private final Header header;
    private final XMLStreamReader2 xmlReader;
    private final ParserContext state = new ParserContext();

    /**
     * Constructor used by Multistream parser
     * @param header   parsed header
     * @param xmlInput parallel input stream
     */
    public XmlDumpParser(Header header, InputStream xmlInput) {
        try {
            this.header = header;

            XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory2.newInstance();
            factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
            factory.setProperty(WstxInputProperties.P_INPUT_PARSING_MODE, WstxInputProperties.PARSING_MODE_FRAGMENT);

            xmlReader = (XMLStreamReader2)factory.createXMLStreamReader(xmlInput);

        } catch (XMLStreamException e) {
            throw new IOError(e);
        }
    }

    /**
     * Parse a MediaWiki header
     * @param xmlReader stream
     * @return header
     */
    public static Header readHeader(XMLStreamReader2 xmlReader) {
        try {
            String lang = null;
            String version = null;
            String sitename = null;
            String dbname = null;
            String base = null;
            String generator = null;
            TreeMap<Integer,String> namespaces = new TreeMap<Integer,String>();

            int type;

            while (xmlReader.hasNext()) {
                type = xmlReader.next();

                if (type == XMLEvent.START_ELEMENT)
                {
                    String localname = xmlReader.getLocalName();
                    if(localname.equals("mediawiki")) {

                        int attributeCount = xmlReader.getAttributeCount();

                        for (int i = 0; i < attributeCount; i++)
                        {
                            String attr = xmlReader.getAttributeLocalName(i);
                            if(attr.equals("lang")) {
                                lang = xmlReader.getAttributeValue(i);
                            }
                            else if(attr.equals("version")) {
                                version = xmlReader.getAttributeValue(i);
                            }
                        }

                        if(lang == null || version == null)
                        {
                            throw new IOError(new IllegalArgumentException("Did not find language and version in supplied header!"));
                        }
                    }
                    else if(localname.equals("siteinfo")) {
                        while(xmlReader.hasNext()) {
                            type = xmlReader.next();
                            if(type == XMLEvent.START_ELEMENT) {
                                localname = xmlReader.getLocalName();
                                if(localname.equals("sitename"))
                                    sitename = xmlReader.getElementText();
                                else if(localname.equals("dbname"))
                                    dbname = xmlReader.getElementText();
                                else if(localname.equals("base"))
                                    base = xmlReader.getElementText();
                                else if(localname.equals("generator"))
                                    generator = xmlReader.getElementText();
                                else if(localname.equals("namespaces")) {
                                    while(xmlReader.hasNext()) {
                                        type = xmlReader.nextTag();
                                        if(type == XMLEvent.START_ELEMENT && xmlReader.getLocalName().equals("namespace")) {
                                            int ns_key = Integer.parseInt(xmlReader.getAttributeValue(null, "key"));
                                            String ns_name = xmlReader.getElementText();
                                            namespaces.put(ns_key, ns_name);
                                        }
                                        else if(type == XMLEvent.END_ELEMENT && xmlReader.getLocalName().equals("namespaces")) {
                                            break;
                                        }
                                    }
                                }
                            }
                            else if(type == XMLEvent.END_ELEMENT && xmlReader.getLocalName().equals("siteinfo")) {
                                break;
                            }
                        }
                        break;
                    }
                    else if(localname.equals("page")) {
                        throw new RuntimeException("BUG: Incorrect behaviour, got unexpected page element.");
                    }
                }
            }

            if(lang == null)
                throw new IOError(new IllegalArgumentException("Did not find language and version in the header."));
            else
                return new Header(lang, version, new Siteinfo(sitename, dbname, base, generator, namespaces));
        } catch (XMLStreamException e) {
            throw new IOError(e);
        }
    }

    /**
     * Standalone constructor
     * @param xmlInput the stream to read from
     */
    public XmlDumpParser(InputStream xmlInput) {
        try {
            XMLInputFactory2 factory = (XMLInputFactory2) XMLInputFactory2.newInstance();
            factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
            factory.setProperty(WstxInputProperties.P_INPUT_PARSING_MODE, WstxInputProperties.PARSING_MODE_FRAGMENT);

            xmlReader = (XMLStreamReader2) factory.createXMLStreamReader(xmlInput);
            this.header = readHeader(xmlReader);
        } catch (XMLStreamException e) {
            throw new IOError(e);
        }
    }

    /**
     * Internal class for parser variables and state variables
     */
    private static class ParserContext
    {
        //Parse state
        public int type = 0;

        //Page state variables
        public int pageExtractMode = 0;
        public int pageId = 0;
        public String pageTitle = "";
        public String pageText = "";
        public int pageNamespace = 0;
        public long pageTimestamp = 0;
        public String pageModel = "";
        public String pageFormat = "";
        public long wikiPageId = -1;
        public ArrayDeque<Page> pagesRead = new ArrayDeque<Page>();

        public void resetPage()
        {
            pageExtractMode = 0;
            pageTitle = "";
            pageText = "";
            pageNamespace = 0;
            pageTimestamp = 0;
            wikiPageId = -1;
            pageModel = "";
            pageFormat = "";
        }

        public boolean isStartElement() {
            return type == XMLEvent.START_ELEMENT;
        }

        public boolean isEndElement() {
            return type == XMLEvent.END_ELEMENT;
        }
    }

    /**
     * The page parsing code
     * @param state  the state variables
     * @param reader the StAX reader
     * @return true if match
     * @throws javax.xml.stream.XMLStreamException
     */
    private boolean processPages(ParserContext state, XMLStreamReader reader) throws XMLStreamException {
        switch (state.pageExtractMode) {
            case 0: //looking for a page
                if (state.isStartElement()) {
                    if (reader.getLocalName().equals("page"))
                    {
                        state.pageId++;
                        state.pageExtractMode = 1;
                    }
                    return true;
                }
                break;
            case 1: //found page content, extract page centric info
                if (state.isStartElement())
                {
                    String localName = reader.getLocalName();
                    if(localName.equals("title"))
                    {
                        state.pageTitle = reader.getElementText();
                    } else if(localName.equals("ns")) {
                        state.pageNamespace = Integer.parseInt(reader.getElementText());
                    } else if(localName.equals("id")) {
                        try
                        {
                            state.wikiPageId = Long.parseLong(reader.getElementText());
                        } catch(NumberFormatException nfe) {
                            nfe.printStackTrace();
                        }
                    } else if(localName.equals("revision")) {
                        state.pageExtractMode = 2;
                        break;
                    }
                    return true;
                }
                else if(state.isEndElement())
                {
                    if (reader.getLocalName().equals("page")) {
                        state.pagesRead.add(getPage(state));
                    }
                    return true;
                }
                break;
            case 2: //revision content found, read that
                if (state.isStartElement())
                {
                    String localName = reader.getLocalName();
                    if(localName.equals("timestamp")) {
                        try {
                            String timestamp = reader.getElementText();
                            Calendar c = javax.xml.bind.DatatypeConverter.parseDate(timestamp);
                            state.pageTimestamp = c.getTimeInMillis();
                        }
                        catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    } else if(localName.equals("text")) {
                        state.pageText = reader.getElementText();
                    } else if(localName.equals("model")) {
                        state.pageModel = reader.getElementText();
                    } else if(localName.equals("format")) {
                        state.pageFormat = reader.getElementText();
                    }

                    return true;
                }
                else if(state.isEndElement())
                {
                    if (reader.getLocalName().equals("page"))
                    {
                        state.pagesRead.add(getPage(state));
                    }
                    else if(reader.getLocalName().equals("revision")) {
                        state.pageExtractMode = 1;
                    }
                    return true;
                }
                break;
        }

        return false;
    }


    private Page getPage(ParserContext state)
    {
        Page page = new Page(
                header,
                state.wikiPageId,
                state.pageTitle,
                state.pageText,
                state.pageTimestamp,
                state.pageNamespace,
                state.pageFormat
        );

        state.resetPage();
        return page;
    }

    public Page next()
    {
        if(!state.pagesRead.isEmpty())
            return state.pagesRead.pop();

        try
        {
            boolean skipToNext = false;

            while (xmlReader.hasNext()) {
                if(skipToNext) {
                    state.type = xmlReader.nextTag();
                    skipToNext = false;
                }
                else {
                    state.type = xmlReader.next();
                }

                if(!processPages(state, xmlReader)) {
                    if(state.type == XMLEvent.START_ELEMENT) {
                        skipToNext = true;
                    }
                }

                if(state.pagesRead.size() > 0)
                    break;
            }

            if(state.pagesRead.isEmpty())
                return null;
            else
                return state.pagesRead.pop();
        }
        catch (XMLStreamException e) {
            //Motivation for this hack:

            /* The problem is that there will be one block with a trailing </mediawiki> tag when reading the
             * multistream blocks. It is caused by the fact that only one thread sees the header with the starting tag.
             *
             * This exception is only raised after everything has been read and only affect the last block, given that
             * and that a potential solution might be very messy just to fix a single exception that does not affect the
             * function of the program I decided to ignore this particular exception. Woodstox is compliant and because
             * of this I could not find a property that allowed it to ignore trailing tags (which is a failure condition
             * under normal circumstances).
             */

            /* If you have a solution (preferably performant) that fixes this exception, _please_ make a contribution and
               this ugly notice can be removed. */
            if(e.getMessage().startsWith("Unbalanced close tag </mediawiki>; no open start tag."))
                return null; //ignore this exception, why? read motivation above.
            else
                throw new IOError(e);
        }
    }
}
