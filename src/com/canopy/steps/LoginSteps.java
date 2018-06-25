package com.canopy.steps;

import org.testng.Assert;

import com.canopy.framework.BaseUtil;
import com.canopy.pages.Homepage;
import com.canopy.pages.LoginPage;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class LoginSteps {
	
	LoginPage login;
	Homepage home;
	BaseUtil base;
	
	public LoginSteps(LoginPage login, Homepage home, BaseUtil base) {
		this.login = login;
		this.base = base;
		this.home = home;
	}
	
	@Given("^User is on (.+) Page$")
	public void user_is_on_page(String uri) {
		uri = (String) base.getGDValue(uri);
		base.load(uri);
	}
	
	@When("^User enters (.+) and (.+)$")
	public void user_enters_and(String username, String password) throws Exception {
		username = (String) base.getGDValue(username);
		password = (String) base.getGDValue(password);
		login.enterUsername(username);
		login.enterPassword(password);
		login.clickLogin();
	}
	
	@Then("^User logged in successfully$")
	public void user_logged_in_successfully() throws Exception {
		Assert.assertTrue(home.waitforLoggedInPage(), "Verify user get logged in");
	}
}