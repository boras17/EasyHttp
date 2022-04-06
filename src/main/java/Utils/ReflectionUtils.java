package Utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getValueForField(Field field, Object object)
            throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }
}
