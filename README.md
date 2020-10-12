[![Build Status](https://travis-ci.org/PerimeterX/perimeterx-java-sdk.svg?branch=master)](https://travis-ci.org/PerimeterX/perimeterx-java-sdk) [![Javadocs](http://www.javadoc.io/badge/com.perimeterx/perimeterx-sdk.svg?color=brightgreen)](http://www.javadoc.io/doc/com.perimeterx/perimeterx-sdk)

![image](https://storage.googleapis.com/perimeterx-logos/primary_logo_red_cropped.png)

# [PerimeterX](http://www.perimeterx.com) Java SDK

> Latest stable version: [v6.2.2](https://search.maven.org/#artifactdetails%7Ccom.perimeterx%7Cperimeterx-sdk%7C6.2.2%7Cjar)

## Table of Contents

- [Usage](#usage)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Upgrading](#upgrading)
- [Basic Usage Example](#basic-usage)
- [Advanced Usage Examples](#advanced-usage)
  - [Data Enrichment](#data-enrichment)
  - [Custom Parameters](#custom-parameters)
  - [Multiple Application Support](#multi-app-support)
- [Configuration](CONFIGURATIONS.md)
- [Logging and Troubleshooting](#loggin-troubleshoot)
- [Contributing](#contribute)


<a name="prerequisites"></a> Prerequisites
----------------------------
### JDK:

Use `jdk 1.7` or higher.

### Unlimited Strength Jurisdiction Policy:

Make sure your JDK supports unlimited key length.

If the SDK is throwing `Unlimited Strength Jurisdiction Policy` assertion errors on startup, follow the instructions below:

1. Download `JCE` for [jdk17](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html) or for [jdk18](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).
2. Replace `local_policy.jar` and `US_export_policy.jar` in your `$JAVA_HOME/jre/lib/security/` with those you have downloaded.
3. Run your project again and the `Unlimited Strength Jurisdiction Policy` error should no appear.


<a name="installation"></a> Installation
----------------------------------------

### Maven:

- Add `perimeterx-sdk` to `pom.xml`:

```xml
<dependency>
   <groupId>com.perimeterx</groupId>
   <artifactId>perimeterx-sdk</artifactId>
   <version>${VERSION}</version>
</dependency>
```

### gradle:

- Add `perimeterx-sdk` to your `build.gradle`:

```groovy
compile group: 'com.perimeterx', name: 'perimeterx-sdk', version: '${VERSION}'
```


<a name="upgrading"></a> Upgrading
----------------------------------------
#### <a name="4x"></a> SDK > v4.x

To upgrade to the latest Enforcer version, run:

`mvn versions:display-dependency-updates`

Open the project’s `pom.xml` and change the version number to the latest version.

Your Enforcer version is now upgraded to the latest enforcer version.

#### SDK < v4.x
The PXContext on SDK v4.x has changed, following these changes, the implementation of PerimeterX SDK on the java filter must be changed accordingly.

PerimeterX SDK reports now if handled the response instead of reporting if request was verified (using `ctx.isVerified()`) instead, its PXContext expose the following methods: `ctx.isHandledResponse()`.  

`isVerified()` is deprecated and from now on, use `isRequestLowScore()`

`isHandledResponse()` will return `true` in the following cases
1. Request is blocked and PerimeterX handled the response by rendering a block page (because score was high)
2. Response was handled by first party mechanism (not score related).

* More information about First Party can be found in the [configurations page](CONFIGURATIONS.md)

Following the instructions above, the filter should be changed according the the example below

```java
  // Verify the request
  PXContext ctx = enforcer.pxVerify(req, new HttpServletResponseWrapper(resp);

  // Notice that isVerified() changed to isHandledResponse()
  if (ctx != null && ctx.isHandledResponse()) {

     // Optional: check why response was handled
     if (ctx.isFirstPartyRequest()) {
       System.out.println("Incoming request was first party request");
     }

     if (!ctx.isRequestLowScore()) {
       System.out.println("Request score was higher than threshold");
     }

    // Must return and not continue to filterChain.doFilter
    return;

 }

 filterChain.doFilter(servletRequest, servletResponse);
```
Once the filter is changed, follow the instructions [above](#4x).

For more information, contact [PerimeterX Support](support@perimeterx.com).

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
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOExcption {
...
    PXContext ctx = enforcer.pxVerify(req, new HttpServletResponseWrapper(resp);
    if (ctx != null && !ctx.isHandledResponse()) {
       // request should be blocked and BlockHandler was triggered on HttpServerResponseWrapper
    }
...
}
```

Please continue reading about the various configurations available on the sdk in the configurations [page](CONFIGURATIONS.md) .

### <a name="advanced-usage"></a> Advanced Usage Examples

#### <a name="data-enrichment"></a> Data Enrichment - pxde(PerimeterX Data Enrichment)

Users can use the additional activity handler to retrieve information for the request using the pxde object.
First, check that the data enrichment object is verified, then you can access it's properties.

MyVerificationHandler.java:
```java
...
public class MyVerificationHandler implements VerificationHandler {
    PXConfiguration pxConfig;
    VerificationHandler defaultVerificationHandler;

    public AutomationVerificationHandler(PXConfiguration pxConfig) throws PXException {
        this.pxConfig = pxConfig;
        PXClient pxClient = new PXHttpClient(pxConfig);
        ActivityHandler activityHandler = new DefaultActivityHandler(pxClient, pxConfig);
        this.defaultVerificationHandler = new DefaultVerificationHandler(pxConfig, activityHandler);
    }

    public boolean handleVerification(PXContext pxContext, HttpServletResponseWrapper httpServletResponseWrapper) throws PXException, IOException {
        if (pxContext.isPxdeVerified()) {
            JsonNode dataEnrichmentPayload = pxContext.getPxde();
            <handle data enrichment payload here>
        }

        return defaultVerificationHandler.handleVerification(pxContext, httpServletResponseWrapper);
    }
}
```

Then, in your filter:
```java
...
PXConfiguration config = new PXConfiguration.Builder()
     ...
     .build();
PerimeterX enforcer = new PerimeterX(config);
enforcer.setVerificationHandler(new MyVerificationHandler(config));
...
```

#### <a name="custom-parameters"></a> Custom Parameters

With the `customParametersProvider` function you can add up to 10 custom parameters to be sent back to PerimeterX servers. When set, the function is called before seting the payload on every request to PerimetrX servers.

MyCustomParametersProvider.java:
```java
...
public class MyCustomParametersProvider implements CustomParametersProvider {
    public CustomParameters buildCustomParameters(PXConfiguration pxConfiguration, PXContext pxContext) {
        CustomParameters customParameters = new CustomParameters();
        customParameters.setCustomParam1("my_custom_param_1");
        customParameters.setCustomParam2("my_custom_param_2");
        ...
        customParameters.setCustomParam10("my_custom_param_10");
        return customParameters;
    }
}
```

Then, in your filter:
```java
...
PXConfiguration pxConfiguration = new PXConfiguration.Builder()
     ...
     .customParametersProvider(new MyCustomParametersProvider())
     .build();
...
```

#### <a name="multi-app-support"></a> Multiple Application Support
Simply create multiple instances of the PerimeterX class:
```java
PerimeterX enforcerApp1 = new PerimeterX(new PXConfiguration.Builder().appId(APP_ID_1)...build(););
PerimeterX enforcerApp2 = new PerimeterX(new PXConfiguration.Builder().appId(APP_ID_2)...build(););

...

// Inside route request handler for app 1:
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOExcption {
    PXContext ctx = enforcerApp1.px(req, new HttpServletResponseWrapper(resp);
    ...
}

...

// Inside route request handler for app 2:
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOExcption {
    PXContext ctx = enforcerApp2.pxVerify(req, new HttpServletResponseWrapper(resp);
    if(ctx != null) {
      ...
    }
}
```

### <a name="loggin-troubleshoot"></a> Logging and Troubleshooting
`perimeterx-java-sdk` is using SLF4J and Logback for logs.

For further information please visit [SLF4J](https://www.slf4j.org/manual.html) and [Logback](https://logback.qos.ch).

The following steps are welcome when contributing to our project.

#### Fork/Clone

First and foremost, [Create](https://guides.github.com/activities/forking/) a fork of the repository, and clone it locally. Create a branch on your fork, preferably using a self descriptive branch name.

#### Code/Run

Code your way out of your mess, and help improve our project by implementing missing features, adding capabilities or fixing bugs.

To run the code, simply follow the steps in the [installation guide](). Grab the keys from the PerimeterX Portal, and try refreshing your page several times continuously. If no default behaviors have been overriden, you should see the PerimeterX block page. Solve the CAPTCHA to clean yourself and start fresh again.

#### Pull Request

After you have completed the process, create a pull request to the Upstream repository. Please provide a complete and thorough description explaining the changes. Remember this code has to be read by our maintainers, so keep it simple, smart and accurate.

#### Thanks
