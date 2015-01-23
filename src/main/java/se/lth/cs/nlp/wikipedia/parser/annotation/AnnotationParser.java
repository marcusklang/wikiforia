package se.lth.cs.nlp.wikipedia.parser.annotation;

import java.util.Collection;

/**
 * Annotation parser, normalized and cleaned up parser API.
 */
public interface AnnotationParser<T,Out> {
    /**
     * Start of document
     * @param context the context
     */
    public void startDocument(AnnotationContext<T> context);

    /**
     * Category listener
     * @param context the context
     * @param target the raw target
     * @param title the page title
     */
    public void category(AnnotationContext<T> context, String target, String title, int start);

    /**
     * Template listener
     * @param context context
     * @param template the template that needs a secondary pass to parse
     */
    public void template(AnnotationContext<T> context, Template template, int start);

    /**
     * Table listener
     * @param context context
     * @param table the table that needs to parsed
     */
    public void table(AnnotationContext<T> context, Table table, int start);

    /**
     * Table cell start
     * @param context the context
     * @param row current row, zero based
     */
    public void tableCellStart(AnnotationContext<T> context, int row, int col, int start);

    /**
     *
     * @param context the context
     * @param row current row, zero based
     */
    public void tableCellEnd(AnnotationContext<T> context, int row, int col, int start, int end);

    /**
     * Called when attempting to parse template argument
     * @param context context
     */
    public void startTemplateArgument(AnnotationContext<T> context, int start);


    /**
     * Called when template argument is fully parsed.
     * @param context context
     * @return the resulting value for a template argument or null if not handled.
     */
    public void endTemplateArgument(AnnotationContext<T> context, int start, int end);

    /**
     * Anchor listener
     * @param context context
     * @param target anchor target, if internally: the page title even if it is the same page, if externally: URL
     * @param fragmentIdentifier if such is present, e.g. [wikipedia-title]#[fragmentIdentifier]
     * @param internal true if it is a link internal to current edition of Wikipedia
     * @param start text start
     * @param end text end
     */
    public void anchor(AnnotationContext<T> context, String target, String fragmentIdentifier, boolean internal, int start, int end);

    /**
     * Header listener
     * @param context context
     * @param level the level
     * @param title the title of the header
     *
     */
    public void header(AnnotationContext<T> context, int level, String title, Collection<String> headerPath, int start);

    /**
     * Text Abstract listener
     * @param context context
     * @param start start
     * @param end end
     */
    public void textAbstract(AnnotationContext<T> context, int start, int end);

    /**
     * Paragraph listener
     * @param context context
     * @param start text start
     * @param end text end
     */
    public void paragraph(AnnotationContext<T> context, String currentHeader, int start, int end);

    /**
     * List Start listener
     * @param context context
     * @param id the unique id for this list
     * @param start text start
     * @param ordered is the list ordered
     */
    public void startList(AnnotationContext<T> context, int id, int start, boolean ordered);

    /**
     * List Item Start listener
     * @param context context
     * @param listId unique list id
     * @param itemId unqiue item id
     * @param start text start
     */
    public void startListItem(AnnotationContext<T> context, int listId, int itemId, int start);

    /**
     * List Item End listener
     * @param context context
     * @param listId unique list id
     * @param itemId unique item id
     * @param start text start
     * @param end text end
     */
    public void endListItem(AnnotationContext<T> context, int listId, int itemId, int start, int end);

    /**
     * List End listener
     * @param context
     * @param id
     * @param start
     * @param end
     * @param ordered
     */
    public void endList(AnnotationContext<T> context, int id, int start, int end, boolean ordered);

    /**
     * End of document and finalize the result
     * @param context the context
     * @return the model that is filled and created.
     */
    public Out endDocument(AnnotationContext<T> context);

}
