package jp.tomorrowkey.android.proxymanager.internal;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import jp.tomorrowkey.android.proxymanager.ProxyManager;
import jp.tomorrowkey.android.proxymanager.model.ProxyInfo;
import jp.tomorrowkey.android.proxymanager.model.ProxySettings;
import jp.tomorrowkey.android.proxymanager.reflection.Reflection;
import jp.tomorrowkey.android.proxymanager.reflection.ReflectionUtil;

public class ProxyManagerMarshmallow extends ProxyManager {
    public ProxyManagerMarshmallow(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        super(wifiManager, wifiConfiguration);
    }

    @Override
    public ProxySettings getProxySetting() {
        Object proxySetting = Reflection.withObject(wifiConfiguration).method("getProxySettings").invoke();
        return ProxySettings.valueOf(proxySetting.toString());
    }

    @Override
    public ProxyInfo getHttpProxy() {
        Object proxyInfo = Reflection.withObject(wifiConfiguration).method("getHttpProxy").invoke();
        if (proxyInfo == null) {
            return ProxyInfo.NULL;
        }
        String host = (String) Reflection.withObject(proxyInfo).method("getHost").invoke();
        int port = (int) Reflection.withObject(proxyInfo).method("getPort").invoke();
        String[] excludeHostList = (String[]) Reflection.withObject(proxyInfo).method("getExclusionList").invoke();
        return new ProxyInfo(host, port, excludeHostList);
    }

    @Override
    public void updateHttpProxy(ProxySettings proxySettings, ProxyInfo proxyInfo) {
        Object newProxySetting = Reflection.withClass("android.net.IpConfiguration$ProxySettings")
                .method("valueOf", String.class)
                .invoke(proxySettings.toString());
        Reflection.withObject(wifiConfiguration)
                .method("setProxySettings", ReflectionUtil.forName("android.net.IpConfiguration$ProxySettings"))
                .invoke(newProxySetting);

        Object newProxyInfo = Reflection.withClass("android.net.ProxyInfo")
                .constructor(String.class, int.class, String.class)
                .newInstance(proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getExcludeHosts());
        Reflection.withObject(wifiConfiguration)
                .method("setHttpProxy", ReflectionUtil.forName("android.net.ProxyInfo"))
                .invoke(newProxyInfo);

        wifiManager.updateNetwork(wifiConfiguration);
    }

    @Override
    public void clearProxy() {
        Object newProxySetting = Reflection.withClass("android.net.IpConfiguration$ProxySettings")
                .method("valueOf", String.class)
                .invoke(ProxySettings.STATIC.toString());
        Reflection.withObject(wifiConfiguration)
                .method("setProxySettings", ReflectionUtil.forName("android.net.IpConfiguration$ProxySettings"))
                .invoke(newProxySetting);

        wifiManager.updateNetwork(wifiConfiguration);
    }
}
