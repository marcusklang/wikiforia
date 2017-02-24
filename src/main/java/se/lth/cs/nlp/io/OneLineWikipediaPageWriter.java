package se.lth.cs.nlp.io;

import se.lth.cs.nlp.mediawiki.model.WikipediaPage;
import se.lth.cs.nlp.pipeline.Sink;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * This file/class is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This file/class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this forked version of wikiforia.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * @author Anton Södergren - karl.aj.sodergren@gmail.com
 */
public class OneLineWikipediaPageWriter implements Sink<WikipediaPage> {

    private final File output;
    private FileChannel fileChannel;

    /**
     * Default constructor
     *
     * @param output which file to write to
     */
    public OneLineWikipediaPageWriter(File output) {
        try {
            this.output = output;

            //Fix so it doesn't crash on file not exists
            File f = new File(output.toURI());
            f.createNewFile();

            this.fileChannel = FileChannel.open(Paths.get(output.toURI()), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public synchronized void process(List<WikipediaPage> batch) {
        if (this.fileChannel == null)
            return;

        try {
            if (batch.size() == 0) {
                this.fileChannel.write(ByteBuffer.wrap("\n".getBytes("utf-8")));
                this.fileChannel.close();
                this.fileChannel = null;
                return;
            }

            for (WikipediaPage wikipediaPage : batch) {
                String text = wikipediaPage.getText();
                if (text.length() > 0) {
                    //Make it one line
                    text = text.replaceAll("\n", " ");
                    //Append an id to the start of the line. {{page:id}}
                    this.fileChannel.write(ByteBuffer.wrap(wikipediaPage.getSignature().getBytes("utf-8")));
                    this.fileChannel.write(ByteBuffer.wrap(text.getBytes("utf-8")));
                    this.fileChannel.write(ByteBuffer.wrap("\n".getBytes("utf-8")));
                }
            }
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public String toString() {
        return String.format("XML Writer { target: %s }", output.getAbsolutePath());
    }
}
