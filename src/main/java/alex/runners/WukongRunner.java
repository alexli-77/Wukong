package alex.runners;

import alex.launchers.WukongLaunchers;
import alex.utils.CsvFileUtil;
import alex.utils.FileUtil;
import alex.utils.JsonUtil;
import picocli.CommandLine;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import alex.utils.CustomLogger;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.support.compiler.SpoonPom;

@CommandLine.Command(
        name = "java -jar target/<Wukong-version-jar-with-dependencies.jar>",
        description = "pankti converts application traces to tests",
        usageHelpWidth = 100)
public class WukongRunner  implements Callable<Integer> {
    private static final Logger LOGGER =
            CustomLogger.log(WukongRunner.class.getName());

    @CommandLine.Parameters(
            paramLabel = "PATH",
            description = "Path of the Maven project or project JAR")
    private Path projectPath;

    @CommandLine.Option(
            names = {"-v", "--void"},
            description = "Include void methods")
    private boolean includeVoidMethods;

    @CommandLine.Option(
            names = {"-s", "--source"},
            description = "Directory with source files")
    private boolean sourceDirectory;

    @CommandLine.Option(
            names = {"--report"},
            defaultValue = "false",
            description = "Generate nested method analysis report")
    private boolean generateReport;

    @CommandLine.Option(
            names = {"-h", "--help"},
            description = "Display help/usage",
            usageHelp = true)
    private boolean usageHelpRequested;

    @CommandLine.Option(
            names = {"--index"},
            description = "generate Invoked Library API statistics JSON")
    private String indexJson;


    @CommandLine.Option(
            names = {"--filter"},
            defaultValue = "false",
            description = "filter the target Java Library")
    private String libName;

    public WukongRunner() {
    }

    public Path getProjectPath() {
        return projectPath;
    }

    public WukongRunner(final Path projectPath, final boolean help) {
        this.projectPath = projectPath;
        this.usageHelpRequested = help;
    }

    public WukongRunner(final Path projectPath,
                      final boolean includeVoidMethods,
                      final boolean sourceDirectory,
                      final String indexJson,
                      final boolean help,
                      final boolean generateReport) {
        this.projectPath = projectPath;
        this.includeVoidMethods = includeVoidMethods;
        this.sourceDirectory = sourceDirectory;
        this.usageHelpRequested = help;
        this.indexJson = indexJson;
        this.generateReport = generateReport;
    }

    @Override
    public Integer call() {
        if (usageHelpRequested) {
            return 1;
        }

        final String path = this.projectPath.toString();
        final String name = this.projectPath.getFileName().toString();
        final String project = this.indexJson;

        WukongLaunchers wukongLaunchers = new WukongLaunchers();
        // Process project
        LOGGER.info(String.format("Processing project: %s", name));
        Launcher launcher;
        if (sourceDirectory) {
            launcher = wukongLaunchers.getLauncher(path, name);
        }
        else {
            if (path.endsWith(".jar")) {
                launcher = wukongLaunchers.getJarLauncher(path, name);
            } else {
                launcher = wukongLaunchers.getMavenLauncher(path, name);
                SpoonPom projectPom = ((MavenLauncher) launcher).getPomFile();
                LOGGER.info(String.format("POM found at: %s", projectPom.getPath()));
                LOGGER.info(String.format("Number of Maven modules: %s",
                        projectPom.getModel().getModules().size()));
            }
        }

        // Build Spoon model
        CtModel model = wukongLaunchers.buildSpoonModel(launcher);

        // Find number of methods in project
        LOGGER.info(String.format("Total number of methods: %s",
                wukongLaunchers.countMethods(model)));

        // load whitelist
        FileUtil fileUtil = new FileUtil("/Users/files/code/github/Wukong/src/main/resources/whitelist");
        Set<String> whitelistSet = fileUtil.readFileAsSet();

        Map<String, Integer> map = wukongLaunchers.countJavaAPIPerMethod(model,whitelistSet,includeVoidMethods,libName);
        System.out.println("includeVoidMethods : " + includeVoidMethods);

        LOGGER.info(String.format("the number of methods that have Java API %s",
                map.size()));
        LOGGER.info(String.format("the number of Invoked Java APIs %s",
                wukongLaunchers.getJavaApiCatagoryMap().size()));
        // get Java Api json file
        if (indexJson != null && !indexJson.isEmpty()){
            String jsonFileName = "./Java API documents-" + indexJson + ".json";
            wukongLaunchers.getDocumentsInJson(indexJson,jsonFileName);
        }
        // get Annotations
        Map<String,List> javaAnnotationsMap = wukongLaunchers.getJavaAnnotationsMap();
        FileUtil javaAnnotationsFileUtil = new FileUtil("javaAnnotationsMap.txt");

        for (String key:javaAnnotationsMap.keySet()){
            if (key.contains("@org.junit.Test")) {
                for (Object document : javaAnnotationsMap.get(key)){
                    if (!javaAnnotationsFileUtil.write(document.toString())){
                        break;
                    };
                }
            }
        }

        try {
            CsvFileUtil.createCSVFile(wukongLaunchers.getSortedApiCatagoryMap(), project);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        for (int i = 10; i >0; i--) {
//            LOGGER.info(String.format("Java API %s ",
//                    list.get(i).getKey() + " times: " + list.get(i).getValue()));
//        }
//        Instant start = Instant.now();
//        // Apply processor to model
//        Set<CtMethod<?>> candidateMethods =
//                wukongLaunchers.applyProcessor(model, includeVoidMethods);
//        Instant finish = Instant.now();
//        long timeElapsed = Duration.between(start, finish).toMillis();
//        LOGGER.info(String.format("Elapsed time (ms): %s", timeElapsed));
//        LOGGER.info(String.format("Number of extracted methods: %s",
//                candidateMethods.size()));
        // Save model in spooned/
        // launcher.prettyprint();

        return 0;
    }
    public static void main(final String[] args) {
        int exitCode =
                new CommandLine(new WukongRunner()).execute(args);
        System.exit(exitCode);
    }
}
