
# Tinyj2c

Tinyj2c translate JAVA source to C source, build as standlone binary, and run without any class file. It's so small can be used to embedded device.

# Feature  
* A very very small java runtime    
* Garbage collection  
* Minimize memory footprint   
* Exception stack trace    
* Thread support  
* Lambda support  

    
# Requirement 
1. JDK 8+ (not only jre).         
2. clang/gcc/vsc.     

     
# Steps    

* git clone https://github.com/digitalgust/tinyj2c.git        
* Idea open /tinyj2c/class2c , run translator.    
* Clion open /tinyj2c/app , compile c source and run .      
* Run binary no java classes dependence.   

Or 
 
* run build script :    
 /tinyj2c/posix_build.sh   

     
# Translated java file Example   
    
* Java code:

```
package test;

public class HelloWorld {

    void test() throws RuntimeException {
        try {
            System.out.println("Hello world!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new HelloWorld().test();
    }
}


```     
     
* converted C code:     
     
```     
// CLASS: test/HelloWorld extends java/lang/Object
#include "metadata.h"


// generation
// globals
//struct test_HelloWorld_static {};



__refer arr_vmtable_test_HelloWorld_from_test_HelloWorld[] = {
    func_test_HelloWorld_test___V  //0
};
__refer arr_vmtable_test_HelloWorld_from_java_lang_Object[] = {
    func_java_lang_Object_toString___Ljava_lang_String_2,  //0
    func_java_lang_Object_wait___V,  //1
    func_java_lang_Object_finalize___V,  //2
    func_java_lang_Object_equals__Ljava_lang_Object_2_Z,  //3
    func_java_lang_Object_clone___Ljava_lang_Object_2,  //4
    func_java_lang_Object_getClass___Ljava_lang_Class_2,  //5
    func_java_lang_Object_wait__J_V,  //6
    func_java_lang_Object_notify___V,  //7
    func_java_lang_Object_notifyAll___V,  //8
    func_java_lang_Object_hashCode___I  //9
};
VMTable vmtable_test_HelloWorld[] = {
    {120, 1, arr_vmtable_test_HelloWorld_from_test_HelloWorld}, //0
    {5, 10, arr_vmtable_test_HelloWorld_from_java_lang_Object}, //1
};



// locals: 1
// stack: 1
// args: 1
void func_test_HelloWorld__init____V(JThreadRuntime *runtime, struct test_HelloWorld* p0){
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3]/* = {0}*/;
    RStackItem rstack[3] = {0};
    s32 sp = 0;
    StackFrame *__frame = method_enter(runtime, 939, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 3 , L944560186 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 3;
    rstack[sp++].obj = rlocal[0].obj;
    // invokespecial java/lang/Object.<init>()V
    sp -= 1;  //pop para
    // it's a empty method
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    switch (find_exception_handler_index(runtime)) {
        default: goto __ExceptionHandlerNotFound;
    }
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

void bridge_test_HelloWorld__init____V(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    func_test_HelloWorld__init____V(runtime, ins);
}

ExceptionItem arr_extable_func_test_HelloWorld_test___V[] = {
    { 0, 8, 130}
};
ExceptionTable extable_func_test_HelloWorld_test___V = {1, arr_extable_func_test_HelloWorld_test___V};

// locals: 2
// stack: 2
// args: 1
void func_test_HelloWorld_test___V(JThreadRuntime *runtime, struct test_HelloWorld* p0){
    
    StackItem local[4] = {0};
    RStackItem rlocal[4] = {0};
    StackItem stack[4]/* = {0}*/;
    RStackItem rstack[4] = {0};
    s32 sp = 0;
    StackFrame *__frame = method_enter(runtime, 940, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    // try catch :L823786669 L1836058082 L866360702 (0,8)->11
    L823786669:
    __frame->bytecodeIndex = 0; //try catch begin
    //  line no 7 , L823786669 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 7;
    // getstatic java/lang/System out Ljava/io/PrintStream;
    rstack[sp].obj =static_var_java_lang_System.out_0;
    sp += 1;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 1368);
    // invokevirtual java/io/PrintStream.println(Ljava/lang/String;)V
    {
        sp -= 2;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = construct_and_throw_exception(runtime, 0, 0, 7);
            goto __ExceptionHandler;
        }
        void (*__func_p) (JThreadRuntime *,struct java_io_PrintStream*,struct java_lang_String*) = find_method(__ins->vm_table, 18, 2);
        __func_p(runtime, rstack[sp + 0].obj, rstack[sp + 1].obj);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    L1836058082:
    __frame->bytecodeIndex = 8; //try catch end
    //  line no 10 , L1836058082 , bytecode index = 8
    goto L2009931623;
    L866360702:
    //  line no 8 , L866360702 , bytecode index = 11
    rlocal[1].obj = rstack[--sp].obj;
    ; 
    //  line no 9 , L156203431 , bytecode index = 12
    rstack[sp++].obj = rlocal[1].obj;
    // invokevirtual java/lang/Exception.printStackTrace()V
    {
        sp -= 1;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = construct_and_throw_exception(runtime, 0, 12, 9);
            goto __ExceptionHandler;
        }
        void (*__func_p) (JThreadRuntime *,struct java_lang_Throwable*) = find_method(__ins->vm_table, 17, 4);
        __func_p(runtime, rstack[sp + 0].obj);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    L2009931623:
    //  line no 11 , L2009931623 , bytecode index = 16
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    switch (find_exception_handler_index(runtime)) {
        case 0 : goto L866360702;
        default: goto __ExceptionHandlerNotFound;
    }
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

void bridge_test_HelloWorld_test___V(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    func_test_HelloWorld_test___V(runtime, ins);
}


// locals: 1
// stack: 2
// args: 1
void func_test_HelloWorld_main___3Ljava_lang_String_2_V(JThreadRuntime *runtime, JArray * p0){
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[4]/* = {0}*/;
    RStackItem rstack[4] = {0};
    s32 sp = 0;
    StackFrame *__frame = method_enter(runtime, 941, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 14 , L1268390885 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 14;
    // new test/HelloWorld
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 120);
    // dup
    stack[sp].j = stack[sp - 1].j; 
    rstack[sp].obj = rstack[sp - 1].obj; 
    ++sp;
    // invokespecial test/HelloWorld.<init>()V
    {
        sp -= 1;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = construct_and_throw_exception(runtime, 0, 0, 14);
            goto __ExceptionHandler;
        }
        func_test_HelloWorld__init____V(runtime, rstack[sp + 0].obj);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // invokevirtual test/HelloWorld.test()V
    {
        sp -= 1;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = construct_and_throw_exception(runtime, 0, 0, 14);
            goto __ExceptionHandler;
        }
        void (*__func_p) (JThreadRuntime *,struct test_HelloWorld*) = find_method(__ins->vm_table, 120, 0);
        __func_p(runtime, rstack[sp + 0].obj);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    ; 
    //  line no 15 , L1421022166 , bytecode index = 10
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    switch (find_exception_handler_index(runtime)) {
        default: goto __ExceptionHandlerNotFound;
    }
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

void bridge_test_HelloWorld_main___3Ljava_lang_String_2_V(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    func_test_HelloWorld_main___3Ljava_lang_String_2_V(runtime, para[0].obj);
}



```     
     
     
     
