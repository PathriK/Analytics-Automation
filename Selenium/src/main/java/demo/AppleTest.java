package demo;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AppleTest {

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
		driver = new ChromeDriver();
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
		// TODO Auto-generated method stub

	}

	private static JsonArray getCaptured() {
		return (JsonArray) (new JsonParser()).parse("[{'pageName':'mac - index'}]");
	}

}
