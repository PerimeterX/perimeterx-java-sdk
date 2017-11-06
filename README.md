[![Build Status](https://travis-ci.org/PerimeterX/perimeterx-java-sdk.svg?branch=master)](https://travis-ci.org/PerimeterX/perimeterx-java-sdk)
[![Javadocs](http://www.javadoc.io/badge/com.perimeterx/perimeterx-sdk.svg?color=brightgreen)](http://www.javadoc.io/doc/com.perimeterx/perimeterx-sdk)


![image](https://s.perimeterx.net/logo.png)

[PerimeterX](http://www.perimeterx.com) Java SDK
=============================================================

Table of Contents
-----------------

- [Usage](#usage)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Basic Usage Example](#basic-usage)
- [Configuration](CONFIGURATIONS.md)
- [Logging and Troubleshooting](#loggin-troubleshoot)
- [Contributing](#contribute)

<a name="prerequisites"></a> Prerequisites
----------------------------

##### JDK:
Use `jdk 1.7` or higher.
##### Unlimited Strength Jurisdiction Policy:

Make sure your JDK supports unlimited key length.

If the SDK is throwing `Unlimited Strength Jurisdiction Policy` assertion errors on startup, follow the instructions below:

1. Download `JCE` for [jdk17](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html) or for [jdk18](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).
2. Replace `local_policy.jar` and `US_export_policy.jar` in your `$JAVA_HOME/jre/lib/security/` with those you have downloaded.
3. Run your project again and the `Unlimited Strength Jurisdiction Policy` error should no appear.

<a name="installation"></a> Installation
----------------------------------------

#### Maven:

* Add `perimeterx-sdk` to `pom.xml`:

```xml
<dependency>
   <groupId>com.perimeterx</groupId>
   <artifactId>perimeterx-sdk</artifactId>
   <version>${VERSION}</version>
</dependency>
```

#### gradle:

* Add `perimeterx-sdk` to your `build.gradle`:

```groovy
compile group: 'com.perimeterx', name: 'perimeterx-sdk', version: '${VERSION}'
```



### <a name="basic-usage"></a> Basic Usage Example

```java
// Create configuration object
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	 .cookieKey(COOKIE_KEY)
	 .authToken(AUTH_TOKEN)
	 .appId(APP_ID)
	 .blockingScore(SCORE)
	 .moduleMode(ModuleMode.BLOCKING)
	 .build();

// Get instance
PerimeterX enforcer = new PerimeterX(pxConfiguration);

// Inside the request / Filter
@Override
protected void doGet(HttpServletRequest req, HttpservletResponse resp) throws ServletException, IOExcption {
...
	PXContext ctx = enforcer.pxVerify(req, new HttpServletResponseWrapper(resp);
	if (!ctx.isVerified()) {
	   // request should be blocked and BlockHandler was triggered on HttpServerResponseWrapper
	}
...
}

```
Please continue reading about the various configurations available on the sdk in the configurations [page](CONFIGURATIONS.md)


### <a name="loggin-troubleshoot"></a> Logging and Troubleshooting
`perimeterx-java-sdk` is using SLF4J for the logging facade, simply hook you Logging
For further information please visit <a href="https://www.slf4j.org/manual.html">SLF4J</a> for more information

### <a name="contribute"></a> Logging and Contributing
The following steps are welcome when contributing to our project.

#### Fork/Clone

First and foremost, <a href="https://guides.github.com/activities/forking/">Create</a> a fork of the repository, and clone it locally. Create a branch on your fork, preferably using a self descriptive branch name.

#### Code/Run

Code your way out of your mess, and help improve our project by implementing missing features, adding capabilities or fixing bugs.

To run the code, simply follow the steps in the <a name="installation">installation guide</a>. Grab the keys from the PerimeterX Portal, and try refreshing your page several times continuously. If no default behaviors have been overriden, you should see the PerimeterX block page. Solve the CAPTCHA to clean yourself and start fresh again.

#### Pull Request

After you have completed the process, create a pull request to the Upstream repository. Please provide a complete and thorough description explaining the changes. Remember this code has to be read by our maintainers, so keep it simple, smart and accurate.

#### Thanks

After all, you are helping us by contributing to this project, and we want to thank you for it. We highly appreciate your time invested in contributing to our project, and are glad to have people like you - kind helpers.
