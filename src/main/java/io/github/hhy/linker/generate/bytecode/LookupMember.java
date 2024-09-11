package io.github.hhy.linker.generate.bytecode;


import io.github.hhy.linker.constant.Lookup;
import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.MethodBody;
import io.github.hhy.linker.generate.bytecode.action.*;
import io.github.hhy.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

import static io.github.hhy.linker.generate.bytecode.action.Condition.any;
import static io.github.hhy.linker.generate.bytecode.action.Condition.must;


/**
 * <p>LookupMember class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class LookupMember extends Member {

    /**
     * 防止多次静态初始化
     */
    private boolean inited;

    /**
     * <p>Constructor for LookupMember.</p>
     *
     * @param access a int.
     * @param owner a {@link java.lang.String} object.
     * @param lookupName a {@link java.lang.String} object.
     */
    public LookupMember(int access, String owner, String lookupName) {
        super(access, owner, lookupName, Lookup.TYPE);
    }

    /**
     * 初始化静态lookup, 生成以下代码
     * <pre>
     * static {
     *     lookup = Runtime.lookup(Type.class);
     * }
     * </pre>
     *
     * @param clinit a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param classLoadAc a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     */
    public void staticInit(MethodBody clinit, Action classLoadAc) {
        if (inited) return;
        this.store(clinit, RuntimeAction.lookup(classLoadAc));
        this.inited = true;
    }

    /**
     * <p>staticInit.</p>
     *
     * @param clinit a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param staticType a {@link org.objectweb.asm.Type} object.
     */
    public void staticInit(MethodBody clinit, Type staticType) {
        if (inited) return;
        this.store(clinit, RuntimeAction.lookup(LdcLoadAction.of(staticType)));
        this.inited = true;
    }


    /**
     * <p>reinit.</p>
     *
     * @param methodBody a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param typeLoadAction a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     */
    public void reinit(MethodBody methodBody, Action typeLoadAction) {
        this.store(methodBody, RuntimeAction.lookup(typeLoadAction));
    }

    /**
     * 生成运行时校验lookup的代码
     *
     * @param methodBody a {@link io.github.hhy.linker.generate.MethodBody} object.
     * @param varInst a {@link io.github.hhy.linker.generate.bytecode.vars.VarInst} object.
     * @param lookupAssign a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     */
    public void runtimeCheck(MethodBody methodBody, VarInst varInst, Action lookupAssign) {
        /*
         * if (lookup == null || (obj != null && obj.getClass() != lookup.lookupClass())) {
         *     // goto @Label lookupAssign
         * }
         * // goto checkMh
         */
        methodBody.append(() -> {
            return new ConditionJumpAction(
                    any(
                            Condition.isNull(this), // lookup == null
                            must(
                                    Condition.notNull(varInst), // obj != null
                                    Condition.notEq(varInst.getThisClass(), lookupClass()) // obj.getClass() != lookup.lookupClass()
                            )
                    ),
                    lookupAssign, null);
        });
    }

    /**
     * <p>lookupClass.</p>
     *
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public MethodInvokeAction lookupClass() {
        return new MethodInvokeAction(MethodHolder.LOOKUP_LOOKUP_CLASS)
                .setInstance(this);
    }
}
