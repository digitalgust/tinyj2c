
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
2. clang/gcc.     

     
# Steps    

* git clone https://github.com/digitalgust/tinyj2c.git        
* Idea open /tinyj2c/class2c , run translator.    
* Clion open /tinyj2c/app , compile c source and run .      
* Run binary no java classes dependence.   

Or 
 
 run build script : /tinyj2c/posix_build.sh  
     
# Translated java file Example   
    
* Java code:

```
package java.lang;


public class Boolean {
    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public String toString() {
        return value ? "true" : "false";
    }

    public boolean booleanValue() {
        return value;
    }

    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }

    public static boolean parseBoolean(String name) {
        return ((name != null) && name.equalsIgnoreCase("true"));
    }
}


```     
     
* converted C code:     
     
```     
// CLASS: java/lang/Boolean extends java/lang/Object
#include "metadata.h"


// generation
// globals
//struct java_lang_Boolean_static {struct java_lang_Boolean* TRUE_0; struct java_lang_Boolean* FALSE_1;};
struct java_lang_Boolean_static static_var_java_lang_Boolean = {NULL, NULL};


__refer arr_vmtable_java_lang_Boolean_from_java_lang_Boolean[] = {
    Java_java_lang_Boolean_toString___Ljava_lang_String_2,  //0
    Java_java_lang_Boolean_booleanValue___Z  //1
};
__refer arr_vmtable_java_lang_Boolean_from_java_lang_Object[] = {
    Java_java_lang_Boolean_toString___Ljava_lang_String_2,  //0
    Java_java_lang_Object_wait___V,  //1
    Java_java_lang_Object_finalize___V,  //2
    Java_java_lang_Object_getClass___Ljava_lang_Class_2,  //3
    Java_java_lang_Object_wait__J_V,  //4
    Java_java_lang_Object_notify___V,  //5
    Java_java_lang_Object_notifyAll___V,  //6
    Java_java_lang_Object_hashCode___I  //7
};
VMTable vmtable_java_lang_Boolean[] = {
    {33, 2, arr_vmtable_java_lang_Boolean_from_java_lang_Boolean}, //0
    {4, 8, arr_vmtable_java_lang_Boolean_from_java_lang_Object}, //1
};


ExceptionItem arr_extable_Java_java_lang_Boolean__init___Z_V[] = {
};
ExceptionTable extable_Java_java_lang_Boolean__init___Z_V = {0, arr_extable_Java_java_lang_Boolean__init___Z_V};

// locals: 2
// stack: 2
// args: 2
void Java_java_lang_Boolean__init___Z_V(JThreadRuntime *runtime, struct java_lang_Boolean* p0, s8 p1){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[4] = {0};
    RStackItem rlocal[4] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 97, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    local[1].i = p1;
    ; 
    //  line no 17 , L579227762 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 17;
    rstack[sp++].obj = rlocal[0].obj;
    // invokespecial java/lang/Object.<init>()V
    sp -= 1;  //pop para
    // it's a empty method
    ; 
    //  line no 18 , L1446188993 , bytecode index = 4
    rstack[sp++].obj = rlocal[0].obj;
    stack[sp++].i = local[1].i;
    // putfield java/lang/Boolean value Z
    sp -= 2;
    ((struct java_lang_Boolean*)rstack[sp + 0].obj)->value_2 = stack[sp + 1].i;
    ; 
    //  line no 19 , L1566390876 , bytecode index = 9
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

ExceptionItem arr_extable_Java_java_lang_Boolean_toString___Ljava_lang_String_2[] = {
};
ExceptionTable extable_Java_java_lang_Boolean_toString___Ljava_lang_String_2 = {0, arr_extable_Java_java_lang_Boolean_toString___Ljava_lang_String_2};

// locals: 1
// stack: 1
// args: 1
struct java_lang_String* Java_java_lang_Boolean_toString___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_Boolean* p0){
    static __refer arr___labtab[] = {&&L811287498, &&L73404487, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3] = {0};
    RStackItem rstack[3] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 98, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 22 , L2115555031 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 22;
    rstack[sp++].obj = rlocal[0].obj;
    // getfield java/lang/Boolean value Z
    stack[sp - 1].i = ((struct java_lang_Boolean*)rstack[sp - 1].obj)->value_2;
    sp += 0;
    if(stack[--sp].i  == 0) goto L811287498;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 457);
    goto L73404487;
    L811287498:
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 458);
    L73404487:
    method_exit(runtime);
    return rstack[sp - 1].obj;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}

ExceptionItem arr_extable_Java_java_lang_Boolean_booleanValue___Z[] = {
};
ExceptionTable extable_Java_java_lang_Boolean_booleanValue___Z = {0, arr_extable_Java_java_lang_Boolean_booleanValue___Z};

// locals: 1
// stack: 1
// args: 1
s8 Java_java_lang_Boolean_booleanValue___Z(JThreadRuntime *runtime, struct java_lang_Boolean* p0){
    static __refer arr___labtab[] = {};
    static LabelTable __labtab = {0, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3] = {0};
    RStackItem rstack[3] = {0};
    s32 sp=0;
    rlocal[0].obj = p0;
    ; 
    //  line no 26 , L973641395 , bytecode index = 0
    rstack[sp++].obj = rlocal[0].obj;
    // getfield java/lang/Boolean value Z
    stack[sp - 1].i = ((struct java_lang_Boolean*)rstack[sp - 1].obj)->value_2;
    sp += 0;
    return stack[sp - 1].i;
    ; 
}

ExceptionItem arr_extable_Java_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2[] = {
};
ExceptionTable extable_Java_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2 = {0, arr_extable_Java_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2};

// locals: 1
// stack: 1
// args: 1
struct java_lang_Boolean* Java_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2(JThreadRuntime *runtime, s8 p0){
    static __refer arr___labtab[] = {&&L572225495, &&L1557712937, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3] = {0};
    RStackItem rstack[3] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 100, &rstack[0], &rlocal[0], &sp);
    local[0].i = p0;
    ; 
    //  line no 30 , L1421940560 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 30;
    stack[sp++].i = local[0].i;
    if(stack[--sp].i  == 0) goto L572225495;
    // getstatic java/lang/Boolean TRUE Ljava/lang/Boolean;
    rstack[sp].obj =static_var_java_lang_Boolean.TRUE_0;
    sp += 1;
    goto L1557712937;
    L572225495:
    // getstatic java/lang/Boolean FALSE Ljava/lang/Boolean;
    rstack[sp].obj =static_var_java_lang_Boolean.FALSE_1;
    sp += 1;
    L1557712937:
    method_exit(runtime);
    return rstack[sp - 1].obj;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}

ExceptionItem arr_extable_Java_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z[] = {
};
ExceptionTable extable_Java_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z = {0, arr_extable_Java_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z};

// locals: 1
// stack: 2
// args: 1
s8 Java_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z(JThreadRuntime *runtime, struct java_lang_String* p0){
    static __refer arr___labtab[] = {&&L391333725, &&__ExceptionHandler, &&L219186182, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[4] = {0};
    RStackItem rstack[4] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 101, &rstack[0], &rlocal[0], &sp);
    rlocal[0].obj = p0;
    ; 
    //  line no 34 , L1854873748 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 34;
    rstack[sp++].obj = rlocal[0].obj;
    if(rstack[--sp].obj  == NULL) goto L391333725;
    rstack[sp++].obj = rlocal[0].obj;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 457);
    // invokevirtual java/lang/String.equalsIgnoreCase(Ljava/lang/String;)Z
    {
        sp -= 2;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 0);
            __frame->bytecodeIndex = 0;//L1854873748
            __frame->lineNo = 34;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        s8 (*__func_p) (JThreadRuntime *runtime,struct java_lang_String*,struct java_lang_String*) = find_method(__ins->vm_table, 14, 11);
        stack[sp].i = __func_p(runtime, rstack[sp + 0].obj, rstack[sp + 1].obj);
        sp += 1;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    if(stack[--sp].i  == 0) goto L391333725;
    // iconst_1
    stack[sp++].i = 1;
    goto L219186182;
    L391333725:
    // iconst_0
    stack[sp++].i = 0;
    L219186182:
    method_exit(runtime);
    return stack[sp - 1].i;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return 0;
}

ExceptionItem arr_extable_Java_java_lang_Boolean__clinit____V[] = {
};
ExceptionTable extable_Java_java_lang_Boolean__clinit____V = {0, arr_extable_Java_java_lang_Boolean__clinit____V};

// locals: 0
// stack: 3
// args: 0
void Java_java_lang_Boolean__clinit____V(JThreadRuntime *runtime){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[2] = {0};
    RStackItem rlocal[2] = {0};
    StackItem stack[5] = {0};
    RStackItem rstack[5] = {0};
    s32 sp=0;
    StackFrame *__frame = method_enter(runtime, 102, &rstack[0], &rlocal[0], &sp);
    ; 
    //  line no 12 , L1845527423 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 12;
    // new java/lang/Boolean
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 33);
    // dup
    stack[sp].j = stack[sp - 1].j; 
    rstack[sp].obj = rstack[sp - 1].obj; 
    ++sp;
    // iconst_1
    stack[sp++].i = 1;
    // invokespecial java/lang/Boolean.<init>(Z)V
    {
        sp -= 2;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 0);
            __frame->bytecodeIndex = 0;//L1845527423
            __frame->lineNo = 12;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        Java_java_lang_Boolean__init___Z_V(runtime, rstack[sp + 0].obj, stack[sp + 1].i);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Boolean TRUE Ljava/lang/Boolean;
    sp -= 1;
    static_var_java_lang_Boolean.TRUE_0 = rstack[sp].obj;
    ; 
    //  line no 13 , L1675905101 , bytecode index = 11
    // new java/lang/Boolean
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 33);
    // dup
    stack[sp].j = stack[sp - 1].j; 
    rstack[sp].obj = rstack[sp - 1].obj; 
    ++sp;
    // iconst_0
    stack[sp++].i = 0;
    // invokespecial java/lang/Boolean.<init>(Z)V
    {
        sp -= 2;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 0);
            __frame->bytecodeIndex = 11;//L1675905101
            __frame->lineNo = 13;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        Java_java_lang_Boolean__init___Z_V(runtime, rstack[sp + 0].obj, stack[sp + 1].i);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Boolean FALSE Ljava/lang/Boolean;
    sp -= 1;
    static_var_java_lang_Boolean.FALSE_1 = rstack[sp].obj;
    method_exit(runtime);
    return;
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}




     
     
```     
     
     
     
