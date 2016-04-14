/*
 * PCG Random Number Generation for C port to Java.
 *
 * Copyright 2016 Gonçalo Amador <g.n.p.amador@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For additional information about the PCG random number generation scheme,
 * including its license and other licensing options, visit
 *
 *     http://www.pcg-random.org
 */
package pcg;

/**
 * @author G. Amador
 */
public class pcg32_random_t {

    // Internals are *Protected* to be used only from other members of the pcg package
    protected long state; // RNG state.  All values are possible.
    protected long inc;   // Controls which RNG sequence (stream) is selected. Must *always* be odd.

    public pcg32_random_t() {
    }

    public pcg32_random_t(long state, long inc) {
        this.state = state;
        this.inc = inc;
    }

    public static int sizeof() {
        return Long.BYTES * 2;
    }
}
