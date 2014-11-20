/**
 * This file is part of Wikiforia.
 *
 * Wikiforia is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Wikiforia is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.lth.cs.nlp.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Emitter base class, sends out batches of data
 */
public abstract class AbstractEmitter<TOutputType, TErrorType> implements Emitter<TOutputType, TErrorType> {
    private final ArrayList<Sink<TOutputType>> outputSink = new ArrayList<Sink<TOutputType>>();
    private final ArrayList<Sink<TErrorType>> errorSink = new ArrayList<Sink<TErrorType>>();
    private final ArrayList<Sink<String>> logSink = new ArrayList<Sink<String>>();

    @Override
    public final void appendOutputSink(Sink<TOutputType> output) {
        this.outputSink.add(output);
    }
    public final void appendErrorSink(Sink<TErrorType> output) {
        errorSink.add(output);
    }
    public final void appendLogSink(Sink<String> output) {
        logSink.add(output);
    }

    protected final void output(List<TOutputType> batch) {
        for(Sink<TOutputType> sink : outputSink) {
            sink.process(batch);
        }
    }

    protected final void error(List<TErrorType> batch) {
        for(Sink<TErrorType> sink : errorSink) {
            sink.process(batch);
        }
    }

    protected final void log(String...message) {
        List<String> strings = Arrays.asList(message);
        for(Sink<String> sink : logSink) {
            sink.process(strings);
        }
    }
}
