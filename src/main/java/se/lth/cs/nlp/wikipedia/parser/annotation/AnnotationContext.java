package se.lth.cs.nlp.wikipedia.parser.annotation;

/**
 * Annotation context, one instance per parser invocation
 */
public abstract class AnnotationContext<T> {

    protected Object tag;
    protected T model;

    /**
     * Get the current state of the text
     */
    public abstract String getText();

    /**
     * Set the model
     * @param model
     */
    public void setModel(T model) {
        this.model = model;
    }

    /**
     * Get the model
     * @return
     */
    public T getModel() {
        return model;
    }

    /**
     * Set a tag for this context, completely userdefined
     * @param tag
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * Get the tag set
     * @return tag or null if none has been set.
     */
    public Object getTag() {
        return tag;
    }
}
