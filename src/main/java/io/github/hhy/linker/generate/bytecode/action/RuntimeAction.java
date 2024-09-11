package io.github.hhy.linker.generate.bytecode.action;

import io.github.hhy.linker.entity.MethodHolder;
import io.github.hhy.linker.generate.bytecode.LookupMember;
import io.github.hhy.linker.runtime.Runtime;

/**
 * <p>RuntimeAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class RuntimeAction {
    /**
     * <p>findLookup.</p>
     *
     * @param lookup a {@link io.github.hhy.linker.generate.bytecode.LookupMember} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public static MethodInvokeAction findLookup(LookupMember lookup, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_LOOKUP)
                .setArgs(new MethodInvokeAction(MethodHolder.LOOKUP_LOOKUP_CLASS)
                        .setInstance(lookup), LdcLoadAction.of(fieldName));
    }

    /**
     * <p>lookup.</p>
     *
     * @param action a {@link io.github.hhy.linker.generate.bytecode.action.Action} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public static MethodInvokeAction lookup(Action action) {
        return new MethodInvokeAction(Runtime.LOOKUP)
                .setArgs(action);
    }

    /**
     * <p>findSetter.</p>
     *
     * @param lookupMember a {@link io.github.hhy.linker.generate.bytecode.LookupMember} object.
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link io.github.hhy.linker.generate.bytecode.action.MethodInvokeAction} object.
     */
    public static MethodInvokeAction findSetter(LookupMember lookupMember, String fieldName) {
        return new MethodInvokeAction(Runtime.FIND_SETTER)
                .setArgs(lookupMember, LdcLoadAction.of(fieldName));
    }
}
