package io.github.hhy.linker.define;

import io.github.hhy.linker.annotations.Target;
import io.github.hhy.linker.define.field.EarlyFieldRef;
import io.github.hhy.linker.define.field.FieldRef;
import io.github.hhy.linker.define.field.RuntimeFieldRef;
import io.github.hhy.linker.define.method.EarlyMethodRef;
import io.github.hhy.linker.define.method.MethodRef;
import io.github.hhy.linker.exceptions.ClassTypeNotMatchException;
import io.github.hhy.linker.exceptions.ParseException;
import io.github.hhy.linker.exceptions.VerifyException;
import io.github.hhy.linker.token.Token;
import io.github.hhy.linker.token.TokenParser;
import io.github.hhy.linker.token.Tokens;
import io.github.hhy.linker.util.ClassUtil;
import io.github.hhy.linker.util.ReflectUtil;
import io.github.hhy.linker.util.StringUtil;
import io.github.hhy.linker.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static io.github.hhy.linker.util.ClassUtil.getTypeDefines;

public class ClassDefineParse {

    private static final String FIRST_OBJ_NAME = "target";
    private static final TokenParser TOKEN_PARSER = new TokenParser();

    public static InvokeClassDefine parseClass(Class<?> define, ClassLoader classLoader) throws ParseException, ClassNotFoundException {
        Target.Bind bindAnno = define.getDeclaredAnnotation(Target.Bind.class);
        if (bindAnno == null || bindAnno.value().equals("")) {
            throw new VerifyException("use @Target.Bind specified a class");
        }
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        Class<?> targetClass = classLoader.loadClass(bindAnno.value());
        return doParseClass(define, targetClass, classLoader);
    }

    public static InvokeClassDefine doParseClass(Class<?> define, Class<?> targetClass, ClassLoader classLoader) throws ParseException, ClassNotFoundException {
        Map<String, String> typeDefines = getTypeDefines(define);
        List<MethodDefine> methodDefines = new ArrayList<>();

        EarlyFieldRef targetField = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, targetClass);
        for (Method defineMethod : define.getDeclaredMethods()) {
            methodDefines.add(parseMethod(targetField, classLoader, defineMethod, typeDefines));
        }

        InvokeClassDefine classDefine = new InvokeClassDefine();
        classDefine.define = define;
        classDefine.targetClass = targetClass;
        classDefine.methodDefines = methodDefines;
        return classDefine;
    }

    /**
     * @param targetClassRef
     * @param classLoader
     * @param defineMethod
     * @param typedDefines
     * @return
     */
    public static MethodDefine parseMethod(EarlyFieldRef targetClassRef, ClassLoader classLoader, Method defineMethod, Map<String, String> typedDefines) throws ClassNotFoundException {
        verify(defineMethod);

        String fieldExpr = null;
        io.github.hhy.linker.annotations.Field.Getter getter = defineMethod.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Getter.class);
        io.github.hhy.linker.annotations.Field.Setter setter = defineMethod.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Setter.class);
        if (getter != null) {
            fieldExpr = Util.getOrElseDefault(getter.value(), defineMethod.getName());
        } else if (setter != null) {
            fieldExpr = Util.getOrElseDefault(setter.value(), defineMethod.getName());
        }

        // copy
        typedDefines = new HashMap<>(typedDefines);
        typedDefines.putAll(getTypeDefines(defineMethod));

        String targetClassName = typedDefines.get(FIRST_OBJ_NAME);
        if (StringUtil.isNotEmpty(targetClassName) && !Objects.equals(targetClassName, targetClassRef.getFieldTypeName())) {
            targetClassRef = new EarlyFieldRef(null, null, FIRST_OBJ_NAME, classLoader.loadClass(targetClassName));
        }
        targetClassRef.setFullName(FIRST_OBJ_NAME);

        MethodDefine methodDefine = new MethodDefine(defineMethod);
        if (fieldExpr != null) {
            Tokens tokens = TOKEN_PARSER.parse(fieldExpr);
            methodDefine.fieldRef = parseFieldExpr(targetClassRef, classLoader, tokens, typedDefines);
        } else {
            io.github.hhy.linker.annotations.Method.Name methodNameAnn = defineMethod.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
            io.github.hhy.linker.annotations.Method.InvokeSuper superClass = defineMethod.getAnnotation(io.github.hhy.linker.annotations.Method.InvokeSuper.class);
            String methodName = Util.getOrElseDefault(methodNameAnn == null ? null : methodNameAnn.value(), defineMethod.getName());
            int i;
            if ((i = methodName.lastIndexOf('.')) != -1) {
                fieldExpr = methodName.substring(0, i);
                methodName = methodName.substring(i + 1);
            }
            methodDefine.methodRef = parseMethodExpr(targetClassRef, classLoader, defineMethod, methodName, TOKEN_PARSER.parse(fieldExpr), typedDefines);
        }
        return methodDefine;
    }

    private static MethodRef parseMethodExpr(EarlyFieldRef targetTargetRef, ClassLoader classLoader, Method defineMethod, String name, final Tokens fieldTokens, Map<String, String> typedDefines) throws ClassNotFoundException {
        FieldRef owner = parseFieldExpr(targetTargetRef, classLoader, fieldTokens, typedDefines);
        if (owner instanceof EarlyFieldRef) {
            Class<?> ownerClass = ((EarlyFieldRef) owner).getFieldTypeClass();
            List<Method> methods = ReflectUtil.getMethods(ownerClass, name);
            Method method = matchMethod(methods, defineMethod.getParameterTypes());
            if (method == null && typedDefines.containsKey(owner.getFullName())) {
                throw new ParseException("can not find method " + name + " in class " + ownerClass.getName());
            }
            return new EarlyMethodRef(owner, method);
        }
        return null;
    }

    private static Method matchMethod(List<Method> methods, Class<?>[] parameters) {
        List<Method> matches = new ArrayList<>();
        for (Method method : methods) {
            if (method.getParameters().length != parameters.length) continue;
            if (ClassUtil.deepEquals(method.getParameterTypes(), parameters)) {
                return method;
            }
            if (ClassUtil.polymorphismMatch(method.getParameterTypes(), parameters)) {
                matches.add(method);
            }
        }
        if (matches.size() > 0) return matches.get(0);
        return null;
    }

    /**
     * 解析目标字段
     *
     * @param targetFieldRef
     * @param tokens
     * @param typedDefines
     * @return
     */
    private static FieldRef parseFieldExpr(EarlyFieldRef targetFieldRef, ClassLoader classLoader, final Tokens tokens, Map<String, String> typedDefines) throws ClassNotFoundException {
        Class<?> currentType = targetFieldRef.getFieldTypeClass();
        FieldRef lastField = targetFieldRef;
        String fullField = null;
        for (Token token : tokens) {
            fullField = fullField == null ? token.value() : (fullField+"."+token.value());
            if (lastField instanceof RuntimeFieldRef) {
                lastField = new RuntimeFieldRef(lastField, lastField.fieldName, token.value());
                continue;
            }
            Field currentField = token.getField(currentType);
            currentType = currentField == null ? null : currentField.getType();

            Class<?> assignedType = null;
            if (typedDefines.containsKey(fullField)) {
                assignedType = classLoader.loadClass(typedDefines.get(fullField));
            }
            if (assignedType != null && currentField != null) {
                if (!ClassUtil.isAssignableFrom(assignedType, currentField.getType())) {
                    throw new ClassTypeNotMatchException(assignedType.getName(), currentField.getType().getName());
                }
                currentType = assignedType;
                lastField = new EarlyFieldRef(lastField, currentField);
            } else {
                lastField = currentField != null ? new EarlyFieldRef(lastField, currentField) : new RuntimeFieldRef(lastField, lastField.fieldName, token.value());
            }
            lastField.setFullName(fullField);
        }
        return lastField;
    }

    /**
     * 校验规则
     * <p>1. 无法为final字段生成setter</p>
     * <p>2. @Field.Setter和@Field.Getter只能有一个</p>
     * <p>3. @Field.Setter和@Field.Getter和@Method.Name只能有一个</p>
     * <p>4.</p>
     *
     * @param method
     * @param method
     */
    private static void verify(Method method) {
        io.github.hhy.linker.annotations.Field.Getter getter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Getter.class);
        io.github.hhy.linker.annotations.Field.Setter setter = method.getDeclaredAnnotation(io.github.hhy.linker.annotations.Field.Setter.class);
        // Field.Setter和@Field.Getter只能有一个
        if (getter != null && setter != null) {
            throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "] cannot have two annotations @Field.getter and @Field.setter");
        }
        if (getter != null) {
            if (StringUtil.isEmpty(getter.value())) {
                throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() == void.class || method.getParameters().length > 0) {
                throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method,its return value cannot be of type void, and the parameter length must be 0");
            }
        } else if (setter != null) {
            if (StringUtil.isEmpty(setter.value())) {
                throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "] is getter method, must be specified field-expression");
            }
            if (method.getReturnType() != void.class || method.getParameters().length != 1) {
                throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "] is setter method,its return value must be of type void, and the parameter length must be 1");
            }
        }

        io.github.hhy.linker.annotations.Method.Name methodNameAnn = method.getAnnotation(io.github.hhy.linker.annotations.Method.Name.class);
        // @Field 和 @Method相关的注解不能同时存在
        if ((getter != null || setter != null) & (methodNameAnn != null)) {
            throw new VerifyException("class [" + method.getDeclaringClass() + "@" + method.getName() + "], @Method.Name and @Field.Setter|@Field.Getter only one can exist");
        }
    }
}
