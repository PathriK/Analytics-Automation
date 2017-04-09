package analytics.automation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class MessageProxy {
	private final String proxyURL;
	private final String metricsDomain;

	private static enum ProxyAction {
		START, STOP
	};

	public MessageProxy(String proxyHost, String metricsDomain) {
		this.proxyURL = "http://" + proxyHost + "/";
		this.metricsDomain = metricsDomain;
	}

	public void startRecording() {
		this.send(ProxyAction.START);
	}

	public JsonArray stopRecording() {
		String analyticsData = this.send(ProxyAction.STOP); //Sends a GET request to proxy server: http://127.0.0.1/stop and gets the data returned
		return (JsonArray) new JsonParser().parse(analyticsData); //Parses the json string received into JSONArray
	}

	private String send(ProxyAction proxyAction) {
		//Standard JAVA code to send GET Request
		String url = proxyURL + proxyAction.toString().toLowerCase();

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			
			con.addRequestProperty("metrics-domain", metricsDomain);

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
