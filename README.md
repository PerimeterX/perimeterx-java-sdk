  [ ![Download](https://api.bintray.com/packages/perimeterx/maven/perimeterx-sdk/images/download.svg) ](https://bintray.com/perimeterx/maven/perimeterx-sdk/_latestVersion)

![image](https://843a2be0f3083c485676508ff87beaf088a889c0-www.googledrive.com/host/0B_r_WoIa581oY01QMWNVUElyM2M)

[PerimeterX](http://www.perimeterx.com) Java SDK
=============================================================

Table of Contents
-----------------

-   [Usage](#usage)
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
  *   [Debug Mode](#debug-mode)
-   [Contributing](#contributing)
  *   [Tests](#tests)

<a name="Usage"></a>

<a name="installation"></a> Installation
----------------------------------------

####Maven:
Add jcenter as you maven repository:

In your `pom.xml`:

* Add `jcenter` as maven resolve repository:

```xml
<repositories>
 	<repository>
 		<snapshots>
 			<enabled>false</enabled>
 		</snapshots>
 		<id>bintray-maven</id>
 		<name>bintray</name>
 		<url>https://dl.bintray.com/perimeterx/maven</url>
 	</repository>
 </repositories>
```

* Add `perimeterx-sdk` as dependency:

```xml
<dependency>
	<groupId>com.perimeterx</groupId>
   <artifactId>perimeterx-sdk<</artifactId>
   <version>${VERSION}</version>
</dependency>
```

####gradle:

In your `build.gradle`:

* Add `jcenter` as your maven resolve repository:

```gradle
repositories {
    maven {
        url  "http://jcenter.bintray.com"
    }
}
```
* Add `perimeterx-sdk` to your `build.gradle`

```groovy
compile group: 'com.perimeterx', name: 'perimeterx-sdk', version: '${VERSION}'
```



### <a name="basic-usage"></a> Basic Usage Example

```java
// Create configuration object
PXConfig pxConfiguration = new PXconfiguration.Builder()
	.cookieKey(COOKIE_KEY)
	.authToken(AUTH_TOKEN)
	.appId(APP_ID)
	.blockingScore(SCORE)
	.build();

// Get instance
PerimeterX px = PerimeterX.getInstance(pxConfig);
px.init(new DefaultBlockHandler(), new DefaultActivityHandler(pxConfig);

// Inside the request / Filter
@Override
protected void doGet(HttpServletRequest req, HttpservletResponse resp) throws ServletException, IOExcption {
...
	PXContext context = new PXContext(req);
	px.pxVerify(context, new HttpServletResponseWrapper(resp);
...
}

```
### <a name="configuration"></a> Configuration Options

#### Configuring Required Parameters

Configuration options are set in `PXconfiguration`

#### Required parameters:

- appId
- cookieKey
- authToken

##### <a name="blocking-score"></a> Changing the Minimum Score for Blocking

**default:** 70

```java
PXConfig pxConfiguration = new PXconfiguration.Builder()
	...
	.blockingScore(70)
	...
	.build();
```

#### <a name="custom-block"></a> Custom Blocking Actions
Setting a custom block handler customizes is done by implementing the `BlockHandler` interface and set the `blockHandler` field in `PerimeterX` object and `pxVerify` method will run `blockHandler.handleBlocking`.

**default:**  - `DefaultBlockHandler` supplied in this SDK return HTTP status code 403 and serve the
Perimeterx block page. (TODO!)

###### Examples

**Serve a Custom HTML Page**

```java
public class LoggerBlockHandler implements BlockHandler {

	public void handleBlocking(HttpServletResponseWrapper responseWrapper) {
		Systm.out.Println("Loggin request " + responseWrapper);
	}
}
```

#### <a name="captcha-support"></a>Enable/disable captcha in the block page

By enabling captcha support, a captcha will be served as part of the block page giving real users the ability to answer, get score clean up and passed to the requested page.

**default: true**

```java
PXConfig pxConfiguration = new PXconfiguration.Builder()
	...
	.captchaEnabled(true)
	...
	.build();
```

##### <a name="real-ip"></a>Extracting the Real User IP Address From HTTP Headers or by defining a function

In order to evaluate user's score properly, the PerimeterX module
requires the real socket ip (client IP address that created the HTTP
request). The user ip can be passed to the PerimeterX module by implementing the `IPProvider` interface and set it on the `PerimeterX` object.

**default with no predefined header:**

This SDK provided two implementations for this.

- `RemoteAddressIPProvider` which extract the Remote address from raw servlet request.
- `IPByHeaderProvider` which can be constructed with a header key and when applied will extract this value haeder as the true IP.

#### <a name="sensitive-headers"></a> Filter sensitive headers

A user can define a list of sensitive header he want to prevent from being send to perimeterx servers, filtering cookie header for privacy is set by default and will be overriden if a user set the configuration

**default: cookie, cookies**

```java
PXConfig pxConfiguration = new PXconfiguration.Builder()
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
PXConfig pxConfiguration = new PXconfiguration.Builder()
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
PXConfig pxConfiguration = new PXconfiguration.Builder()
	...
	.sendPageActivities(true)
	...
	.build()
```

#### <a name="debug-mode"></a> Debug Mode

Enables debug logging

**default:** false

```php
PXConfig pxConfiguration = new PXconfiguration.Builder()
	...
	.debugMode(true)
	...
	.build()
```
<a name="contributing"></a> Contributing
----------------------------------------
