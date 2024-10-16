package alex.transformers;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import alex.util.JsonUtil;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WukongTransformerImp implements ClassFileTransformer {

    public static List<Object> objectList = new ArrayList<>();

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//        System.out.println("classname before: " + className);
        Map<String, String> nestedObject = new HashMap<>();

        if (className.equals("ListTest")) {
            System.out.println("classname : " + className);
            try {
                ClassPool cp = ClassPool.getDefault();
                CtClass cc = cp.get(className);
                // Get the specific method from the class
                CtMethod ctMethod = cc.getDeclaredMethod("testList");
                // Use an ExprEditor to edit method calls within the method
                ctMethod.instrument(new ExprEditor() {
                    @Override
                    public void edit(MethodCall m) throws CannotCompileException {
                        // invoked method of a Library
                        if (m.getMethodName().equals("add")) {
                            nestedObject.put("className", m.getClassName());
                            nestedObject.put("methodName", m.getMethodName());
                            System.out.println(m.getClassName() + "." + m.getMethodName() + " line number: " + m.getLineNumber() + " " + m.getSignature());
                            // Insert logging before and after the method call
                            m.replace("{" +
                                    "$_ = $proceed($$);" +
                                    "System.out.println(\"Calling add with: \" + $1); }" +
                                    "java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(" +
                                    "new java.io.FileWriter(\"/Users/files/code/TestProjects/javaagent-test/target/text.txt\", false));" +
                                    "bufferedWriter.write(\"Alex_\" + $sig + \"_\" + $1 + \"_\" +  $_ + \"_\" + $type);bufferedWriter.newLine();bufferedWriter.flush();");
                        }
                    }
                });
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/files/code/TestProjects/javaagent-test/target/text.txt"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strings = line.split("_");
                        nestedObject.put("paramType",strings[1].substring(0,strings[1].indexOf(";")));
                        nestedObject.put("paramValue",strings[2]);
                        nestedObject.put("result",strings[3]);
                        nestedObject.put("resultType",strings[4]);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String jsonOutput = JsonUtil.toJsonString(nestedObject);
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("/Users/files/code/TestProjects/javaagent-test/target/json.json",false));
                    bufferedWriter.write(jsonOutput);
                    bufferedWriter.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Map<String, String> addBlock(Map<String, String> nestedObject, String key, String value) {



        return nestedObject;
    }

}
