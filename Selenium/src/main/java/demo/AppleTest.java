package demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AppleTest {

	private static final String proxyHost = "127.0.0.1:8080";
	private static final String proxURL = "http://" + proxyHost + "/";
	private static enum ProxyAction { START, STOP };
	
	private static WebDriver driver;

	public static void main(String[] args) {
		driverInit();
		try {
			testPageLoad();
			testPageNavigate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTest();
		}
	}

	private static void driverInit() {
		String chromeDriverPath = AppleTest.class.getResource("/chromedriver.exe").getPath(); //Chrome Driver is in Resource folder of this project
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		// Add the WebDriver proxy capability.
		Proxy proxy = new Proxy();
		proxy.setHttpProxy(proxyHost);
		capabilities.setCapability("proxy", proxy); //Setting the proxy
		
		driver = new ChromeDriver(capabilities);
	}

	private static void testPageLoad() {
		triggerCapture(); //Sends GET request to Proxy to make it start capturing Analytics call
		loadPage("http://www.apple.com/");
		JsonArray formDatas = getCaptured(); //Receive the captured Analytics data from Proxy server
		validate((JsonObject) formDatas.get(0), "PAGELOAD", "apple"); //Getting the first captured analytic call only for Demo purpose and validating it.
	}

	private static void testPageNavigate() {
		triggerCapture(); //Sends GET request to Proxy to make it start capturing Analytics call
		navigatePage("MAC_LINK");
		JsonArray formDatas = getCaptured(); //Receive the captured Analytics data from Proxy server
		validate((JsonObject) formDatas.get(0), "PAGENAVIGATE", "mac"); //Validate the Analytics call. Does validation for a specific parameter only for Demo
	}

	private static void validate(JsonObject formData, String validationType, String expectedValue) {
		//For demo purpose only a particular attribute is being validated
		if (validationType.equalsIgnoreCase("PAGELOAD") || validationType.equalsIgnoreCase("PAGENAVIGATE")) {
			String actualVal = formData.get("pageName").getAsString(); //Get the pageName attribute in the Analytics data
			actualVal = actualVal.split("-")[0].trim();
			if (expectedValue.equalsIgnoreCase(actualVal)) { //Comparing the value in Analytics data to the expected value: apple for landing page and mac for the Mac page
				customReport("PASS", "Analytics Validation for " + validationType + " Passed");
			} else {
				customReport("FAIL", "Analytics Validation for " + validationType + " Failed " + "Expected: " + expectedValue + " ActualValue: " + actualVal);
			}
		}
	}

	private static void loadPage(String pageURL) {
		driver.get(pageURL);
		customWait();
		customReport("INFO", "Page Loaded-" + pageURL);
	}

	private static void navigatePage(String clickElem) {
		WebElement macElement = driver.findElement(getBy(clickElem));
		macElement.click();
		customWait();
		customReport("INFO", "Page Navigated to " + clickElem);
	}

	private static void endTest() {
		driver.close();
		driver.quit();
	}

	private static By getBy(String elemName) {
		if (elemName.equalsIgnoreCase("MAC_LINK")) {
			return By.className("ac-gn-link-mac");
		}

		return null;
	}

	private static void customWait() {
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void customReport(String status, String message) {
		System.out.println(status + "::" + message);
	}

	private static void triggerCapture() {
		messageProxy(ProxyAction.START); //Sends a GET request to proxy server: http://127.0.0.1/start
	}

	private static JsonArray getCaptured() {
		String analyticsData = messageProxy(ProxyAction.STOP); //Sends a GET request to proxy server: http://127.0.0.1/stop and gets the data returned
		return (JsonArray)new JsonParser().parse(analyticsData); //Parses the json string received into JSONArray
	}
	
	private static String messageProxy(ProxyAction proxyAction ){
		//Standard JAVA code to send GET Request
		String url = proxURL + proxyAction.toString().toLowerCase();

		try{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// optional default is GET
			con.setRequestMethod("GET");
	
//			int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	
			//print result
//			System.out.println("Response" + response.toString());		
			return response.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}

}
