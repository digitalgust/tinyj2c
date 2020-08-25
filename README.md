
# java2c

Convert java source to c source , then compile to standalone binary.    

    
# Requirement
     
1. Jetbrain Idea for compile and run converter.    
2. cmake compile c source , recommend Jetbrain Clion.     
3. JDK 8+.     
4. miniJVM for java runtime classes.       
5. clang     

     
# Step    

* git clone https://github.com/digitalgust/miniJVM.git     
* git clone https://github.com/digitalgust/java2c.git        
* Idea open class2c , run converter.    
* Clion open java2c , compile c source and run .      
     
     
# Example   
    
* Java code:

```
package java.lang;


public class Runtime {
    private static Runtime currentRuntime = new Runtime();

    public static Runtime getRuntime() { 
        return currentRuntime;
    }

    private Runtime() {}

    private native void exitInternal(int status);

    public void exit(int status) {
        exitInternal(status);
    }

    public native long freeMemory();

    public native long totalMemory();

    public native void gc();

}



```     
     
* converted C code:     
     
```     

// CLASS: java/lang/Runtime extends java/lang/Object
#include "metadata.h"

s64 Java_java_lang_Runtime_totalMemory___J(JThreadRuntime *runtime, struct java_lang_Runtime* p0);
s64 Java_java_lang_Runtime_freeMemory___J(JThreadRuntime *runtime, struct java_lang_Runtime* p0);
void Java_java_lang_Runtime_gc___V(JThreadRuntime *runtime, struct java_lang_Runtime* p0);
void Java_java_lang_Runtime_exitInternal__I_V(JThreadRuntime *runtime, struct java_lang_Runtime* p0, s32 p1);

// generation
// globals
//struct java_lang_Runtime_static {struct java_lang_Runtime* currentRuntime_0;};
struct java_lang_Runtime_static static_var_java_lang_Runtime = {NULL};


__refer arr_vmtable_java_lang_Runtime_from_java_lang_Runtime[] = {
    Java_java_lang_Runtime_exit__I_V,  //0
    Java_java_lang_Runtime_freeMemory___J,  //1
    Java_java_lang_Runtime_totalMemory___J,  //2
    Java_java_lang_Runtime_gc___V  //3
};
__refer arr_vmtable_java_lang_Runtime_from_java_lang_Object[] = {
    Java_java_lang_Object_getClass___Ljava_lang_Class_2,  //0
    Java_java_lang_Object_hashCode___I,  //1
    Java_java_lang_Object_equals__Ljava_lang_Object_2_Z,  //2
    Java_java_lang_Object_toString___Ljava_lang_String_2,  //3
    Java_java_lang_Object_notify___V,  //4
    Java_java_lang_Object_notifyAll___V,  //5
    Java_java_lang_Object_wait__J_V,  //6
    Java_java_lang_Object_wait__JI_V,  //7
    Java_java_lang_Object_wait___V,  //8
    Java_java_lang_Object_clone___Ljava_lang_Object_2,  //9
    Java_java_lang_Object_finalize___V  //10
};
VMTable vmtable_java_lang_Runtime[] = {
    {224, 4, arr_vmtable_java_lang_Runtime_from_java_lang_Runtime}, //0
    {17, 11, arr_vmtable_java_lang_Runtime_from_java_lang_Object}, //1
};


ExceptionItem arr_extable_Java_java_lang_Runtime_getRuntime___Ljava_lang_Runtime_2[] = {
};
ExceptionTable extable_Java_java_lang_Runtime_getRuntime___Ljava_lang_Runtime_2 = {0, arr_extable_Java_java_lang_Runtime_getRuntime___Ljava_lang_Runtime_2};

// locals: 0
// stack: 1
// args: 0
struct java_lang_Runtime* Java_java_lang_Runtime_getRuntime___Ljava_lang_Runtime_2(JThreadRuntime *runtime){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[2] = {0};
    RStackItem rlocal[2] = {0};
    StackItem stack[3] = {0};
    RStackItem rstack[3] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 1352, &rstack[0], &rlocal[0], &sp);
    ; 
    //  line no 35 , L1839202804 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 35;
    // getstatic java/lang/Runtime currentRuntime Ljava/lang/Runtime;
    rstack[sp].obj =static_var_java_lang_Runtime.currentRuntime_0;
    sp += 1;
    method_exit(runtime);
    return rstack[sp - 1].obj;
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}

ExceptionItem arr_extable_Java_java_lang_Runtime__init____V[] = {
};
ExceptionTable extable_Java_java_lang_Runtime__init____V = {0, arr_extable_Java_java_lang_Runtime__init____V};

// locals: 1
// stack: 1
// args: 1
void Java_java_lang_Runtime__init____V(JThreadRuntime *runtime, struct java_lang_Runtime* p0){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3] = {0};
    RStackItem rstack[3] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 1353, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 39 , L1364451198 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 39;
    rstack[sp++].obj = rlocal[0].obj;
    // invokespecial java/lang/Object.<init>()V
    sp -= 1;  //pop para
    // it's a empty method
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

ExceptionItem arr_extable_Java_java_lang_Runtime_exit__I_V[] = {
};
ExceptionTable extable_Java_java_lang_Runtime_exit__I_V = {0, arr_extable_Java_java_lang_Runtime_exit__I_V};

// locals: 2
// stack: 2
// args: 2
void Java_java_lang_Runtime_exit__I_V(JThreadRuntime *runtime, struct java_lang_Runtime* p0, s32 p1){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {3, arr___labtab};
    
    StackItem local[4] = {0};
    RStackItem rlocal[4] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 1355, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    local[1].i = p1;
    ; 
    //  line no 56 , L1345743107 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 56;
    rstack[sp++].obj = rlocal[0].obj;
    stack[sp++].i = local[1].i;
    // invokespecial java/lang/Runtime.exitInternal(I)V
    {
        sp -= 2;
        if (!rstack[sp + 0].obj) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 2);
            __frame->bytecodeIndex = 0;//L1345743107
            __frame->lineNo = 56;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        Java_java_lang_Runtime_exitInternal__I_V(runtime, rstack[sp + 0].obj, stack[sp + 1].i);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    ; 
    //  line no 57 , L525460564 , bytecode index = 5
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

ExceptionItem arr_extable_Java_java_lang_Runtime__clinit____V[] = {
};
ExceptionTable extable_Java_java_lang_Runtime__clinit____V = {0, arr_extable_Java_java_lang_Runtime__clinit____V};

// locals: 0
// stack: 2
// args: 0
void Java_java_lang_Runtime__clinit____V(JThreadRuntime *runtime){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {3, arr___labtab};
    
    StackItem local[2] = {0};
    RStackItem rlocal[2] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 1359, &rstack[0], &rlocal[0], &sp);
    ; 
    //  line no 24 , L1835800276 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 24;
    // new java/lang/Runtime
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 224);
    // dup
    stack[sp].j = stack[sp - 1].j; 
    rstack[sp].obj = rstack[sp - 1].obj; 
    ++sp;
    // invokespecial java/lang/Runtime.<init>()V
    {
        sp -= 1;
        if (!rstack[sp + 0].obj) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 2);
            __frame->bytecodeIndex = 0;//L1835800276
            __frame->lineNo = 24;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        Java_java_lang_Runtime__init____V(runtime, rstack[sp + 0].obj);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Runtime currentRuntime Ljava/lang/Runtime;
    sp -= 1;
    static_var_java_lang_Runtime.currentRuntime_0 = rstack[sp].obj;
    method_exit(runtime);
    return;
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}



     
     
```     
     
     
     
