package baseclass;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


import org.json.simple.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;

import Armorcode.login;

import Armorcode.login.TestRails;
import io.github.bonigarcia.wdm.WebDriverManager;

public class baseclass {


	protected static WebDriver driver;

	public static String Test_Run_ID = "1";

	public static String Test_Rail_Username = "tgpunithkumar@gmail.com";
	public static String Test_Rail_Password = "pUnI@3kt";

	public static String TestRail_URL = "https://tgpunithkumar.testrail.io/";

	public static int TestCase_Pass_Status = 1;
	public static int TestCase_Fail_Status = 5;

	APIClient client;

	@BeforeSuite
	public void createSuite(ITestContext ctx) throws IOException, APIException {
		/*
		 * Login to TestRail
		 */
		client = new APIClient(TestRail_URL);
		client.setUser(Test_Rail_Username);
		client.setPassword(Test_Rail_Password);
		/*
		 * Create Test Run 
		 */
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("include_all",true);
		data.put("name","TestNG Run "+System.currentTimeMillis());
		JSONObject c = null;
		c = (JSONObject)client.sendPost("add_run/"+Test_Run_ID,data);
		/*
		 * //Extract Test Run Id
		 */
		Long suite_id = (Long)c.get("id");
		ctx.setAttribute("suiteId",suite_id);
	}

	@BeforeTest
	public void LaunchApplication() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://app.armorcode.com/#/login?/");
	}
	@AfterTest
	public void TearDown() {
		driver.manage().deleteAllCookies();
		driver.quit();
	}
	@BeforeMethod
	public void beforeTest(ITestContext ctx,Method method) throws NoSuchMethodException {
		Method m = login.class.getMethod(method.getName());
		if (m.isAnnotationPresent((Class<? extends Annotation>) TestRails.class)) {
			TestRails ta = m.getAnnotation(TestRails.class);
			ctx.setAttribute("caseId",ta.id());
		}
	}
	@AfterMethod
	public void afterTest(ITestResult result, ITestContext ctx) throws IOException, APIException {
		Map<String, Object> data = new HashMap<String, Object>();
		if(result.isSuccess()) {
			data.put("status_id",TestCase_Pass_Status);
		}
		else{
			data.put("status_id",TestCase_Fail_Status);
			data.put("comment", result.getThrowable().toString());
		}
		String caseId = (String)ctx.getAttribute("caseId");
		Long suiteId = (Long)ctx.getAttribute("suiteId");
		client.sendPost("add_result_for_case/"+suiteId+"/"+caseId,data);

	}

}
