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
 * Sink, an event listener
 */
public interface Sink<T> {

    /**
     * Process batch
     * @param batch
     * @remarks the way the pipeline is built right now, this method is assumed
     * to be blocking and this affects the rest of the pipeline.
     *
     * This method is assumed to be threadsafe, add necessary synchronizations if needed.
     */
    public void process(List<T> batch);
}
