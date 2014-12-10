package se.lth.cs.nlp.io;

public interface Writer<T> {
    /**
     * Write stuff
     * @param item item
     * @return true if more to write is possible, false if last item should have been written.
     */
    public boolean write(T item);

    /**
     * Check if writer is full, i.e. should not write any more.
     * @return true
     */
    public boolean isFull();

    public void flush();

    /**
     * Close and flush writer
     */
    public void close();
}
