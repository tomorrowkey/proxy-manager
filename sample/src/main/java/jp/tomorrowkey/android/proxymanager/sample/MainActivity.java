package jp.tomorrowkey.android.proxymanager.sample;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import jp.tomorrowkey.android.proxymanager.ProxyManager;
import jp.tomorrowkey.android.proxymanager.model.ProxySetting;
import jp.tomorrowkey.android.proxymanager.util.WifiUtil;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "android-proxy-manager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wifiConfiguration = WifiUtil.getCurrentWifiConfiguration(wifiManager);

        Log.d(LOG_TAG, "SSID=" + wifiConfiguration.SSID);
        ProxyManager proxyManager = ProxyManager.obtain(wifiManager, wifiConfiguration);
        ProxySetting proxySetting = proxyManager.getProxySetting();

        Log.d(LOG_TAG, "Proxy host=" + proxySetting.getHost());
        Log.d(LOG_TAG, "Proxy port=" + proxySetting.getPort());
        Log.d(LOG_TAG, "Proxy exclude host list=" + proxySetting.getExcludeHostList());

        ProxySetting newProxySetting = new ProxySetting("192.168.102.118", 8888, new String[0]);
        proxyManager.updateProxySetting(newProxySetting);
    }
}
