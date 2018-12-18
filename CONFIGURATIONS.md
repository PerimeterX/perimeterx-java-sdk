Directives
===========================================

- [PXConfiguration](#px-config)
- [Interfaces](#interfaces)
- [Examples](#examples)

## <a name="px-config"></a>PXConfiguration

|Interface Name| Description | Default value | Values | Note |
|--------------|:-------------:|---------------|--------|------|
|appId|PX custom application id in the format of PX______|null|String|mandatory|
|cookieKey|Key used for cookie signing - Can be found \ generated in PX portal - Policy page.|null|String|mandatory|
|authToken|JWT token used for REST API - Can be found \ generated in PX portal - Application page.|null|String|mandatory|
|moduleMode|Set the mode for PerimeterX module, Blocking or Monitor, setting to blocking mode meaning the module will be active blocking, monitor mode will only inspect the request but will not block it|Monitor|ModuleMode.BLOCKING / ModuleMode.MONITOR|enum, mandatory for active blocking|
|moduleEnabled|Flag for enabling \ disabling Perimeterx protection|true|Boolean| |
|encryptionEnabled|Flag indicating the module to decode or decrypt a cookie|true|Boolean| |
|blockingScore|When requests with a score equal to or higher value they will be blocked.|100|int| |
|sensitiveHeaders|Marks which headers will not be send to PerimeterX backends|[cookie, cookies]|Set<String> | |
|maxBufferLen|Set the number of activities to send in batched activities|10|int| |
|apiTimeout |REST API timeout in milliseconds|1000|int|Milliseconds|
|connectionTimeout|Connection timeout in milliseconds|1000|int|Milliseconds|
|maxConnectionsPerRoute|Set the maximum connection per route for risk api requests in the connections pool|20|int| |
|maxConnections|Set the total maximum connections for risk api client|20|int| |
|sendPageActivities|Toggle sending asynchronous page activities|true|Boolean| |
|serverURL|Set the base url for PerimeterX servers|https://sapi-\<app_id>.perimeterx.net|String| |
|customLogo|The logo will be displayed at the top div of the the block page. The logo's max-height property would be 150px and width would be set to auto.|null|String| |
|cssRef|The block page can be modified with a custom CSS by adding the CSSRef directive and providing a valid URL to the css|null|String| |
|jsRef|The block page can be added with custom JS file by adding JSRef directive and providing the JS file that will be loaded with the block page.|null|String| |
|sensitiveRoutes|List of routes the Perimeterx module will always do a server-to-server call for, even if the cookie score is low and valid|Empty list|Set<String>| |
|remoteConfigurationEnabled|Toggle remote configurations, when true, initial configurations will be set through constructor, then can be tuned from the portal|false|Boolean| |
|remoteConfigurationInterval|Set the interval value for when to fetch configurations from PerimeterX configuration service|5000|int|Milliseconds|
|remoteConfigurationDelay|Set amount of time to delay the remote configuration thread before it starts|0|int|Milliseconds|
|remoteConfigurationUrl|Set the url for PerimeterX configuration service||String| |
|ipHeaders|List of headers to extract the user ip from, if not set, it will be taken from default|Empty List|Set<String>|Use with `CombinedIPProvider`|
|firstPartyEnabled|Toggle first party requests enabled|true|boolean|Read more details about first pary integration [here](#first-party-integration)|
|xhrFirstPartyEnabled|Toggle first party XHR requests will be forwarded to PerimeterX servers|true|boolean| |
|useProxy|The http client shall use a proxy for message forwarding|false|boolean|
|proxyHost|The proxy's host name|String|
|proxyPort|The proxy's port|None - required|int|
|testingMode|Running the enforcer in dev environment for testing purposes, response returned as a JSON object. When you run the enforer in testing mode, please do not set costume verification handler|false|boolean
|validateRequestQueueInterval|Interval in seconds of cleaning requests queue. Solves HttpComponent reference leak bug.|5|int|

## <a name="interfaces"></a> Interfaces
`perimeterx-java-sdk` can be tuned and set a different type of interface in order to make the module more flexible
 Below you can find a list of available interfaces and their setter

|Interface Name| Description | Default Interface | method |
|--------------|-------------|-------------------|--------|
| ActivityHandler |Handler for all asynchronous activities from type enforcer_telemetry, page_requested and block|BufferedActivityHandler|setActivityHandler|
| BlockHandler |Blocking handle will be called when pxVerify will return that user is not verified|DefaultBlockHandler| blockHandler|
| IPProvider |Handles IP address extraction from request|CombinedIPProvider| setIpProvider|
| HostnameProvider |Handles hostname extraction from request|DefaultHostnameProvider| setHostnameProvider|
| VerificationHandler |handling verification after PerimeterX service finished analyzing the request|DefaultVerificationHandler|setVerificationHandler|
| CustomParametersProvider | Adds to the risk api additional custom parameters | CustomParametersProvider| customParametersProvider|

The interfaces should be set after PerimeterX instance has been initialized
```java
        BlockHandler exampleBlockHandler = new ExampleBlockHandler();
        PXConfiguration pxConf = new PXConfiguration.Builder(exampleBlockHandler)
                  // This will set the blocking handler from the default one to
                  // the our custom block handler
                .blockHandler()
                .build();
        this.enforcer = new PerimeterX(pxConf)
        this.enforcer.setActivityHandler(new BlockingActivityHandler());
```

## <a name="examples"></a>Configurations Example

##### Basic Active And Blocking Configuration
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
    .appId("APP_ID")
    .cookieKey("AUTH_TOKEN") // Should copy from RiskCookie section in https://console.perimeterx.com/#/app/policiesmgmt
    .moduleMode(ModuleMode.BLOCKING)
    .authToken("AUTH_TOKEN") // PX Server request auth token to be copied from Token section in https://console.perimeterx.com/#/app/applicationsmgmt
    .build();
```

<a name="module-mode"></a>
##### Module Enabled/Disabled
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .moduleEnabled(false) // default is true
...
```
<a name="blocking-score"></a>
##### Tune Blocking Score Threshold
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .blockingScore(95)
```

<a name="sensitive-headers"></a>
##### Sensitive Headers
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .sensitiveHeaders(new HashSet<String>(Arrays.asList("cookie", "cookies")))
...
```
<a name="ip-headers"></a>
##### Ip Headers
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .ipHeaders(new HashSet<String>(Arrays.asList("x-px-true-ip", "x-true-ip")))
...
```

<a name="sensitive-routes"></a>
##### Sensitive Routes
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .sensitiveRoutes(new HashSet<String>(Arrays.asList("/cartCheckout")))
...
```
<a name="css-js-logo"></a>
##### Customizing Default Block Page
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .customLogo(URL_TO_LOGO)
    .cssRef(URL_TO_CSS)
    .jsRef(URL_TO_JS)
...
```

##### Custom Parameters Provider
Risk api requests can be enriched with custom parameters by implementing CustomParametersProvider and adding logic to extract
the custom parameters from the request
Before implementing the interface, please make sure to configure custom parameters on the portal.  
Make sure when the custom parameters are configured that they are NOT marked as query string  

```
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
PXConfiguration pxConf = new PXConfiguration.Builder()
PerimeterxCustomParamsProvider perimeterxCustomParamsProvider = new PerimeterxCustomParamsProvider();
...
    .customParametersProvider(PerimeterxCustomParamsProvider)
...
```

## <a name="first-party-integration"></a> First Party Integration
First-Party will allow the sensor to be served from your domain. Using the First-Party mode is recommended.
By enabling first party mode on the java SDK you will achieve:  

Improved performance - Serving the sensor as part of the standard site content removes the need to open a new connection to PerimeterX servers when a page is loaded.
Improved detection - Third party content may be blocked by certain browser plugins and privacy addons. The First-Party sensor leads directly to improved detection, as seen with customers who previously moved from Third Party sensor to First-Party sensor.


#### Integration:
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

## <a name="Proxy-integration"></a> Proxy Integration
Providing a proxy allows the communication between the enforcer and our backend service via
proxy. You can set the proxy as a hostname (with a domain name), or as an ip port combination.
If you choose to use the proxy's domain, do not enter a port in the configuration.
Make sure you have the proxy's certificate installed on the machine that is running the enforcer.
```
PXConfiguration config = new PXConfiguration.Builder()
        .appId("PXaBcDeFgH")
        .cookieKey("COOKIE_KEY")
        .authToken("AUTH_TOKEN")
        .useProxy(true)
        .proxyHost(127.0.0.1)
        .proxyPort(80)
        .build();
        
PXConfiguration config = new PXConfiguration.Builder()
        .appId("PXaBcDeFgH")
        .cookieKey("COOKIE_KEY")
        .authToken("AUTH_TOKEN")
        .useProxy(true)
        .proxyHost(yourdomain.com)
        .build();
```

