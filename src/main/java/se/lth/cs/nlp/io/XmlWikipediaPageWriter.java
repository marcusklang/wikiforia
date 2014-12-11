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
package se.lth.cs.nlp.io;

import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.XMLStreamWriter2;
import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.pipeline.Sink;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.List;

/**
 * WikipediaPage UTF-8 XML writer, only writes WikipediaPages with content
 */
public class XmlWikipediaPageWriter implements Sink<WikipediaPage> {
    private final File output;
    private XMLStreamWriter2 writer;

    /**
     * Default constructor
     * @param output which file to write to
     */
    public XmlWikipediaPageWriter(File output) {
        try {
            this.writer = (XMLStreamWriter2)XMLOutputFactory2.newInstance().createXMLStreamWriter(new BufferedOutputStream(new FileOutputStream(output)), "utf-8");
            this.output = output;
            this.writer.writeStartDocument("utf-8", "1.0");
            this.writer.writeSpace("\n");
            this.writer.writeStartElement("pages");
            this.writer.writeSpace("\n");
        } catch (XMLStreamException e) {
            throw new IOError(e);
        } catch (FileNotFoundException e) {
            throw new IOError(e);
        }
    }

    @Override
    public synchronized void process(List<WikipediaPage> batch) {
        if(writer == null)
            return;

        try {
            if(batch.size() == 0) {
                writer.writeEndElement();
                writer.writeSpace("\n");
                writer.writeEndDocument();
                writer.flush();
                writer.closeCompletely();
                writer = null;
                return;
            }

            for (WikipediaPage wikipediaPage : batch) {
                if(wikipediaPage.getText().length() > 0) {
                    writer.writeStartElement("page");
                    writer.writeAttribute("id", String.valueOf(wikipediaPage.getId()));
                    writer.writeAttribute("title", String.valueOf(wikipediaPage.getTitle()));
                    writer.writeAttribute("revision", String.valueOf(wikipediaPage.getRevision()));
                    writer.writeAttribute("type", String.valueOf(wikipediaPage.getFormat()));
                    writer.writeAttribute("ns-id", String.valueOf(wikipediaPage.getNamespace()));

                    String name = wikipediaPage.getHeader().getSiteinfo().getNamespaces().get(wikipediaPage.getNamespace());
                    if (name == null)
                        writer.writeAttribute("ns-name", "?");
                    else
                        writer.writeAttribute("ns-name", name);

                    writer.writeCharacters(wikipediaPage.getText());
                    writer.writeEndElement();
                    writer.writeSpace("\n");
                }
            }
        } catch (XMLStreamException e) {
            throw new IOError(e);
        }
    }

    @Override
    public String toString() {
        return String.format("XML Writer { target: %s }", output.getAbsolutePath());
    }
}
