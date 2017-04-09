package demo;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import analytics.automation.MessageProxy;

public class AppleTest {

	private static final String proxyHost = "127.0.0.1:8080";
	private static final String metricsDomain = "metrics.apple.com";
	private static MessageProxy messageProxy;

	
	private static WebDriver driver;

	public static void main(String[] args) {
		driverInit();
		messageProxy = new MessageProxy(proxyHost, metricsDomain); //Initializing Message Proxy
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
		messageProxy.startRecording(); //Sends GET request to Proxy to make it start capturing Analytics call
		loadPage("http://www.apple.com/");
		JsonArray formDatas = messageProxy.stopRecording(); //Receive the captured Analytics data from Proxy server
		if(formDatas.isJsonArray() && formDatas.size() > 0){
			validate((JsonObject) formDatas.get(0), "PAGELOAD", "apple"); //Getting the first captured analytic call only for Demo purpose and validating it.
		}else{
			customReport("ERROR", "Analytics Validation for PAGELOAD failed. Nothing was recorded");
		}
			
	}

	private static void testPageNavigate() {
		messageProxy.startRecording(); //Sends GET request to Proxy to make it start capturing Analytics call
		navigatePage("MAC_LINK");
		JsonArray formDatas = messageProxy.stopRecording(); //Receive the captured Analytics data from Proxy server
		if(formDatas.isJsonArray() && formDatas.size() > 0){
			validate((JsonObject) formDatas.get(0), "PAGENAVIGATE", "mac"); //Validate the Analytics call. Does validation for a specific parameter only for Demo
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
}
