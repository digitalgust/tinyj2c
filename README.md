
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
//struct java_lang_Boolean_static {struct java_lang_Class* TYPE_0; struct java_lang_Boolean* TRUE_1; struct java_lang_Boolean* FALSE_2;};
struct java_lang_Boolean_static static_var_java_lang_Boolean = {NULL, NULL, NULL};


__refer arr_vmtable_java_lang_Boolean_from_java_lang_Boolean[] = {
    func_java_lang_Boolean_toString___Ljava_lang_String_2,  //0
    func_java_lang_Boolean_booleanValue___Z  //1
};
__refer arr_vmtable_java_lang_Boolean_from_java_lang_Object[] = {
    func_java_lang_Boolean_toString___Ljava_lang_String_2,  //0
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
VMTable vmtable_java_lang_Boolean[] = {
    {9, 2, arr_vmtable_java_lang_Boolean_from_java_lang_Boolean}, //0
    {1, 10, arr_vmtable_java_lang_Boolean_from_java_lang_Object}, //1
};



// locals: 2
// stack: 2
// args: 2
void func_java_lang_Boolean__init___Z_V(JThreadRuntime *runtime, struct java_lang_Boolean* p0, s8 p1){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[4] = {0};
    RStackItem rlocal[4] = {0};
    StackItem stack[4];
    RStackItem rstack[4] = {0};
    s16 sp=0;
    StackFrame *__frame = method_enter(runtime, 16, &rstack[0], &rlocal[0], NULL);
    rlocal[0].obj = p0;
    local[1].i = p1;
    ; 
    //  line no 19 , L1980953477 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 19;
    rstack[sp++].obj = rlocal[0].obj;
    // invokespecial java/lang/Object.<init>()V
    sp -= 1;  //pop para
    // it's a empty method
    ; 
    //  line no 20 , L856495791 , bytecode index = 4
    rstack[sp++].obj = rlocal[0].obj;
    stack[sp++].i = local[1].i;
    // putfield java/lang/Boolean value Z
    sp -= 2;
    ((struct java_lang_Boolean*)rstack[sp + 0].obj)->value_3 = stack[sp + 1].i;
    ; 
    //  line no 21 , L24057252 , bytecode index = 9
    method_exit(runtime);
    return;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

void bridge_java_lang_Boolean__init___Z_V(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    func_java_lang_Boolean__init___Z_V(runtime, ins, para[0].i);
}


// locals: 1
// stack: 1
// args: 1
struct java_lang_String* func_java_lang_Boolean_toString___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_Boolean* p0){
    static __refer arr___labtab[] = {&&L1853939333, &&L389752004, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3];
    RStackItem rstack[3] = {0};
    s16 sp=0;
    StackFrame *__frame = method_enter(runtime, 17, &rstack[0], &rlocal[0], NULL);
    rlocal[0].obj = p0;
    ; 
    //  line no 24 , L895684328 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 24;
    rstack[sp++].obj = rlocal[0].obj;
    // getfield java/lang/Boolean value Z
    stack[sp - 1].i = ((struct java_lang_Boolean*)rstack[sp - 1].obj)->value_3;
    sp += 0;
    if(stack[--sp].i  == 0) goto L1853939333;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 38);
    goto L389752004;
    L1853939333:
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 40);
    L389752004:
    method_exit(runtime);
    return rstack[sp - 1].obj;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}

void bridge_java_lang_Boolean_toString___Ljava_lang_String_2(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    ret->obj = func_java_lang_Boolean_toString___Ljava_lang_String_2(runtime, ins);
}


// locals: 1
// stack: 1
// args: 1
s8 func_java_lang_Boolean_booleanValue___Z(JThreadRuntime *runtime, struct java_lang_Boolean* p0){
    static __refer arr___labtab[] = {};
    static LabelTable __labtab = {0, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3];
    RStackItem rstack[3] = {0};
    s16 sp=0;
    rlocal[0].obj = p0;
    ; 
    //  line no 28 , L204543210 , bytecode index = 0
    rstack[sp++].obj = rlocal[0].obj;
    // getfield java/lang/Boolean value Z
    stack[sp - 1].i = ((struct java_lang_Boolean*)rstack[sp - 1].obj)->value_3;
    sp += 0;
    return stack[sp - 1].i;
    ; 
}

void bridge_java_lang_Boolean_booleanValue___Z(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    ret->i = func_java_lang_Boolean_booleanValue___Z(runtime, ins);
}


// locals: 1
// stack: 1
// args: 1
struct java_lang_Boolean* func_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2(JThreadRuntime *runtime, s8 p0){
    static __refer arr___labtab[] = {&&L1237773898, &&L1305748956, &&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[3];
    RStackItem rstack[3] = {0};
    s16 sp=0;
    StackFrame *__frame = method_enter(runtime, 19, &rstack[0], &rlocal[0], NULL);
    local[0].i = p0;
    ; 
    //  line no 32 , L721322586 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 32;
    stack[sp++].i = local[0].i;
    if(stack[--sp].i  == 0) goto L1237773898;
    // getstatic java/lang/Boolean TRUE Ljava/lang/Boolean;
    rstack[sp].obj =static_var_java_lang_Boolean.TRUE_1;
    sp += 1;
    goto L1305748956;
    L1237773898:
    // getstatic java/lang/Boolean FALSE Ljava/lang/Boolean;
    rstack[sp].obj =static_var_java_lang_Boolean.FALSE_2;
    sp += 1;
    L1305748956:
    method_exit(runtime);
    return rstack[sp - 1].obj;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return NULL;
}

void bridge_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    ret->obj = func_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2(runtime, para[0].i);
}


// locals: 1
// stack: 2
// args: 1
s8 func_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z(JThreadRuntime *runtime, struct java_lang_String* p0){
    static __refer arr___labtab[] = {&&L1706125628, &&__ExceptionHandler, &&L39179479, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {4, arr___labtab};
    
    StackItem local[3] = {0};
    RStackItem rlocal[3] = {0};
    StackItem stack[4];
    RStackItem rstack[4] = {0};
    s16 sp=0;
    StackFrame *__frame = method_enter(runtime, 20, &rstack[0], &rlocal[0], NULL);
    rlocal[0].obj = p0;
    ; 
    //  line no 36 , L469459127 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 36;
    rstack[sp++].obj = rlocal[0].obj;
    if(rstack[--sp].obj  == NULL) goto L1706125628;
    rstack[sp++].obj = rlocal[0].obj;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 38);
    // invokevirtual java/lang/String.equalsIgnoreCase(Ljava/lang/String;)Z
    {
        sp -= 2;
        JObject *__ins = rstack[sp + 0].ins;
        if (!__ins) {
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 3);
            __frame->bytecodeIndex = 0;//L469459127
            __frame->lineNo = 36;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        s8 (*__func_p) (JThreadRuntime *runtime,struct java_lang_String*,struct java_lang_String*) = find_method(__ins->vm_table, 8, 11);
        stack[sp].i = __func_p(runtime, rstack[sp + 0].obj, rstack[sp + 1].obj);
        sp += 1;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    if(stack[--sp].i  == 0) goto L1706125628;
    // iconst_1
    stack[sp++].i = 1;
    goto L39179479;
    L1706125628:
    // iconst_0
    stack[sp++].i = 0;
    L39179479:
    method_exit(runtime);
    return stack[sp - 1].i;
    ; 
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return 0;
}

void bridge_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    ret->i = func_java_lang_Boolean_parseBoolean__Ljava_lang_String_2_Z(runtime, para[0].obj);
}


// locals: 0
// stack: 3
// args: 0
void func_java_lang_Boolean__clinit____V(JThreadRuntime *runtime){
    static __refer arr___labtab[] = {&&__ExceptionHandler, &&__ExceptionHandlerNotFound};
    static LabelTable __labtab = {2, arr___labtab};
    
    StackItem local[2] = {0};
    RStackItem rlocal[2] = {0};
    StackItem stack[5];
    RStackItem rstack[5] = {0};
    s16 sp=0;
    StackFrame *__frame = method_enter(runtime, 21, &rstack[0], &rlocal[0], NULL);
    ; 
    //  line no 13 , L524136404 , bytecode index = 0
    __frame->bytecodeIndex = 0; //first
    __frame->lineNo = 13;
    //  ldc 
    rstack[sp++].obj = construct_string_with_utfraw_index(runtime, 48);
    // invokestatic java/lang/Class.getPrimitiveClass(Ljava/lang/String;)Ljava/lang/Class;
    {
        sp -= 1;
        rstack[sp].obj = func_java_lang_Class_getPrimitiveClass__Ljava_lang_String_2_Ljava_lang_Class_2(runtime, rstack[sp + 0].obj);
        sp += 1;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Boolean TYPE Ljava/lang/Class;
    sp -= 1;
    static_var_java_lang_Boolean.TYPE_0 = rstack[sp].obj;
    ; 
    //  line no 14 , L862069640 , bytecode index = 8
    // new java/lang/Boolean
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 9);
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
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 3);
            __frame->bytecodeIndex = 8;//L862069640
            __frame->lineNo = 14;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        func_java_lang_Boolean__init___Z_V(runtime, rstack[sp + 0].obj, stack[sp + 1].i);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Boolean TRUE Ljava/lang/Boolean;
    sp -= 1;
    static_var_java_lang_Boolean.TRUE_1 = rstack[sp].obj;
    ; 
    //  line no 15 , L771996532 , bytecode index = 19
    // new java/lang/Boolean
    rstack[sp++].obj = new_instance_with_classraw_index(runtime, 9);
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
            rstack[sp++].obj = new_instance_with_classraw_index(runtime, 3);
            __frame->bytecodeIndex = 19;//L771996532
            __frame->lineNo = 15;
            throw_exception(runtime, rstack[sp - 1].obj);
            goto __ExceptionHandler;
        }
        func_java_lang_Boolean__init___Z_V(runtime, rstack[sp + 0].obj, stack[sp + 1].i);
        sp += 0;
        if (runtime->exception) {
            rstack[sp++].obj = runtime->exception;
            goto __ExceptionHandler;
        }
    }
    // putstatic java/lang/Boolean FALSE Ljava/lang/Boolean;
    sp -= 1;
    static_var_java_lang_Boolean.FALSE_2 = rstack[sp].obj;
    method_exit(runtime);
    return;
    __ExceptionHandler:
    goto  *find_exception_handler(runtime, &__labtab, &&__ExceptionHandlerNotFound);
    __ExceptionHandlerNotFound:
    method_exit(runtime);
    return ;
}

void bridge_java_lang_Boolean__clinit____V(JThreadRuntime *runtime, __refer ins, ParaItem *para, ParaItem *ret) {
    func_java_lang_Boolean__clinit____V(runtime);
}



     
```     
     
     
     
