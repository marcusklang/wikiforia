package se.lth.cs.nlp.pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csz-mkg on 14-12-11.
 */
public abstract class Filter<T> extends Mapper<T,T,Void> {
    @Override
    public void process(List<T> batch) {
        if(batch.size() == 0)
            output(batch); //let the end signal get through.

        ArrayList<T> output = new ArrayList<T>();

        for (T item : batch) {
            if(accept(item)) {
                output.add(item);
            }
        }

        if(output.size() > 0)
            output(output);
    }

    protected static <T> boolean delegatedAccept(Filter<T> filter, T item) {
        return filter.accept(item);
    }

    protected abstract boolean accept(T item);
}
