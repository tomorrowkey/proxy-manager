package jp.tomorrowkey.android.proxymanager.util;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.List;

public final class WifiUtil {

    private WifiUtil() {
    }

    @Nullable
    public static WifiConfiguration getCurrentWifiConfiguration(WifiManager wifiManager) {
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
            if (TextUtils.equals(wifiConfiguration.SSID, connectionInfo.getSSID())) {
                return wifiConfiguration;
            }
        }
        return null;
    }
}
