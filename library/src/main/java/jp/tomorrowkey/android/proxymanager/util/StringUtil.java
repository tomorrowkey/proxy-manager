package jp.tomorrowkey.android.proxymanager.util;

import android.support.annotation.Nullable;

public class StringUtil {
    public static String join(@Nullable String[] array, @Nullable String separator) {
        if (array == null) {
            return "";
        }
        if (separator == null) {
            separator = "";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(s);
        }

        return sb.toString();
    }
}
