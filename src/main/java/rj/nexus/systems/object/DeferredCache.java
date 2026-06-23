/*MIT License

Copyright (c) 2026 GeckoLib

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package rj.nexus.systems.object;


import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/// Functional object that acts as a two-stage memoizing function of sorts.
///
/// Takes an input object and a mapping function. Up until the mapping function is called
///
/// @param <I> The input object type
/// @param <O> The output object type
public class DeferredCache<I, O> {
    private boolean computed = false;
    private final Function<I, O> mappingFunction;
    private @Nullable I input;
    private @Nullable O output = null;

    public DeferredCache(I input, Function<I, O> mappingFunction) {
        this.input = Objects.requireNonNull(input);
        this.mappingFunction = mappingFunction;
    }

    /// Retrieve the input object for this cache.
    ///
    /// Can only be accessed until the output has been computed
    public I getInput() {
        if (this.computed || this.input == null)
            throw new IllegalStateException("Attempting to access input after output of deferred cache has been calculated!");

        return this.input;
    }

    /// Retrieve the output object for this cache.
    ///
    /// Can only be accessed once the output has been computed
    public O getOutput() {
        if (!this.computed || this.output == null)
            throw new IllegalStateException("Attempting to access output before it has been calculated!");

        return this.output;
    }

    /// Compute the output object for this cache and invalidate the input
    ///
    /// @return The computed output
    public O compute() {
        if (!this.computed) {
            this.output = this.mappingFunction.apply(Objects.requireNonNull(this.input));
            this.input = null;
            this.computed = true;
        }

        return getOutput();
    }
}