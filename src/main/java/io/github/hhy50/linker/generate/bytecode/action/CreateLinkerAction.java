package io.github.hhy50.linker.generate.bytecode.action;

import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.generate.MethodBody;
import io.github.hhy50.linker.generate.bytecode.vars.VarInst;
import org.objectweb.asm.Type;

/**
 * <p>CreateLinkerAction class.</p>
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
public class CreateLinkerAction implements Action {

    private final Type linkerType;
    private final VarInst obj;

    /**
     * <p>Constructor for CreateLinkerAction.</p>
     *
     * @param linkerType a {@link org.objectweb.asm.Type} object.
     * @param obj a {@link io.github.hhy50.linker.generate.bytecode.vars.VarInst} object.
     */
    public CreateLinkerAction(Type linkerType, VarInst obj) {
        this.linkerType = linkerType;
        this.obj = obj;
    }


    /** {@inheritDoc} */
    @Override
    public void apply(MethodBody body) {
        body.append(obj.ifNull(Action.returnNull(), new MethodInvokeAction(MethodHolder.LINKER_FACTORY_CREATE_LINKER)
                .setArgs(LdcLoadAction.of(linkerType), obj)));;
    }
}
