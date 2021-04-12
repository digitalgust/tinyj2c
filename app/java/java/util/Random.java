/*
 * Copyright (c) 2003, 2006, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */



package java.util;

public
class Random {

    private long seed;

    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    public Random() { this(System.currentTimeMillis()); }

    public Random(long seed) {
        setSeed(seed);
    }

    synchronized public void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    }

    synchronized protected int next(int bits) {
        long nextseed = (seed * multiplier + addend) & mask;
        seed = nextseed;
        return (int)(nextseed >>> (48 - bits));
    }

    public int nextInt() {  return next(32); }

    public int nextInt(int n) {
        if (n<=0)
            throw new IllegalArgumentException("n must be positive");

        if ((n & -n) == n)  // i.e., n is a power of 2
            return (int)((n * (long)next(31)) >> 31);

        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while(bits - val + (n-1) < 0);
        return val;
    }

    public long nextLong() {
        return ((long)(next(32)) << 32) + next(32);
    }

    public float nextFloat() {
        int i = next(24);
        return i / ((float)(1 << 24));
    }

    public double nextDouble() {
        long l = ((long)(next(26)) << 27) + next(27);
        return l / (double)(1L << 53);
    }


    
}

