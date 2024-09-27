package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.generate.MethodBody;
import org.objectweb.asm.Opcodes;

/**
 * The interface Load action.
 */
public interface LoadAction extends Action {

    /**
     * The constant LOAD0.
     */
    Action LOAD0 = new LoadAction() {
        @Override
        public void load(MethodBody body) {
            body.getWriter().visitVarInsn(Opcodes.ALOAD, 0);
        }
    /** Constant <code>LOAD0</code> */
    };

    @Override
    default void apply(MethodBody body) {
        load(body);
    }

    /**
     * If null action.
     *
     * @param ifBlock the if block
     * @return the action
     */
    default Action ifNull(Action ifBlock) {
        return ifNull(ifBlock, null);
    }

    /**
     * If null action.
     *
     * @param ifBlock   the if block
     * @param elseBlock the else block
     * @return the action
     */
    default Action ifNull(Action ifBlock, Action elseBlock) {
        return new ConditionJumpAction(Condition.isNull(this), ifBlock, elseBlock);
    }

    /**
     * Load.
     *
     * @param body the body
     */
    void load(MethodBody body);
}
