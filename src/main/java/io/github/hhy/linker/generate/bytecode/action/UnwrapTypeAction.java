package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import io.github.hhy.linker.runtime.RuntimeUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * <p>UnwrapTypeAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class UnwrapTypeAction implements Action {
    private final VarInst obj;
    private final Type primitiveType;

    /**
     * <p>Constructor for UnwrapTypeAction.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     */
    public UnwrapTypeAction(VarInst obj) {
        this.obj = obj;
        this.primitiveType = AsmUtil.getPrimitiveType(obj.getType());
    }

    /**
     * <p>Constructor for UnwrapTypeAction.</p>
     *
     * @param obj a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @param primitiveType a {@link org.objectweb.asm.Type} object.
     */
    public UnwrapTypeAction(VarInst obj, Type primitiveType) {
        this.obj = obj;
        this.primitiveType = primitiveType;
    }

    /** {@inheritDoc} */
    @Override
    public void apply(MethodBody body) {
        obj.apply(body);

        MethodVisitor mv = body.getWriter();
        switch (primitiveType.getSort()) {
            case Type.BYTE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapByte", RuntimeUtil.UNWRAP_BYTE_DESC, false);
                break;
            case Type.SHORT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapShort", RuntimeUtil.UNWRAP_SHORT_DESC, false);
                break;
            case Type.INT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapInt", RuntimeUtil.UNWRAP_INT_DESC, false);
                break;
            case Type.LONG:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapLong", RuntimeUtil.UNWRAP_LONG_DESC, false);
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapFloat", RuntimeUtil.UNWRAP_FLOAT_DESC, false);
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapDouble", RuntimeUtil.UNWRAP_DOUBLE_DESC, false);
                break;
            case Type.CHAR:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapChar", RuntimeUtil.UNWRAP_CHAR_DESC, false);
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, RuntimeUtil.OWNER, "unwrapBool", RuntimeUtil.UNWRAP_BOOL_DESC, false);
                break;
            default:
                break;
        }
    }
}
