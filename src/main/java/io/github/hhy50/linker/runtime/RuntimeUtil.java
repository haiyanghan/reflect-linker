package io.github.hhy50.linker.runtime;


import io.github.hhy50.linker.AccessTool;
import io.github.hhy50.linker.entity.MethodHolder;
import io.github.hhy50.linker.exceptions.LinkerException;
import io.github.hhy50.linker.syslinker.DirectMethodHandleLinker;
import io.github.hhy50.linker.util.ClassUtil;
import org.objectweb.asm.Type;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The type Runtime util.
 */
public class RuntimeUtil {
    /**
     * The constant OWNER.
     */
    public static final String OWNER = "io/github/hhy50/linker/runtime/RuntimeUtil";
    /**
     * The constant IS_STATIC.
     */
    public static final MethodHolder IS_STATIC = new MethodHolder(OWNER, "isStatic", "(Ljava/lang/invoke/MethodHandle;)Z");
    /**
     * The constant UNWRAP_BYTE_DESC.
     */
    public static final String UNWRAP_BYTE_DESC = "(Ljava/lang/Object;)"+Type.BYTE_TYPE;
    /**
     * The constant UNWRAP_SHORT_DESC.
     */
    public static final String UNWRAP_SHORT_DESC = "(Ljava/lang/Object;)"+Type.SHORT;
    /**
     * The constant UNWRAP_INT_DESC.
     */
    public static final String UNWRAP_INT_DESC = "(Ljava/lang/Object;)"+Type.INT_TYPE;
    /**
     * The constant UNWRAP_LONG_DESC.
     */
    public static final String UNWRAP_LONG_DESC = "(Ljava/lang/Object;)"+Type.LONG_TYPE;
    /**
     * The constant UNWRAP_FLOAT_DESC.
     */
    public static final String UNWRAP_FLOAT_DESC = "(Ljava/lang/Object;)"+Type.FLOAT_TYPE;
    /**
     * The constant UNWRAP_DOUBLE_DESC.
     */
    public static final String UNWRAP_DOUBLE_DESC = "(Ljava/lang/Object;)"+Type.DOUBLE_TYPE;
    /**
     * The constant UNWRAP_CHAR_DESC.
     */
    public static final String UNWRAP_CHAR_DESC = "(Ljava/lang/Object;)"+Type.CHAR_TYPE;
    /**
     * The constant UNWRAP_BOOL_DESC.
     */
    public static final String UNWRAP_BOOL_DESC = "(Ljava/lang/Object;)"+Type.BOOLEAN_TYPE;
    /**
     * The constant TYPE_MATCH.
     */
    public static final MethodHolder TYPE_MATCH = new MethodHolder(OWNER, "typeMatch", "(Ljava/lang/Class;Ljava/lang/String;)Z");

    /**
     * Check null.
     *
     * @param obj the obj
     */
    public static void checkNull(Object obj) {

    }

    /**
     * Is static boolean.
     *
     * @param methodHandle the method handle
     * @return the boolean
     * @throws LinkerException the linker exception
     */
    public static boolean isStatic(MethodHandle methodHandle) throws LinkerException {
        DirectMethodHandleLinker mh = AccessTool.createSysLinker(DirectMethodHandleLinker.class, methodHandle);
        return Modifier.isStatic(mh.modifiers());
    }

    /**
     * Type match boolean.
     *
     * @param clazz the clazz
     * @param type  the type
     * @return the boolean
     */
    public static boolean typeMatch(Class<?> clazz, String type) {
        if (clazz.isArray()) {
            return clazz.getCanonicalName().equals(type);
        }
        return ClassUtil.isAssignableFrom(clazz, type);
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(byte i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(short i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(int i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(long i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(float i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(double i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(char i) {
        return i;
    }

    /**
     * Wrap object.
     *
     * @param i the
     * @return the object
     */
    public static Object wrap(boolean i) {
        return i;
    }

    /**
     * Unwrap byte byte.
     *
     * @param obj the obj
     * @return the byte
     */
    public static byte unwrapByte(Object obj) {
        if (obj instanceof Byte) {
            return (byte) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to byte", obj.getClass()));
    }

    /**
     * Unwrap short short.
     *
     * @param obj the obj
     * @return the short
     */
    public static short unwrapShort(Object obj) {
        if (obj instanceof Short) {
            return (short) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to short", obj.getClass()));
    }

    /**
     * Unwrap int int.
     *
     * @param obj the obj
     * @return the int
     */
    public static int unwrapInt(Object obj) {
        if (obj instanceof Integer) {
            return (int) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to int", obj.getClass()));
    }

    /**
     * Unwrap long long.
     *
     * @param obj the obj
     * @return the long
     */
    public static long unwrapLong(Object obj) {
        if (obj instanceof Long) {
            return (long) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to long", obj.getClass()));
    }

    /**
     * Unwrap float float.
     *
     * @param obj the obj
     * @return the float
     */
    public static float unwrapFloat(Object obj) {
        if (obj instanceof Float) {
            return (float) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to float", obj.getClass()));
    }

    /**
     * Unwrap double double.
     *
     * @param obj the obj
     * @return the double
     */
    public static double unwrapDouble(Object obj) {
        if (obj instanceof Double) {
            return (double) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to double", obj.getClass()));
    }

    /**
     * Unwrap char char.
     *
     * @param obj the obj
     * @return the char
     */
    public static char unwrapChar(Object obj) {
        if (obj instanceof Character) {
            return (char) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to char", obj.getClass()));
    }

    /**
     * Unwrap bool boolean.
     *
     * @param obj the obj
     * @return the boolean
     */
    public static boolean unwrapBool(Object obj) {
        if (obj instanceof Boolean) {
            return (boolean) obj;
        }
        throw new ClassCastException(String.format("class '%s' not cast to boolean", obj.getClass()));
    }

    /**
     * Gets lookup by unsafe.
     *
     * @return the lookup by unsafe
     */
    public static MethodHandles.Lookup getLookupByUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object base = unsafe.staticFieldBase(implLookupField);
            long fieldOffset = unsafe.staticFieldOffset(implLookupField);
            return ((MethodHandles.Lookup) unsafe.getObject(base, fieldOffset));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
