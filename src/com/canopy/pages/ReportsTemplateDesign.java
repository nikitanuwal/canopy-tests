package com.canopy.pages;

import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.canopy.framework.BaseUtil;
import com.canopy.framework.Driver.HashMapNew;
import com.canopy.framework.WebDriverFactory;

public class ReportsTemplateDesign extends BaseUtil {
	
	WebDriver driver;
	
	public ReportsTemplateDesign(WebDriverFactory driverFactory, HashMapNew Dictionary, HashMapNew Environment) {
		super(driverFactory, Dictionary, Environment);
		this.driver = driverFactory.getDriver();
	}
	
	By createReport = By.cssSelector("a[class*='k-grid-addReport']");
	By deleteReport = By.cssSelector("a[class*='k-grid-deleteReport']");
	By popupConfirmButton = By.cssSelector("button[data-bb-handler='confirm']");
	By reportName = By.id("reportName");
	By chooseStyleTemplate = By.cssSelector("div[data-test-engine='style_template'] span[class='k-input']");
	By styleTemplateTextbox = By.cssSelector("div[class='k-animation-container'][style*='block'] input[class='k-textbox']");
	By save = By.cssSelector("button[data-bind*='Save']");
	By search_name = By.cssSelector("thead[class='tableFloatingHeaderOriginal'] input[data-text-field='name']");
	By refresh = By.cssSelector("div[class*='pagerTop'] span[class*='k-i-reload']");
	By chooseStatus = By.cssSelector("div[data-test-engine='status'] span[class='k-input']");
	By edit = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row')]//td[contains(@class, 'k-command-cell')]/a[contains(@class, 'edit')]");
	By copy = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row')]//td[contains(@class, 'k-command-cell')]/a[contains(@class, 'copy')]");
	By noRecordsAvailable = By.cssSelector("div[class='k-grid-norecords-template']");
	
	public void clickCreate() throws Exception {
		getElementWhenVisible(By.xpath(".//tr[@class='k-grouping-row']/following-sibling::tr[not(@class='k-grouping-row')]"));
		click(createReport, reportName, 1);
	}
	
	public void fillreportTemplateForm(String templatename) throws Exception {
		Dictionary.put("NEW_REPORT_NAME", enterReportName());
		click(chooseStyleTemplate, true);
		type(styleTemplateTextbox, "styleTemplateTextbox", templatename, true);
		By li = By.xpath(".//div[@class='k-animation-container' and contains(@style, 'block')]//li[text()='" + templatename + "']");
		click(li, true);
	}
	
	public String enterReportName() throws Exception {
		String generatedString = UUID.randomUUID().toString();
		generatedString = generatedString.replaceAll("-", "");
		generatedString = generatedString.substring(0, Math.min(generatedString.length(), 10));
		String name = generatedString;
		WebElement we = getElementWhenVisible(this.reportName);
		we.clear();
		we.sendKeys(name, Keys.TAB);
		return name;
	}
	
	public void clickSave() throws Exception {
		click(save, false);
	}
	
	public void selectReportTemplates(String reportName1, String reportName2) throws Exception {
		getElementWhenVisible(By.xpath(".//tr[@class='k-grouping-row']/following-sibling::tr[not(@class='k-grouping-row')]"));
		By checkbox1 = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row') and descendant::td[@role='gridcell' and text()='" + reportName1 + "']]//input[@type='checkbox']");
		By checkbox2 = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row') and descendant::td[@role='gridcell' and text()='" + reportName2 + "']]//input[@type='checkbox']");
		click(checkbox1, true);
		click(checkbox2, true);
	}
	
	public void clickDelete() throws Exception {
		click(deleteReport, popupConfirmButton, 1);
		click(popupConfirmButton, true);
	}
	
	public void searchReport(String name) throws Exception {
		getElementWhenVisible(By.xpath("//tr[@class='k-grouping-row']/following-sibling::tr[not(@class='k-grouping-row')] | //div[@class='k-grid-norecords-template']"));
		type(search_name, "search_name", name, true);
		click(refresh, false);
	}
	
	public boolean verifyNoRecordsAvailable() {
		getElementWhenVisible(noRecordsAvailable);
		return true;
	}
	
	public void waitTillGridUpdated(String name) {
		By table_report_name = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row')]//td[contains(@class, 'k-command-cell')]/following-sibling::td[1]");
		getElementWhenRefreshed(table_report_name, "innerHTML", name);
	}
	
	public void clickEditIcon() throws Exception {
		click(edit, reportName, 1);
	}
	
	public void clickCopyIcon() throws Exception {
		click(copy, reportName, 1);
	}
	
	public void setStatus(String status) throws Exception {
		click(chooseStatus, true);
		type(styleTemplateTextbox, "styleTemplateTextbox", status, true);
		By li = By.xpath(".//div[@class='k-animation-container' and contains(@style, 'block')]//li[text()='" + status + "']");
		click(li, true);
	}
	
	public String getStatus(String status) {
		By status_value = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row')]//td[contains(@class, 'k-command-cell')]/following-sibling::td[4]");
		WebElement we = getElementWhenRefreshed(status_value, "innerHTML", status);
		return we.getText();
	}
	
	public boolean verifyReportisDisplayedAfterCreation(String reportname) throws Exception {
		searchReport(reportname);
		waitTillGridUpdated(reportname);
		By table_report_name = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row')]//td[contains(@class, 'k-command-cell')]/following-sibling::td[1]");
//		By table_report_name = By.xpath(".//tr[@class='k-grouping-row' and contains(., 'My Templates: yes')]/following-sibling::tr[not(@class='k-grouping-row') and following-sibling::tr[contains(., 'My Templates: no')]]//td[contains(@class, 'k-command-cell')]/following-sibling::td[1]");
		WebElement we = getElementWhenRefreshed(table_report_name, "innerHTML", reportname);
		if(we.getText().trim().equalsIgnoreCase(reportname))
			return true;
		return false;
	}
}