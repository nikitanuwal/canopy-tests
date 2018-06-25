package com.canopy.pages;

import org.openqa.selenium.By;

import com.canopy.framework.BaseUtil;
import com.canopy.framework.Driver.HashMapNew;
import com.canopy.framework.WebDriverFactory;

public class Homepage extends BaseUtil {
	
	public Homepage(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment) {
		super(driverFactory, Dictionary, Environment);
	}
	
	By toggleMenu = By.id("toggleMenu");
	
	public boolean waitforLoggedInPage() {
		getElementWhenVisible(toggleMenu, 40);
		return true;
	}
}