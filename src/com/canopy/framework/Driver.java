package com.canopy.framework;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import cucumber.api.testng.TestNGCucumberRunner;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;

public abstract class Driver implements Runnable {
	
	protected HashMapNew Environment;
	protected HashMapNew Dictionary;
	protected WebDriverFactory driverFactory;
	protected String driverType;
	protected WebDriver driver;
	private static int totalPassedTCs;
	private static int totalFailedTCs;
	private static int totalSkippedTCs;
	protected BaseUtil base;
	static boolean bThreadFlag = false;
	protected TestNGCucumberRunner testNGCucumberRunner;
	private Date g_StartTime;
	private Date g_EndTime;
	
	public Driver() {
		Dictionary = new HashMapNew();
		driverFactory = new WebDriverFactory();
		base = new BaseUtil(driverFactory, Dictionary, Environment);
		Environment = base.getEnvValues();
	}
	
	@BeforeSuite(alwaysRun = true)
	public void setup(final ITestContext testContext) throws IOException {
		g_StartTime = new Date();
		totalPassedTCs = 0;
		totalFailedTCs = 0;
		totalSkippedTCs = 0;
		if(new File("target/cucumber-html-reports").exists()) {
			BaseUtil.deleteFile(new File("target/cucumber-html-reports"));
		}
		File element = new File("target");
		for (File sub : element.listFiles()) {
			if(sub.getName().trim().startsWith("cucumber-report-feature-composite"))
				BaseUtil.deleteFile(sub);
        }
	}
	
	@Parameters({ "browser" })
	@BeforeTest(alwaysRun = true)
	public void setupTest(@Optional("chrome") final String browser, ITestContext context) throws Exception {
		testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
		driverType = browser;
		driverFactory.setDriverType(browser);
		String env = System.getProperty("env") != null && !System.getProperty("env").trim().equalsIgnoreCase("") ? System.getProperty("env").trim() : Environment.get("env").trim();
		Environment.putAll(base.getEnvValues(env));
    	HashMapNew temp = base.GetXMLNodeValue("/src/Configuration.xml", "//" + driverType.toLowerCase(), 0);
    	if(temp != null){
    		Environment.putAll(temp);
    	}
	}
	
	@BeforeMethod(alwaysRun = true)
	public void init(Method method, Object[] args, ITestContext context) {
		String testName = driverType.trim().toUpperCase().substring(0, driverType.trim().length() - 1);
    	
    	if (args.length > 0) {
            if (args[0] != null) {
            	String str = String.valueOf(args[0]);
            	testName += " (" + str.toString() + ")";
            }
        }
		System.out.println("########################" + testName + " EXECUTION STARTED########################");
		WebDriver driver = createDriver();
		this.driver = driver;
	}
	
	@AfterMethod(alwaysRun = true)
    public void quitDriver(ITestResult tr, Object[] args) throws Exception {
		String testName = driverType.trim().toUpperCase().substring(0, driverType.trim().length() - 1);
		
		if (args.length > 0) {
            if (args[0] != null) {
            	String str = String.valueOf(args[0]);
            	testName += " (" + str.toString() + ")";
            }
        }
			
		if(tr.getStatus() == 1){
			System.out.println("########################" + testName + " EXECUTION PASSED########################");
			totalPassedTCs += 1;
		} else if(tr.getStatus() == 3){
			System.out.println("########################" + testName + " EXECUTION SKIPPED########################");
			totalSkippedTCs += 1;
		} else if(tr.getStatus() == 2){
			System.out.println("########################" + testName + " EXECUTION FAILED########################");
			totalFailedTCs += 1;
		}
			
		if(this.driver != null) {
			try {
				this.driver.quit();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			WebDriver driver = driverFactory.getDriver();
			driver = null;
			driverFactory.setDriver(driver);
    	}
    }
	
	@AfterClass(alwaysRun = true)
	public void closeTestSummary() throws IOException {
		if(testNGCucumberRunner != null)
			testNGCucumberRunner.finish();
		if(new File("target/cucumber-report-feature-composite.json").exists()) {
			java.util.Date today = new java.util.Date();
			Timestamp now = new java.sql.Timestamp(today.getTime());
			String tempNow[] = now.toString().split("\\.");
			final String sStartTime = tempNow[0].replaceAll(":", ".").replaceAll(" ", "T");
			String ReportFilePath = "target";
			BaseUtil.copyFolder(new File("target/cucumber-report-feature-composite.json"), new File(ReportFilePath + "/cucumber-report-feature-composite-" + sStartTime + ".json"));
		}
	}
	
    @AfterSuite(alwaysRun=true)
	public void tearDown() {
    	g_EndTime = new Date();
		if(g_StartTime != null && g_EndTime != null){
			String strTimeDifference = base.fnTimeDiffference(g_StartTime.getTime(), g_EndTime.getTime());
			System.out.println("Total suite execution time : " + strTimeDifference);
			System.out.println("Total passed test cases : " + totalPassedTCs);
			System.out.println("Total failed test cases : " + totalFailedTCs);
			System.out.println("Total skipped test cases : " + totalSkippedTCs);
			generateCucumberConsolidatedReport();
		}
    }
    
    public void generateCucumberConsolidatedReport() {
    	File reportOutputDirectory = new File(System.getProperty("user.dir") + "/target");
    	List<String> jsonFiles = new ArrayList<String>();
    	for(File sub : reportOutputDirectory.listFiles()) {
    		if(sub.getName().trim().startsWith("cucumber-report-feature-composite") && !sub.getName().trim().startsWith("cucumber-report-feature-composite.json"))
    			jsonFiles.add(sub.getAbsolutePath());
    	}
    	String buildName = System.getProperty("buildName") != null && !System.getProperty("buildName").trim().equalsIgnoreCase("") ? System.getProperty("buildName").trim() : Environment.get("buildName").trim();
    	String buildNumber = buildName;
    	String projectName = "Canopy Cucumber Project";
    	boolean runWithJenkins = false;
    	boolean parallelTesting = true;

    	Configuration configuration = new Configuration(reportOutputDirectory, projectName);
    	configuration.setParallelTesting(parallelTesting);
    	configuration.setRunWithJenkins(runWithJenkins);
    	configuration.setBuildNumber(buildNumber);
    	configuration.addClassifications("Platform", "MacOSX");
    	configuration.addClassifications("Browser", "Chrome");
    	configuration.addClassifications("Branch", "master");
    	configuration.setTrends(new File("target/trends"), 15);
    	
    	ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
    	reportBuilder.generateReports();
    }
    
    @DataProvider(name = "scenarios", parallel = false)
    public Object[][] scenarios() {
        return testNGCucumberRunner.provideScenarios();
    }
    
    public void run() {}
	
	public WebDriver createDriver() {
    	try{
	    	WebDriver driver = null;
	    	if(driverType.trim().toUpperCase().contains("CHROME")) {
				driver = initializeChrome();
			}
			else if(driverType.trim().toUpperCase().contains("FIREFOX")){
				driver = initializeFirefox();
			}
			else {
				System.out.println("SKIPEXCEPTION :: " + "Invalid driver type " + driverType);
				throw new SkipException("Invalid driver type " + driverType);
			}
	    	String _dimension = "1280x960";
	    	String[] dimens = _dimension.trim().toLowerCase().split("x");
			int x = Integer.valueOf(dimens[0]);
			int y = Integer.valueOf(dimens[1]);
			driver.manage().window().setSize(new Dimension(x, y));
	        driver.manage().timeouts().implicitlyWait(Integer.parseInt(Environment.get("implicitWait")), TimeUnit.MILLISECONDS);
	        driverFactory.setDriverType(driverType);
	        final WebDriver newDriver = driver;
	        driverFactory.setDriver(newDriver);
	        driverFactory.setDictionary(Dictionary);
	        driverFactory.setEnvironment(Environment);
	        base = new BaseUtil(driverFactory, Dictionary, Environment);
	        return driver;
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	return null;
    }
	
	WebDriver initializeChrome() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1280,960");
		String chromeArgument = System.getProperty("chromeArgument") != null && !System.getProperty("chromeArgument").trim().equalsIgnoreCase("") ? System.getProperty("chromeArgument").trim() : Environment.get("chromeArgument").trim();
		if(!chromeArgument.trim().equalsIgnoreCase(""))
			options.addArguments(chromeArgument.trim());
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_settings.popups", 0);
		options.setExperimentalOption("prefs", prefs);
		WebDriver driver;
		String chromedriver = System.getProperty("chromedriverPath") != null && !System.getProperty("chromedriverPath").trim().equalsIgnoreCase("") ? System.getProperty("chromedriverPath").trim() : Environment.get("executablePath").trim();
		if(!new File(chromedriver).exists()){
			System.out.println("SKIPEXCEPTION :: " + "Chromedriver executable not found in root directory");
			throw new SkipException("Chromedriver executable not found in root directory");
		}
		System.setProperty("webdriver.chrome.driver", chromedriver);
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		driver = new ChromeDriver(capabilities);
		return driver;
    }
	
	WebDriver initializeFirefox() {
		FirefoxProfile profile;
		profile = new FirefoxProfile();
		
		if(!Environment.get("firefox_extension_file_path").trim().equalsIgnoreCase("")){
			File addonpath = new File(System.getProperty("user.dir") + Environment.get("firefox_extension_file_path"));
			profile.addExtension(addonpath);
		}
		
		profile.setAssumeUntrustedCertificateIssuer(false);
		profile.setAcceptUntrustedCertificates(true);
		profile.setPreference("xpinstall.signatures.required", false);
		profile.setPreference("browser.startup.homepage_override.mstone", "ignore"); 
		profile.setPreference("startup.homepage_welcome_url.additional", "about:blank");
		
		WebDriver driver;
		String firefoxdriver = System.getProperty("firefoxdriverPath") != null && !System.getProperty("firefoxdriverPath").trim().equalsIgnoreCase("") ? System.getProperty("firefoxdriverPath").trim() : Environment.get("executablePath").trim();
		if(!new File(firefoxdriver).exists()){
			System.out.println("SKIPEXCEPTION :: " + "Firefoxdriver executable not found in root directory");
			throw new SkipException("Firefoxdriver executable not found in root directory");
		}
		System.setProperty("webdriver.gecko.driver", firefoxdriver);
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability("acceptSslCerts", true);
		capabilities.setCapability("handlesAlerts", true);
		driver = new FirefoxDriver(capabilities);
		return driver;
    }
	
	public static class HashMapNew extends HashMap<String, String>{
		static final long serialVersionUID = 1L;
		public String get(Object key){
			String value = (String)super.get(key);
			if (value == null) {
				return "";
			}
			return value;
		}
		
		public String put(String key, String value) {
			String val = super.put(key, value);
			WebDriverFactory driverFactory = new WebDriverFactory();
			final HashMapNew me = this;
			while (bThreadFlag) {
				try{
					Thread.sleep(500L);
				}
				catch (Exception localException1) {}
			}
		    
		    bThreadFlag = true;
			if(this.toString().contains("APP_URL="))
				driverFactory.setEnvironment(me);
			else
				driverFactory.setDictionary(me);
			bThreadFlag = false;
			return val;
		}
	}
}