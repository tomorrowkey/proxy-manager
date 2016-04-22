package jp.tomorrowkey.android.proxymanager.model;

import jp.tomorrowkey.android.proxymanager.util.StringUtil;

public class ProxySetting {

    private String host;

    private int port;

    private String[] excludeHostList;

    public ProxySetting(String host, int port, String[] excludeHostList) {
        this.host = host;
        this.port = port;
        this.excludeHostList = excludeHostList;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String[] getExcludeHostList() {
        return excludeHostList;
    }

    public String getExcludeHosts() {
        return StringUtil.join(excludeHostList, ",");
    }
}
