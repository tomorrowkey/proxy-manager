package jp.tomorrowkey.android.proxymanager.reflection;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    public static Reflection withObject(Object object) {
        return new Reflection(object);
    }

    public static Reflection withClass(Class<?> clazz) {
        return new Reflection(clazz);
    }

    public static Reflection withClass(String className) {
        try {
            return new Reflection(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Object receiver;

    private Class<?> clazz;

    public Reflection(Object receiver) {
        if (receiver == null) {
            throw new IllegalArgumentException("receiver must not be null");
        }

        this.receiver = receiver;
        this.clazz = receiver.getClass();
    }

    public Reflection(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must not be null");
        }

        this.receiver = null;
        this.clazz = clazz;
    }

    public AbstractFieldReflection field(String fieldName) {
        try {
            if (hasReceiver()) {
                return new FieldReflection(receiver, fieldName);
            } else {
                return new DeclareFieldReflection(clazz, fieldName);
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public MethodReflection method(String methodName, Class<?>... parameterTypes) {
        try {
            return new MethodReflection(clazz, receiver, methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ConstructorReflection constructor(Class<?>... parameterTypes) {
        try {
            return new ConstructorReflection(clazz, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasReceiver() {
        return receiver != null;
    }

    public static abstract class AbstractFieldReflection {

        private Object receiver;

        private Field field;

        public AbstractFieldReflection(@NonNull Class<?> clazz, @Nullable Object receiver, @NonNull String fieldName) {
            if (clazz == null) {
                throw new IllegalArgumentException("clazz must not be null");
            }
            if (fieldName == null || fieldName.length() == 0) {
                throw new IllegalArgumentException("fieldName must not be null or blank");
            }

            try {
                this.receiver = receiver;
                this.field = clazz.getField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        public AbstractFieldReflection accessible(boolean accessible) {
            field.setAccessible(accessible);
            return this;
        }

        public Object getObject() {
            try {
                return field.get(receiver);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public void setObject(Object object) {
            try {
                field.set(receiver, object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FieldReflection extends AbstractFieldReflection {
        public FieldReflection(Object receiver, String fieldName) throws NoSuchFieldException {
            super(receiver.getClass(), receiver, fieldName);
        }
    }

    public static class DeclareFieldReflection extends AbstractFieldReflection {
        public DeclareFieldReflection(Class<?> clazz, String fieldName) throws NoSuchFieldException {
            super(clazz, null, fieldName);
        }
    }

    public static class MethodReflection {

        private Object receiver;

        private Method method;

        public MethodReflection(Class<?> clazz, Object receiver, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
            this.receiver = receiver;
            this.method = clazz.getMethod(methodName, parameterTypes);
        }

        public Object invoke(Object... args) {
            try {
                return this.method.invoke(receiver, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public MethodReflection accessible(boolean accessible) {
            this.method.setAccessible(accessible);
            return this;
        }
    }

    public static class ConstructorReflection {

        private Constructor<?> constructor;

        public ConstructorReflection(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
            constructor = clazz.getConstructor(parameterTypes);
        }

        public void accessible(boolean accessible) {
            constructor.setAccessible(accessible);
        }

        public Object newInstance(Object... args) {
            try {
                return constructor.newInstance(args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
