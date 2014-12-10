package se.lth.cs.nlp.io;

import java.io.*;

/**
 * Writes full strings, partial strings are not clipped.
 */
public class StringWriter implements Writer<String> {

    private final BufferedWriter writer;
    private final long limit;
    private long written = 0;

    public static final long DEFAULT_LIMIT = 128*1024*1024;

    public StringWriter(File target) {
        this(target, DEFAULT_LIMIT);
    }

    public StringWriter(File target, long limit) {
        this.limit = limit;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), "UTF-8"));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public StringWriter(OutputStream stream) {
        this(stream, DEFAULT_LIMIT);
    }

    public StringWriter(OutputStream stream, long limit) {
        this.limit = limit;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
        }
        catch(IOException ex) {
            throw new IOError(ex);
        }
    }


    @Override
    public boolean write(String item) {
        try {
            written += item.length();
            writer.write(item);
        } catch (IOException e) {
            throw new IOError(e);
        }

        return written < limit;
    }

    @Override
    public boolean isFull() {
        return written >= limit;
    }

    @Override
    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    @Override
    public void close() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
