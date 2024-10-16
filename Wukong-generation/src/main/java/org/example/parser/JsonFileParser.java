package org.example.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.data.InstrumentedMethod;
import org.example.util.FileUtil;
import org.example.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class JsonFileParser {
    public static String signature;
    public static String parentFQN;
    public static String result;
    public static String methodName;

    public static List<String> paramList = new ArrayList<>();

    public static List<String> paramValueList = new ArrayList<>();
    public static String className;
    public static String visibility = "public";
    public static String resultType;
    public static String paramValue;
    public static Class<?> parseParamType(String pType) {
        String subType = pType.substring(pType.indexOf("("),pType.indexOf(")")+1);
        if (subType.isEmpty())
            return null;
        switch (subType) {
            case "(Ljava/lang/Object;)":
                return Object.class;
            case "(Z)":
                return boolean.class;
            case "(B)":
                return byte.class;
            case "(C)":
                return char.class;
            case "(D)":
                return double.class;
            case "(F)":
                return float.class;
            case "(I)":
                return int.class;
            case "(J)":
                return long.class;
            case "(S)":
                return short.class;
            default:
                throw new IllegalStateException("Unexpected value: " + subType);
        }
    }

    public static List<InstrumentedMethod> readJsonFile(String jsonFilePath){
        List<InstrumentedMethod> instrumentedMethods = new ArrayList<>();
        FileUtil fileUtil = new FileUtil(jsonFilePath);
        String str = fileUtil.readLine();
        System.out.println(str);
        JsonNode jsonNode = JsonUtil.toObject(str);
        if (jsonNode.isArray()) {
            for (JsonNode jsonNode1 : jsonNode) {
                //TODO do not consider multiple parameters
                signature = String.valueOf(jsonNode1.get("signature"));
                Class<?> aclass = parseParamType(signature);
                System.out.println(aclass.toString());
                paramList.add(aclass.toString());
                result = String.valueOf(jsonNode1.get("result"));
                parentFQN = jsonNode1.get("parentFQN").textValue();
                methodName = String.valueOf(jsonNode1.get("methodName"));
                className = String.valueOf(jsonNode1.get("className"));
                resultType = String.valueOf(jsonNode1.get("resultType"));
                paramValue = String.valueOf(jsonNode1.get("paramValue"));
                paramValueList.add(paramValue);
                //TODO
                //no params,no visibility,no nestedmap
                instrumentedMethods.add(new InstrumentedMethod(parentFQN,methodName,paramList,paramValueList,resultType,result,visibility,null));
            }
        }
        return instrumentedMethods;
    }
}
