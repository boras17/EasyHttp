package jsonoperations;

import Utils.ReflectionUtils;
import jsonoperations.serialization.EasySerialize;
import jsonoperations.serialization.EasySerializer;

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
        json.append("{");
        List<Object> nestedObjecte = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        Class<?> fieldType = null;
        Object fieldValue = null;
        Field field = null;

        for(int i = 0; i < fields.length; ++i){
            field = fields[i];
            fieldType = field.getType();
            fieldValue = ReflectionUtils.getValueForField(field, object);
            json.append("\"").append(field.getName()).append("\":");
            if(fieldValue == null){
                json.append("null");
            }else if(field.isAnnotationPresent(EasySerialize.class)){
                try{
                      this.json.append(this.getJsonValueForCustomSerializer(fieldValue, field));
                }catch (NoSuchMethodException ignored){

                } catch (InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
            else if(fieldType.equals(String.class) || fieldType.equals(Character.class)){
                serializeToText(fieldValue);
            }else if(fieldType.equals(Integer.class) ||
                    fieldType.equals(Double.class) ||
                    fieldType.equals(Float.class)){
                serializeToNums(fieldValue);
            }else if(isJavaCollection(fieldType)){
                Object[] objects = ((Collection<Object>)field.get(object)).toArray();
                serializeToArray(objects);
            }
            else if(fieldType.isArray()){
                Object[] arr = (Object[])field.get(object);
                serializeToArray(arr);
            } else if(fieldType.equals(Boolean.class)) {
                json.append(fieldValue);
            }else{
                generateJson(field.get(object));
            }
            if(i < (fields.length -1)) json.append(",").append("\n");
        }

        json.append("}");
    }

    private void serializeToArray(Object[] arr) throws IllegalAccessException {
        json.append("[");
        Object arrElem = null;
        for(int j = 0; j < arr.length; ++j){
            System.out.println("j: " + j);
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
        return Collection.class.isAssignableFrom(clazz);
    }
}
