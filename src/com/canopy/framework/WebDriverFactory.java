package com.canopy.framework;

import org.openqa.selenium.WebDriver;
import com.canopy.framework.Driver.HashMapNew;

public class WebDriverFactory {
	public static ThreadLocal<String> sDriverType = new ThreadLocal<String>(){
		@Override public String initialValue() {
			return null;
		}	
	};
	
	public static ThreadLocal<WebDriver> sDriver = new ThreadLocal<WebDriver>() {
		@Override public WebDriver initialValue() {
			return null;
		}
	};
	
	public static ThreadLocal<HashMapNew> sDict = new ThreadLocal<HashMapNew>() {
		@Override public HashMapNew initialValue() {
			return null;
		}
	};
	
	public static ThreadLocal<HashMapNew> sEnv = new ThreadLocal<HashMapNew>(){
		@Override public HashMapNew initialValue() {
			return null;
		}
	};
	
	public WebDriver getDriver() {
		return sDriver.get();
	}

	public void setDriver(WebDriver driver) {
		sDriver.set(driver);
	}
	
	public HashMapNew getDictionary() {
		return sDict.get();
	}

	public void setDictionary(HashMapNew dict) {
		sDict.set(dict);
	}
	
	public HashMapNew getEnvironment() {
		return sEnv.get();
	}

	public void setEnvironment(HashMapNew env) {
		sEnv.set(env);
	}
	
	public String getDriverType() {
		return sDriverType.get();
	}

	public void setDriverType(String driverType) {
		sDriverType.set(driverType);
	}
}
