@all
Feature: Canopy scenarios
	Background: User landed on loginpage
		Given User is on / Page
		
	@reportTemplate
	Scenario: Create Report Template
		When User enters %{GD_USERNAME} and %{GD_PASSWORD}
		Then User logged in successfully
		Given User navigates to /Reporting/Manage
		When User creates report template with BaseTemplateDL
		Then User verifies new report template %{GD_NEW_REPORT_NAME} is created
		
	@reportTemplate
	Scenario: Edit Report Template
		When User enters %{GD_USERNAME} and %{GD_PASSWORD}
		Then User logged in successfully
		Given User navigates to /Reporting/Manage
		And User search report %{GD_NEW_REPORT_NAME}
		When User clicks on edit icon
		And Selects status "Ready To Assign"
		Then User verifies the new status "Ready To Assign"
		
	@reportTemplate
	Scenario: Copy Report Template
		When User enters %{GD_USERNAME} and %{GD_PASSWORD}
		Then User logged in successfully
		Given User navigates to /Reporting/Manage
		And User search report %{GD_NEW_REPORT_NAME}
		When User clicks on copy icon
		And Enters new report name
		Then User verifies new report template %{GD_NEW_REPORT_NAME_1} is created
		
	@reportTemplate
	Scenario: Delete Report Template
		When User enters %{GD_USERNAME} and %{GD_PASSWORD}
		Then User logged in successfully
		Given User navigates to /Reporting/Manage
		When User selects two reports %{GD_NEW_REPORT_NAME} and %{GD_NEW_REPORT_NAME_1}
		And Clicks on delete button
		Then User verifies reports %{GD_NEW_REPORT_NAME} and %{GD_NEW_REPORT_NAME_1} got deleted
	
	@networth
	Scenario: Calculate total networth
		When User enters %{GD_USERNAME} and %{GD_PASSWORD}
		Then User logged in successfully
		Given User navigates to /ControlRoom/HoldingsPerUser
		When User selects account type acdc4ever
		And Click apply filters
		Then User verifies current value USD with total value USD