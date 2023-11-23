package alex.processors;

import picocli.CommandLine;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.visitor.filter.AnnotationFilter;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;

import java.util.*;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "methodsDescription",
        description = "Process methods"
)
public class MethodsProcessor extends AbstractProcessor<CtMethod<?>> implements Callable<String> {

    final private boolean includeVoidMethods;
    List<CtMethod<?>> publicMethods = new ArrayList<>();
    List<CtMethod<?>> privateMethods = new ArrayList<>();
    List<CtMethod<?>> protectedMethods = new ArrayList<>();
    List<CtMethod<?>> abstractMethods = new ArrayList<>();
    List<CtMethod<?>> staticMethods = new ArrayList<>();
    List<CtMethod<?>> methodsWithSynchronization = new ArrayList<>();
    List<CtMethod<?>> emptyMethods = new ArrayList<>();
    List<CtMethod<?>> deprecatedMethods = new ArrayList<>();
    List<CtMethod<?>> methodsInAnnotationType = new ArrayList<>();
    Set<ModifierKind> allMethodModifiers = new HashSet<>();
    Set<CtMethod<?>> candidateMethods = new HashSet<>();

    public MethodsProcessor(boolean includeVoidMethods) {
        this.includeVoidMethods = includeVoidMethods;
    }

    @Override
    public String call() {
        return toString();
    }

    public Set<ModifierKind> getAllMethodModifiersInProject() {
        return allMethodModifiers;
    }

    // Find if method / parent class is @Deprecated
    public boolean isDeprecated(CtMethod<?> ctMethod) {
        AnnotationFilter<?> deprecationFilter = new AnnotationFilter<>(Deprecated.class);
        if (ctMethod.hasAnnotation(Deprecated.class) || (ctMethod.getParent(deprecationFilter) != null)) {
            deprecatedMethods.add(ctMethod);
            return true;
        }
        return false;
    }

    // Find if parent class of method is @interface
    public boolean parentHasInterfaceAnnotation(CtMethod<?> ctMethod) {
        ReferenceTypeFilter referenceTypeFilter = new ReferenceTypeFilter(CtAnnotationType.class);
        if (referenceTypeFilter.matches(ctMethod.getParent())) {
            methodsInAnnotationType.add(ctMethod);
            return true;
        }
        return false;
    }

    // Find if method has no statements
    public boolean isMethodEmpty(CtMethod<?> ctMethod) {
        // The body of an abstract method is null
        Optional<CtBlock<?>> methodBody = Optional.ofNullable(ctMethod.getBody());
        if (methodBody.isPresent() && methodBody.get().getStatements().size() == 0) {
            emptyMethods.add(ctMethod);
            return true;
        }
        return false;
    }

    // Find method modifiers
    public Set<ModifierKind> getMethodModifiers(CtMethod<?> ctMethod) {
        allMethodModifiers.addAll(ctMethod.getModifiers());
        if (ctMethod.getModifiers().contains(ModifierKind.ABSTRACT)) {
            abstractMethods.add(ctMethod);
        }
        if (ctMethod.getModifiers().contains(ModifierKind.STATIC)) {
            staticMethods.add(ctMethod);
        }
        if (ctMethod.getModifiers().contains(ModifierKind.SYNCHRONIZED)) {
            methodsWithSynchronization.add(ctMethod);
        }
        if (ctMethod.getModifiers().contains(ModifierKind.PUBLIC)) {
            publicMethods.add(ctMethod);
        } else if (ctMethod.getModifiers().contains(ModifierKind.PRIVATE)) {
            privateMethods.add(ctMethod);
        } else if (ctMethod.getModifiers().contains(ModifierKind.PROTECTED)) {
            protectedMethods.add(ctMethod);
        }
        return ctMethod.getModifiers();
    }

    public boolean isReturnTypeVoid(CtMethod<?> ctMethod) {
        return ctMethod.getType().getSimpleName().equals("void");
    }

    public boolean isParentPrivateStatic(CtMethod<?> ctMethod) {
        if (ctMethod.getParent() instanceof CtClass<?>) {
            CtClass<?> parent = (CtClass<?>) ctMethod.getParent();
            return parent.hasModifier(ModifierKind.PRIVATE) && (parent.hasModifier(ModifierKind.STATIC));
        }
        return false;
    }

    public boolean isParentNonClass(CtMethod<?> ctMethod) {
        return ctMethod.getParent(CtClass.class) == null;
    }

    public Set<CtMethod<?>> getCandidateMethods() {
        return candidateMethods;
    }

    @Override
    public void process(CtMethod<?> ctMethod) {
        Set<ModifierKind> methodModifiers = getMethodModifiers(ctMethod);
        if (methodModifiers.contains(ModifierKind.PUBLIC) &
                (includeVoidMethods || !isReturnTypeVoid(ctMethod)) &
                !methodModifiers.contains(ModifierKind.ABSTRACT) &
                !methodModifiers.contains(ModifierKind.STATIC) &
                !isMethodEmpty(ctMethod) &
                !isDeprecated(ctMethod) &
                !isParentNonClass(ctMethod) &
                !isParentPrivateStatic(ctMethod) &
                !parentHasInterfaceAnnotation(ctMethod)) {
            candidateMethods.add(ctMethod);
        }
    }

    @Override
    public String toString() {
        return "MethodProcessor{" +
                "publicMethods=" + publicMethods.size() +
                ", privateMethods=" + privateMethods.size() +
                ", protectedMethods=" + protectedMethods.size() +
                ", abstractMethods=" + abstractMethods.size() +
                ", staticMethods=" + staticMethods.size() +
                ", synchronizedMethods=" + methodsWithSynchronization.size() +
                ", emptyMethods=" + emptyMethods.size() +
                ", deprecatedMethods=" + deprecatedMethods.size() +
                ", methodsInAnnotationType=" + methodsInAnnotationType.size() +
                '}';
    }
}
