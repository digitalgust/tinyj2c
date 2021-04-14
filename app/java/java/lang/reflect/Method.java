package java.lang.reflect;

import java.util.ArrayList;
import java.util.List;

public class Method {
    /*native vm methodinfo*/
    private long methodHandle;
    Class<?> clazz;
    String name;
    String desc;
    String signature;

    public boolean match(String name, Class<?>... parameterTypes) {
        if (!this.name.equals(name)) return false;
        int i = 0;
        String argsStr = desc.substring(1, desc.indexOf(')'));
        List<String> args = splitSignature(argsStr);
        if (parameterTypes.length != args.size()) return false;
        for (String s : args) {
            //System.out.println(name + " para:" + s);
            Class c = Class.getClassByDescriptor(s);
            if (c != parameterTypes[i++]) return false;
        }
        return true;
    }

    //Ljava/util/List<Ljava/lang/Object;>;BII
    public static List<String> splitSignature(String signature) {
        List<String> args = new ArrayList<>();
        //System.out.println("methodType:" + methodType);
        String s = signature;
        //从后往前拆分方法参数，从栈中弹出放入本地变量
        while (s.length() > 0) {
            char ch = s.charAt(0);
            String types = "";
            switch (ch) {
                case 'S':
                case 'C':
                case 'B':
                case 'I':
                case 'F':
                case 'Z':
                case 'D':
                case 'J': {
                    String tmps = s.substring(0, 1);
                    args.add(tmps);
                    s = s.substring(1);
                    break;
                }
                case 'L': {
                    int ltCount = 0;
                    int end = 1;
                    while (!((ch = s.charAt(end)) == ';' && ltCount == 0)) {// Ljava/util/List<Ljava/lang/Object;>;
                        if (ch == '<') ltCount++;
                        if (ch == '>') ltCount--;
                        end++;
                    }
                    end++;
                    String tmps = s.substring(0, end);
                    args.add(tmps);
                    s = s.substring(end);
                    break;
                }
                case '[': {
                    int end = 1;
                    while (s.charAt(end) == '[') {//去掉多维中的 [[[[LObject; 中的 [符
                        end++;
                    }
                    if (s.charAt(end) == 'L') {
                        int ltCount = 0;
                        while (!((ch = s.charAt(end)) == ';' && ltCount == 0)) {// Ljava/util/List<Ljava/lang/Object;>;
                            if (ch == '<') ltCount++;
                            if (ch == '>') ltCount--;
                            end++;
                        }
                        end++;
                    } else {
                        end++;
                    }
                    String tmps = s.substring(0, end);
                    args.add(tmps);
                    s = s.substring(end);
                    break;
                }
            }

        }

        return args;
    }

    public String getName() {
        return name;
    }

    public native Object invoke(Object ins, Object... args);

}
