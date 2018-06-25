package com.canopy.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.canopy.framework.Driver.HashMapNew;;

public class BaseUtil {
	
	private static long DEFAULT_FIND_ELEMENT_TIMEOUT;
	public WebDriverFactory driverFactory;
	public HashMapNew Dictionary;
	public HashMapNew Environment;
	public WebDriver driver;
	public String driverType;
	
	public BaseUtil(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment) {
		this.driverFactory = driverFactory;
		this.driver = driverFactory.getDriver();
		this.driverType = driverFactory.getDriverType();
		this.Dictionary = Dictionary == null || Dictionary.size() == 0 ? (driverFactory.getDictionary() == null ? null : driverFactory.getDictionary()) : Dictionary;
		this.Environment = Environment == null || Environment.size() == 0 ? (driverFactory.getEnvironment() == null ? null : driverFactory.getEnvironment()) : Environment;
		if(this.Environment != null)
			DEFAULT_FIND_ELEMENT_TIMEOUT = this.Environment.get("implicitWait").trim().equalsIgnoreCase("") ? 26 : Long.valueOf(this.Environment.get("implicitWait")) / 1000;
	}
	
	public HashMapNew getEnvValues() {
		HashMapNew temp;
		temp = GetXMLNodeValue("/src/Configuration.xml", "//common", 0);
		if(temp != null){
			String env = System.getProperty("env") != null && !System.getProperty("env").trim().equalsIgnoreCase("") ? System.getProperty("env").trim() : temp.get("env");
			temp.put("env", env);
			String version = temp.get("version");
			String envFilePath = temp.get("envFilePath");
			if(!envFilePath.trim().equalsIgnoreCase("")){
				if(!env.trim().equalsIgnoreCase("") && !version.trim().equalsIgnoreCase(""))
					temp.putAll(GetXMLNodeValue(envFilePath, "//" + env + "/" + version, 0));
				else if(!env.trim().equalsIgnoreCase(""))
					temp.putAll(GetXMLNodeValue(envFilePath, "//" + env, 0));
			}
		}
		return temp;
	}
	
	public HashMapNew getEnvValues(String env) {
		env = env.trim();
		String environment = env;
		HashMapNew temp = GetXMLNodeValue("/src/Configuration.xml", "//common", 0);
		if(temp != null){
			temp.put("env", env.trim());
			String version = temp.get("version");
			String envFilePath = temp.get("envFilePath");
			if(!envFilePath.trim().equalsIgnoreCase("")){
				if(!env.trim().equalsIgnoreCase("") && !version.trim().equalsIgnoreCase(""))
					temp.putAll(GetXMLNodeValue(envFilePath, "//" + env + "/" + version, 0));
				else if(!env.trim().equalsIgnoreCase(""))
					temp.putAll(GetXMLNodeValue(envFilePath, "//" + env, 0));
			}
			environment = temp.get("env");
		}
		
		temp.put("env", environment.trim());
		return temp;
	}
	
	public HashMapNew GetXMLNodeValue(String path, String parentNode, int index){
		HashMapNew dict = new HashMapNew();
	    String RootPath = System.getProperty("user.dir");
	    try
	    {
	      String xmlPath = RootPath + path;
	      File fXmlFile = new File(xmlPath);
	      
	      if(!fXmlFile.exists())
	    	  return dict;
	      
	      DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
	      DocumentBuilder docBuilder = dbFac.newDocumentBuilder();
	      Document xmldoc = docBuilder.parse(fXmlFile);
	      
	      XPathFactory xPathfac = XPathFactory.newInstance();
	      XPath xpath = xPathfac.newXPath();

	      XPathExpression expr = xpath.compile(parentNode);
	      Object obj = expr.evaluate(xmldoc, XPathConstants.NODESET);
	      if(obj != null){
	    	  Node node = ((NodeList)obj).item(index);
	    	  if(node != null){
			      NodeList nl = node.getChildNodes();
			      for (int child = 0; child < nl.getLength(); child++) {
			    	  dict.put(nl.item(child).getNodeName().trim(), nl.item(child).getTextContent().trim());
			      }
	    	  }
	      }
	    }
	    catch (Exception excep) {
	    	excep.printStackTrace();
	    }
	    
	    return dict;
	}

	public String fnTimeDiffference(long startTime, long endTime) {
		long delta = endTime - startTime;
		int days = (int)delta / 86400000;
		delta = (int)delta % 86400000;
		int hrs = (int)delta / 3600000;
		delta = (int)delta % 3600000;
		int min = (int)delta / 60000;
		delta = (int)delta % 60000;
		int sec = (int)delta / 1000;
		
		String strTimeDifference = days + "d " + hrs + "h " + min + "m " + sec + "s";
		return strTimeDifference;
	}
	
	public void click(By locator, boolean throughJavascript, long... waitSeconds) throws Exception {
		int counter = !Environment.get("noOfRetriesForSameOperation").trim().equalsIgnoreCase("") ? Integer.valueOf(Environment.get("noOfRetriesForSameOperation").trim()) : 2;
		while(counter >= 0){
			try{
				WebElement we = getElementWhenClickable(locator, waitSeconds);
				if(we != null){
					javascriptClick(we, throughJavascript);
					break;
				}
			} catch(Exception ex){
				if(counter == 0){
					throw ex;
				}
				sync(500L);
				counter--;
			}
		}
	}
		
	public WebElement getElementWhenClickable(By locator, long...waitSeconds) {
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebDriverWait wait  = new WebDriverWait(driver, seconds);
		WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		return element;
	}
	
	public boolean javascriptClick(WebElement webElement, boolean throughJavascript) throws Exception {   	 		
        int intCount = 1;        
        while (intCount<=4){
        	try {
        		if(throughJavascript) {
	        		try {
	        			((JavascriptExecutor) driver).executeScript("return arguments[0].click()", webElement);
	        		} catch(WebDriverException we) {
	        			webElement.click();
	        		}
        		} else {
        			webElement.click();
        		}
        		break;
	        }catch (Exception e){
	        	sync(500L);
	        	if(intCount==4){
	    	    	throw e;
	        	}
    	    }  	    
    	    intCount++;
        }	        
        return true;    	       
    }
	
	public void sync(Long sTime) {
		try {
			Thread.sleep(sTime);
		} catch (InterruptedException e) {			
			//Do Nothing
		}
	}
	
	public void type(By locator, String objName, String textToType, boolean skipValueCheck, long... waitSeconds) throws Exception{
		WebElement we = getElementWhenVisible(locator, waitSeconds);
		int intCount = 1;        
		while (intCount <= 4){
			try {	        		
				clear(we);
				sendKeys(we, textToType);
				if(skipValueCheck)
					break;
				if(((driverType.trim().toUpperCase().contains("CHROME") || driverType.trim().toUpperCase().contains("FIREFOX")) && we.getAttribute("value").trim().equalsIgnoreCase(textToType.trim())) || we.getText().trim().equalsIgnoreCase(textToType.trim()) || we.getAttribute("name").trim().equalsIgnoreCase(textToType.trim()))
					break;
			}catch (Exception e){	
				we = getElementWhenVisible(locator, waitSeconds);
			}
			if(intCount==4){
				throw new Exception("Not able to enter text - " + textToType + " into editbox - " + objName.toLowerCase());
			}
			intCount++;
		}
	}
	
	public WebElement getElementWhenVisible(By locater, long... waitSeconds){
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebDriverWait wait  = new WebDriverWait(driver, seconds);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locater));
		return element;
	}
	
	public void clear(WebElement we) {
//		if(driverType.trim().toUpperCase().contains("CHROME")) {
//			Actions navigator = new Actions(driver);
//		    navigator.click(we)
//		        .sendKeys(Keys.END)
//		        .keyDown(Keys.SHIFT)
//		        .sendKeys(Keys.HOME)
//		        .keyUp(Keys.SHIFT)
//		        .sendKeys(Keys.BACK_SPACE)
//		        .perform();
//		} else
			we.clear();
	}
	
	public void sendKeys(WebElement we, CharSequence... textToType) throws Exception{
		we.sendKeys(textToType);
	}
	
	public Object getGDValue(Object value) {
		Object newValue = null;
		if(value instanceof String){
			if(((String)value).trim().endsWith("L") && StringUtils.isNumeric(((String)value).trim().substring(0, ((String)value).trim().length() - 1)))
				return Long.valueOf(((String)value).trim().substring(0, ((String)value).trim().length() - 1));
			value = getComplexValue((String) value, "%{GD_");
			newValue = getComplexValue((String) value, "%{ENV_");
		}
		else{
			newValue = value;
		}
		if(((String)newValue).trim().startsWith("\""))
			newValue = ((String)newValue).trim().substring(1);
		if(((String)newValue).trim().endsWith("\""))
			newValue = ((String)newValue).trim().substring(0, ((String)newValue).trim().length() - 1);
		return newValue;
	}
	
	public String getComplexValue(String value, String prefix) {
		Stack<String> pos = new Stack<String>();
		while(value.contains(prefix)){
			String initialString = value.substring(0, value.indexOf(prefix));
			pos.push(initialString);
			pos.push(prefix);
			int startindex = value.indexOf(prefix) + 5;
			int endindex =  value.indexOf("}");
			String nextValue = value.substring(startindex);
			int firstendindex = nextValue.indexOf("}");
			int lastendindex = nextValue.indexOf("%");
			if(lastendindex != -1 && lastendindex < firstendindex) {
				value = value.substring(startindex);
				continue;
			} else {
				String key = value.trim().substring(startindex, endindex).trim();
				value = value.substring(endindex + 1);
				pos.push(key);
				String _value;
				if(prefix.trim().equalsIgnoreCase(prefix))
					_value = Dictionary.containsKey(key) ? Dictionary.get(key).trim() : Environment.get(key).trim();
				else
					_value = Environment.containsKey(key) ? Environment.get(key).trim() : Dictionary.get(key).trim();
				pos.pop();
				pos.pop();
				value = pos.pop() + _value + value;
			}
		}
		if(!pos.empty()) {
			while(!pos.empty()) {
				value = pos.pop() + value;
			}
			value = getComplexValue(value, prefix);
		}
		return value;
	}
	
	public void load(String uri) {
		driver.get(Environment.get("APP_URL").trim() + uri);
		Assert.assertTrue(true, "Verify page launched - " + Environment.get("APP_URL").trim() + uri);
	}
	
	public static void copyFolder(File src, File dest) throws IOException{
		if(src.isDirectory()){
			if(!dest.exists()){
			   dest.mkdir();
			   System.out.println("Directory copied from "+ src + "  to " + dest);
			}
			String files[] = src.list();
 
			for (String file : files) {
			   File srcFile = new File(src, file);
			   File destFile = new File(dest, file);
			   copyFolder(srcFile,destFile);
			}
		} else{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0){
			   out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}
	
	public static void deleteFile(File element) {
	    if (element.isDirectory()) {
	        for (File sub : element.listFiles()) {
	            deleteFile(sub);
	        }
	    }
	    element.delete();
	}
	
	public void navigateTo(String uri) {
		driver.navigate().to(Environment.get("APP_URL") + uri.trim());
	}
	
	public WebElement getElementWhenRefreshed(final By locater, final String attribute, final String text, long... waitSeconds){
		assert waitSeconds.length <= 1;
		long seconds = waitSeconds.length > 0 ? waitSeconds[0] : DEFAULT_FIND_ELEMENT_TIMEOUT;
		WebElement we = null;
		WebDriverWait wait  = new WebDriverWait(driver, seconds);
		Boolean val = wait.until(ExpectedConditions.refreshed(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				String value = "";
				if(driverType.trim().toUpperCase().contains("FIREFOX") && attribute.trim().equalsIgnoreCase("innerHTML")) {
					value = driver.findElement(locater).getText();
				} else {
					value = driver.findElement(locater).getAttribute(attribute);
				}
				if(attribute.trim().equalsIgnoreCase("disabled"))
					return value == null ? true : value.trim().equalsIgnoreCase(text);
				else
					return value == null ? false : value.trim().equalsIgnoreCase(text);
			}
			
		}));
		if(val){
			we = driver.findElement(locater);
		}
		return we;
	}
	
	public void click(By locator, By expectedLocator, long expectedLocatorWaitSeconds, long... waitSeconds) throws Exception {
		int counter = !Environment.get("noOfRetriesForSameOperation").trim().equalsIgnoreCase("") ? Integer.valueOf(Environment.get("noOfRetriesForSameOperation").trim()) : 2;
		while(counter >= 0){
			try{
				WebElement we = getElementWhenClickable(locator, waitSeconds);
				if(we != null){
					javascriptClick(we, true);
					getElementWhenVisible(expectedLocator, expectedLocatorWaitSeconds);
					break;
				}
			} catch(Exception ex){
				if(counter == 0){
					throw ex;
				}
				sync(500L);
				counter--;
			}
		}
	}
	
	public void click(WebElement we, By expectedLocator, long expectedLocatorWaitSeconds, long... waitSeconds) throws Exception {
		int counter = !Environment.get("noOfRetriesForSameOperation").trim().equalsIgnoreCase("") ? Integer.valueOf(Environment.get("noOfRetriesForSameOperation").trim()) : 2;
		while(counter >= 0){
			try{
				if(we != null){
					javascriptClick(we, true);
					scrollingToElementofAPage(expectedLocator);
					getElementWhenVisible(expectedLocator, expectedLocatorWaitSeconds);
					break;
				}
			} catch(Exception ex){
				if(counter == 0){
					throw ex;
				}
				sync(500L);
				counter--;
			}
		}
	}
	
	public void scrollingToElementofAPage(By locator) {
		WebElement webElement = driver.findElement(locator);		
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
		sync(1000L);
	}
}