package io.github.hhy.linker.generate;

import io.github.hhy.linker.asm.AsmUtil;
import io.github.hhy.linker.generate.bytecode.action.Action;
import io.github.hhy.linker.generate.bytecode.vars.LocalVarInst;
import io.github.hhy.linker.generate.bytecode.vars.ObjectVar;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MethodBody {
    private final MethodVisitor writer;
    private Type methodType;
    public int lvbIndex;
    private VarInst[] args;

    // 构建语句
    private List<Action> interceptors;
    // ============================ Labels ===================================
    private Label checkLookupLabel;


    public MethodBody(MethodVisitor mv, Type methodType) {
        Type[] argumentTypes = methodType.getArgumentTypes();
        this.writer = mv;
        this.methodType = methodType;
        this.lvbIndex = AsmUtil.calculateLvbOffset(false, argumentTypes);
        this.args = new VarInst[argumentTypes.length];

        initArgsTable(argumentTypes);
    }

    private void initArgsTable(Type[] argumentTypes) {
        int index = 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            args[i] = new ObjectVar(index++, argumentTypes[i]);
            if (argumentTypes[i].getSort() == Type.DOUBLE || argumentTypes[i].getSort() == Type.LONG) {
                index++;
            }
        }
    }

    public void append(Consumer<MethodVisitor> interceptor) {
        interceptor.accept(this.writer);
    }

    public void append(Supplier<Action> interceptor) {
        Action action = interceptor.get();
        action.apply(this);
    }

    /**
     * 根据参数生命的顺序， 获取第几个参数
     *
     * @param i
     * @return
     */
    public VarInst getArg(int i) {
        return args[i];
    }

    public void loadArgs() {
        for (int i = 0; i < methodType.getArgumentTypes().length; i++) {
            getArg(i).load(this);
        }
    }

    public Label getCheckLookupLabel() {
        if (checkLookupLabel == null) {
            checkLookupLabel = new Label();
        }
        return checkLookupLabel;
    }

    public void setArg(int i, VarInst arg) {
        this.args[i] = arg;
    }

    public MethodVisitor getWriter() {
        return writer;
    }

    /**
     *
     * @param returnType
     * @param fieldName
     */
    public LocalVarInst newLocalVar(Type type, String fieldName, Action action) {
        LocalVarInst localVarInst = new LocalVarInst(lvbIndex++, type, fieldName);
        localVarInst.store(this, action);
        return localVarInst;
    }

    public void visitLabel(Label label) {
        this.writer.visitLabel(label);
    }
}
