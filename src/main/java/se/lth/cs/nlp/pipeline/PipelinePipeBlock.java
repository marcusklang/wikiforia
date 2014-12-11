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

/**
 * Pipeline fluent interface
 */
public interface PipelinePipeBlock<Input, Error> {

    /**
     * Pipe result to a mapper
     * @param mapper the mapper.
     * @param <Output> the type of mapped output
     * @param <Error> the type of mapping errors
     * @return new piping stage
     */
    public <Output,Error> PipelinePipeBlock<Output,Error> pipe(Mapper<Input,Output,Error> mapper);

    /**
     * Send output to a sink
     * @param sink the sink
     * @return this instance
     */
    public PipelinePipeBlock<Input,Error> sendOutput(Sink<Input> sink);

    /**
     * Send error results to a sink
     * @param sink the sink
     * @return this instance
     */
    public PipelinePipeBlock<Input,Error> sendError(Sink<Error> sink);

    /**
     * Send log results to a sink
     * @param sink the sink
     * @return this instance
     */
    public PipelinePipeBlock<Input,Error> sendLog(Sink<String> sink);

    /**
     * Final pipe to an endpoint sink
     * @param sink the sink
     * @return a pipeline that is ready for execution
     */
    public Pipeline pipe(Sink<Input> sink);

    /**
     * Build a pipeline based on current state
     * @return a pipeline that is ready for execution
     */
    public Pipeline build();
}
