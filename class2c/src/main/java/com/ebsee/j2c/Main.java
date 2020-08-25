package com.ebsee.j2c;


import java.io.*;


/**
 *
 */
public class Main {


    public static void main(String[] args) throws IOException {
        String jsrcPath = "../miniJVM/minijvm/java/src/main/java/"
                + File.pathSeparator + "../miniJVM/test/minijvm_test/src/main/java/"//
//                + File.pathSeparator + "../miniJVM/mobile/java/glfm_gui/src/main/java"//
//                + File.pathSeparator + "../g3d/src/main/java/"//
                ;
        String classesPath = "./app/out/classes/";
        String csrcPath = "./app/out/c/";


        if (args.length < 3) {
            System.out.println("Posix :");
            System.out.println("Convert java to c file:");
            System.out.println("java -cp ./class2ir/dist/class2c.jar com.ebsee.j2c.Main ./app/java ./app/out/classes ./app/out/c/");
        } else {
            jsrcPath = args[0] + "/";
            classesPath = args[1] + "/";
            csrcPath = args[2] + "/";
        }

        System.out.println("java source *.java path      : " + jsrcPath);
        System.out.println("classes *.class output path  : " + classesPath);
        System.out.println("c *.c output path            : " + csrcPath);

        File f = new File(classesPath);
        f.delete();
        f.mkdirs();
        f = new File(csrcPath);
        f.delete();
        f.mkdirs();

        long startAt = System.currentTimeMillis();
        AssistLLVM.convert(jsrcPath, classesPath, csrcPath);
        System.out.println("convert success , cost :" + (System.currentTimeMillis() - startAt));
    }

}


