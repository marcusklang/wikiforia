package se.lth.cs.nlp.pipeline;

import java.util.List;

/**
 * Created by csz-mkg on 14-12-11.
 */
public final class IdentityFilter<T> extends Filter<T> {
    @Override
    public final void process(List<T> batch) {
        output(batch);
    }

    @Override
    protected final boolean accept(T item) {
        return true;
    }
}
