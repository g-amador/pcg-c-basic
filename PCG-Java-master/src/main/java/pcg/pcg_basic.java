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
 * This code provides a port to Java of the minimal implementation of one member
 * of the PCG family of random number generators, which are fast, statistically
 * excellent, and offer a number of useful features.
 *
 * This original C code is derived from the full C implementation, which is in
 * turn derived from the canonical C++ PCG implementation. The C++ version has
 * many additional features and is preferable if you can use C++ in your
 * project.
 *
 * @author G. Amador
 */
public class pcg_basic {

    // If you *must* statically initialize it, here's one.
    static pcg32_random_t pcg32_global
            = new pcg32_random_t(0x853c49e6748fea9bL, 0xda3e39cb94b95bdbL);

    // pcg32_srandom(initstate, initseq)
    // pcg32_srandom_r(rng, initstate, initseq):
    //     Seed the rng.  Specified in two parts, state initializer and a
    //     sequence selection constant (a.k.a. stream id)
    public static void pcg32_srandom_r(pcg32_random_t rng, long initstate, long initseq) {
        rng.state = 0;
        rng.inc = (initseq << 1) | 1;
        pcg32_random_r(rng);
        rng.state += initstate;
        pcg32_random_r(rng);
    }

    public static void pcg32_srandom(long seed, long seq) {
        pcg32_srandom_r(pcg32_global, seed, seq);
    }

    // pcg32_random()
    // pcg32_random_r(rng)
    //     Generate a uniformly distributed 32-bit random number
    public static int pcg32_random_r(pcg32_random_t rng) {
        long oldstate = rng.state;
        rng.state = oldstate * 6364136223846793005L + rng.inc;
        int xorshifted = (int) (((oldstate >>> 18) ^ oldstate) >>> 27);
        int rot = (int) (oldstate >>> 59);
        return ((xorshifted >>> rot) | (xorshifted << ((-rot) & 31)));
    }

    public static int pcg32_random() {
        return pcg32_random_r(pcg32_global);
    }

    // pcg32_boundedrand(bound):
    // pcg32_boundedrand_r(rng, bound):
    //     Generate a uniformly distributed number, r, where 0 <= r < bound
    public static int pcg32_boundedrand_r(pcg32_random_t rng, int bound) {
        // To avoid bias, we need to make the range of the RNG a multiple of
        // bound, which we do by dropping output less than a threshold.
        // A naive scheme to calculate the threshold would be to do
        //
        //     int threshold = 0x100000000ull % bound;
        //
        // but 64-bit div/mod is slower than 32-bit div/mod (especially on
        // 32-bit platforms).  In essence, we do
        //
        //     int threshold = (0x100000000ull-bound) % bound;
        //
        // because this version will calculate the same modulus, but the LHS
        // value is less than 2^32.

        int threshold = -bound % bound;

        // Uniformity guarantees that this loop will terminate.  In practice, it
        // should usually terminate quickly; on average (assuming all bounds are
        // equally likely), 82.25% of the time, we can expect it to require just
        // one iteration.  In the worst case, someone passes a bound of 2^31 + 1
        // (i.e., 2147483649), which invalidates almost 50% of the range.  In 
        // practice, bounds are typically small and only a tiny amount of the range
        // is eliminated.
        for (;;) {
            int r = pcg32_random_r(rng);
            if (r >= threshold) {
                return r % bound;
            }
        }
    }

    public static int pcg32_boundedrand(int bound) {
        return pcg32_boundedrand_r(pcg32_global, bound);
    }
}
