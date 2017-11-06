Directives
===========================================

- [PXConfiguration](#px-config)
- [Examples](#examples)

## <a name="px-config"></a>PXConfiguration

|Directive Name| Description   | Default value   | Values  | Note |
|--------------|---------------|-----------------|---------|------|
|appId|PX custom application id in the format of PX______|null|String|mandatory|
|cookieKey|Key used for cookie signing - Can be found \ generated in PX portal - Policy page.|null|String|mandatory|
|authToken|JWT token used for REST API - Can be found \ generated in PX portal - Application page.|null|String|mandatory|
|moduleMode|Set the mode for PerimeterX module, Blocking or Monitor, setting to blocking mode meaning the module will be active blocking, monitor mode will only inspect the request but will not block it|Monitor|ModuleMode.BLOCKING / ModuleMode.MONITOR|enum, mandatory for active blocking|
|moduleEnabled|Flag for enabling \ disabling Perimeterx protection|true|Boolean| |
|encryptionEnabled|Flag indicating the module to decode or decrypt a cookie|true|Boolean| |
|blockingScore|When requests with a score equal to or higher value they will be blocked.|100|Number| |
|sensitiveHeaders|Marks which headers will not be send to PerimeterX backends|[cookie, cookies]|Set<String> | |
|maxBufferLen|Set the number of activities to send in batched activities|10|Number| |
|apiTimeout |REST API timeout in milliseconds|1000|Number|Milliseconds|
|connectionTimeout|Connection timeout in milliseconds|1000|Number|Milliseconds|
|maxConnectionsPerRoute|Set the maximum connection per route for risk api requests in the connections pool|20|Number| |
|maxConnections|Set the total maximum connections for risk api client|20|Number| |
|sendPageActivities|Toggle sending asynchronous page activities|true|Boolean| |
|serverURL|Set the base url for PerimeterX servers|https://sapi-\<app_id>.perimeterx.net|String| |
|customLogo|The logo will be displayed at the top div of the the block page. The logo's max-height property would be 150px and width would be set to auto.|null|String| |
|cssRef|The block page can be modified with a custom CSS by adding the CSSRef directive and providing a valid URL to the css|null|String| |
|jsRef|The block page can be added with custom JS file by adding JSRef directive and providing the JS file that will be loaded with the block page.|null|String| |
|sensitiveRoutes|List of routes the Perimeterx module will always do a server-to-server call for, even if the cookie score is low and valid|Empty list|Set<String>| |
|remoteConfigurationEnabled|Toggle remote configurations, when true, initial configurations will be set through constructor, then can be tuned from the portal|false|Boolean| |
|remoteConfigurationInterval|Set the interval value for when to fetch configurations from PerimeterX configuration service|5000|Number|Milliseconds|
|remoteConfigurationDelay|Set amount of time to delay the remote configuration thread before it starts|0|Number|Milliseconds|
|remoteConfigurationUrl|Set the url for PerimeterX configuration service||String| |
|captchaProvider|Set the captcha provider on the default block page|CaptchaProvider.RECAPTCHA|CaptchaProvider.RECAPTCHA / CaptchaProvider.FUNCAPTCHA|enum|

Examples below

## <a name="examples"></a>Examples

##### Basic Active And Blocking Configuration
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
    .appId("APP_ID")
    .cookieKey("AUTH_TOKEN") // Should copy from RiskCookie section in https://console.perimeterx.com/#/app/policiesmgmt
    .moduleMode(ModuleMode.BLOCKING)
    .authToken("AUTH_TOKEN") // PX Server request auth token to be copied from Token section in https://console.perimeterx.com/#/app/applicationsmgmt
    .build();
```

##### Module Enabled/Disabled
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .moduleEnabled(false) // default is true
...
```

##### Tune Blocking Score Threshold
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .blockingScore(95)
```

##### Sensitive Headers
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .sensitiveHeaders(new HashSet<String>(Arrays.asList("cookie", "cookies")))
...
```

##### Sensitive Routes
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .sensitiveRoutes(new HashSet<String>(Arrays.asList("/cartCheckout")))
...
```

##### Customizing Default Block Page
 ```java
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .customLogo(URL_TO_LOGO)
    .cssRef(URL_TO_CSS)
    .jsRef(URL_TO_JS)
...
```

##### Captcha Provider
```
PXConfiguration pxConf = new PXConfiguration.Builder()
...
    .captchaProvider(CaptchaProvider.FUNCAPTCHA)
...
```
