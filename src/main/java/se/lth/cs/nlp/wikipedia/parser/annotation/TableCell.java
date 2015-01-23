package se.lth.cs.nlp.wikipedia.parser.annotation;

/**
 * Table cell
 */
public abstract class TableCell {

    protected final int row;
    protected final int col;

    public TableCell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Parse this cell using a different model and parser
     * @param model the model
     * @param parser the parser
     * @param <T> the model type
     * @param <Out> the output type, not relevant for this method because endDocument is never used.
     */
    public abstract <T,Out> void parse(T model, AnnotationParser<T,Out> parser);

    /**
     * Parse this cell using the current model and parser
     * @remarks This might be undesireable because it often generates a lot of
     * small paragraphs depending on how the orginal documenet was written.
     */
    public abstract void parse();
}
