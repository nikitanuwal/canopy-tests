package com.canopy.steps;

import java.math.BigDecimal;

import org.testng.Assert;
import com.canopy.framework.BaseUtil;
import com.canopy.pages.AccountHoldings;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AccountHoldingsSteps {
	
	AccountHoldings accountHoldings;
	BaseUtil base;
	
	public AccountHoldingsSteps(BaseUtil base, AccountHoldings accountHoldings) {
		this.base = base;
		this.accountHoldings = accountHoldings;
	}
	
	@When("^User selects account type (.+)$")
	public void user_selects_account(String accountType) throws Exception {
		accountType = (String) base.getGDValue(accountType);
		accountHoldings.selectAccountType(accountType);
	}
	
	@When("^Click apply filters$")
	public void click_apply_filers() throws Exception {
		accountHoldings.clickApplyFilters();
	}
	
	@Then("^User verifies current value USD with total value USD$")
	public void user_verifies_cuurent_value_usd_with_total_value_usd() throws Exception {
		BigDecimal current_value = accountHoldings.getCurrentValue();
		BigDecimal total_value = accountHoldings.getSumOfValueFromHeaders();
		Assert.assertEquals(total_value.doubleValue(), current_value.doubleValue(), "Verify current value USD with total value USD");
	}
}
