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

import java.util.List;

/**
 * Mapper, converts from an input type to an output type in batches,
 * optionally sends out failed mappings via a separate typed stream.
 */
public abstract class Mapper<TInput,TOutput,TError> extends AbstractEmitter<TOutput,TError> implements Sink<TInput>, Emitter<TOutput,TError> {

    /**
     * Map batches of data
     * @param batch input batch
     * @remarks uses emitter methods to send out the result.
     */
    @Override
    public abstract void process(List<TInput> batch);
}
