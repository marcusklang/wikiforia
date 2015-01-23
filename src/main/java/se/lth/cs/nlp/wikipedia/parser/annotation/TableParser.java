package se.lth.cs.nlp.wikipedia.parser.annotation;

import de.fau.cs.osr.ptk.common.AstVisitor;
import org.sweble.wikitext.parser.nodes.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Table parser AST visitor, basically searches up all rows and cells present in a table.
 */
public class TableParser extends AstVisitor<WtNode> {
    protected List<List<WtTableCell>> cells = new ArrayList<List<WtTableCell>>();
    protected List<WtTableCell> row;

    public void visit(WtNode n)
    {
    }

    public void visit(WtTableImplicitTableBody body) {
        iterate(body.getBody());
    }

    public void visit(WtTableHeader header) {
        //TODO: Future support
    }

    public void visit(WtTableCaption caption) {
        //TODO: Future support
    }

    public void visit(WtBody body) {
        iterate(body);
    }

    public void visit(WtTable table) {
        iterate(table.getBody());
    }

    public void visit(WtTableRow row) {
        this.row = new ArrayList<WtTableCell>();
        iterate(row);
        cells.add(this.row);
        this.row = null;
    }

    public void visit(WtTableCell cell) {
        if(row != null)
            row.add(cell);
    }
}
