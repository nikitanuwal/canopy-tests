package com.canopy.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.canopy.framework.BaseUtil;
import com.canopy.framework.Driver.HashMapNew;
import com.canopy.framework.WebDriverFactory;

public class LoginPage extends BaseUtil {
	
	WebDriver driver;

	public LoginPage(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment) {
		super(driverFactory, Dictionary, Environment);
		this.driver = driverFactory.getDriver();
		PageFactory.initElements(this.driver, this);
	}
	
	@FindBy(name = "username")
	WebElement username;
	@FindBy(name = "password")
	WebElement password;
	@FindBy(name = "returnUrl")
	WebElement submit;
	
	public void enterUsername(String username) throws Exception {
		this.username.sendKeys(username);
	}
	
	public void enterPassword(String password) throws Exception {
		this.password.sendKeys(password);
	}
	
	public void clickLogin() throws Exception {
		this.submit.click();
	}
}
