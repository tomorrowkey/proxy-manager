package jp.tomorrowkey.android.proxymanager.internal;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import jp.tomorrowkey.android.proxymanager.ProxyManager;
import jp.tomorrowkey.android.proxymanager.model.ProxyInfo;
import jp.tomorrowkey.android.proxymanager.model.ProxySettings;
import jp.tomorrowkey.android.proxymanager.reflection.Reflection;
import jp.tomorrowkey.android.proxymanager.reflection.ReflectionUtil;

public class ProxyManagerKitkat extends ProxyManager {

    public ProxyManagerKitkat(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        super(wifiManager, wifiConfiguration);
    }

    @Override
    public ProxySettings getProxySetting() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ProxyInfo getHttpProxy() {
        Object linkProperties = Reflection.withObject(wifiConfiguration).field("linkProperties").getObject();
        Object proxyProperties = Reflection.withObject(linkProperties).method("getHttpProxy").invoke();
        String host = (String) Reflection.withObject(proxyProperties).method("getHost").invoke();
        int port = (Integer) Reflection.withObject(proxyProperties).method("getPort").invoke();
        String excludeHosts = (String) Reflection.withObject(proxyProperties).method("getExclusionList").invoke();
        return new ProxyInfo(host, port, excludeHosts.split(","));
    }

    @Override
    public void updateHttpProxy(ProxySettings proxySettings, ProxyInfo proxyInfo) {
        Object linkProperties = Reflection.withObject(wifiConfiguration).field("linkProperties").getObject();
        Object newProxyProperties = Reflection.withClass("android.net.ProxyProperties")
                .constructor(String.class, int.class, String.class)
                .newInstance(proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getExcludeHostList());
        Reflection.withObject(linkProperties)
                .method("setHttpProxy", ReflectionUtil.forName("android.net.ProxyProperties"))
                .invoke(newProxyProperties);

        // FIXME: update proxy settings

        wifiManager.updateNetwork(wifiConfiguration);
    }

    @Override
    public void clearProxy() {
        throw new RuntimeException("Not implemented");
    }
}
