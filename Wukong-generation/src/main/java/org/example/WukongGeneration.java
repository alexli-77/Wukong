package org.example;

import org.example.generators.TestGenerator;
import picocli.CommandLine;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.support.compiler.SpoonPom;

import java.nio.file.Path;
import java.util.concurrent.Callable;

enum TestFormat { xml, json }

@CommandLine.Command(
        name = "java -jar target/<XXXX-jar-with-dependencies.jar>",
        description = "XXXX test cases from serialized objects",
        usageHelpWidth = 200)
public class WukongGeneration implements Callable<Integer> {
    @CommandLine.Parameters(
            index = "0",
            paramLabel = "PATH",
            description = "Path of the Maven project")
    private Path projectPath;

    @CommandLine.Parameters(
            index = "1",
            paramLabel = "JSON_FILE",
            description = "Path to JSON_FILE")
    private Path methodJSONFilePath;

//    @CommandLine.Parameters(
//            index = "2",
//            paramLabel = "DIRECTORY_WITH_OBJECT_XML_FILES",
//            description = "Path to directory containing object XML files")
//    private Path objectXMLDirectoryPath;
//
//    @CommandLine.Option(
//            names = {"--format"},
//            defaultValue = "xml",
//            paramLabel = "TEST_FORMAT",
//            description = "Specify the string format that is used in the generated tests, " +
//                    "default: ${DEFAULT-VALUE}, candidates values: ${COMPLETION-CANDIDATES}")
    private TestFormat testFormat;

    @CommandLine.Option(
            names = {"-h", "--help"},
            description = "Display help/usage.",
            usageHelp = true)
    private boolean usageHelpRequested;

    public WukongGeneration() {}

    public Path getProjectPath() {
        return projectPath;
    }

    public WukongGeneration(final Path projectPath, final Path methodJSONFilePath,
                         final boolean help) {
        this.projectPath = projectPath;
        this.methodJSONFilePath = methodJSONFilePath;
        this.usageHelpRequested = help;
    }

    public Integer call() {
        if (usageHelpRequested) {
            return 1;
        }
        final String path = this.projectPath.toString();
        final String name = this.projectPath.getFileName().toString();
        testFormat = testFormat == null ? TestFormat.xml : testFormat;

        WukongGenLauncher wukongGenLauncher = new WukongGenLauncher();
        MavenLauncher launcher = wukongGenLauncher.getMavenLauncher(path, name);
        SpoonPom projectPom = launcher.getPomFile();

        CtModel model = wukongGenLauncher.buildSpoonModel(launcher);
        System.out.println("POM found at: " + projectPom.getPath());
        System.out.println("Number of Maven modules: " + projectPom.getModel().getModules().size());

        TestGenerator testGenerator = new TestGenerator(testFormat.toString(), launcher, false);
        System.out.println("Number of new test cases: " + testGenerator.process(model,
                methodJSONFilePath.toString()));

        System.out.println("Number of new test cases with mocks: " +
                testGenerator.getNumberOfTestCasesWithMocksGenerated());
//
//        // Save model in outputdir/
//
//        String outputDirectory = "./output/generated/" + name;
//        launcher.setSourceOutputDirectory(outputDirectory);
//        launcher.prettyprint();
        return 0;
    }

    public static void main(String[] args) {
//        int exitCode = new CommandLine(new PanktiGenMain()).execute(args);
//        System.exit(exitCode);
    }
}
