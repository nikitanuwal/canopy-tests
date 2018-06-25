# cucumber-testng

To execute project using docker, run following command:<br/><br/>
docker run --shm-size 1gb -v "$PWD":/usr/src -w /usr/src --net=host nikitanuwal/maven-chrome:base mvn test -Psurefire -Dtestng-suite-xml=testng-customsuite.xml -DchromedriverPath=/usr/bin/chromedriver -DchromeArgument=headless -Denv=DEV -DbuildName=dev-latest-tag-3

To execute project using maven, run following command:<br/><br/>
mvn test -Psurefire -Dtestng-suite-xml=testng-customsuite.xml -Denv=DEV -DbuildName=dev-latest-tag-3<br/><br/>

* Make sure you have chrome installed in your machine
* Enter the new buildName to see the trend by comparing with last ran builds
* Cucumber reports are stored in target/cucumber-html-reports
