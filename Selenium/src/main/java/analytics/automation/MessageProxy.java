package analytics.automation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class MessageProxy {
	private ProxyConfig proxyConfig;

	private static enum ProxyAction {
		START, STOP
	};

	public MessageProxy(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
	}

	public void startRecording() {
		this.send(ProxyAction.START);
	}

	public JsonArray stopRecording() {
		// Sends a GET request to proxyserver:http://127.0.0.1/stop and gets the data returned
		String analyticsData = this.send(ProxyAction.STOP);
		// Parses the json string received into JSONArray
		return (JsonArray) new JsonParser().parse(analyticsData);
	}

	private String send(ProxyAction proxyAction) {
		// Standard JAVA code to send GET Request
		String url = proxyConfig.getAnalyticsProxyURL() + proxyAction.toString().toLowerCase();

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			if (proxyAction.equals(ProxyAction.START)) {
				con.setDoOutput(true);
				con.setDoInput(true);
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestMethod("POST");

				OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
				wr.write(proxyConfig.getProxyConfig().toString());
				wr.flush();
			} else {
				con.setRequestMethod("GET");
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
