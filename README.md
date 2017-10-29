[![Build Status](https://travis-ci.org/PerimeterX/perimeterx-java-sdk.svg?branch=master)](https://travis-ci.org/PerimeterX/perimeterx-java-sdk)
[![Javadocs](http://www.javadoc.io/badge/com.perimeterx/perimeterx-sdk.svg?color=brightgreen)](http://www.javadoc.io/doc/com.perimeterx/perimeterx-sdk)


![image](https://s.perimeterx.net/logo.png)

[PerimeterX](http://www.perimeterx.com) Java SDK
=============================================================

Table of Contents
-----------------

-   [Usage](#usage)
  *   [Prerequisites](#prerequisites)
  *   [Installation](#installation)
  *   [Basic Usage Example](#basic-usage)
-   [Configuration](#configuration)
  *   [Blocking Score](#blocking-riskScore)
  *   [Customizing Default Blocking Pages](#custom-block-page)
  *   [Custom Block Action](#custom-block)
  *   [Enable/Disable Captcha](#captcha-support)
  *   [Extracting Real IP Address](#real-ip)
  *   [Filter Sensitive Headers](#sensitive-headers)
  *   [API Timeout Milliseconds](#api-timeout)
  *   [Send Page Activities](#send-page-activities)
  *   [Custom Blocking Actions](#custom-blocking-action)

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

####Maven:

* Add `perimeterx-sdk` to `pom.xml`:

```xml
<dependency>
	<groupId>com.perimeterx</groupId>
   <artifactId>perimeterx-sdk</artifactId>
   <version>${VERSION}</version>
</dependency>
```

####gradle:

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
### <a name="configuration"></a> Configuration Options

#### Configuring Required Parameters

Configuration options are set in `PXConfiguration`

#### Required Parameters:

- appId
- cookieKey
- authToken

##### <a name="blocking-riskScore"></a> Changing the Minimum Score for Blocking

**default:** 70

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.blockingScore(50)
	...
	.build();
```

#### <a name="custom-block"></a> Custom Blocking Actions
Setting a custom block handler customizes is done by implementing the `BlockHandler` interface and set the `blockHandler` field in `PerimeterX` object and `pxVerify` method will run `blockHandler.handleBlocking`.

**default:**  The `DefaultBlockHandler` supplied in this SDK returns an HTTP status code 403 and serves the
PerimeterX block page.

###### Examples

**Serve a Custom HTML Page**

```java
public class LoggerBlockHandler implements BlockHandler {

	public void handleBlocking(PXContext context, HttpServletResponseWrapper responseWrapper) {
		Systm.out.Println("Loggin request " + responseWrapper);
	}
}
```

## <a name="custom-block-page"></a> Customizing Default Block Pages
**Custom logo insertion**

Adding a custom logo to the blocking page is by providing the pxConfig a key ```customLogo``` , the logo will be displayed at the top div of the the block page
The logo's ```max-heigh``` property would be 150px and width would be set to ```auto```

The key ```customLogo```  expects a valid URL address such as ```https://s.perimeterx.net/logo.png```

Example below:
```java
// Create configuration object
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	.cookieKey(COOKIE_KEY)
	.authToken(AUTH_TOKEN)
	.appId(APP_ID)
	.blockingScore(SCORE)
   	.customLogo(LOGO_URL)
	.build();
```

**Custom JS/CSS**

The block page can be modified with a custom CSS by adding to the ```pxConfig``` the key ```cssRef``` and providing a valid URL to the css
In addition there is also the option to add a custom JS file by adding ```jsRef``` key to the ```pxConfig``` and providing the JS file that will be loaded with the block page, this key also expects a valid URL

On both cases if the URL is not a valid format an exception will be thrown

Example below:
```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	.cookieKey(COOKIE_KEY)
	.authToken(AUTH_TOKEN)
	.appId(APP_ID)
 	.cssRef(CSS_URL)
 	.jsRef(URL)
	.blockingScore(SCORE)
	.build();
```

Side notes: Custom logo/js/css can be added together


#### <a name="captcha-support"></a>Enable/disable captcha in the block page

By enabling captcha support, a captcha will be served as part of the block page giving real users the ability to answer, get riskScore clean up and be passed to the requested page.

**default: true**

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.captchaEnabled(true)
	...
	.build();
```

##### <a name="real-ip"></a>Extracting the Real User IP Address From HTTP Headers or by defining a function

> Note: IP extraction according to your network setup is important. It is common to have a load balancer/proxy on top of your applications. In this case, the PerimeterX module will send an internal IP as the user's. In order to perform processing and detection for server-to-server calls, the PerimeterX module needs the real user IP.

The user IP can be passed to the PerimeterX module by implementing the `IPProvider` interface and configuring it on the `PerimeterX` object.


This SDK provids two implementations for this:

- `RemoteAddressIPProvider` (deault) which extracts the Remote address from a raw servlet request.
- `IPByHeaderProvider` which can be constructed with a header key, and when applied, will extract this value haeder as the true IP.

You can set your own `IPProvider`:

```java
// java < 8
px.setIpProvider(new IPProvider() {
    public String getRequestIP(HttpServletRequest request) {
    	return "127.0.0.1";
    }
});

// java >= 8
px.setIpProvider(httpRequest -> "127.0.0.1");
```

#### <a name="sensitive-headers"></a> Filter sensitive headers

A user can define a list of sensitive headers a user wants to prevent from being send to PerimeterX servers. Filtering the cookie header for privacy is set by default and will be overriden if a user sets the configuration.

**default: cookie, cookies**

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.addSensitiveHeader("Authorization")
	.addSensitiveHeader("Secret-Header")
	...
	.build()
```

#### <a name="api-timeout"></a>API Timeout Milliseconds

Timeout in seconds (float) to wait for the PerimeterX server API response.
The API is called when the risk cookie does not exist, or is expired or
invalid.

**default:** 1000 Miliseconds

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.apiTimeout(2000)
	...
	.build()
```

#### <a name="send-page-activities"></a> Send Page Activities

A Boolean flag to enable or disable sending activities and metrics to
PerimeterX on each page request. Enabling this feature will provide data
that populates the PerimeterX portal with valuable information, such as
amount of requests blocked and API usage statistics.

**default:** false

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.sendPageActivities(true)
	...
	.build()
```

#### <a name="custom-blocking-action"></a> Custom Blocking Actions
In order to customize the action performed on a valid block value, implement the interface `VerificationHandler`, and set it after the initialization of PerimeterX class.

The custom handler should contain the action to be taken, when a visitor receives a score higher than the 'blockingScore' value. Common customization options are presenting of a reCAPTCHA, or supplying a custom branded block page.

**Default block behaviour:** return an HTTP status code of 403 and serve the PerimeterX block page.

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.build();

PerimeterX enforcer = new PerimeterX(pxConfiguration);
enforcer.setVerificationHandler(new VerificationHandler() {
    @Override
    public boolean handleVerification(PXContext context, HttpServletResponseWrapper responseWrapper) throws Exception {
        if (context.getRiskScore() >= configuration.getBlockingScore()) {
            // Score was higher than threshold, request should be blocked
            return false;
        }
        // Score was below threshold, request should pass
        return true;
    }
});
```