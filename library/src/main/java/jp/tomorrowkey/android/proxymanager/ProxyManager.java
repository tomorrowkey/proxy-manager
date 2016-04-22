package jp.tomorrowkey.android.proxymanager;


import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import jp.tomorrowkey.android.proxymanager.model.ProxySetting;
import jp.tomorrowkey.android.proxymanager.reflection.Reflection;
import jp.tomorrowkey.android.proxymanager.reflection.ReflectionUtil;

public abstract class ProxyManager {

    protected WifiManager wifiManager;

    protected WifiConfiguration wifiConfiguration;

    public ProxyManager(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        this.wifiManager = wifiManager;
        this.wifiConfiguration = wifiConfiguration;
    }

    public abstract ProxySetting getProxySetting();

    public abstract void updateProxySetting(ProxySetting proxySetting);

    public static ProxyManager obtain(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.KITKAT: // API Level 19
                return new ProxyManagerImpl17(wifiManager, wifiConfiguration);
            case Build.VERSION_CODES.M:      // API Level 23
                return new ProxyManagerImpl23(wifiManager, wifiConfiguration);
            default:
                throw new RuntimeException("No implementation for API Level " + Build.VERSION.SDK_INT);
        }
    }

    private static class ProxyManagerImpl17 extends ProxyManager {

        public ProxyManagerImpl17(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
            super(wifiManager, wifiConfiguration);
        }

        @Override
        public ProxySetting getProxySetting() {
            Object linkProperties = Reflection.withObject(wifiConfiguration).filed("linkProperties").getObject();
            Object proxyProperties = Reflection.withObject(linkProperties).method("getHttpProxy").invoke();
            String host = (String) Reflection.withObject(proxyProperties).method("getHost").invoke();
            int port = (Integer) Reflection.withObject(proxyProperties).method("getPort").invoke();
            String excludeHosts = (String) Reflection.withObject(proxyProperties).method("getExclusionList").invoke();
            return new ProxySetting(host, port, excludeHosts.split(","));
        }

        @Override
        public void updateProxySetting(ProxySetting proxySetting) {
            Object linkProperties = Reflection.withObject(wifiConfiguration).filed("linkProperties").getObject();
            Object newProxyProperties = Reflection.withClass("android.net.ProxyProperties")
                    .constructor(String.class, int.class, String.class)
                    .newInstance(proxySetting.getHost(), proxySetting.getPort(), proxySetting.getExcludeHostList());
            Reflection.withObject(linkProperties)
                    .method("setHttpProxy", ReflectionUtil.forName("android.net.ProxyProperties"))
                    .invoke(newProxyProperties);
            wifiManager.updateNetwork(wifiConfiguration);
        }
    }

    private static class ProxyManagerImpl23 extends ProxyManager {
        public ProxyManagerImpl23(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
            super(wifiManager, wifiConfiguration);
        }

        @Override
        public ProxySetting getProxySetting() {
            Object proxyInfo = Reflection.withObject(wifiConfiguration).method("getHttpProxy").invoke();
            Log.d("Hoge", "proxyInfo=" + proxyInfo);
            String host = (String) Reflection.withObject(proxyInfo).method("getHost").invoke();
            int port = (int) Reflection.withObject(proxyInfo).method("getPort").invoke();
            String[] excludeHostList = (String[]) Reflection.withObject(proxyInfo).method("getExclusionList").invoke();
            return new ProxySetting(host, port, excludeHostList);
        }

        @Override
        public void updateProxySetting(ProxySetting proxySetting) {
            Object proxyInfo = Reflection.withClass("android.net.ProxyInfo")
                    .constructor(String.class, int.class, String.class)
                    .newInstance(proxySetting.getHost(), proxySetting.getPort(), proxySetting.getExcludeHosts());
            Reflection.withObject(wifiConfiguration)
                    .method("setHttpProxy", ReflectionUtil.forName("android.net.ProxyInfo"))
                    .invoke(proxyInfo);
            wifiManager.updateNetwork(wifiConfiguration);
        }

    }
}
