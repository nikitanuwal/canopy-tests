package com.canopy.steps;

import org.testng.Assert;
import org.testng.SkipException;

import com.canopy.framework.BaseUtil;
import com.canopy.pages.ReportsTemplateDesign;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReportTemplateDesignSteps {
	
	ReportsTemplateDesign reportTemplateDesign;
	BaseUtil base;
	
	public ReportTemplateDesignSteps(BaseUtil base, ReportsTemplateDesign reportTemplateDesign) {
		this.base = base;
		this.reportTemplateDesign = reportTemplateDesign;
	}
	
	@Given("^User search report (.+)$")
	public void user_search_report(String reportname) throws Exception {
		reportname = (String) base.getGDValue(reportname);
		if(reportname.trim().equalsIgnoreCase(""))
			throw new SkipException("New report is not created");
		reportTemplateDesign.searchReport(reportname);
		reportTemplateDesign.waitTillGridUpdated(reportname);
	}
	
	@When("^User creates report template with (.+)$")
	public void user_creates_report_template_with(String templatename) throws Exception {
		templatename = (String) base.getGDValue(templatename);
		reportTemplateDesign.clickCreate();
		reportTemplateDesign.fillreportTemplateForm(templatename);
		reportTemplateDesign.clickSave();
	}
	
	@Then("^User verifies new report template (.+) is created$")
	public void user_verifies_new_report_template_is_created(String reportName) throws Exception {
		reportName = (String) base.getGDValue(reportName);
		Assert.assertTrue(reportTemplateDesign.verifyReportisDisplayedAfterCreation(reportName), "Verify new report is created");
	}
	
	@When("^User clicks on edit icon$")
	public void user_clicks_on_edit_icon() throws Exception {
		reportTemplateDesign.clickEditIcon();
	}
	
	@When("^User clicks on copy icon$")
	public void user_clicks_on_copy_icon() throws Exception {
		reportTemplateDesign.clickCopyIcon();
	}
	
	@When("^Selects status \"(.+)\"$")
	public void selects_status(String status) throws Exception {
		status = (String) base.getGDValue(status);
		reportTemplateDesign.setStatus(status);
		reportTemplateDesign.clickSave();
	}
	
	@When("^Enters new report name$")
	public void enter_new_report_name() throws Exception {
		String newReportName = reportTemplateDesign.enterReportName();
		reportTemplateDesign.clickSave();
		base.Dictionary.put("NEW_REPORT_NAME_1", newReportName);
	}
	
	@Then("^User verifies the new status \"(.+)\"$")
	public void user_verifies_the_new_status(String status) throws Exception {
		status = (String) base.getGDValue(status);
		Assert.assertEquals(reportTemplateDesign.getStatus(status), status, "Verify status value is updated");
	}
	
	@When("^User selects two reports (.+) and (.+)$")
	public void user_selectd_two_reports_and(String reportName1, String reportName2) throws Exception {
		reportName1 = (String) base.getGDValue(reportName1);
		reportName2 = (String) base.getGDValue(reportName2);
		if(reportName1.trim().equalsIgnoreCase("") || reportName2.trim().equalsIgnoreCase(""))
			throw new SkipException("New report is not created");
		reportTemplateDesign.selectReportTemplates(reportName1, reportName2);
	}
	
	@When("^Clicks on delete button$")
	public void clicks_on_delete_button() throws Exception {
		reportTemplateDesign.clickDelete();
	}
	
	@Then("^User verifies reports (.+) and (.+) got deleted$")
	public void user_verifies_reports_and_got_deleted(String reportName1, String reportName2) throws Exception {
		reportName1 = (String) base.getGDValue(reportName1);
		reportName2 = (String) base.getGDValue(reportName2);
		reportTemplateDesign.searchReport(reportName1);
		boolean success = reportTemplateDesign.verifyNoRecordsAvailable();
		Assert.assertTrue(success, "Verify " + reportName1 + " got deleted");
		reportTemplateDesign.searchReport(reportName2);
		success = reportTemplateDesign.verifyNoRecordsAvailable();
		Assert.assertTrue(success, "Verify " + reportName1 + " got deleted");
	}
}
