package alex.launchers;

import alex.processors.CandidateTrigger;
import alex.processors.MethodsProcessor;
import alex.utils.CustomLogger;
import alex.utils.FileUtil;
import alex.utils.JsonUtil;
import spoon.JarLauncher;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.*;
import java.util.logging.Logger;

public class WukongLaunchers {
    private static final Logger LOGGER = CustomLogger.log(WukongLaunchers.class.getName());
    private static Set<String> JavaApiSet = new HashSet<>();
    private static Map<String,Integer> JavaApiCatagoryMap = new HashMap<>();
    private static Map<String,List> JavaApiAnnotationsMap = new HashMap<>();

    private static String projectName;
//    public void setReportGeneration(boolean generateReport) {
//        MockableSelector.generateReport = generateReport;
//    }

    public Launcher getLauncher(final String projectPath, final String projectName) {
        WukongLaunchers.projectName = projectName;
        LOGGER.info("Invoking launcher for source directory");
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        return launcher;
    }

    public MavenLauncher getMavenLauncher(final String projectPath, final String projectName) {
        WukongLaunchers.projectName = projectName;
        LOGGER.info("Invoking launcher for Maven project");
        MavenLauncher launcher = new MavenLauncher(projectPath, MavenLauncher.SOURCE_TYPE.APP_SOURCE);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        return launcher;
    }

    public JarLauncher getJarLauncher(final String projectPath, final String projectName) {
        WukongLaunchers.projectName = projectName;
        LOGGER.info("Invoking launcher for JAR");
        JarLauncher launcher = new JarLauncher(projectPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        return launcher;
    }

    public CtModel buildSpoonModel(final Launcher launcher) {
        launcher.buildModel();
        return launcher.getModel();
    }

    public int countMethods(final CtModel model) {
        int numberOfMethodsInProject = 0;
        for (CtType<?> s : model.getAllTypes()) numberOfMethodsInProject += s.getMethods().size();
        return numberOfMethodsInProject;
    }

    public Map<String,Integer> countJavaAPIPerMethod(final CtModel model, Set<String> whitelist, final boolean includeVoidMethods) {
        Map<String, Integer> map = new HashMap<>();
        Set<String> deduplicateData = new HashSet<>();

        MethodsProcessor methodsProcessor = new MethodsProcessor(includeVoidMethods);
        model.processWith(methodsProcessor);
        Set<CtMethod<?>> candidateMethods = methodsProcessor.getCandidateMethods();
        LOGGER.info(String.format("Number of extracted methods: %s", candidateMethods.size()));
        int count;
        for (CtMethod<?> m :candidateMethods){
            count = 0;
            if (!m.getAnnotations().isEmpty()) {
                if (!(JavaApiAnnotationsMap.get(m.getAnnotations().get(0).toString()) == null)) {
                    JavaApiAnnotationsMap.get(m.getAnnotations().get(0).toString()).add(m.getSimpleName());
                } else {
                    JavaApiAnnotationsMap.put(m.getAnnotations().get(0).toString(),new ArrayList(){{add(m.getSimpleName());}});
                }
            }
            for(CtElement se : m.getElements(null)) {
                //filtering by whitelist
                if(whitelist.contains(se.toString())){
                    continue;
                }
                //filtering by keywords
                if (!se.toString().startsWith("java.") && !se.toString().startsWith("jdk.")) {
                    continue;
                }
                String apiName = se.toString().split("[\\(\\s\\<]")[0];
                //filter deduplicate
                if (deduplicateData.isEmpty() || !deduplicateData.contains(apiName)) {
                    count++;
                }
                //filter
                if (!JavaApiCatagoryMap.containsKey(apiName)) {
                    //store contents into a String
                    JavaApiCatagoryMap.put(apiName,1);
                    //System.out.println("elements name：" + apiName);
                } else {
                    JavaApiCatagoryMap.replace(apiName,JavaApiCatagoryMap.get(apiName)+1);
                }
            }
            if(count != 0) {
                map.put(m.getSimpleName(),count);
            }
            deduplicateData.clear();
            deduplicateData = new HashSet<>();
        }
        return map;
    }

    /**
     * get sorted Api catagory map
     * @return
     */
    public List<Map.Entry<String, Integer>> getSortedApiCatagoryMap(){

        List<Map.Entry<String, Integer>> list = new ArrayList<>(JavaApiCatagoryMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }
    /**
     * get invoked Library Apis in Json format
     * @param appName
     */
    public void getDocumentsInJson(String appName, String filename){
        List<String> documents = new ArrayList<>();
        Boolean writerflag = false;
        for (String key:JavaApiCatagoryMap.keySet()){
            Map<String,String> map = new HashMap<String,String>(){{
               put("apiName",key);
               put("app",appName);
            }};
            documents.add(JsonUtil.toJsonString(map));
        }
        FileUtil fileUtil = new FileUtil(filename);
        for (String document : documents){
            if (!fileUtil.write(document)){
                break;
            };
        }
        System.out.println("---------File writer finished-----------");
    }
    public void addMetaDataToCandidateMethods(Set<CtMethod<?>> candidateMethods) {
        for (CtMethod<?> candidateMethod : candidateMethods) {
            candidateMethod.putMetadata("pankti-target", true);
        }
    }

    public Set<String> getJavaApiSet() {
        return JavaApiSet;
    }

    public Map<String, List> getJavaAnnotationsMap() {
        return JavaApiAnnotationsMap;
    }

    public Map<String,Integer> getJavaApiCatagoryMap() {
        return JavaApiCatagoryMap;
    }

//    public void createCSVFile(Map<CtMethod<?>, Map<String, Boolean>> allMethodTags) throws IOException {
//        List<String> paramList;
//        try (FileWriter out = new FileWriter("./extracted-methods-" + projectName + ".csv");
//             CSVPrinter csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT
//                     .withHeader(HEADERS));
//        ) {
//            for (Map.Entry<CtMethod<?>, Map<String, Boolean>> entry : allMethodTags.entrySet()) {
//                CtMethod<?> method = entry.getKey();
//                StringBuilder paramSignature = new StringBuilder();
//                paramList = new ArrayList<>();
//                if (method.getParameters().size() > 0) {
//                    for (CtParameter<?> parameter : method.getParameters()) {
//                        String paramType = parameter.getType().getQualifiedName();
//                        paramList.add(paramType);
//                        paramSignature.append(MethodUtil.findMethodParamSignature(paramType));
//                    }
//                }
//                // Find nested method invocations that can be mocked
//                numberOfNestedInvocationsOnFieldsOrParameters += MockableSelector.getNumberOfNestedInvocations(method).size();
//                LinkedHashSet<NestedTarget> nestedMethodInvocations = MockableSelector.getNestedMethodInvocationSet(method);
//                int methodLOC = method.getBody().getStatements().size();
//                boolean isMockable = !nestedMethodInvocations.isEmpty() && methodLOC > 1;
//                csvPrinter.printRecord(
//                        method.getVisibility(),
//                        method.getParent(CtClass.class).getQualifiedName(),
//                        method.getSimpleName(),
//                        paramList,
//                        method.getType().getQualifiedName(),
//                        paramSignature.toString(),
//                        isMockable,
//                        nestedMethodInvocations);
//            }
//        }
//    }

    public Set<CtMethod<?>> applyProcessor(final CtModel model, final boolean includeVoidMethods) {
        // Filter out target methods and add metadata to them
        MethodsProcessor methodsProcessor = new MethodsProcessor(includeVoidMethods);
        model.processWith(methodsProcessor);
        LOGGER.info(methodsProcessor.toString());
        LOGGER.info(String.format(!includeVoidMethods ? "not %s" : "%s", "including void methods"));
        Set<CtMethod<?>> candidateMethods = methodsProcessor.getCandidateMethods();
        addMetaDataToCandidateMethods(candidateMethods);

        // Tag target methods based on their properties
        CandidateTrigger candidateTrigger = new CandidateTrigger();
        model.processWith(candidateTrigger);
        LOGGER.info(candidateTrigger.toString());

        Map<CtMethod<?>, Map<String, Boolean>> allMethodTags = candidateTrigger.getAllMethodTags();
//        try {
//            createCSVFile(allMethodTags);
//            if (MockableSelector.generateReport) {
//                NestedMethodAnalysis.createCSVFile();
//                LOGGER.info("Generated nested method analysis report ./nested-method-anlysis.csv");
//            }
//        } catch (IOException e) {
//            LOGGER.warning(e.getMessage());
//        }
//        LOGGER.info("Number of nested invocations on fields or parameters: "
//                + numberOfNestedInvocationsOnFieldsOrParameters);
//        LOGGER.info("Output saved in ./extracted-methods-" + projectName + ".csv");
        return candidateMethods;
    }
}
