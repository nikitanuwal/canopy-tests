package com.canopy.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.canopy.framework.BaseUtil;
import com.canopy.framework.Driver.HashMapNew;
import com.canopy.framework.WebDriverFactory;

public class AccountHoldings extends BaseUtil {
	
	WebDriver driver;
	
	public AccountHoldings(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment) {
		super(driverFactory, Dictionary, Environment);
		this.driver = driverFactory.getDriver();
	}
	
	By accountType = By.cssSelector("input[class*='k-input'][class*='k-readonly']");
	By dateFrom = By.id("dateFrom");
	By applyFilters = By.cssSelector("button[class*='btn-success']");
	By currentValueUSD = By.cssSelector("span[data-bind*='totalNetworthInUSDFormatted']");
	By headers = By.xpath(".//div[@data-bind='foreach: sectionsArray']//span[starts-with(@class,'k-link k-header') and descendant::span[@data-bind='text: resultCount' and text() > '0']]");
	
	public void selectAccountType(String accountType) throws Exception {
		type(this.accountType, "Account Type", accountType, true);
		By li = By.xpath(".//ul[@data-role='staticlist']//li[starts-with(text(), '" + accountType + "')]");
		click(li, true);
	}
	
	public void clickApplyFilters() throws Exception {
		click(applyFilters, true);
	}
	
	public BigDecimal getCurrentValue() {
		sync(2000L);
		WebElement we = getElementWhenVisible(currentValueUSD);
		BigDecimal price = new BigDecimal(we.getText().replaceAll("[A-Za-z,: ]", ""));
		return price;
	}
	
	public BigDecimal getSumOfValueFromHeaders() throws Exception {
		List<WebElement> listofheaders = driver.findElements(headers);
		BigDecimal total_value = new BigDecimal(0);
		for(int i = 0 ; i < listofheaders.size(); i++) {
			By value = By.xpath("(.//div[@data-bind='foreach: sectionsArray']//span[starts-with(@class,'k-link k-header') and descendant::span[@data-bind='text: resultCount' and text() > '0']])[" + (i+1) + "]/..//tr[@class='k-footer-template']//td[last()]");
			click(listofheaders.get(i), value, 1);
			WebElement we = getElementWhenVisible(value);
			total_value = total_value.add(new BigDecimal(we.getText().replaceAll("[A-Za-z,:() ]", "")));
		}
		return total_value;
	}
}