# Spring Boot RESTful API Skeleton HI

This Project is used as a starting point for a Spring Boot RESTful API application. Security is enable for this project and it uses JWT for authorization.
1) rename the project to whatever you need.
	- Need to rename the skeleton package name.
	- Rename the project name
	- Rename the SkeletonApplication.java class
	- In the pom.xml, rename the following:
		- artifactId
		- name
		- description
	- In the applicaiton.properties file, rename the following:
		- contextRoot
		- springdoc.swagger-ui.path (update to match the context root)
		- springdoc.api-docs.path (update to match the context root)

2) create your own self signed certificates
	- The command to run this is in the properties file or you can run the one provided here.
	a) get the path to your local keytool.exe  (c:\Program Files\IBM\SDP\jdk\bin\keytool.exe or something like that)
	b) cd into the root directory of your project.  Its expected that you didn't change the folder location of the keystore or truststore folders
	c) run> {full_keytool_exe_path} -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore mycert.p12 -validity 3650
	eg f:\IBM\SDP\jdk\bin\keytool.exe -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore mycert.p12 -validity 3650
		- for password, enter 'changeit' (or whatever you want, you'll just have to update the value in the properties file
		- for all other values, leave them blank
		- enter 'yes' on the last question 
	This will have created a 'mycert.p12' file in the root of your project.  Now copy and paste this file to the following locations:
		- src/main/resources/keystore
		- src/main/resources/truststore

