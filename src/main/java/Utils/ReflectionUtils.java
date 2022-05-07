package Utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Object getValueForField(Field field, Object object)
            throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }
    public static boolean checkIfNumeric(Class<?> type) {
        return type.equals(Integer.class) || type.equals(float.class) ||
                type.equals(Double.class) || type.equals(double.class)
                ||type.equals(Float.class) || type.equals(int.class);
    }
    public static boolean checkIfCharOrStr(Class<?> type){
        return type.equals(String.class) || type.equals(Character.class);
    }
    public static boolean checkIfBoolean(Class<?> type){
        return type.equals(Boolean.class) || type.equals(boolean.class);
    }
}
