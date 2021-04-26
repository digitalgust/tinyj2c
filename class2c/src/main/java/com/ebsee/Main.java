package com.ebsee;


import com.ebsee.j2c.AssistLLVM;
import com.ebsee.j2c.Util;

import java.io.*;


/**
 *
 */
public class Main {

    static String[][] microDefFields = {
            {"java.lang.String", "value", "[C", "value_in_string"},
            {"java.lang.String", "count", "I", "count_in_string"},
            {"java.lang.String", "offset", "I", "offset_in_string"},
            {"java.lang.Class", "classHandle", "J", "classHandle_in_class"},
            {"java.lang.Class", "modifiers", "I", "modifiers_in_class"},
            {"java.lang.Class", "constructors", "[Ljava/lang/reflect/Constructor;", "constructors_in_class"},
            {"java.lang.Class", "fields", "[Ljava/lang/reflect/Field;", "fields_in_class"},
            {"java.lang.Class", "methods", "[Ljava/lang/reflect/Method;", "methods_in_class"},
            {"java.lang.Boolean", "value", "Z", "value_in_boolean"},
            {"java.lang.Byte", "value", "B", "value_in_byte"},
            {"java.lang.Short", "value", "S", "value_in_short"},
            {"java.lang.Character", "value", "C", "value_in_character"},
            {"java.lang.Integer", "value", "I", "value_in_integer"},
            {"java.lang.Long", "value", "J", "value_in_long"},
            {"java.lang.Float", "value", "F", "value_in_float"},
            {"java.lang.Double", "value", "D", "value_in_double"},

            {"java.lang.reflect.Method", "methodHandle", "J", "methodHandle_in_method"},
            {"java.lang.reflect.Method", "clazz", "Ljava/lang/Class;", "clazz_in_method"},
            {"java.lang.reflect.Method", "name", "Ljava/lang/String;", "name_in_method"},
            {"java.lang.reflect.Method", "desc", "Ljava/lang/String;", "desc_in_method"},
            {"java.lang.reflect.Method", "signature", "Ljava/lang/String;", "signature_in_method"},

            {"java.lang.reflect.Field", "fieldHandle", "J", "fieldHandle_in_field"},
            {"java.lang.reflect.Field", "clazz", "Ljava/lang/Class;", "clazz_in_field"},
            {"java.lang.reflect.Field", "name", "Ljava/lang/String;", "name_in_field"},
            {"java.lang.reflect.Field", "desc", "Ljava/lang/String;", "desc_in_field"},
            {"java.lang.reflect.Field", "signature", "Ljava/lang/String;", "signature_in_field"},

            {"java.lang.ref.WeakReference", "target", "Ljava/lang/Object;", "target_in_weakreference"},

            {"java.lang.Thread", "stackFrame", "J", "stackFrame_in_thread"},
            {"java.lang.StackTraceElement", "declaringClass", "Ljava/lang/String;", "declaringClass_in_stacktraceelement"},
            {"java.lang.StackTraceElement", "methodName", "Ljava/lang/String;", "methodName_in_stacktraceelement"},
            {"java.lang.StackTraceElement", "fileName", "Ljava/lang/String;", "fileName_in_stacktraceelement"},
            {"java.lang.StackTraceElement", "lineNumber", "I", "lineNumber_in_stacktraceelement"},
            {"java.lang.StackTraceElement", "parent", "Ljava/lang/StackTraceElement;", "parent_in_stacktraceelement"},
            {"java.lang.StackTraceElement", "declaringClazz", "Ljava/lang/Class;", "declaringClazz_in_stacktraceelement"},
    };

    public static void main(String[] args) throws IOException {
        String jsrcPath = "../app/java/"
//                + File.pathSeparator + "../../miniJVM/desktop/awtk_gui/java/src/main/java/"//
//                + File.pathSeparator + "../option_pack/file/src/main/java/"//
//                + File.pathSeparator + "../option_pack/luaj/core/src/"//
//                + File.pathSeparator + "../option_pack/luaj/luncher/src/"//
                ;
        String classesPath = "../app/out/classes/";
        String csrcPath = "../app/out/c/";


        if (args.length < 3) {
            System.out.println("Posix :");
            System.out.println("Convert java to c file:");
            System.out.println("java -cp ./class2ir/dist/class2c.jar com.ebsee.Main ./app/java ./app/out/classes ./app/out/c/");
        } else {
            jsrcPath = args[0] + "/";
            classesPath = args[1] + "/";
            csrcPath = args[2] + "/";
        }

        System.out.println("java source *.java path      : " + jsrcPath);
        System.out.println("classes *.class output path  : " + classesPath);
        System.out.println("c *.c output path            : " + csrcPath);

        File f;
        boolean res;
        f = new File(classesPath);
        System.out.println(f.getAbsolutePath() + (f.exists() ? " exists " : " not exists"));
        res = Util.deleteTree(f);
        res = f.mkdirs();
        f = new File(csrcPath);
        System.out.println(f.getAbsolutePath() + (f.exists() ? " exists " : " not exists"));
        res = Util.deleteTree(f);
        res = f.mkdirs();

        long startAt = System.currentTimeMillis();
        AssistLLVM.convert(jsrcPath, classesPath, csrcPath, microDefFields);
        System.out.println("convert success , cost :" + (System.currentTimeMillis() - startAt));
    }

}


