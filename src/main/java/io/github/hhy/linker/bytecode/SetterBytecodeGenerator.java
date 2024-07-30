package io.github.hhy.linker.bytecode;

import io.github.hhy.linker.asm.AsmClassBuilder;
import io.github.hhy.linker.util.ClassUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class SetterBytecodeGenerator implements BytecodeGenerator {

    private Type target;
    private String fieldName;
    private Type fieldType;


    public SetterBytecodeGenerator(Class<?> target, String fieldName, Class<?> fieldType) {
        this.target = Type.getType(target);
        this.fieldName = fieldName;
        this.fieldType = Type.getType(fieldType);
    }

    /**
     * 生成
     *
     * @param writer
     */
    public void generate(AsmClassBuilder classBuilder, MethodVisitor writer) {
        String implDesc = ClassUtil.className2path(classBuilder.getClassName());

        // 创建methodHandle
        String mhVar = fieldName+"_setter_mh";
        classBuilder.defineField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, mhVar,
                        "Ljava/lang/invoke/MethodHandle;", null, null)
                .writeClint((staticWriter) -> {
                    // mhVar = lookup.findGetter(target.class, "fieldName", fieldType.class);
                    staticWriter.visitFieldInsn(Opcodes.GETSTATIC, implDesc, "lookup", "Ljava/lang/invoke/MethodHandles$Lookup;");
                    staticWriter.visitLdcInsn(target);
                    staticWriter.visitLdcInsn(fieldName);
                    staticWriter.visitLdcInsn(fieldType);
                    staticWriter.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;");
                    staticWriter.visitFieldInsn(Opcodes.PUTSTATIC, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
                });

        // 调用 mh.invoke(target, obj)
        writer.visitFieldInsn(Opcodes.GETSTATIC, implDesc, mhVar, "Ljava/lang/invoke/MethodHandle;");
        writer.visitVarInsn(Opcodes.ALOAD, 0);
        writer.visitMethodInsn(Opcodes.INVOKEINTERFACE, "io/github/hhy/linker/define/TargetProvider", "getTarget", "()Ljava/lang/Object;");
        writer.visitTypeInsn(Opcodes.CHECKCAST, target.getInternalName());
        writer.visitVarInsn(Opcodes.ALOAD, 1); // obj
        writer.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", Type.getMethodDescriptor(Type.VOID_TYPE, target, fieldType));
        writer.visitInsn(Opcodes.RETURN);
    }
}
