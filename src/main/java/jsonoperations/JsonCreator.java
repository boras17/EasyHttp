package jsonoperations;

import Utils.ReflectionUtils;
import jsonoperations.serialization.EasySerialize;
import jsonoperations.serialization.EasySerializer;
import jsonoperations.serialization.SerializedName;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonCreator {
    private StringBuilder json;

    public JsonCreator() {
        this.json = new StringBuilder();
    }

    public String getJson() {
        return json.toString();
    }

    private <T> String getJsonValueForCustomSerializer(T fieldVal, Field field) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        EasySerialize easySerialize = field.getAnnotation(EasySerialize.class);
        Class<?> serilizerClass = easySerialize.use();
        Constructor<?> serializerConstructor = serilizerClass.getConstructor();
        EasySerializer<T> easySerializer = (EasySerializer<T>)serializerConstructor.newInstance();
        return easySerializer.serialize(fieldVal);
    }

    public void generateJson(Object object) throws IllegalAccessException {
        if(object instanceof Collection<?>){
            json.append("[");
            List<Object> list = (List<Object>)object;
            int a = 0;
            for(Object o: list){
                if(o.getClass().isPrimitive() && !o.getClass().equals(String.class)){
                    generateJson(o);
                }{
                    Class<?> obClazz = o.getClass();
                    if(obClazz.equals(String.class) || obClazz.equals(Character.class)){
                        json.append("\"").append(o).append("\"").append(a < list.size()-1 ? ",": "");
                    }else{
                        json.append(o);
                    }
                }
                a += 1;
            }
            json.append("]");
        }else{
            serializeObject(object);
        }

    }

    public String getSerializeNameValueFromAnnotation(Field field) {
        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        String serializedNameValue = serializedName.name();
        return (serializedNameValue.isEmpty() || serializedNameValue.isBlank()) ? field.getName() : serializedNameValue;
    }

    private void serializeObject(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Class<?> fieldType = null;
        Object fieldValue = null;
        Field field = null;

        json.append("{");
        for (int i = 0; i < fields.length; ++i) {
            field = fields[i];
            fieldType = field.getType();
            fieldValue = ReflectionUtils.getValueForField(field, object);
            if(fieldType.isNestmateOf(clazz)) {
                int index = this.json.lastIndexOf(",");
                this.json.replace(index, index+1,"");
                continue;
            }
            // serialize json property name
            json.append("\"");
            if(field.isAnnotationPresent(SerializedName.class)) {
                json.append(this.getSerializeNameValueFromAnnotation(field));
            }else{
                json.append(field.getName());
            }
            json.append("\":");
            // serialize json property name

            if (fieldValue == null) {
                json.append("null");
            } else if (field.isAnnotationPresent(EasySerialize.class)) {
                try {
                    this.json.append(this.getJsonValueForCustomSerializer(fieldValue, field));
                } catch (NoSuchMethodException ignored) {

                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (ReflectionUtils.checkIfCharOrStr(fieldType)) {
                serializeToText(fieldValue);
            } else if (ReflectionUtils.checkIfNumeric(fieldType)) {
                serializeToNums(fieldValue);
            } else if (isJavaCollection(fieldType)) {
                Object[] objects = ((Collection<Object>) field.get(object)).toArray();
                serializeToArray(objects);
            } else if (fieldType.isArray()) {
                Object[] arr = (Object[]) field.get(object);
                serializeToArray(arr);
            } else if (ReflectionUtils.checkIfBoolean(fieldType)) {
                json.append(fieldValue);
            } else {
                generateJson(field.get(object));
            }
            if (i < (fields.length - 1)) json.append(",");
        }
        json.append("}");
    }
    private void serializeToArray(Object[] arr) throws IllegalAccessException {
        json.append("[");
        Object arrElem = null;
        for(int j = 0; j < arr.length; ++j){
            arrElem = arr[j];
            if(!arrElem.getClass().isPrimitive() && !arrElem.getClass().isAssignableFrom(String.class)){
                generateJson(arrElem);
                if(j < arr.length - 1) json.append(",");
                continue;
            }
            json.append("\"").append(arrElem).append("\"");
            if(j < arr.length-1) json.append(",");
        }
        json.append("]");
    }

    private void serializeToText(Object fieldValue){
        json.append("\"").append(fieldValue).append("\"");
    }

    private void serializeToNums(Object numValue){
        json.append(numValue);
    }

    private boolean isJavaCollection(Class<?> clazz){
        return Collection.class.isAssignableFrom(clazz) || clazz.isArray();
    }
}
