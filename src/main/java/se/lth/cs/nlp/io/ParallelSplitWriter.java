package se.lth.cs.nlp.io;

import java.io.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Parallel split writer, writes in parallel IO data out to files, creates files as needed.
 */
public abstract class ParallelSplitWriter<T> extends SplitWriter {

    private ConcurrentLinkedDeque<Writer<T>> writers;
    private int openWriters = 0;
    private final int maxWriters;
    private final AtomicLong written = new AtomicLong(0);

    /**
     * Default constructor
     * @param basepath target location for splits
     * @remarks Uses a potentially bad default value for concurrent writers,
     *          it is equal to number of logical cores of the current machine.
     */
    public ParallelSplitWriter(File basepath) {
        this(basepath, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Parametric constructor
     * @param basepath  target location for splits
     * @param maxWriters the maximum number of concurrent writers
     * @remarks Recommended value for maxWriters differs depending on hard drive type, RAM and OS caching algorithms.
     */
    public ParallelSplitWriter(File basepath, int maxWriters) {
        super(basepath);
        this.maxWriters = maxWriters;
        this.writers = new ConcurrentLinkedDeque<Writer<T>>();
    }

    /**
     * Write helper function
     * @param item the value to write
     * @remarks Concurrency is a problem when items are small, consider batching small writes for better performance.
     */
    public void write(T item) {
        Writer<T> writer = getWriter();
        writer.write(item);
        returnWriter(writer);

    }

    /**
     * Get writer, thread safe method
     * @return writer instance
     */
    protected Writer<T> getWriter() {
        Writer<T> writer = writers.poll();
        if(writer == null) {
            File toOpen;
            synchronized (this) {
                while(writer == null && openWriters == maxWriters) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    writer = writers.poll();
                }

                if(writer != null)
                    return writer;

                openWriters++;
            }

            toOpen = newSplit();

            return newWriter(toOpen);
        }
        else
            return writer;
    }

    /**
     * Write a batch of data, thread safe
     * @param batch batch of data
     */
    public void write(List<T> batch) {
        if(batch.size() == 0)
            return;

        Writer<T> writer = getWriter();
        for(T item : batch)
        {
            if(!writer.write(item)) {
                returnWriter(writer);
                writer = getWriter();
            }
        }
        returnWriter(writer);
        written.addAndGet(batch.size());
    }

    /**
     * Return a used writer
     * @param writer the write used
     */
    protected void returnWriter(Writer<T> writer) {
        if(writer.isFull()) {
            writer.close();
            synchronized (this) {
                openWriters--;
                notifyAll();
            }
        }
        else {
            writers.add(writer);
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * Construct a new writer
     * @param path the target file for the writer
     * @return writer instance
     */
    protected abstract Writer<T> newWriter(File path);

    /**
     * Flush and close all writers
     */
    public void close() {
        Writer<T> datawriter;
        while( (datawriter = writers.poll()) != null ) {
            datawriter.close();
            openWriters--;
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(basepath.getAbsolutePath(), "_count")));
            writer.write(Long.toString(written.get())+"\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
