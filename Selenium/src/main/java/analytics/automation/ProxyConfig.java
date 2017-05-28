package analytics.automation;

import com.google.gson.JsonObject;

public class ProxyConfig {
	private String analyticsProxyURL;
	public String getAnalyticsProxyURL() {
		return analyticsProxyURL;
	}

	private String filterRegEx = "*";
	private String proxyURL = "";
	private String proxyPort = "";

	public ProxyConfig setAnalyticsProxy(String analyticsProxyDomain) {
		this.analyticsProxyURL = "http://" + analyticsProxyDomain + "/";
		return this;
	}

	public ProxyConfig setFilterRegEx(String filterRegEx) {
		this.filterRegEx = filterRegEx;
		return this;
	}

	public ProxyConfig setProxyURL(String proxyURL) {
		this.proxyURL = proxyURL;
		return this;
	}

	public ProxyConfig setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}

	public JsonObject getProxyConfig() {
		JsonObject proxyConfig = new JsonObject();
		proxyConfig.addProperty("Filter", this.filterRegEx);
		proxyConfig.addProperty("ProxyURL", this.proxyURL);
		proxyConfig.addProperty("ProxyPort", this.proxyPort);

		return proxyConfig;
	}
}
