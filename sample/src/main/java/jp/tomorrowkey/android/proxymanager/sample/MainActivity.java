package jp.tomorrowkey.android.proxymanager.sample;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import jp.tomorrowkey.android.proxymanager.ProxyManager;
import jp.tomorrowkey.android.proxymanager.model.ProxyInfo;
import jp.tomorrowkey.android.proxymanager.model.ProxySettings;
import jp.tomorrowkey.android.proxymanager.sample.databinding.ActivityMainBinding;
import jp.tomorrowkey.android.proxymanager.util.WifiUtil;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        loadProxy();
        binding.updateProxyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConfiguration wifiConfiguration = WifiUtil.getCurrentWifiConfiguration(wifiManager);
                ProxyManager proxyManager = ProxyManager.obtain(wifiManager, wifiConfiguration);
                proxyManager.updateHttpProxy(ProxySettings.STATIC, new ProxyInfo("192.168.12.2", 8888, new String[0]));

                Toast.makeText(getApplicationContext(), "Proxy updated", Toast.LENGTH_SHORT).show();
                loadProxy();
            }
        });
        binding.clearProxyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConfiguration wifiConfiguration = WifiUtil.getCurrentWifiConfiguration(wifiManager);
                ProxyManager proxyManager = ProxyManager.obtain(wifiManager, wifiConfiguration);
                proxyManager.clearProxy();

                Toast.makeText(getApplicationContext(), "Proxy cleared", Toast.LENGTH_SHORT).show();
                loadProxy();
            }
        });

    }

    private void loadProxy() {
        WifiConfiguration wifiConfiguration = WifiUtil.getCurrentWifiConfiguration(wifiManager);

        if (wifiConfiguration == null) {
            Toast.makeText(getApplicationContext(), "WiFi is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String ssid = wifiConfiguration.SSID;
        binding.ssidTextView.setText("SSID: " + ssid);

        ProxyManager proxyManager = ProxyManager.obtain(wifiManager, wifiConfiguration);

        ProxySettings proxySetting = proxyManager.getProxySetting();
        binding.proxyModeTextView.setText("Proxy mode: " + proxySetting);

        ProxyInfo proxyInfo = proxyManager.getHttpProxy();
        String host = proxyInfo.getHost();
        int port = proxyInfo.getPort();
        String excludeHosts = proxyInfo.getExcludeHosts();
        binding.hostTextView.setText("Host: " + host);
        binding.portTextView.setText("Port: " + port);
        binding.excludeHostsTextView.setText("Exclude hosts: " + excludeHosts);
    }
}
