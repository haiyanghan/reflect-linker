package io.github.hhy50.linker.asm;

import io.github.hhy50.linker.generate.bytecode.Member;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;


/**
 * The type Asm class builder.
 */
public class AsmClassBuilder {

    /**
     * The Class desc.
     */
    protected String classOwner;
    /**
     * The Class name.
     */
    protected String className;
    /**
     * The Clinit method writer.
     */
    protected MethodVisitor clinitMethodWriter;
    /**
     * The Class writer.
     */
    protected ClassWriter classWriter;

    /**
     * Instantiates a new Asm class builder.
     *
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public AsmClassBuilder(int access, String className, String superName, String[] interfaces, String signature) {
        this(COMPUTE_MAXS | COMPUTE_FRAMES, access, className, superName, interfaces, signature);
    }

    /**
     * Instantiates a new Asm class builder.
     *
     * @param asmFlags   the asm flags
     * @param access     the access
     * @param className  the class name
     * @param superName  the super name
     * @param interfaces the interfaces
     * @param signature  the signature
     */
    public AsmClassBuilder(int asmFlags, int access, String className, String superName, String[] interfaces, String signature) {
        this.className = className;
        this.classOwner = ClassUtil.className2path(className);
        this.classWriter = new ClassWriter(asmFlags);
        this.classWriter.visit(Opcodes.V1_8, access, this.classOwner, signature,
                superName != null ? ClassUtil.className2path(superName) : "java/lang/Object", Arrays.stream(interfaces == null ? new String[0] : interfaces).map(ClassUtil::className2path).toArray(String[]::new));
    }

    /**
     * Define field member.
     *
     * @param access         the access
     * @param fieldName      the field name
     * @param fieldType      the field type
     * @param fieldSignature the field signature
     * @param value          the value
     * @return the member
     */
    public Member defineField(int access, String fieldName, Type fieldType, String fieldSignature, Object value) {
        this.classWriter.visitField(access, fieldName, fieldType.getDescriptor(), fieldSignature, value);
        return new Member(access, classOwner, fieldName, fieldType);
    }

    /**
     * Define construct method builder.
     *
     * @param access     the access
     * @param argsType   the args type
     * @param exceptions the exceptions
     * @param sign       the sign
     * @return the method builder
     */
    public MethodBuilder defineConstruct(int access, String[] argsType, String[] exceptions, String sign) {
        String types = "";
        if (argsType != null && argsType.length > 0) {
            types = Arrays.stream(argsType).map(AsmUtil::toTypeDesc).collect(Collectors.joining());
        }
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, "<init>", "("+types+")V", sign, exceptions);
        return new MethodBuilder(this, methodVisitor, "("+types+")V");
    }

    /**
     * Define method method builder.
     *
     * @param access     the access
     * @param methodName the method name
     * @param methodDesc the method desc
     * @param exceptions the exceptions
     * @return the method builder
     */
    public MethodBuilder defineMethod(int access, String methodName, String methodDesc, String[] exceptions) {
        MethodVisitor methodVisitor = this.classWriter.visitMethod(access, methodName, methodDesc, null, exceptions);
        return new MethodBuilder(this, methodVisitor, methodDesc);
    }

    /**
     * Add annotation asm class builder.
     *
     * @param descriptor the descriptor
     * @param props      the props
     * @return the asm class builder
     */
    public AsmClassBuilder addAnnotation(String descriptor, Map<String, Object> props) {
        AnnotationVisitor annotationVisitor = this.classWriter.visitAnnotation(descriptor, true);
        if (props != null && props.size() > 0) {
            for (Map.Entry<String, Object> kv : props.entrySet()) {
                annotationVisitor.visit(kv.getKey(), kv.getValue());
            }
        }
        annotationVisitor.visitEnd();
        return this;
    }

    /**
     * End asm class builder.
     *
     * @return the asm class builder
     */
    public AsmClassBuilder end() {
        this.classWriter.visitEnd();
        if (this.clinitMethodWriter != null) {
            this.clinitMethodWriter.visitInsn(Opcodes.RETURN);
            this.clinitMethodWriter.visitMaxs(0, 0);
        }

        return this;
    }

    /**
     * To bytecode byte [ ].
     *
     * @return the byte [ ]
     */
    public byte[] toBytecode() {
        return classWriter.toByteArray();
    }

    /**
     * Gets class desc.
     *
     * @return the class desc
     */
    public String getClassOwner() {
        return this.classOwner;
    }

    /**
     * Gets class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return this.className;
    }
}
