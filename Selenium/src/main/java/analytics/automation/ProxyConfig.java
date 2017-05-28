package analytics.automation;

import com.google.gson.JsonObject;

public class ProxyConfig {
	private String analyticsProxyURL;
	private String filterRegEx = ".*";
	private String proxyURL = "127.0.0.1";
	private String proxyPort = "8080";

	private boolean hasProxy = false;

	public ProxyConfig setAnalyticsProxy(String analyticsProxyDomain) {
		this.analyticsProxyURL = "http://" + analyticsProxyDomain + "/";
		return this;
	}

	public ProxyConfig setFilterRegEx(String filterRegEx) {
		this.filterRegEx = filterRegEx;
		return this;
	}

	public ProxyConfig setProxyURL(String proxyURL) {
		hasProxy = true;
		this.proxyURL = proxyURL;
		return this;
	}

	public ProxyConfig setProxyPort(String proxyPort) {
		hasProxy = true;
		this.proxyPort = proxyPort;
		return this;
	}

	public String getAnalyticsProxyURL() {
		return analyticsProxyURL;
	}

	public JsonObject getProxyConfig() {
		JsonObject proxyConfig = new JsonObject();
		proxyConfig.addProperty("Filter", this.filterRegEx);
		proxyConfig.addProperty("HasProxy", this.hasProxy);
		if (hasProxy) {
			proxyConfig.addProperty("ProxyURL", this.proxyURL);
			proxyConfig.addProperty("ProxyPort", this.proxyPort);
		}
		return proxyConfig;
	}
}
