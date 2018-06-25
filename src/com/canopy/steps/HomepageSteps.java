package com.canopy.steps;

import com.canopy.framework.BaseUtil;

import cucumber.api.java.en.Given;

public class HomepageSteps {
	
	BaseUtil base;
	
	public HomepageSteps(BaseUtil base) {
		this.base = base;
	}
	
	@Given("^User navigates to (.+)$")
	public void user_navigates_to(String uri) {
		uri = (String) base.getGDValue(uri);
		base.navigateTo(uri);
	}
	
	@Given("^Save \"(.+)\" into \"(.+)\"$")
	public void save_into(String value, String key) {
		value = (String) base.getGDValue(value);
		base.Dictionary.put(key, value);	
	}
}
