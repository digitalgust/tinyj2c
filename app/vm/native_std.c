//
// Created by Gust on 2020/5/20.
//

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#include <dirent.h>
#include <errno.h>
#include <stdlib.h>
#include <math.h>
#include <sys/stat.h>


#include "jvm.h"
#include "metadata.h"


#include "bytebuf.h"
#include "miniz_wrapper.h"
#include "garbage.h"


s32 jstring_2_utf8(struct java_lang_String *jstr, Utf8String *utf8) {
    if (!jstr)return 1;
    JArray *arr = jstr->value_in_string;
    if (arr) {
        s32 count = jstr->count_in_string;
        s32 offset = jstr->offset_in_string;
        u16 *arrbody = arr->prop.as_u16_arr;
        if (arr->prop.as_u16_arr)unicode_2_utf8(&arrbody[offset], utf8, count);
    }
    return 0;
}
//----------------------------------------------------------------
//               setter and getter implementation

void jstring_debug_print(JObject *jobj, c8 *appendix) {
    java_lang_String *jstr = (java_lang_String *) jobj;
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(jstr, ustr);
    jvm_printf("%s%s", utf8_cstr(ustr), appendix);
    utf8_destory(ustr);
}

void jstring_set_count(JObject *jobj, s32 count) {
    java_lang_String *jstr = (java_lang_String *) jobj;
    jstr->count_in_string = count;
}

void jthread_set_stackFrame(JObject *jobj, JThreadRuntime *runtime) {
    java_lang_Thread *jthread = (java_lang_Thread *) jobj;
    jthread->stackFrame_in_thread = (s64) (intptr_t) runtime;
}

void jclass_set_classHandle(JObject *jobj, JClass *clazz) {
    java_lang_Class *ins = (java_lang_Class *) jobj;
    ins->classHandle_in_class = (s64) (intptr_t) clazz;
}

void jclass_init_insOfClass(JThreadRuntime *runtime, JObject *jobj) {
    java_lang_Class *ins = (java_lang_Class *) jobj;
    Java_java_lang_Class__init____V(runtime, ins);
}
//----------------------------------------------------------------


JObject *buildStackElement(JThreadRuntime *runtime, StackFrame *target) {
    JClass *clazz = get_class_by_name_c(STR_JAVA_LANG_STACKTRACEELEMENT);
    if (clazz) {
        struct java_lang_StackTraceElement *ins = (__refer) new_instance_with_class(runtime, clazz);
        instance_hold_to_thread(runtime, ins);
        instance_init(runtime, (__refer) ins);
        MethodInfo *method = get_methodinfo_by_rawindex(target->methodRawIndex);

        //
        ins->declaringClass_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->clazz->name));
        ins->methodName_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->name));
        ins->fileName_in_stacktraceelement = (__refer) construct_string_with_cstr(runtime, utf8_cstr(method->clazz->source_name));
        ins->lineNumber_in_stacktraceelement = target->lineNo;
        if (target->next) {
            ins->parent_in_stacktraceelement = (__refer) buildStackElement(runtime, target->next);
        }
        ins->declaringClazz_in_stacktraceelement = (__refer) ins_of_Class_create_get(runtime, method->clazz);
        instance_release_from_thread(runtime, ins);
        return (__refer) ins;
    }
    return NULL;
}
//----------------------------------------------------------------


//=================================  assist ====================================

//native methods
void Java_java_io_PrintStream_printImpl__Ljava_lang_String_2I_V(JThreadRuntime *runtime, struct java_lang_String *p0, s32 p1) {
    if (p0) {
        jstring_debug_print((JObject *) p0, p1 ? "\n" : "");
    } else {
        if (p1)printf("\n");
    }
}


struct java_lang_Class *Java_java_lang_Class_forName__Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    JClass *cl = NULL;
    if (p0) {
        Utf8String *ustr = utf8_create();
        jstring_2_utf8(p0, ustr);
        utf8_replace_c(ustr, ".", "/");
        class_clinit(runtime, ustr);
        cl = get_class_by_name(ustr);
        utf8_destory(ustr);
        if (!cl) {
            JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_CLASS_NOT_FOUND_EXCEPTION);
            instance_init(runtime, exception);
            throw_exception(runtime, exception);
        } else {
            JObject *ins = ins_of_Class_create_get(runtime, cl);
            return (__refer) ins;
        }
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_NULL_POINTER_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return NULL;
}


struct java_lang_String *Java_java_lang_Class_getName___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    if (cl) {
        Utf8String *ustr = utf8_create_copy(cl->name);
        utf8_replace_c(ustr, "/", ".");
        JObject *ins = construct_string_with_cstr(runtime, utf8_cstr(ustr));
        utf8_destory(ustr);
        return (__refer) ins;
    } else {
        return NULL;
    }
}


struct java_lang_Class *Java_java_lang_Class_getSuperclass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    if (p0) {
        JClass *scl = getSuperClass((__refer) (intptr_t) p0->classHandle_in_class);
        if (!scl) return NULL;
        JObject *ins = ins_of_Class_create_get(runtime, scl);
        return (__refer) ins;
    } else {
        return NULL;
    }
}


struct java_lang_Class *Java_java_lang_Object_getClass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    JClass *cl = (__refer) p0->prop.clazz;
    return (__refer) ins_of_Class_create_get(runtime, cl);
}


struct java_lang_Object *Java_java_lang_Class_newInstance___Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    if (cl && !cl->prop.arr_type) {//class exists and not array class
        JObject *ins = new_instance_with_class(runtime, cl);
        instance_init(runtime, ins);
        return (__refer) ins;
    } else {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_INSTANTIATION_EXCEPTION);
        instance_init(runtime, exception);
        throw_exception(runtime, exception);
    }
    return NULL;
}


s32 Java_java_lang_Object_hashCode___I(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    u64 a = (u64) (intptr_t) p0;
    s32 h = (s32) (a ^ (a >> 32));
    return h;
}


void Java_java_lang_Object_notify___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notify(&p0->prop);
}


void Java_java_lang_Object_notifyAll___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notifyAll(&p0->prop);
}


void Java_java_lang_Object_wait__J_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s64 p1) {
    jthread_waitTime(&p0->prop, runtime, p1);
}


s8 Java_java_lang_Class_isAssignableFrom__Ljava_lang_Class_2_Z(JThreadRuntime *runtime, struct java_lang_Class *p0, struct java_lang_Class *p1) {
    JClass *c0 = (__refer) (intptr_t) p0->classHandle_in_class;
    JClass *c1 = (__refer) (intptr_t) p1->classHandle_in_class;

    return assignable_from(c1, c0);
}


void Java_java_lang_System_arraycopy__Ljava_lang_Object_2ILjava_lang_Object_2II_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s32 p1, struct java_lang_Object *p2, s32 p3, s32 p4) {
    struct java_lang_Object *src = p0;
    struct java_lang_Object *dst = p2;
    s32 srcPos = p1;
    s32 dstPos = p3;
    s32 len = p4;
    if (src == NULL || dst == NULL) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_NULL_POINTER_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else if (src->prop.type != INS_TYPE_ARRAY
               || dst->prop.type != src->prop.type) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ILLEGAL_ARGUMENT_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else if (srcPos < 0 || srcPos + len > src->prop.arr_length
               || dstPos < 0 || dstPos + len > dst->prop.arr_length
            ) {
        JObject *exception = new_instance_with_name(runtime, STR_JAVA_LANG_ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
        instance_init(runtime, exception);
        runtime->exception = exception;
        return;
    } else {
        s32 bytes = data_type_bytes[src->prop.clazz->array_cell_type];
        memmove(&dst->prop.as_s8_arr[dstPos * bytes], &src->prop.as_s8_arr[srcPos * bytes], len * bytes);
    }
}


s64 Java_java_lang_System_currentTimeMillis___J(JThreadRuntime *runtime) {
    return currentTimeMillis();
}


struct java_lang_String *Java_java_lang_System_doubleToString__D_Ljava_lang_String_2(JThreadRuntime *runtime, f64 p0) {
    char buf[100];
    sprintf(buf, "%lf", p0);
    Utf8String *ustr = utf8_create_c(buf);
    JObject *jstr = construct_string_with_ustr(runtime, ustr);
    utf8_destory(ustr);
    return (struct java_lang_String *) jstr;
}


JArray *Java_java_lang_System_utf16ToUtf8__Ljava_lang_String_2__3B(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    s32 len = ustr->length;
    JArray *jarr = multi_array_create_by_typename(runtime, &len, 1, "[B");
    memcpy(jarr->prop.as_c8_arr, ustr->data, ustr->length);
    utf8_destory(ustr);
    return jarr;
}


struct java_lang_String *Java_java_lang_System_utf8ToUtf16___3BII_Ljava_lang_String_2(JThreadRuntime *runtime, JArray *p0, s32 p1, s32 p2) {
    Utf8String *ustr = utf8_create_part_c(p0->prop.as_c8_arr, p1, p2);
    JObject *jstr = construct_string_with_ustr(runtime, ustr);
    utf8_destory(ustr);
    return (struct java_lang_String *) jstr;
}


s64 Java_java_lang_System_nanoTime___J(JThreadRuntime *runtime) {
    return nanoTime();
}


f64 Java_java_lang_System_stringToDouble__Ljava_lang_String_2_D(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    double d = atof(utf8_cstr(ustr));
    utf8_destory(ustr);
    return d;
}


s32 Java_java_lang_Thread_activeCount___I(JThreadRuntime *runtime) {
    return g_jvm->thread_list->length;
}


struct java_lang_Thread *Java_java_lang_Thread_currentThread___Ljava_lang_Thread_2(JThreadRuntime *runtime) {
    return (__refer) runtime->jthread;
}


void Java_java_lang_Thread_interrupt0___V(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return;
}


s8 Java_java_lang_Thread_isAlive___Z(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    s32 i, imax;
    spin_lock(&g_jvm->thread_list->spinlock);
    for (i = 0, imax = g_jvm->thread_list->length; i < imax; i++) {
        JThreadRuntime *runtime = arraylist_get_value_unsafe(g_jvm->thread_list, i);
        if (runtime->jthread == (__refer) p0) {
            return runtime->thread_status != THREAD_STATUS_DEAD;
        }
    }
    spin_unlock(&g_jvm->thread_list->spinlock);
    return 0;
}


void Java_java_lang_Thread_setPriority0__I_V(JThreadRuntime *runtime, struct java_lang_Thread *p0, s32 p1) {
    p0->priority_0 = p1;
}


void Java_java_lang_Thread_sleep__J_V(JThreadRuntime *runtime, s64 p0) {
    jthread_sleep(runtime, p0);
}


void Java_java_lang_Thread_start___V(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    p0->stackFrame_in_thread = (s64) (intptr_t) jthread_start((JObject *) p0);
}


void Java_java_lang_Thread_yield___V(JThreadRuntime *runtime) {
    jthread_yield();
}


struct java_lang_StackTraceElement *Java_java_lang_Throwable_buildStackElement___Ljava_lang_StackTraceElement_2(JThreadRuntime *runtime, struct java_lang_Throwable *p0) {
    return (__refer) buildStackElement(runtime, runtime->tail);
}



