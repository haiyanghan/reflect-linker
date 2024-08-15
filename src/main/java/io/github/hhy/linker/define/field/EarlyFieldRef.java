package io.github.hhy.linker.define.field;


import java.lang.reflect.Field;

/**
 * 前期就确定好类型的字段，包括：
 * 1. target字段
 * 2. target中类型是final的字段
 * 3. 使用@Typed注解的字段
 */
public class EarlyFieldRef extends FieldRef {

    private Class<?> type;

    public EarlyFieldRef(FieldRef prev, String name, Class<?> type) {
        super(prev, name);
        this.type = type;
    }

    public EarlyFieldRef(FieldRef prev, String name, String type) {
        super(prev, name);
    }

    public EarlyFieldRef(FieldRef prev, Field field) {
        super(prev, field.getName());
    }

    public Class<?> getType() {
        return type;
    }
}
