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

	private static final String proxURL = "http://127.0.0.1:8080/";
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
		String chromeDriverPath = AppleTest.class.getResource("/chromedriver.exe").getPath();
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		// Add the WebDriver proxy capability.
		Proxy proxy = new Proxy();
		proxy.setHttpProxy("127.0.0.1:8080");
		capabilities.setCapability("proxy", proxy);
		
		driver = new ChromeDriver(capabilities);
	}

	private static void testPageLoad() {
		triggerCapture();
		loadPage("http://www.apple.com/");
		JsonArray formDatas = getCaptured();
		validate((JsonObject) formDatas.get(0), "PAGELOAD", "apple");
	}

	private static void testPageNavigate() {
		triggerCapture();
		navigatePage("MAC_LINK");
		JsonArray formDatas = getCaptured();
		validate((JsonObject) formDatas.get(0), "PAGENAVIGATE", "mac");
	}

	private static void validate(JsonObject formData, String validationType, String expectedValue) {
		if (validationType.equalsIgnoreCase("PAGELOAD") || validationType.equalsIgnoreCase("PAGENAVIGATE")) {
			String actualVal = formData.get("pageName").getAsString();
			actualVal = actualVal.split("-")[0].trim();
			if (expectedValue.equalsIgnoreCase(actualVal)) {
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
		messageProxy(ProxyAction.START);
	}

	private static JsonArray getCaptured() {
		String analyticsData = messageProxy(ProxyAction.STOP);
		return (JsonArray)new JsonParser().parse(analyticsData);
	}
	
	private static String messageProxy(ProxyAction proxyAction ){
		String url = proxURL + proxyAction.toString().toLowerCase();

		try{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// optional default is GET
			con.setRequestMethod("GET");
	
//			int responseCode = con.getResponseCode();
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
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
