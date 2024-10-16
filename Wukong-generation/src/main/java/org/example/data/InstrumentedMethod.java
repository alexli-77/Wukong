package org.example.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstrumentedMethod {
    String parentFQN;
    String parentSimpleName;
    String methodName;
    List<String> paramList;
    List<String> paramValueList;
    String returnType;
    String returnValue;
    String visibility;
    String nestedMethodMap;
    boolean isOverloaded;
    public InstrumentedMethod(
            String parentFQN,
            String methodName,
            List<String> paramList,
            List<String> paramValueList,
            String returnType,
            String returnValue,
            String visibility,
            String nestedMethodMap) {
        this.parentFQN = parentFQN;
        this.parentSimpleName = setParentSimpleName();
        this.methodName = methodName;
        this.paramList = paramList;
        this.returnType = returnType.replaceAll("\\$", ".");
        this.visibility = visibility;
        this.returnValue = returnValue;
        this.nestedMethodMap = nestedMethodMap;
    }

    public String getParentFQN() {
        return parentFQN;
    }

    public String setParentSimpleName() {
        List<String> components = List.of(parentFQN.split("\\."));
        return components.get(components.size() - 1);
    }

    public String getParentSimpleName() {
        return parentSimpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public List<String> getParamValueList() {
        return paramValueList;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public boolean hasParams() {
        return this.getParamList().size() > 0;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getFullMethodPath() {
        return this.parentFQN + "." + this.getMethodName();
    }

    public String getNestedMethodMap() {
        return this.nestedMethodMap;
    }

    public boolean isOverloaded() {
        return isOverloaded;
    }

    public void setOverloaded(boolean overloaded) {
        isOverloaded = overloaded;
    }


    @Override
    public String toString() {
        return "InstrumentedMethod{" +
                "parentFQN='" + parentFQN + '\'' +
                ", parentSimpleName='" + parentSimpleName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramList=" + paramList +
                ", returnType='" + returnType + '\'' +
                ", visibility='" + visibility + '\'' +
                '}';
    }
}
