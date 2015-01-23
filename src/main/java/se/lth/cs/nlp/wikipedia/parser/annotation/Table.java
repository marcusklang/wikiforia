package se.lth.cs.nlp.wikipedia.parser.annotation;

import java.util.List;

/**
 * Table instance
 */
public abstract class Table {

    /**
     * Conduct parsing of the table to find all its cells
     * @return list of rows of cells.
     */
    public abstract List<List<TableCell>> parse();
}
