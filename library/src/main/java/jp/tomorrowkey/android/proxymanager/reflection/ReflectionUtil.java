package jp.tomorrowkey.android.proxymanager.reflection;

public class ReflectionUtil {
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
