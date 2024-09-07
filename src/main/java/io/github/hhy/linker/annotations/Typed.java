package io.github.hhy.linker.annotations;


import java.lang.annotation.Target;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Repeatable(Types.class)
public @interface Typed {
    String name() default "";
    String type();
}
