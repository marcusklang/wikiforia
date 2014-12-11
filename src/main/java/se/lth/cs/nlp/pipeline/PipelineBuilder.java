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
 * Pipeline builder
 */
public class PipelineBuilder {
    public static <Input,Error> PipelinePipeBlock<Input,Error> input(Source<Input,Error> source) {
        return new FluentPipeIntermediary<Input,Error>(source);
    }

    private static class FluentPipeIntermediary<Input,Error> implements PipelinePipeBlock<Input,Error> {
        private final FluentPipeIntermediary<?,?> parent;
        private final Emitter<Input,Error> emitter;

        public FluentPipeIntermediary(FluentPipeIntermediary<?,?> parent, Emitter<Input,Error> emitter) {
            this.emitter = emitter;
            this.parent = parent;
        }

        public FluentPipeIntermediary(Source<Input,Error> source) {
            this.parent = null;
            this.emitter = source;
        }

        /**
         * Pipe result to a mapper
         * @param mapper the mapper
         * @param <Output> the type of mapped output
         * @param <NewError> the type of mapping errors
         * @return new piping stage
         */
        @Override
        public <Output, NewError> PipelinePipeBlock<Output, NewError> pipe(Mapper<Input,Output,NewError> mapper) {
            emitter.appendOutputSink(mapper);
            return new FluentPipeIntermediary<Output,NewError>(this, mapper);
        }

        /**
         * Send error results to a sink
         * @param sink the sink
         * @return this instance
         */
        @Override
        public PipelinePipeBlock<Input, Error> sendError(Sink<Error> sink) {
            emitter.appendErrorSink(sink);
            return this;
        }

        /**
         * Send log results to a sink
         * @param sink the sink
         * @return this instance
         */
        @Override
        public PipelinePipeBlock<Input, Error> sendLog(Sink<String> sink) {
            emitter.appendLogSink(sink);
            return this;
        }

        /**
         * Send output to a sink
         * @param sink the sink
         * @return this instance
         */
        @Override
        public PipelinePipeBlock<Input,Error> sendOutput(Sink<Input> sink) {
            emitter.appendOutputSink(sink);
            return this;
        }

        /**
         * Final pipe to an endpoint sink
         * @param sink the sink
         * @return a pipeline that is ready for execution
         */
        @Override
        public Pipeline pipe(Sink<Input> sink) {
            final Source<?,?> source;

            emitter.appendOutputSink(sink);

            FluentPipeIntermediary<?,?> current = this;
            while(current.parent != null)
                current = current.parent;

            source = (Source<?,?>)current.emitter;

            return new Pipeline() {
                @Override
                public void run() {
                    source.run();
                }
            };
        }

        /**
         * Build a pipeline based on current state
         * @return a pipeline that is ready for execution
         */
        @Override
        public Pipeline build() {
            final Source<?,?> source;

            FluentPipeIntermediary<?,?> current = this;
            while(current.parent != null)
                current = current.parent;

            source = (Source<?,?>)current.emitter;

            return new Pipeline() {
                @Override
                public void run() {
                    source.run();
                }
            };
        }
    }
}
