package com.rumaruka.powercraft.api.reflect;

import com.rumaruka.powercraft.api.PCLogger;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumMap;


public class PCReflect {

    private static boolean thrownSecurityExceptionBefore;

    private static interface PC_ICallerGetter {

        public Class<?> getCallerClass(int num);

    }

    private static class PC_CallerGetterSecMan extends SecurityManager implements PC_ICallerGetter{

        PC_CallerGetterSecMan(){

        }

        @SuppressWarnings("boxing")
        @Override
        public Class<?> getCallerClass(int num){
            Class<?>[] classes = getClassContext();
            if (classes.length > 3 + num) {
                return classes[num+3];
            }
            PCLogger.severe("Class %s has no %s callers, only %s", classes[classes.length-1], num, classes.length - 2);
            return null;
        }

    }

    private static class PC_CallerGetterFallback implements PC_ICallerGetter{

        PC_CallerGetterFallback() {

        }

        @SuppressWarnings("boxing")
        @Override
        public Class<?> getCallerClass(int num) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            if (stackTraceElements.length > 3 + num) {
                try {
                    return Class.forName(stackTraceElements[3 + num].getClassName());
                } catch(SecurityException se){
                    onSecurityException(se);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            PCLogger.severe("Class %s has no %s callers, only %s", stackTraceElements[0], num, stackTraceElements.length - 2);
            return null;
        }


    }

    private static final PC_ICallerGetter callerGetter;

    static{
        PC_ICallerGetter cGetter;
        try{
            cGetter = new PC_CallerGetterSecMan();
        } catch(Throwable e){
            cGetter = new PC_CallerGetterFallback();
        }
        callerGetter = cGetter;
    }

    public static Class<?> getCallerClass() {
        return callerGetter.getCallerClass(0);
    }

    public static Class<?> getCallerClass(int num) {
        return callerGetter.getCallerClass(num);
    }

    @SuppressWarnings("boxing")
    public static Field findNearestBestField(Class<?> clasz, int index, Class<?> type) {
        Field fields[] = clasz.getDeclaredFields();
        Field f;
        int i = index;
        if (i >= 0 && i < fields.length) {
            f = fields[i];
            if (type.isAssignableFrom(f.getType())) {
                return f;
            }
        } else {
            if (i < 0) i = 0;
            if (i >= fields.length) {
                i = fields.length - 1;
            }
        }
        int min = i - 1, max = i + 1;
        while (min >= 0 || max < fields.length) {
            if (max < fields.length) {
                f = fields[max];
                if (type.isAssignableFrom(f.getType())) {
                    PCLogger.warning("Field in %s which should be at index %s not found, now using index %s", clasz, i, max);
                    return f;
                }
                max++;
            }
            if (min >= 0) {
                f = fields[min];
                if (type.isAssignableFrom(f.getType())) {
                    PCLogger.warning("Field in %s which should be at index %s not found, now using index %s", clasz, i, min);
                    return f;
                }
                min--;
            }
        }
        PCLogger.severe("Field in %s which should be at index %s not found", clasz, i);
        return null;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getValue(Class<?> clasz, Object object, int index, Class<T> type) {
        try {
            Field field = findNearestBestField(clasz, index, type);
            setAccessible(field);
            return (T) field.get(object);
        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(PCReflectionField<?, T> f, Object object) {
        try {
            Field field = f.getField();
            setAccessible(field);
            return (T) field.get(object);
        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValue(Class<?> clasz, Object object, int index, Class<?> type, Object value) {

        try {
            Field field = findNearestBestField(clasz, index, type);
            setAccessible(field);
            field.set(object, value);
        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setValue(PCReflectionField<?, ?> f, Object object, Object value) {

        try {
            Field field = f.getField();
            setAccessible(field);
            field.set(object, value);
        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setValueAndFinals(Class<?> clasz, Object object, int index, Class<?> type, Object value) {

        try {
            Field field = findNearestBestField(clasz, index, type);
            setAccessible(field);
            Field field_modifiers = Field.class.getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            int modifier = field_modifiers.getInt(field);

            if ((modifier & Modifier.FINAL) != 0) {
                field_modifiers.setInt(field, modifier & ~Modifier.FINAL);
            }

            field.set(object, value);

            if ((modifier & Modifier.FINAL) != 0) {
                field_modifiers.setInt(field, modifier);
            }

        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setValueAndFinals(PCReflectionField<?, ?> f, Object object, Object value) {
        try {
            Field field = f.getField();
            setAccessible(field);
            Field field_modifiers = Field.class.getDeclaredField("modifiers");
            field_modifiers.setAccessible(true);
            int modifier = field_modifiers.getInt(field);

            if ((modifier & Modifier.FINAL) != 0) {
                field_modifiers.setInt(field, modifier & ~Modifier.FINAL);
            }

            field.set(object, value);

            if ((modifier & Modifier.FINAL) != 0) {
                field_modifiers.setInt(field, modifier);
            }

        } catch(SecurityException se){
            onSecurityException(se);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Method AccessibleObject_setAccessible0;

    public static void setAccessible(AccessibleObject ao){
        if(AccessibleObject_setAccessible0!=null){
            try{
                AccessibleObject_setAccessible0.invoke(null, ao, Boolean.TRUE);
            }catch(Exception e){/**/}
        }
        try{
            ao.setAccessible(true);
        }catch(Exception e){
            try{
                ReflectionHelper.setPrivateValue(AccessibleObject.class, ao, Boolean.TRUE, "override");
            }catch(Exception ee){
                if(AccessibleObject_setAccessible0==null){
                    try{

                    }catch(Exception eee){/**/}
                }
            }
        }
    }

    public static Field[] getDeclaredFields(Class<?> c){
        try{
            return c.getDeclaredFields();
        } catch(SecurityException se){
            onSecurityException(se);
            try{
                return c.getFields();
            } catch(SecurityException se2){
                return new Field[0];
            }
        }
    }

    public static Object processFields(Object obj, PCProcessor processor){
        Class<?> c = obj.getClass();
        EnumMap<PCProcessor.Result, Object> results = new EnumMap<PCProcessor.Result, Object>(PCProcessor.Result.class);
        while(c!=Object.class){
            Field[] fields = PCReflect.getDeclaredFields(c);
            for(Field field:fields){
                results.clear();
                try{
                    setAccessible(field);
                    Object value = field.get(obj);
                    processor.process(field, value, results);
                    if(results.containsKey(PCProcessor.Result.SET)){
                        field.set(obj, results.get(PCProcessor.Result.SET));
                    }
                    if(results.containsKey(PCProcessor.Result.STOP)){
                        return results.get(PCProcessor.Result.STOP);
                    }
                } catch(SecurityException se){
                    onSecurityException(se);
                } catch(IllegalAccessException e){
                    PCLogger.severe("Cannot access field %s.%s", c, field);
                } catch(IllegalArgumentException e){
                    PCLogger.severe("Wrong arguments for field %s.%s", c, field);
                }
            }
            c = c.getSuperclass();
        }
        return null;
    }

    public static <T> T newInstance(Class<T> c) {
        try {
            return c.newInstance();
        } catch (SecurityException e){
            onSecurityException(e);
        } catch (InstantiationException e) {
            PCLogger.severe("Error while constructing %s", c);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            PCLogger.severe("Cannot access constructor %s", c);
        }
        return null;
    }

    public static <T> T newInstanceNoHandling(Class<T> c, Class<?>[] types, Object...values) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            Constructor<T> cc = c.getConstructor(types);
            setAccessible(cc);
            return cc.newInstance(values);
        } catch (SecurityException e){
            onSecurityException(e);
        }
        return null;
    }

    public static <T> T newInstance(Class<T> c, Class<?>[] types, Object...values) {
        try {
            Constructor<T> cc = c.getConstructor(types);
            setAccessible(cc);
            return cc.newInstance(values);
        } catch (SecurityException e){
            onSecurityException(e);
        } catch (InstantiationException e) {
            PCLogger.severe("Error while constructing %s", c);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            PCLogger.severe("Cannot access constructor %s", c);
        } catch (NoSuchMethodException e) {
            PCLogger.severe("Cannot find constructor %s", c);
        } catch (Exception e) {
            PCLogger.severe("Error in constructor %s", c);
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    static void onSecurityException(SecurityException e){
        if(!thrownSecurityExceptionBefore){
            PCLogger.warning("PowerCraft has no permission for reflection");
            thrownSecurityExceptionBefore = true;
        }
    }

    private PCReflect() {
        PCUtils.staticClassConstructor();
    }
}
