package jp.tomorrowkey.android.proxymanager;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import jp.tomorrowkey.android.proxymanager.internal.ProxyManagerKitkat;
import jp.tomorrowkey.android.proxymanager.internal.ProxyManagerMarshmallow;
import jp.tomorrowkey.android.proxymanager.model.ProxyInfo;
import jp.tomorrowkey.android.proxymanager.model.ProxySettings;

public abstract class ProxyManager {

    private static final String LOG_TAG = "proxy-manager";

    protected WifiManager wifiManager;

    protected WifiConfiguration wifiConfiguration;

    public ProxyManager(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        this.wifiManager = wifiManager;
        this.wifiConfiguration = wifiConfiguration;
    }

    public static ProxyManager obtain(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.KITKAT:
                Log.d(LOG_TAG, "Kitkat");
                return new ProxyManagerKitkat(wifiManager, wifiConfiguration);
            case Build.VERSION_CODES.M:
                Log.d(LOG_TAG, "Marshmallow");
                return new ProxyManagerMarshmallow(wifiManager, wifiConfiguration);
            default:
                throw new RuntimeException("No implementation for API Level " + Build.VERSION.SDK_INT);
        }
    }

    public abstract ProxySettings getProxySetting();

    public abstract ProxyInfo getHttpProxy();

    public abstract void updateHttpProxy(ProxySettings mode, ProxyInfo proxyInfo);

    public abstract void clearProxy();

}
