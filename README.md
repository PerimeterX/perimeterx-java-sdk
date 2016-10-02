[![Javadocs](http://www.javadoc.io/badge/com.perimeterx/perimeterx-sdk.svg?color=brightgreen)](http://www.javadoc.io/doc/com.perimeterx/perimeterx-sdk)


![image](http://media.marketwire.com/attachments/201604/34215_PerimeterX_logo.jpg)

[PerimeterX](http://www.perimeterx.com) Java SDK
=============================================================

Table of Contents
-----------------

-   [Usage](#usage)
  *   [Prerequisites](#prerequisites)
  *   [Installation](#installation)
  *   [Basic Usage Example](#basic-usage)
-   [Configuration](#configuration)
  *   [Blocking Score](#blocking-score)
  *   [Custom Block Action](#custom-block)
  *   [Enable/Disable Captcha](#captcha-support)
  *   [Extracting Real IP Address](#real-ip)
  *   [Filter Sensitive Headers](#sensitive-headers)
  *   [API Timeout Milliseconds](#api-timeout)
  *   [Send Page Activities](#send-page-activities)

<a name="prerequisites"></a> Prerequisites
----------------------------

##### JDK:
Use `jdk 1.7` or higher.
##### Unlimited Strength Jurisdiction Policy:

Make sure your jdk support unlimited key length.

If the SDK is throwing `Unlimited Strength Jurisdiction Policy` assertion error on startup, follow the instructions below: 

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
PerimeterX px = PerimeterX.getInstance(pxConfiguration);

// Inside the request / Filter
@Override
protected void doGet(HttpServletRequest req, HttpservletResponse resp) throws ServletException, IOExcption {
...
	px.pxVerify(req, new HttpServletResponseWrapper(resp);
...
}

```
### <a name="configuration"></a> Configuration Options

#### Configuring Required Parameters

Configuration options are set in `PXConfiguration`

#### Required parameters:

- appId
- cookieKey
- authToken

##### <a name="blocking-score"></a> Changing the Minimum Score for Blocking

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

**default:**  - `DefaultBlockHandler` supplied in this SDK return HTTP status code 403 and serve the
Perimeterx block page.

###### Examples

**Serve a Custom HTML Page**

```java
public class LoggerBlockHandler implements BlockHandler {

	public void handleBlocking(PXContext context, HttpServletResponseWrapper responseWrapper) {
		Systm.out.Println("Loggin request " + responseWrapper);
	}
}
```

#### <a name="captcha-support"></a>Enable/disable captcha in the block page

By enabling captcha support, a captcha will be served as part of the block page giving real users the ability to answer, get score clean up and passed to the requested page.

**default: true**

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.captchaEnabled(true)
	...
	.build();
```

##### <a name="real-ip"></a>Extracting the Real User IP Address From HTTP Headers or by defining a function

In order to evaluate user's score properly, the PerimeterX module
requires the real socket ip (client IP address that created the HTTP
request). The user ip can be passed to the PerimeterX module by implementing the `IPProvider` interface and set it on the `PerimeterX` object.


This SDK provided two implementations for this:

- `RemoteAddressIPProvider` (deault) which extract the Remote address from raw servlet request.
- `IPByHeaderProvider` which can be constructed with a header key and when applied will extract this value haeder as the true IP.

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

A user can define a list of sensitive header he want to prevent from being send to perimeterx servers, filtering cookie header for privacy is set by default and will be overriden if a user set the configuration

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

Boolean flag to enable or disable sending activities and metrics to
PerimeterX on each page request. Enabling this feature will provide data
that populates the PerimeterX portal with valuable information such as
amount requests blocked and API usage statistics.

**default:** false

```java
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	...
	.sendPageActivities(true)
	...
	.build()
```
