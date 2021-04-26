/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.luaj.vm2.lib.jme;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Print;
import org.luaj.vm2.Prototype;

/**
 * Test the plain old bytecode interpreter
 */
public class TestLuaJ {
    // create the script
    public static String name = "script";
    public static String script =
            "MAX = 500000\n" +
                    "PRINT_COUNT = 10000\n" +
                    "\n" +
                    "coco = coroutine.create(function ()\n" +
                    "    local arrco={}\n" +
                    "    local i = 0\n" +
                    "    while i < MAX do\n" +
                    "        a=\"abc\"\n" +
                    "        b=\"def\"\n" +
                    "        c=a--..b..i\n" +
                    "        if math.fmod(i,PRINT_COUNT) ==0 then\n" +
                    "        \tprint(\"main coi=\" .. i)\n" +
                    "        \tcoroutine.yield()\n" +
                    "        end\n" +
                    "        arrco[i]=c\n" +
                    "        i = i + 1\n" +
                    "    end\n" +
                    "    print( \"arrco[]=\"..arrco[20000])\n" +
                    "    print(os.clock())\n" +
                    "end)\n" +
                    "coroutine.resume(coco)\n" +
                    "\n" +
                    "\n" +
                    "arr={}\n" +
                    "i = 0\n" +
                    "while i < MAX do\n" +
                    "    a=\"abc\"\n" +
                    "    b=\"def\"\n" +
                    "    c=a--..b..i\n" +
                    "    if math.fmod(i,PRINT_COUNT) ==0 then\n" +
                    "    \tprint(\"main i=\" .. i)\n" +
                    "    \tcoroutine.resume(coco)\n" +
                    "    end\n" +
                    "    arr[i]=c\n" +
                    "    i = i + 1\n" +
                    "end\n" +
                    "print(os.clock())\n" +
                    "print( \"arr[]=\"..arr[20000])\n" +
                    "\n" +
                    "print(\"end .\"..i..\"  c=\"..c)";
//            "function r(q,...)\n" +
//                    "	local a=arg\n" +
//                    "	return a and a[2]\n" +
//                    "end\n" +
//                    "function s(q,...)\n" +
//                    "	local a=arg\n" +
//                    "	local b=...\n" +
//                    "	return a and a[2],b\n" +
//                    "end\n" +
//                    "print( r(111,222,333),s(111,222,333) )";

    public static void main(String[] args) {
        System.out.println(script);

        // create an environment to run in
        Globals globals = JmePlatform.standardGlobals();

        // compile into a chunk, or load as a class
        LuaValue chunk = globals.load(script, "script");

        // The loaded chunk should be a closure, which contains the prototype.
//        print(chunk.checkclosure().p);

        // The chunk can be called with arguments as desired.
        chunk.call(LuaValue.ZERO, LuaValue.ONE);
    }

    private static void print(Prototype p) {
        System.out.println("--- " + p);
        Print.printCode(p);
        if (p.p != null)
            for (int i = 0, n = p.p.length; i < n; i++)
                print(p.p[i]);
    }

}
