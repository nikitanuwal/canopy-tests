# cucumber-testng

This project can be executed in following two ways:<br/><br/>
To execute project using docker, run following command:<br/><br/>
docker run --shm-size 1gb -v "$PWD":/usr/src -w /usr/src --net=host nikitanuwal/maven-chrome:base mvn test -Psurefire -Dtestng-suite-xml=testng-customsuite.xml -DchromedriverPath=/usr/bin/chromedriver -DchromeArgument=headless -Denv=DEV -DbuildName=dev-latest-tag-3

Precondition:
* Docker is installed in your machine

To execute project using maven, run following command:<br/><br/>
mvn test -Psurefire -Dtestng-suite-xml=testng-customsuite.xml -Denv=DEV -DbuildName=dev-latest-tag-3<br/><br/>

Precondition:
* Make sure you have chrome and maven is installed in your machine

Note:
* Enter the new buildName to see the trend by comparing with last ran builds
* Cucumber reports are stored in target/cucumber-html-reports
