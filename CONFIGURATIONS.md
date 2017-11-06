Directives
===========================================

- [PXConfiguration](#px-config)

## <a name="#px-config"></a>PXConfiguration

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
|signedWithIP|Toggles if signing cookie with user-ip|false|Boolean|Must be aligned in portal|
|serverURL|Set the base url for PerimeterX servers|https://sapi-\<app_id>.perimeterx.net|String| |
|customLogo|The logo will be displayed at the top div of the the block page. The logo's max-height property would be 150px and width would be set to auto.|null|String| |
|cssRef|The block page can be modified with a custom CSS by adding the CSSRef directive and providing a valid URL to the css|null|String| |
|jsRef|The block page can be added with custom JS file by adding JSRef directive and providing the JS file that will be loaded with the block page.|null|String| |
|sensitiveRoutes|List of routes the Perimeterx module will always do a server-to-server call for, even if the cookie score is low and valid|Empty list|Set<String>| |
|remoteConfigurationEnabled|Toggle remote configurations, when true, initial configurations will be set through constructor, then can be tuned from the portal|false|Boolean| |
|remoteConfigurationInterval|Set the interval value for when to fetch configurations from PerimeterX configuration service|5000|Number|Milliseconds|
|remoteConfigurationDelay|Set amount of time to delay the remote configuration thread before it starts|0|Number|Milliseconds|
|remoteConfigurationUrl|Set the url for PerimeterX configuration service||String| |
