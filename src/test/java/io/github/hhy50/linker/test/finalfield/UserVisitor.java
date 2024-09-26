package io.github.hhy50.linker.test.finalfield;

import io.github.hhy50.linker.annotations.Field;
import io.github.hhy50.linker.annotations.Target;

@Target.Bind(value = "io.github.hhy50.linker.test.finalfield.User")
public interface UserVisitor {

    @Field.Getter("name")
    String getName();

    @Field.Setter("name")
    void setName(String name);

    @Field.Setter("_name")
    void setStaticName(String name);

    @Field.Getter("_name")
    String getStaticName();
}
