package demo;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import analytics.automation.MessageProxy;
import analytics.automation.ProxyConfig;

public class SamsungTest {
	private static final String analyticsProxyURL = "127.0.0.1:8080";
	private static final String metricsFilter = ".*nmetrics.samsung.com.*";
	private static final String proxyHost = "127.0.0.1";	
	private static final String proxyPort = "8877";
	private static final String homePage = "http://www.samsung.com/in/";
	private static final String pageLoad = "in:home";
	private static final String pageNavigate = "in:support";
	
	private static MessageProxy messageProxy;

	
	private static WebDriver driver;

	public static void main(String[] args) {
		driverInit();
		ProxyConfig proxyConfig = new ProxyConfig()
				.setAnalyticsProxy(analyticsProxyURL)
				.setFilterRegEx(metricsFilter)
				.setProxyURL(proxyHost)
				.setProxyPort(proxyPort);
		messageProxy = new MessageProxy(proxyConfig); //Initializing Message Proxy
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
		String chromeDriverPath = SamsungTest.class.getResource("/chromedriver.exe").getPath(); //Chrome Driver is in Resource folder of this project
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		// Add the WebDriver proxy capability.
		Proxy proxy = new Proxy();
		proxy.setHttpProxy(analyticsProxyURL);
		capabilities.setCapability("proxy", proxy); //Setting the proxy
		
		driver = new ChromeDriver(capabilities);
	}

	private static void testPageLoad() throws InterruptedException {
		messageProxy.startRecording(); //Sends GET request to Proxy to make it start capturing Analytics call
		loadPage(homePage);
//		TimeUnit.SECONDS.sleep(10);
		JsonArray formDatas = messageProxy.stopRecording(); //Receive the captured Analytics data from Proxy server
		if(formDatas.isJsonArray() && formDatas.size() > 0){
			validate((JsonObject) formDatas.get(0), "PAGELOAD", pageLoad); //Getting the first captured analytic call only for Demo purpose and validating it.
		}else{
			customReport("ERROR", "Analytics Validation for PAGELOAD failed. Nothing was recorded");
		}
			
	}

	private static void testPageNavigate() throws InterruptedException {
		messageProxy.startRecording(); //Sends GET request to Proxy to make it start capturing Analytics call
		navigatePage("SUPPORT_LINK");
//		TimeUnit.SECONDS.sleep(10);
		JsonArray formDatas = messageProxy.stopRecording(); //Receive the captured Analytics data from Proxy server
		if(formDatas.isJsonArray() && formDatas.size() > 0){
			for (JsonElement jsonElement : formDatas) {
				JsonObject obj = (JsonObject)jsonElement;
				if(obj.has("events")){
					if(obj.get("events").getAsString().equalsIgnoreCase("event1")){
						//Validate the Analytics call. Does validation for a specific parameter only for Demo
						validate(obj, "PAGENAVIGATE", pageNavigate); 			
					}
				}				
			}
		}else{
			customReport("ERROR", "Analytics Validation for PAGENAVIGATE failed. Nothing was recorded");
		}
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
		if (elemName.equalsIgnoreCase("SUPPORT_LINK")) {
			return By.linkText("SUPPORT");
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
}
