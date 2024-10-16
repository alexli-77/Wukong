import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class JavaassistInsertTest {
    public static void main(String[] args) throws Exception {

        // Create an instance of the modified class and invoke the method
        ListTest instance = new ListTest();
        instance.testList();

        // Obtain the default ClassPool
        ClassPool pool = ClassPool.getDefault();

        // Get the CtClass object for the class containing the method
        CtClass ctClass = pool.get("ListTest");

        // Get the specific method from the class
        CtMethod ctMethod = ctClass.getDeclaredMethod("testList");
//        ctMethod.insertBefore("{" +
//                "java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(" +
//                "new java.io.FileWriter(\"/Users/files/code/TestProjects/javaagent-test/target/text.txt\", true));" +
//                "bufferedWriter.write(\"Finished add method\\n\");" +
//                "}");
//        ctMethod.addLocalVariable("newVar1", CtClass.intType);
//        ctMethod.addLocalVariable("newVar2", CtClass.doubleType);

        // Use an ExprEditor to edit method calls within the method
        ctMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {

                if (m.getMethodName().equals("add")) {
                    System.out.println(m.getClassName() + " line number: " + m.getLineNumber());
                    // Insert logging before and after the method call
                    m.replace("{" +
                                    "java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(" +
                                    "new java.io.FileWriter(\"/Users/files/code/TestProjects/javaagent-test/target/text.txt\", true));" +
                                    "bufferedWriter.write(\"Finished add method\\n\");bufferedWriter.newLine();bufferedWriter.flush();" +
                                    "$_ = $proceed($$);" +
                                    "System.out.println(\"Calling add with: \" + $1); }");
                }
            }
        });
        // Load the modified class into JVM
        ctClass.debugWriteFile("/Users/files/code/TestProjects/javaagent-test/target/java.txt");
        ctClass.toBytecode();


    }
}
