import org.example.WukongGenLauncher;
import org.example.generators.TestGenerator;
import org.junit.Test;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.support.compiler.SpoonPom;

import java.util.Collections;

public class TestGeneration {
    private static Factory factory;
    @Test
    public void testSpoon(){
        // Create a new Spoon Launcher
        Launcher launcher = new Launcher();
        Factory factory = launcher.getFactory();

        // Create a new class named "GeneratedClass"
        CtClass<?> ctClass = factory.Class().create("GeneratedClass");

        CtMethod<Void> method = factory.Method().create(ctClass, Collections.singleton(ModifierKind.PUBLIC),factory.Type().VOID_PRIMITIVE,"ATest",null,null);

        // Define the body of the method using a code snippet
        String methodBody = "{\n" +
                "    int i = 1;\n" +
                "    int j = 2;\n" +
                "    List<Integer> list = new ArrayList<>();\n" +
                "    list.add(i);\n" +
                "    list.add(j);\n" +
                "    System.out.println(list.remove(1));\n" +
                "    System.out.println(\"I'm in testList\");\n" +
                "    assertEquals(1, list.size());\n" +
                "}";

        // Set the body of the method
        CtCodeSnippetStatement snippet = factory.Code().createCodeSnippetStatement(methodBody);
        method.setBody(snippet);

        // Add the method to the class
        ctClass.addMethod(method);
        // Print out the generated class
        System.out.println(ctClass);
    }

    @Test
    public void testWukongGeneration() {
        final String path = "/Users/files/code/TestProjects/singleTestMethod/";
        final String name = "singleTestMethod";

        WukongGenLauncher wukongGenLauncher = new WukongGenLauncher();
        MavenLauncher launcher = wukongGenLauncher.getMavenLauncher(path, name);
        SpoonPom projectPom = launcher.getPomFile();

        CtModel model = wukongGenLauncher.buildSpoonModel(launcher);
        System.out.println("POM found at: " + projectPom.getPath());
        System.out.println("Number of Maven modules: " + projectPom.getModel().getModules().size());

        TestGenerator testGenerator = new TestGenerator("xml", launcher, false);
        System.out.println("Number of new test cases: " + testGenerator.process(model,
                "/Users/files/code/github/Wukong/Wukong-generation/src/test/resource/json.json"));

        System.out.println("Number of new test cases with mocks: " +
                testGenerator.getNumberOfTestCasesWithMocksGenerated());
    }
}
