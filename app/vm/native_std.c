//
// Created by Gust on 2020/5/20.
//

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <math.h>


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

struct java_lang_String *utf8_2_jstring(JThreadRuntime *runtime, Utf8String *utf8) {
    if (!utf8)return NULL;
    return (__refer) construct_string_with_ustr(runtime, utf8);
}
//----------------------------------------------------------------
//               setter and getter implementation

void jstring_debug_print(int std, JObject *jobj, c8 *appendix) {
    java_lang_String *jstr = (java_lang_String *) jobj;
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(jstr, ustr);
    fprintf(std ? stderr : stdout, "%s%s", utf8_cstr(ustr), appendix);
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
    if (clazz->raw)ins->modifiers_in_class = clazz->raw->acc_flag;
}

void jclass_init_insOfClass(JThreadRuntime *runtime, JObject *jobj) {
    java_lang_Class *ins = (java_lang_Class *) jobj;
    func_java_lang_Class__init____V(runtime, ins);
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
void func_java_io_PrintStream_printImpl__ILjava_lang_String_2I_V(JThreadRuntime *runtime, s32 p0, struct java_lang_String *p1, s32 p2) {
    if (p1) {
        jstring_debug_print(p0, (JObject *) p1, p2 ? "\n" : "");
    } else {
        if (p2)fprintf(p0 ? stderr : stdout, "\n");
    }
}


struct java_lang_Class *func_java_lang_Class_forName__Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
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


struct java_lang_String *func_java_lang_Class_getName___Ljava_lang_String_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
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


struct java_lang_Class *func_java_lang_Class_getPrimitiveClass__Ljava_lang_String_2_Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_String *p0) {
    if (p0) {
        Utf8String *ustr = utf8_create();
        jstring_2_utf8(p0, ustr);
        JClass *cl = primitive_class_create_get(runtime, ustr);
        utf8_destory(ustr);
        return (__refer) cl->ins_of_Class;
    } else {
        return NULL;
    }
}


struct java_lang_Class *func_java_lang_Class_getSuperclass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    if (p0) {
        JClass *scl = getSuperClass((__refer) (intptr_t) p0->classHandle_in_class);
        if (!scl) return NULL;
        JObject *ins = ins_of_Class_create_get(runtime, scl);
        return (__refer) ins;
    } else {
        return NULL;
    }
}


void func_java_lang_Class_initReflect__Ljava_lang_Class_2_V(JThreadRuntime *runtime, struct java_lang_Class *p0) {

    JClass *cl = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    if (cl && !cl->prop.arr_type) {//class exists and not array class
        JArray *farr = p0->fields_in_class = multi_array_create_by_typename(runtime, &cl->fields->length, 1, "[Ljava/lang/reflect/Field;");
        s32 i, con = 0, mthd = 0;
        for (i = 0; i < cl->fields->length; i++) {
            FieldInfo *fi = arraylist_get_value(cl->fields, i);
            JObject *fobj = new_instance_with_name(runtime, "java/lang/reflect/Field");
            instance_init(runtime, fobj);
            farr->prop.as_obj_arr[i] = fobj;
            struct java_lang_reflect_Field *f = (struct java_lang_reflect_Field *) fobj;
            f->fieldHandle_in_field = (s64) (intptr_t) fi;
            f->clazz_in_field = p0;
            f->name_in_field = (__refer) construct_string_with_ustr(runtime, fi->name);
            f->signature_in_field = (__refer) construct_string_with_ustr(runtime, fi->signature);
            f->desc_in_field = (__refer) construct_string_with_ustr(runtime, fi->desc);
        }
        for (i = 0; i < cl->methods->length; i++) {
            MethodInfo *mi = arraylist_get_value(cl->methods, i);
            if (utf8_equals_c(mi->name, "<init>")) {
                con++;
            } else {
                mthd++;
            }
        }
        JArray *conarr = p0->constructors_in_class = multi_array_create_by_typename(runtime, &con, 1, "[Ljava/lang/reflect/Constructor;");
        JArray *mthdarr = p0->methods_in_class = multi_array_create_by_typename(runtime, &mthd, 1, "[Ljava/lang/reflect/Method;");
        con = mthd = 0;
        for (i = 0; i < cl->methods->length; i++) {
            MethodInfo *mi = arraylist_get_value(cl->methods, i);
            JObject *mobj;
            if (utf8_equals_c(mi->name, "<init>")) {
                mobj = new_instance_with_name(runtime, "java/lang/reflect/Constructor");
                conarr->prop.as_obj_arr[con] = mobj;
                con++;
            } else {
                mobj = new_instance_with_name(runtime, "java/lang/reflect/Method");
                mthdarr->prop.as_obj_arr[mthd] = mobj;
                mthd++;
            }
            instance_init(runtime, mobj);
            struct java_lang_reflect_Method *m = (struct java_lang_reflect_Method *) mobj;
            m->methodHandle_in_method = (s64) (intptr_t) mi;
            m->clazz_in_method = p0;
            m->name_in_method = (__refer) construct_string_with_ustr(runtime, mi->name);
            m->desc_in_method = (__refer) construct_string_with_ustr(runtime, mi->desc);
            m->signature_in_method = (__refer) construct_string_with_ustr(runtime, mi->signature);
        }
    }
    return;
}


s8 func_java_lang_Class_isArray___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *clazz = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    return clazz->array_cell_class != NULL;
}

s8 func_java_lang_Class_isAssignableFrom__Ljava_lang_Class_2_Z(JThreadRuntime *runtime, struct java_lang_Class *p0, struct java_lang_Class *p1) {
    JClass *c0 = (__refer) (intptr_t) p0->classHandle_in_class;
    JClass *c1 = (__refer) (intptr_t) p1->classHandle_in_class;

    return assignable_from(c1, c0);
}

s8 func_java_lang_Class_isInstance__Ljava_lang_Object_2_Z(JThreadRuntime *runtime, struct java_lang_Class *p0, struct java_lang_Object *p1) {
    return instance_of((InstProp *) p1, (__refer) (intptr_t) p0->classHandle_in_class);
}

s8 func_java_lang_Class_isInterface___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    ClassRaw *raw = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class)->raw;
    if (raw)return (s8) (raw->acc_flag & ACC_INTERFACE);
    return 0;//array
}

s8 func_java_lang_Class_isPrimitive___Z(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *clazz = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    return (s8) (clazz->primitive);
}

struct java_lang_Class *func_java_lang_Object_getClass___Ljava_lang_Class_2(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    JClass *cl = (__refer) p0->prop.clazz;
    return (__refer) ins_of_Class_create_get(runtime, cl);
}


struct java_lang_Object *func_java_lang_Object_clone___Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    return (__refer) instance_copy(runtime, (InstProp *) p0, 0);
}


struct java_lang_Object *func_java_lang_Class_newInstance___Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
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


s32 func_java_lang_Object_hashCode___I(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    u64 a = (u64) (intptr_t) p0;
    s32 h = (s32) (a ^ (a >> 32));
    return h;
}


void func_java_lang_Object_notify___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notify(&p0->prop);
}


void func_java_lang_Object_notifyAll___V(JThreadRuntime *runtime, struct java_lang_Object *p0) {
    jthread_notifyAll(&p0->prop);
}


void func_java_lang_Object_wait__J_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s64 p1) {
    jthread_waitTime(&p0->prop, runtime, p1);
}


s64 func_java_lang_Double_doubleToLongBits__D_J(JThreadRuntime *runtime, f64 p0) {
    StackItem si;
    si.d = p0;
    return si.j;
}

f64 func_java_lang_Double_longBitsToDouble__J_D(JThreadRuntime *runtime, s64 p0) {
    StackItem si;
    si.j = p0;
    return si.d;
}

s32 func_java_lang_Float_floatToIntBits__F_I(JThreadRuntime *runtime, f32 p0) {
    StackItem si;
    si.f = p0;
    return si.i;
}

f32 func_java_lang_Float_intBitsToFloat__I_F(JThreadRuntime *runtime, s32 p0) {
    StackItem si;
    si.i = p0;
    return si.f;
}

f64 func_java_lang_Math_acos__D_D(JThreadRuntime *runtime, f64 p0) {
    return acos(p0);
}

f64 func_java_lang_Math_asin__D_D(JThreadRuntime *runtime, f64 p0) {
    return asin(p0);
}

f64 func_java_lang_Math_atan__D_D(JThreadRuntime *runtime, f64 p0) {
    return atan(p0);
}

f64 func_java_lang_Math_atan2__DD_D(JThreadRuntime *runtime, f64 p0, f64 p1) {
    return atan2(p0, p1);
}

f64 func_java_lang_Math_ceil__D_D(JThreadRuntime *runtime, f64 p0) {
    return ceil(p0);
}

f64 func_java_lang_Math_cos__D_D(JThreadRuntime *runtime, f64 p0) {
    return cos(p0);
}

f64 func_java_lang_Math_exp__D_D(JThreadRuntime *runtime, f64 p0) {
    return exp(p0);
}

f64 func_java_lang_Math_floor__D_D(JThreadRuntime *runtime, f64 p0) {
    return floor(p0);
}

f64 func_java_lang_Math_log__D_D(JThreadRuntime *runtime, f64 p0) {
    return log(p0);
}

f64 func_java_lang_Math_pow__DD_D(JThreadRuntime *runtime, f64 p0, f64 p1) {
    return pow(p0, p1);
}

f64 func_java_lang_Math_sin__D_D(JThreadRuntime *runtime, f64 p0) {
    return sin(p0);
}

f64 func_java_lang_Math_sqrt__D_D(JThreadRuntime *runtime, f64 p0) {
    return sqrt(p0);
}

f64 func_java_lang_Math_tan__D_D(JThreadRuntime *runtime, f64 p0) {
    return tan(p0);
}


void func_java_lang_System_arraycopy__Ljava_lang_Object_2ILjava_lang_Object_2II_V(JThreadRuntime *runtime, struct java_lang_Object *p0, s32 p1, struct java_lang_Object *p2, s32 p3, s32 p4) {
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


s64 func_java_lang_System_currentTimeMillis___J(JThreadRuntime *runtime) {
    return currentTimeMillis();
}


struct java_lang_String *func_java_lang_System_doubleToString__D_Ljava_lang_String_2(JThreadRuntime *runtime, f64 p0) {
    char buf[100];
    sprintf(buf, "%lf", p0);
    Utf8String *ustr = utf8_create_c(buf);
    JObject *jstr = construct_string_with_ustr(runtime, ustr);
    utf8_destory(ustr);
    return (struct java_lang_String *) jstr;
}


JArray *func_java_lang_System_utf16ToUtf8__Ljava_lang_String_2__3B(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    s32 len = ustr->length;
    JArray *jarr = multi_array_create_by_typename(runtime, &len, 1, "[B");
    memcpy(jarr->prop.as_c8_arr, ustr->data, ustr->length);
    utf8_destory(ustr);
    return jarr;
}


struct java_lang_String *func_java_lang_System_utf8ToUtf16___3BII_Ljava_lang_String_2(JThreadRuntime *runtime, JArray *p0, s32 p1, s32 p2) {
    Utf8String *ustr = utf8_create_part_c(p0->prop.as_c8_arr, p1, p2);
    JObject *jstr = construct_string_with_ustr(runtime, ustr);
    utf8_destory(ustr);
    return (struct java_lang_String *) jstr;
}


s64 func_java_lang_System_nanoTime___J(JThreadRuntime *runtime) {
    return nanoTime();
}


f64 func_java_lang_System_stringToDouble__Ljava_lang_String_2_D(JThreadRuntime *runtime, struct java_lang_String *p0) {
    Utf8String *ustr = utf8_create();
    jstring_2_utf8(p0, ustr);
    double d = atof(utf8_cstr(ustr));
    utf8_destory(ustr);
    return d;
}


s32 func_java_lang_Thread_activeCount___I(JThreadRuntime *runtime) {
    return g_jvm->thread_list->length;
}


struct java_lang_Thread *func_java_lang_Thread_currentThread___Ljava_lang_Thread_2(JThreadRuntime *runtime) {
    return (__refer) runtime->jthread;
}


void func_java_lang_Thread_interrupt0___V(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    return;
}


s8 func_java_lang_Thread_isAlive___Z(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
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


void func_java_lang_Thread_setPriority0__I_V(JThreadRuntime *runtime, struct java_lang_Thread *p0, s32 p1) {
    p0->priority_0 = p1;
}


void func_java_lang_Thread_sleep__J_V(JThreadRuntime *runtime, s64 p0) {
    jthread_sleep(runtime, p0);
}


void func_java_lang_Thread_start___V(JThreadRuntime *runtime, struct java_lang_Thread *p0) {
    p0->stackFrame_in_thread = (s64) (intptr_t) jthread_start((JObject *) p0);
}


void func_java_lang_Thread_yield___V(JThreadRuntime *runtime) {
    jthread_yield();
}


struct java_lang_StackTraceElement *func_java_lang_Throwable_buildStackElement___Ljava_lang_StackTraceElement_2(JThreadRuntime *runtime, struct java_lang_Throwable *p0) {
    return (__refer) buildStackElement(runtime, runtime->tail);
}


struct java_lang_Object *func_java_lang_reflect_Array_multiNewArray__Ljava_lang_Class_2_3I_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0, JArray *p1) {
    JClass *cl = (__refer) (intptr_t) p0->classHandle_in_class;
    Utf8String *desc = utf8_create();
    if (cl->primitive) {
        utf8_pushback(desc, getDataTypeTagByName(cl->name));
    } else if (cl->prop.arr_type) {
        utf8_append(desc, cl->name);
    } else {
        utf8_append_c(desc, "L");
        utf8_append(desc, cl->name);
        utf8_append_c(desc, ";");
    }
    s32 i;
    for (i = 0; i < p1->prop.arr_length; i++) {
        utf8_insert(desc, 0, '[');
    }

    JArray *arr = multi_array_create_by_typename(runtime, p1->prop.as_s32_arr, p1->prop.arr_length, utf8_cstr(desc));
    utf8_destory(desc);
    return (__refer) arr;
}


struct java_lang_Object *func_java_lang_reflect_Constructor_newInstanceWithoutInit__Ljava_lang_Class_2_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_Class *p0) {
    JClass *cl = ((JClass *) (__refer) (intptr_t) p0->classHandle_in_class);
    if (cl && !cl->prop.arr_type) {//class exists and not array class
        JObject *ins = new_instance_with_class(runtime, cl);
        return (__refer) ins;
    }
    return NULL;
}


struct java_lang_Object *func_java_lang_reflect_Method_invoke__Ljava_lang_Object_2_3Ljava_lang_Object_2_Ljava_lang_Object_2(JThreadRuntime *runtime, struct java_lang_reflect_Method *p0, struct java_lang_Object *p1, JArray *p2) {
    MethodInfo *mi = (__refer) (intptr_t) p0->methodHandle_in_method;
#ifdef __JVM_OS_VS__
#define len  32
#else
    s32 len = p2->prop.arr_length;
#endif
    ParaItem para[len], ret;
    s32 i;
    //method para unboxing
    for (i = 0; i < mi->paratype->length; i++) {
        c8 ch = utf8_char_at(mi->paratype, i);
        switch (ch) {
            case 'L':
            case '[':
                para[i].obj = p2->prop.as_obj_arr[i];
                break;
            case 'I':
                para[i].i = ((struct java_lang_Integer *) p2->prop.as_obj_arr[i])->value_in_integer;
                break;
            case 'J':
                para[i].j = ((struct java_lang_Long *) p2->prop.as_obj_arr[i])->value_in_long;
                break;
            case 'C':
                para[i].i = ((struct java_lang_Character *) p2->prop.as_obj_arr[i])->value_in_character;
                break;
            case 'B':
                para[i].i = ((struct java_lang_Byte *) p2->prop.as_obj_arr[i])->value_in_byte;
                break;
            case 'F':
                para[i].f = ((struct java_lang_Float *) p2->prop.as_obj_arr[i])->value_in_float;
                break;
            case 'D':
                para[i].d = ((struct java_lang_Double *) p2->prop.as_obj_arr[i])->value_in_double;
                break;
            case 'S':
                para[i].i = ((struct java_lang_Short *) p2->prop.as_obj_arr[i])->value_in_short;
                break;
            case 'Z':
                para[i].i = ((struct java_lang_Boolean *) p2->prop.as_obj_arr[i])->value_in_boolean;
                break;
        }
    }
    //call
    mi->raw->bridge_ptr(runtime, p1, para, &ret);

    JObject *retobj = NULL;
    //method result boxing
    switch (utf8_char_at(mi->returntype, 0)) {
        case 'L':
        case '[':
            retobj = ret.obj;
            break;
        case 'I':
            retobj = (__refer) func_java_lang_Integer_valueOf__I_Ljava_lang_Integer_2(runtime, ret.i);
            break;
        case 'J':
            retobj = (__refer) func_java_lang_Long_valueOf__J_Ljava_lang_Long_2(runtime, ret.j);
            break;
        case 'C':
            retobj = (__refer) func_java_lang_Character_valueOf__C_Ljava_lang_Character_2(runtime, (u16) ret.i);
            break;
        case 'B':
            retobj = (__refer) func_java_lang_Byte_valueOf__B_Ljava_lang_Byte_2(runtime, (s8) ret.i);
            break;
        case 'F':
            retobj = (__refer) func_java_lang_Float_valueOf__F_Ljava_lang_Float_2(runtime, ret.f);
            break;
        case 'D':
            retobj = (__refer) func_java_lang_Double_valueOf__D_Ljava_lang_Double_2(runtime, ret.d);
            break;
        case 'S':
            retobj = (__refer) func_java_lang_Short_valueOf__S_Ljava_lang_Short_2(runtime, (s16) ret.i);
            break;
        case 'Z':
            retobj = (__refer) func_java_lang_Boolean_valueOf__Z_Ljava_lang_Boolean_2(runtime, ret.i);
            break;
        case 'V':
            break;

    }
    return (struct java_lang_Object *) retobj;
}

