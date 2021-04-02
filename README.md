
# java2c

Translate JAVA source to C source, generate standlone binary, with out any class file.
    
# Requirement
     
1. Jetbrain Idea for compile and run converter.    
2. cmake for compile c source , recommend Jetbrain Clion.     
3. JDK 8+.         
5. clang/gcc.     

     
# Steps    

* git clone https://github.com/digitalgust/java2c.git        
* Idea open /java2c/class2c , run translator.    
* Clion open /java2c/app , compile c source and run .      
* Run binary no java classes dependence.   

Or 
 
 run build script : /java2c/posix_build.sh  
     
# Translated java file Example   
    
* Java code:

```
package java.lang;

public class Float {

    private final float value = 0.0f;

    static public String toString(float val) {
        return System.doubleToString(val);
    }
}



```     
     
* converted C code:     
     
```     

// CLASS: java/lang/Float extends java/lang/Object
#include "metadata.h"


// generation
// globals
//struct java_lang_Float_static {;};
struct java_lang_Float_static static_var_java_lang_Float = {};


__refer arr_vmtable_java_lang_Float_from_java_lang_Float[] = {
};
__refer arr_vmtable_java_lang_Float_from_java_lang_Object[] = {
    Java_java_lang_Object_toString___Ljava_lang_String_2,  //0
    Java_java_lang_Object_wait__J_V  //1
};
VMTable vmtable_java_lang_Float[] = {
    {24, 0, arr_vmtable_java_lang_Float_from_java_lang_Float}, //0
    {4, 2, arr_vmtable_java_lang_Float_from_java_lang_Object}, //1
};


ExceptionItem arr_extable_Java_java_lang_Float__init____V[] = {
};
ExceptionTable extable_Java_java_lang_Float__init____V = {0, arr_extable_Java_java_lang_Float__init____V};

// locals: 1
// stack: 2
// args: 1
void Java_java_lang_Float__init____V(JThreadRuntime *runtime, struct java_lang_Float* p0){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 57, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 12 , L1720797452 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 12;
    rstack[sp++].obj = rlocal[0].obj;
    // invokespecial java/lang/Object.<init>()V
    sp -= 1;  //pop para
    // it's a empty method
    ; 
    //  line no 14 , L562561015 , bytecode index = 4
    rstack[sp++].obj = rlocal[0].obj;
    //  fconst 0
    stack[sp++].f = 0;
    // putfield java/lang/Float value F
    sp -= 2;
    ((struct java_lang_Float*)rstack[sp + 0].obj)->value_0 = stack[sp + 1].f;
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

ExceptionItem arr_extable_Java_java_lang_Float_toString__F_Ljava_lang_String_2[] = {
};
ExceptionTable extable_Java_java_lang_Float_toString__F_Ljava_lang_String_2 = {0, arr_extable_Java_java_lang_Float_toString__F_Ljava_lang_String_2};

// locals: 1
// stack: 2
// args: 1
struct java_lang_String* Java_java_lang_Float_toString__F_Ljava_lang_String_2(JThreadRuntime *runtime, f32 p0){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {3, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 58, &rstack[0], &rlocal[0], &sp);
    local[0].f = p0;
    ; 
    //  line no 17 , L567656864 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 17;
    stack[sp++].f = local[0].f;
    stack[sp - 1].d = (f64)stack[sp - 1].f; 
    ++sp;
    // invokestatic java/lang/System.doubleToString(D)Ljava/lang/String;
    {
        sp -= 2;
        rstack[sp].obj = Java_java_lang_System_doubleToString__D_Ljava_lang_String_2(runtime, stack[sp + 0].d);
        sp += 1;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    method_exit(runtime);
    return rstack[sp - 1].obj;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler_index(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}




     
     
```     
     
     
     
