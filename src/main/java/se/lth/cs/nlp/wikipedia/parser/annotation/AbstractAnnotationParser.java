package se.lth.cs.nlp.wikipedia.parser.annotation;

import java.util.Collection;

/**
 * Abstract annotation parser base
 * @remarks the default implementation will assume that T == Out, but this might be so
 * and in that case: override endDocument and return proper Out value.
 */
public class AbstractAnnotationParser<T,Out> implements AnnotationParser<T,Out> {
    @Override
    public void startDocument(AnnotationContext<T> context) {

    }

    @Override
    public void category(AnnotationContext<T> context, String target, String title, int start) {

    }

    @Override
    public void template(AnnotationContext<T> context, Template template, int start) {

    }

    @Override
    public void table(AnnotationContext<T> context, Table table, int start) {

    }

    @Override
    public void tableCellStart(AnnotationContext<T> context, int row, int col, int start) {

    }

    @Override
    public void tableCellEnd(AnnotationContext<T> context, int row, int col, int start, int end) {

    }

    @Override
    public void startTemplateArgument(AnnotationContext<T> context, int start) {

    }

    @Override
    public void endTemplateArgument(AnnotationContext<T> context, int start, int end) {

    }

    @Override
    public void anchor(AnnotationContext<T> context, String target, String fragmentIdentifier, boolean internal, int start, int end) {

    }

    @Override
    public void header(AnnotationContext<T> context, int level, String title, Collection<String> headerPath, int start) {

    }

    @Override
    public void textAbstract(AnnotationContext<T> context, int start, int end) {

    }

    @Override
    public void paragraph(AnnotationContext<T> context, String currentHeader, int start, int end) {

    }

    @Override
    public void startList(AnnotationContext<T> context, int id, int start, boolean ordered) {

    }

    @Override
    public void startListItem(AnnotationContext<T> context, int listId, int itemId, int start) {

    }

    @Override
    public void endListItem(AnnotationContext<T> context, int listId, int itemId, int start, int end) {

    }

    @Override
    public void endList(AnnotationContext<T> context, int id, int start, int end, boolean ordered) {

    }

    @Override
    public Out endDocument(AnnotationContext<T> context) {
        return (Out)context.getModel();
    }
}
