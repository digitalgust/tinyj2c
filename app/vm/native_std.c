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

#include "jni.h"
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
//----------------------------------------------------------------


//=================================  assist ====================================

//native methods
void Java_java_io_PrintStream_printImpl__Ljava_lang_String_2_V(JThreadRuntime *runtime, struct java_lang_String *p0) {
    jstring_debug_print((JObject *) p0, "");
}


void Java_java_lang_Object_wait__J_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s64 p1) {
    jthread_waitTime((InstProp *) p0, runtime, p1);
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
    char buf[100] = {0};
    sprintf(buf, "%lf", p0);
    Utf8String *ustr = utf8_create_c(buf);
    JObject *jstr = construct_string_with_ustr(runtime, ustr);
    utf8_destory(ustr);
    return (struct java_lang_String *) jstr;
}


s64 Java_java_lang_System_nanoTime___J(JThreadRuntime *runtime) {
    return nanoTime();
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





