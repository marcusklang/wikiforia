package se.lth.cs.nlp.io;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;

import java.io.*;
import java.util.zip.Deflater;

/**
 * Line writer
 */
public class LineWriter extends StringWriter {
    public LineWriter(File target) {
        super(target);
    }

    public LineWriter(File target, long limit) {
        super(target, limit);
    }

    public LineWriter(OutputStream stream) {
        super(stream);
    }

    public LineWriter(OutputStream stream, long limit) {
        super(stream, limit);
    }

    @Override
    public boolean write(String item) {
        return super.write(item + "\n");
    }

    /**
     * Open a concurrent gzip compressed line writer (fastest compression)
     * @param target target location
     * @param limit the limit per split
     * @return parallel writer
     */
    public static ParallelSplitWriter<String> openFastGzipParallelWriter(File target, final int limit) {
        return openFastGzipParallelWriter(target, limit, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Open a concurrent gzip compressed line writer (fastest compression)
     * @param target target location
     * @param limit the limit per split
     * @param maxWriters the maximum number of writers
     * @return parallel writer
     */
    public static ParallelSplitWriter<String> openFastGzipParallelWriter(File target, final int limit, final int maxWriters) {
        return new ParallelSplitWriter<String>(target, maxWriters) {
            @Override
            protected Writer<String> newWriter(File path) {
                GzipParameters parameters = new GzipParameters();
                parameters.setCompressionLevel(Deflater.BEST_SPEED);
                parameters.setFilename(path.getName());
                try {
                    return new LineWriter(new GzipCompressorOutputStream(new FileOutputStream(path.getAbsolutePath() + ".gz"), parameters), limit);
                } catch (IOException e) {
                    throw new IOError(e);
                }
            }
        };
    }

    /**
     * Open a uncompressed concurrent line writer
     * @param target target location
     * @return parallel writer
     */
    public static ParallelSplitWriter<String> openParallelWriter(File target) {
        return new ParallelSplitWriter<String>(target) {
            @Override
            protected Writer<String> newWriter(File path) {
                return new LineWriter(path);
            }
        };
    }

    /**
     * Open a uncompressed concurrent line writer
     * @param target target location
     * @param limit the limit per split
     * @return parallel writer
     */
    public static ParallelSplitWriter<String> openParallelWriter(File target, final int limit) {
        return new ParallelSplitWriter<String>(target) {
            @Override
            protected Writer<String> newWriter(File path) {
                return new LineWriter(path, limit);
            }
        };
    }

    /**
     * Open a uncompressed concurrent line writer
     * @param target target location
     * @param limit the limit per split
     * @param maxWriters the maximum number of writers
     * @return parallel writer
     */
    public static ParallelSplitWriter<String> openParallelWriter(File target, final int limit, final int maxWriters) {
        return new ParallelSplitWriter<String>(target, maxWriters) {
            @Override
            protected Writer<String> newWriter(File path) {
                return new LineWriter(path, limit);
            }
        };
    }
}
