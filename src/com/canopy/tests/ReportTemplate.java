package com.canopy.tests;

import org.testng.annotations.Test;

import com.canopy.framework.Driver;

import cucumber.api.CucumberOptions;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.PickleEventWrapper;

@CucumberOptions(plugin = {"pretty", "html:target/cucumber-reports", "json:target/cucumber-report-feature-composite.json"}, features = {"features/canopy.feature"}, glue = {"com.canopy.steps"}, monochrome = true, strict = true)
public class ReportTemplate extends Driver {
	
	@Test(dataProvider="scenarios", priority = 0)
	public void runSSOScenarios(PickleEventWrapper pickleEvent, CucumberFeatureWrapper cucumberFeature) throws Throwable {
		testNGCucumberRunner.runScenario(pickleEvent.getPickleEvent());
	}
}
