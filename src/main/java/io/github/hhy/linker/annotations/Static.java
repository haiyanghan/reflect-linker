package io.github.hhy.linker.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于给runtime字段强制指定是否是静态字段/方法
 *
 * @author hanhaiyang
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.METHOD)
public @interface Static {
    boolean value() default true;
    String[] name() default {};
}
