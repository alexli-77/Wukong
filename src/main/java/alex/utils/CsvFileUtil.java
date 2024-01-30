package alex.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class CsvFileUtil {
    private static String[] HEADERS =
            {"Java API", "Counts"};
    public static void createCSVFile(List<Map.Entry<String, Integer>> invokedApiList, String projectName) throws IOException {
        List<String> paramList;
        try (FileWriter out = new FileWriter("./extracted-Java API-" + projectName + ".csv");
             CSVPrinter csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT
                     .withHeader(HEADERS));
        ) {
            for(Map.Entry<String, Integer> map : invokedApiList) {
                csvPrinter.printRecord(
                        map.getKey(),
                        map.getValue());
            }
        }
    }
}
