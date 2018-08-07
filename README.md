[![Build Status](https://travis-ci.org/PerimeterX/perimeterx-java-sdk.svg?branch=master)](https://travis-ci.org/PerimeterX/perimeterx-java-sdk) [![Javadocs](http://www.javadoc.io/badge/com.perimeterx/perimeterx-sdk.svg?color=brightgreen)](http://www.javadoc.io/doc/com.perimeterx/perimeterx-sdk)

![image](https://s.perimeterx.net/logo.png)

# [PerimeterX](http://www.perimeterx.com) Java SDK

> Latest stable version: [v4.1.1](https://search.maven.org/#artifactdetails%7Ccom.perimeterx%7Cperimeterx-sdk%7C4.1.1%7Cjar)

## [Introduction](#introduction)
- [Prerequisites](#prerequisites)

## [Upgrading](#upgrading)

## [Installation](#installation)
- [Installing with Maven](#maven)
- [Installing with Gradel](#gradel)

## [Configuration](#configuration)
- [Required Configuration](#required_config)
- [First-Party Configuration](#first-party-integration)
  -[First Party Mode](#first-party_mode)
- [Optional Configuration](#optional_config) 
  - [moduleMode](#moduleMode)
  - [moduleEnabled](#moduleEnabled)
  - [blockingScore](#blockingScore)
  - [sensitiveHeaders](#sensitiveHeaders)
  - [apiTimeout](#apiTimeout)
  - [connectionTimeout](#connectionTimeout)
  - [customLogo](#customLogo)
  - [cssRef](#cssRef)
  - [jsRef](#jsRef)
  - [sensitiveRoutes](#sensitiveRoutes)
  - [remoteConfigurationEnabled](#remoteConfigurationEnabled)
  - [captchaProvider](#captchaProvider)
  - [ipHeaders](#ipHeaders)
- [Custom Parameters Provider](#customParametersProvider) 
- [Interfaces](#interfaces)

## [Appendix](#appendix)
- [Logging and Troubleshooting](#loggin-troubleshoot)
- [Contributing](#contribute)


### <a name="prerequisites"></a> Prerequisites

#### JDK:

Use `jdk 1.7` or higher.

#### Unlimited Strength Jurisdiction Policy:

Make sure your JDK supports unlimited key length.

If the SDK is throwing `Unlimited Strength Jurisdiction Policy` assertion errors on startup:

1. Download `JCE` for [jdk17](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html) or for [jdk18](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).
2. Replace `local_policy.jar` and `US_export_policy.jar` in your `$JAVA_HOME/jre/lib/security/` with those you have downloaded.
3. Run your project again. The `Unlimited Strength Jurisdiction Policy` error should no appear.


##<a name="upgrading"></a> Upgrading

#### SDK < v4.x
The PXContext on SDK v4.x has changed, following these changes, the implementation of PerimeterX SDK on the java filter must be changed accordingly.

PerimeterX SDK reports now if handled the response instead of reporting if request was verified (using `ctx.isVerified()`) instead, its PXContext expose the following methods: `ctx.isHandledResponse()`.  

`isVerified()` is deprecated and from now on, use `isRequestLowScore()`

`isHandledResponse()` will return `true` in the following cases
1. Request is blocked and PerimeterX handled the response by rendering a block page (because score was high)
2. Response was handled by First-Party mechanism (not score related).

Following the instructions above, the filter should be changed according the the example below

```java
  // Verify the request
  PXContext ctx = enforcer.pxVerify(req, new HttpServletResponseWrapper(resp);

  // Notice that isVerified() changed to isHandledResponse()
  if (ctx.isHandledResponse()) {

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

##<a name="installation"></a> Installation

### <a name="maven"></a> Installing with Maven:

- Add `perimeterx-sdk` to `pom.xml`:

```xml
<dependency>
   <groupId>com.perimeterx</groupId>
   <artifactId>perimeterx-sdk</artifactId>
   <version>${VERSION}</version>
</dependency>
```

### <a name="gradel"></a> Installing with Gradle:

- Add `perimeterx-sdk` to your `build.gradle`:

```groovy
compile group: 'com.perimeterx', name: 'perimeterx-sdk', version: '${VERSION}'
```

## <a name="configuration"></a> Configuration

### <a name="required_config"></a> Required Configuration

1. Create the configuration object:

 ```java
 PXConfiguration pxConfiguration = new PXConfiguration.Builder()
	 .cookieKey(COOKIE_KEY)
	 .authToken(AUTH_TOKEN)
	 .appId(APP_ID)
	 ```
   
 - The PerimeterX **Application ID / AppId** and PerimeterX **Token / Auth Token** can be found in the Portal, in <a href="https://console.perimeterx.com/#/app/applicationsmgmt" onclick="window.open(this.href); return false;">**Applications**</a>.

 - PerimeterX **Risk Cookie / Cookie Key** can be found in the portal, in <a href="https://console.perimeterx.com/#/app/policiesmgmt" onclick="window.open(this.href); return false;">**Policies**</a>.

 The Policy from where the **Risk Cookie / Cookie Key** is taken must correspond with the Application from where the **Application ID / AppId** and PerimeterX **Token / Auth Token**

 By default, the basic configuration is set to Monitor mode only. To include active blocking in the basic configuration, see **moduleMode** [below](#<a name="moduleMode"></a>) for more information.
   
2. Get the instance:

 ```java
 PerimeterX enforcer = new PerimeterX(pxConfiguration);
 ```

3. Inside the request, filter:

 ```java
 @Override
protected void doGet(HttpServletRequest req, HttpservletResponse resp) throws ServletException, IOExcption {
...
	PXContext ctx = enforcer.pxVerify(req, new HttpServletResponseWrapper(resp);
	if (!ctx.isVerified()) 
...
}
 ```
 
 The request should be blocked, and `BlockHandler` triggered on `HttpServerResponseWrapper`


### <a name="first-party-integration"></a> First-Party Configuration

####<a name="first-party_mode"></a> First-Party Mode
First-Party will allow the sensor to be served from your domain. Using the First-Party mode is recommended.
By enabling First Party Mode on the java SDK you will achieve:  

Improved performance - Serving the sensor as part of the standard site content removes the need to open a new connection to PerimeterX servers when a page is loaded.
Improved detection - Third-Party content may be blocked by certain browser plugins and privacy add-ons. The First-Party sensor leads directly to improved detection, as seen with customers who previously moved from Third Party sensor to First-Party sensor.

#### Configuration:
```java
public class PXFilter implements Filter {

    private PerimeterX enforcer;

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            PXConfiguration config = new PXConfiguration.Builder()
                    .appId("PXaBcDeFgH")
                    .cookieKey("COOKIE_KEY")
                    .authToken("AUTH_TOKEN")
                    .build();
            this.enforcer = new PerimeterX(config);
        } catch (PXException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            PXContext pxContext = this.enforcer.pxVerify((HttpServletRequest) servletRequest, new HttpServletResponseWrapper((HttpServletResponse) servletResponse));

            boolean isHandledResponse = pxContext.isHandledResponse();
            if (isHandledResponse) {
                return;
            }

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            // Fail open in case of Exception
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    public void destroy() {

    }
}
```

```xml
<web-app>
  <filter>
     <filter-name>PXFilter</filter-name>
     <display-name>PXFilter</display-name>
     <filter-class>com.webapp.filters.PXFilter</filter-class>
  </filter>

  <!-- Either apply PXFilter on all traffic -->
  <filter-mapping>
     <filter-name>PXFilter</filter-name>
     <url-pattern>/</url-pattern>
  </filter-mapping>

  <!-- OR apply PXFilter on first party routes only, also remember to apply a filter on the rest of the traffic -->
  <filter-mapping>
     <filter-name>PXFilter</filter-name>
     <!-- Use the app id from the configurations with PX prefix -->
     <url-pattern>/aBcDeFgH</url-pattern>
  </filter-mapping>
</web-app>
```

### <a name="optional_config"></a> Optional Configuration

### <a name="moduleMode"></a>moduleMode
An enum that sets the working mode of the module. `ModuleMode.BLOCKING` sets the module to active blocking. `ModuleMode.MONITOR` inspects the request but does not block it.
Mandatory for active blocking.

Possible values:

- `ModuleMode.BLOCKING`
- `ModuleMode.MONITOR`

**Default:** `ModuleMode.MONITOR`

```java
const pxConfig = {
  ...
   .moduleMode(ModuleMode.BLOCKING)
  ...
};
```

###<a name="moduleEnabled"></a>moduleEnabled
A boolean flag to enable/disable the PerimeterX worker.                                                                                             

**Default:** True       

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .moduleEnabled(false)
...
```

###<a name="blockingScore"></a>blockingScore
Sets the minimum blocking score of a request. When the score is equal to or higher than the blockingScore the request is blocked.                                                                         

**Default:** 100

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .blockingScore(95)
```   

###<a name="sensitiveHeaders"></a>sensitiveHeaders
An list of headers that are not sent to PerimeterX servers on API calls.

**Default:** [cookie, cookies] 

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .sensitiveHeaders(new HashSet<String>(Arrays.asList("cookie", "cookies")))
...
```

###<a name="apiTimeout"></a>apiTimeout
The REST API timeout in milliseconds.

**Default:** 1000

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .apiTimeout(2000)
...
```

###<a name="connectionTimeout"></a>connectionTimeout
The connection timeout in milliseconds.                                                                                                               

**Default** 1000

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .connectionTimeout(2000)§
...
```

###<a name="customLogo"></a>customLogo
The logo is displayed at the top of the the block page. Max-height = 150px, Width = auto.

**Default:** null

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .customLogo(URL_TO_LOGO)
...
```                               

###<a name="cssRef"></a>cssRef
Modifies a custom CSS by adding the CSSRef directive and providing a valid URL to the CSS.

**Default:** null

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .cssRef(URL_TO_CSS)
...
```

###<a name="jsRef"></a>jsRef
Adds a custom JS file by adding JSRef directive and providing the JS file that is loaded with the block page. 

**Default:** null

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .jsRef(URL_TO_JS)
...
```

###<a name="sensitiveRoutes"></a>sensitiveRoutes
An list of route prefixes that trigger a server call to PerimeterX servers every time the page is viewed, regardless of viewing history.                       

**Default:** Empty list

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .sensitiveRoutes(new HashSet<String>(Arrays.asList("/cartCheckout")))
...
```

###<a name="remoteConfigurationEnabled"></a>remoteConfigurationEnabled
A boolean flag to enable/disable remote configurations. When enabled, the initial configurations are set through the constructor and are set in the portal.

**Default:** false

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .remoteConfigurationEnabled(true)
...
```

###<a name="captcha"></a>captchaProvider
An enum that sets the CAPTCHA provider that is displayed on the PerimeterX default CAPTCHA page.                                                                                               

Possible values:

* `CaptchaProvider.RECAPTCHA`


**Default:** `CaptchaProvider.RECAPTCHA`

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .customParametersProvider(CaptchaProvider.RECAPTCHA)
...
```

###<a name="ipHeaders"></a>ipHeaders
An list of trusted headers that specify an IP to be extracted. If the list is empty, the default IP header `cf-connecting-ip` is used.                                                           

`ipHeaders` is used with `CombinedIPProvider`

**Default:** Empty List

```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
   .ipHeaders(new HashSet<String>(Arrays.asList("x-px-true-ip", "x-true-ip")))
...
```

###<a name="customParametersProvider"></a>Custom Parameters Provider
Risk API requests can be enriched with custom parameters by implementing `CustomParametersProvider` and adding logic to extract the custom parameters from the request. Before implementing the interface, make sure that the custom parameters on the portal are configured.
The custom parameters must not be marked as query string.

```java
public class PerimeterxCustomParamsProvider implements CustomParametersProvider {
        public CustomParameters buildCustomParameters(PXConfiguration pxConfiguration, PXContext pxContext) {
            ... Some logic ...
            String cp2 = "PerimeterX_Custom_param2";
            String cp5 = "PerimeterX_Custom_param5";
            customParameters.setCustomParam2(cp2);
            customParameters.setCustomParam5(cp5);
            ... Some logic ...
            
            return customParameters;
        }
    }
```

### <a name="interfaces"></a> Interfaces
Modifying the `perimeterx-java-sdk` interface allows for more flexibility in the PerimeterX module.

The following interfaces are available:
 
|Interface Name| Description | Default Interface | Method |
|--------------|-------------|-------------------|--------|
| ActivityHandler |The handler for all asynchronous activities from type `enforcer_telemetry`, `page_requested` and `block`. |BufferedActivityHandler|setActivityHandler|
| BlockHandler |The BlockingHandler is called when `pxVerify` returns that the user is not verified.|DefaultBlockHandler| setBlockHandler|
| IPProvider |Handles IP address extraction in the request. |CombinedIPProvider| setIpProvider|
| HostnameProvider |Handles hostname extraction in the request|DefaultHostnameProvider| setHostnameProvider|
| VerificationHandler |Handles verification after PerimeterX finished analyzing the request. |DefaultVerificationHandler|setVerificationHandler|
| CustomParametersProvider | Adds additional custom parameters to the Risk API. | CustomParametersProvider| customParametersProvider|

1. Once the PerimeterX instance is initialized, set the interfaces:

 ```java
        PXConfiguration pxConf = new PXConfiguration.Builder()
                .build();

        this.enforcer = PerimeterX.getInstance(pxConf);
 ```
                
2. Change the blocking handler from the default to the PerimeterX custom block handler, as follows:

 ```java        
        this.enforcer.setBlockHandler(new NewBlockHandler());
        this.enforcer.setActivityHandler(new BlockingActivityHandler());
 ```

> Note: When CAPTCHA logic is enabled, a blocking handler that displays the appropriate html page with CAPTCHA must be used. For example,  CaptchaBlockHandler that is included in the SDK.


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
    if (!ctx.isHandledResponse()) {
       // request should be blocked and BlockHandler was triggered on HttpServerResponseWrapper
    }
...
}
```

## <a name="appendix"></a> Appendix

### <a name="loggin-troubleshoot"></a> Logging and Troubleshooting
`perimeterx-java-sdk` is using SLF4J for logs.  
For further information please visit [SLF4J](https://www.slf4j.org/manual.html)

The following steps are welcome when contributing to our project.

#### Fork/Clone

First and foremost, [Create](https://guides.github.com/activities/forking/) a fork of the repository, and clone it locally. Create a branch on your fork, preferably using a self descriptive branch name.

#### Code/Run

Code your way out of your mess, and help improve our project by implementing missing features, adding capabilities or fixing bugs.

To run the code, simply follow the steps in the [installation guide](). Grab the keys from the PerimeterX Portal, and try refreshing your page several times continuously. If no default behaviors have been overriden, you should see the PerimeterX block page. Solve the CAPTCHA to clean yourself and start fresh again.

#### Pull Request

After you have completed the process, create a pull request to the Upstream repository. Please provide a complete and thorough description explaining the changes. Remember this code has to be read by our maintainers, so keep it simple, smart and accurate.
