Requires Maven 2 to be installed.

Then install Burp Suite pro into the local maven repo:

mvn install:install-file -Dfile=burpsuite-pro.jar -DgroupId=portswigger.net -DartifactId=burpsuite-pro -Dversion=1.4.04 -Dpackaging=jar

You should already have activated a BurpSuite license.

Launch the server with:
mvn exec:java

