package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Map;
import java.util.function.Consumer;

/**
 * The type Method builder.
 */
public class MethodBuilder {

    private AsmClassBuilder classBuilder;

    private MethodVisitor methodVisitor;

    private final String methodDesc;

    /**
     * Instantiates a new Method builder.
     *
     * @param classBuilder  the class builder
     * @param methodVisitor the method visitor
     * @param methodDesc    the method desc
     */
    public MethodBuilder(AsmClassBuilder classBuilder, MethodVisitor methodVisitor, String methodDesc) {
        this.classBuilder = classBuilder;
        this.methodVisitor = methodVisitor;
        this.methodDesc = methodDesc;
    }

    /**
     * Accept asm class builder.
     *
     * @param consumer the consumer
     * @return the asm class builder
     */
    public AsmClassBuilder accept(Consumer<MethodBody> consumer) {
        MethodBody body = new MethodBody(this.classBuilder, this.methodVisitor, Type.getMethodType(methodDesc));
        consumer.accept(body);
        this.methodVisitor.visitMaxs(0, 0); // auto
        return this.classBuilder;
    }

    public MethodBuilder addAnnotation(String descriptor, Map<String, Object> props) {
        AnnotationVisitor annotationVisitor = this.methodVisitor.visitAnnotation(descriptor, true);
        if (props != null && props.size() > 0) {
            for (Map.Entry<String, Object> kv : props.entrySet()) {
                annotationVisitor.visit(kv.getKey(), kv.getValue());
            }
        }
        annotationVisitor.visitEnd();
        return this;
    }

    /**
     * Gets class builder.
     *
     * @return the class builder
     */
    public AsmClassBuilder getClassBuilder() {
        return classBuilder;
    }

    /**
     * Gets method visitor.
     *
     * @return the method visitor
     */
    public MethodVisitor getMethodVisitor() {
        return methodVisitor;
    }
}
