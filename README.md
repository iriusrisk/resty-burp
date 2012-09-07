# Resty-Burp
A REST/JSON API to the Burp Suite security tool.

## Requires
- Maven 2
- Licensed version of Burp Suite Pro from: http://portswigger.net/burp/

## Install
Install Burp Suite pro into the local maven repo:

	mvn install:install-file -Dfile=burpsuite-pro.jar -DgroupId=net.portswigger -DartifactId=burpsuite-pro -Dversion=1.4.04 -Dpackaging=jar

Then install burp-client:
	cd burp-client
	mvn install -DskipTests
	
Then compile resty-burp:
	cd ..
	mvn compile

## Configuration
The URL to access the web service can be edited in the BurpService.java file, by default it's http://localhost:8181 with the WADL available from http://localhost:8181/application.wadl

Everytime the reset() method is called, the burp.blank.state state is loaded- so if you'd like to make changes to Burp's state, save the state to this file.

## Run
Before running for the first time, compile with:

	mvn exec:java

## Usage
It ships with a client written in Java which can be used from other Java programs.
For example:

```java
	public void scan(String url) {
		BurpClient burp = new BurpClient("http://localhost:8181/");
		try {
			int scanId = burp.scan(url);
			while (burp.percentComplete(scanId) < 100) {
				Thread.sleep(3000);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
```

## Console
To experiment, the client can be run in a Groovy console:
	
	cd burp-client
	mvn exec:java
	
The following groovy script will scan a target for Cross Site Scripting vulnerabilities only:

```groovy
	import net.continuumsecurity.burpclient.ScanPolicy;
	import net.continuumsecurity.restyburp.model.ScanIssueBean;
	import net.continuumsecurity.restyburp.model.ScanIssueList;

	def scanPolicy = new ScanPolicy()
                    .enable(ScanPolicy.REFLECTED_XSS,ScanPolicy.STORED_XSS).getPolicy();
					burp.setConfig(scanPolicy);
					def scanId = burp.scan("http://localhost:8081/ispatula");
					while (burp.percentComplete(scanId) < 100) {
    					println("Completed: "+burp.percentComplete(scanId)+"%");
    					Thread.sleep(2000);
					}
					def issues = burp.getIssueList(scanId).getIssues();

					println("Found "+issues.size()+" XSS vulnerabilities: ");
					if (issues.size() != 0) {
    					for (ScanIssueBean issue : issues) {
        					println("URL: "+issue.getUrl());
        					println("Issue detail: "+issue.getIssueDetail());
    					}
					}
					burp.reset();
```
