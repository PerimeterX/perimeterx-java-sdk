# Change Log

## [v6.10.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.X.X...HEAD) (2023-XX-XX)
- Added feature  request-header-based-logger
- Align risk api and async activities fields
- Added sending risk field and enforcer start timestamp to activities schema
- Removed the `blockedUrl` window variable from the block page to prevent XSS vulnerability
- Added blocked URL to the captcha query params

## [v6.9.5](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.5...HEAD) (2023-11-23)
- Updated the configuration of PX first-party requests to include a connection timeout.
- Updated the captcha template to handle empty captcha responses.

## [v6.9.4](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.4...HEAD) (2023-11-21)
- Fixed first party connection timeout issue.
- Updated the captcha template with timeout mechanism addressing scenarios where delays occurred in retrieving the captcha.

## [v6.9.3](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.3...HEAD) (2023-11-16)
- Fixed risk request schema.
- Fixed cookie validation.

## [v6.9.2](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.2...HEAD) (2023-11-15)
- Fixed potential XHR first party issue.

## [v6.9.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.1...HEAD) (2023-11-13)
- Added blocked URL to ABR and captcha template

## [v6.9.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.9.0...HEAD) (2023-11-08)
- Added RequestFilter to consolidate all request filters for improved management and organization.
- Added CustomFilterByFunction feature to enhance filtering capabilities.
- Fixed Context URI to Servlet Path for better compatibility.
- Fixed Cookie Issue with 2-Byte Encoded Characters**: Resolved an issue related to cookies containing 2-byte encoded characters.
- Fixed Risk Request Schema to include the `client_uuid` for better data handling and analysis.
- Fixed the Block and Captcha pages, aligning them with the specified design and adding hard block functionality to align with spec.
- Fixed Risk UUID to ensure it is set even when encountering a server-to-server error.
- Fixed Async Activities Schema Addressed issues with the activity schema to ensure data accuracy and integrity.
- Fixed resource management issue in various code locations.

## [v6.8.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.8.1...HEAD) (2023-10-22)
- Fixed handling of cookies with illegal arguments.

## [v6.8.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.8.0...HEAD) (2023-10-18)
- Fixed unhandled Telemetry error
- configurable IPXHttpClient
- configurable PXClient
- PXHD doesn't set cookie after risk_api
- Added http method check for static content extensions

## [v6.7.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/6.7.1...HEAD) (2023-09-05)
- Added logs for timeouts
- Running async activities via ExecutorService

## [v6.7.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.7.0...HEAD) (2023-11-05)
- Added feature custom cookie header
- Changed `getTelemetryConfig` is now using builder.
- Bugfix `NullPointerException` when using `ConsoleLogger`.

## [v6.6.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.6.0...HEAD) (2023-27-04)
- Updating readme with `customIsSensitve`, `customParametersExtraction`
- Added an option to configure logger without slf4j using `PXConfiguration.setPxLoggerSeverity(<loggerSeverity>)`
- Added an option to close PerimeterX [SDKNEW-2781](https://perimeterx.atlassian.net/browse/SDKNEW-2781)

## [v6.5.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.5.0...HEAD) (2023-03-04)
- Adding custom is sensitive configuration option
- Lazy read the request body
- Added new custom parameters function signature which receives the original HTTP request
- Reading the body binary instead of textually

## [v6.4.5](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.5...HEAD) (2023-01-11)
- Fixed invalid http connections for risk requests bug.

## [v6.4.4](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.4...HEAD) (2022-09-05)
- Added pass reason `enforcer_error`
- Changed s2s_error_reason to error_reason

## [v6.4.3](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.3...HEAD) (2022-06-28)
- Fixed `s2s_call_reason` as `sensitive_route` in case of Credentials Intelligence request.

## [v6.4.2](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.2...HEAD) (2022-06-01)
- Added sending telemetry by Slack command

## [v6.4.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.1...HEAD) (2022-04-17)
- Support creating block activity after block handler invocation

## [v6.4.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.4.0...HEAD) (2022-04-13)
- Support for credentials intelligence protocols `v1`, `v2` and `multistep_sso`
- Support for login successful reporting methods `header`, `status`, `body`, and `custom`
- Support for manual sending of `additional_s2s` activity via header and function call.
- Support for sending raw username on `additional_s2s` activity
- Support for login credentials extraction via custom callback
- New `request_id` field to all enforcer activities

## [v6.3.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.3.0...HEAD) (2022-04-11)
- Added new block page implementation 

##[v6.2.8](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.8...HEAD) (2022-03-31)
- Added monitored routes feature. 
- Added enforced routes feature.
- Updated Lombok dependency version to 1.18.22

## [v6.2.7](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.7...HEAD) (2022-02-21)
- Added ability to mark simulated block on context

## [v6.2.6](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.6...HEAD) (2021-05-31)
- Added supported features list to project metadata

## [v6.2.5](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.5...HEAD) (2021-05-03)
- Fixed dependencies vulnerability issue by upgrading dependencies

## [v6.2.4](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.4...HEAD) (2020-12-06)
- fix http_method bug when there is no http_version

## [v6.2.3](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.3...HEAD) (2020-11-04)
- fixed CLIENT_HOST scheme
- add query params to URL field

## [v6.2.2](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.2...HEAD) (2020-10-09)
- new version to update files on Maven Central

## [v6.2.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.1...HEAD) (2020-10-08)
- fixed CLIENT_HOST configuration field

## [v6.2.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.2.0...HEAD) (2020-08-23)
- Support regex values for sensitive-routes configuration

## [v6.1.5](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.1.5...HEAD) (2020-05-13)
- Fixed 3rd party libs vulnerability issues

## [v6.1.4](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.1.4...HEAD) (2020-03-04)
- Log exception information on deserialize by cookie selector
- Fixed vulnerability issue by upgrading FasterXML version

## [v6.1.3](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.1.3...HEAD) (2020-02-24)
- fix PBKDF2 iterations range check to be greater than 0

## [v6.1.2](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.1.2...HEAD) (2019-09-29)
- Fix potential concurrency problems within activity buffer
- Increase default activities batch size from 10 to 20
- Update underlying libs versions

## [v6.1.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v6.1.1...HEAD) (2019-06-30)
- Fixed vulnerability issue by upgrading FasterXML version

## [v6.1.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2019-04-08)
- Support advanced blocking response - response can be json structured instead of html
- Ignoring static files (json, imgs ...)
- Support for testing blocking flow in monitor mode
- Bypass Bypass
- Added support to load config from a file

## [v6.0.5](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2019-02-25)
- Fixed the setting process of the pxhd cookie

## [v6.0.4](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2019-02-04)
- Added multiple applications support (PerimeterX class can be initialized multiple times within the same process)
- Added some logs to increase visibility over httpasyncclient exceptions
- Add client PXVID as a vid source
- Changed simulatedBlock to be a boolean
- Added vid_source to additional in async activities and renamed to enforcer_vid_source

## [v6.0.3](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2019-01-15)
- Removed pxvid from no_cookie_w_vid assertion

## [v6.0.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-12-25)
- Added PXHD handling (new px cookie has been added)
- Added async custom params
- Fixed activities connection errors


## [v5.4.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-12-13)
- Removed logback log implementation.
- Removed debugMode configuration, instead use log level configuration per logger implementation.

## [v5.3.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-12-12)
- Fixed http components memory leak ([HTTPASYNC-116](https://issues.apache.org/jira/browse/HTTPASYNC-116))
- Added custom params to async activities (page_requested, block)
- Added data enrichment to context
- Changed logger implementation to logback
- formatted code style across project files
- Added debugMode configuration that changes the log level from ERROR to DEBUG

## [v5.2.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-11-13)
- Fixed an issue when the acitivity telemetry won't send pxConfiguration.
- Fixed the usage of custom activity handlers: The verification handler used to override the custom
activity handler.
- Added request cookie name extraction, requestCookieNames field sent during risk api call

## [v5.1.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-10-29)
- Added testing mode capability
- Added Firsty party fallback when encountering redirection errors
- Reordered the cookies such that the v3 cookie will be selected before v1

## [v5.0.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-08-28)
- Added handling of mobile tokens: x-px-tokens, x-px-original-tokens
- Now using CaptchaV2 instead of a third party captcha provider
- Added proxy support


## [v4.2.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-08-06)
- Additional mobile handling
- Better cookie decryption

## [v4.1.1](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-07-22)
- Fixed logging level for unexpected risk result

## [v4.1.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-07-20)
- Fixed index out of bound error

## [v4.0.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-06-06)
- Fixed CustomBlockHandler implementations
- Added support for First Party
- Deprecated PXContext's method `isVerified()`, instead use `isHandledResponse()`, read more about it on at the Upgrading section
- Update jackson packages

## [v3.1.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2018-04-04)
- Replaced footer on block pages
- New logs format
- Improved enforcer telemetry
- Mobile SDK support
- Custom Params support

## [v3.0.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2017-11-07)
- Remote Configuration support (by default is off)
- Fixed `risk_rtt` for `s2s` on exception
- Support `js challenge`
- Sending `enforcer_telemetry` activities on init and remote config updates, telemetry includes px_config as json, os name and machine name
- Supporting `funCaptcha`
- New captcha flow
- Fixed bug in S2S `pass_reason`
- New documentation
- Support for `monitor mode` (default set to `true`)
- Support for ipHeaders (using new class `CombinedIPProvider`)

**This version includes breaking changes in the following configurations:**
- Monitor mode is now on by default, for blocking mode it should be set to ACTIVE
- BlockingScore was changed from 70 -> 100
- Using ipHeaders from configuration - use default interface of IPProvider (`CombinedIPProvider` instead of `RemoteAddressIPProvider`)

## [v2.1.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2017-30-07)
 - Renamed expired_cookie call reason to cookie_expired
 - Custom verification handler is now supported
 - Added `pass_reason` to `page_requested`
 - Sending `client_uuid` on `page_requested` activities
 - `pxVerify` now returning context instead of boolean value
 - Fixed wrong hostname being collected on `DefualtHostnameProvider`

## [v2.0.0](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.17...HEAD) (2017-25-04)

- Support cookie v3
- Support risk API v2
- Support custom css/javascript/logo on block page
- Send px_cookie_orig when cookie decryption fails
- Invalid cookie format handling
- Buffered activities handling (async send)
- Updated server URL
- Redesign block/captcha page

## [v1.0.16](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.16...HEAD) (2016-02-10)

- `HostnameProvider` interface to allow user defined hostname extraction from http request.


## [v1.0.15](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.15...HEAD)

- Decrypted risk cookie was added to page_requested activity.
- UUID was added to captcha api request.
- PerimeterX server base url changed.
- Documentation updated.
- Bug fix: page_requested payload do not include block activities field.

**Merged pull requests:**

- Build and deploy automation [\#5](https://github.com/PerimeterX/perimeterx-java-sdk/pull/5) ([pxaviad](https://github.com/pxaviad))

## [v1.0.14](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.14) (2016-10-10)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.13...v1.0.14)

**Merged pull requests:**

- compatibility with older jackson [\#4](https://github.com/PerimeterX/perimeterx-java-sdk/pull/4) ([pukomuko](https://github.com/pukomuko))

## [v1.0.13](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.13) (2016-09-27)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.12...v1.0.13)

## [v1.0.12](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.12) (2016-09-26)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.11...v1.0.12)

## [v1.0.11](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.11) (2016-09-26)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.10...v1.0.11)

## [v1.0.10](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.10) (2016-09-23)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v.1.0.9...v1.0.10)

**Merged pull requests:**

- user-agent and cookie fix case insensitive [\#3](https://github.com/PerimeterX/perimeterx-java-sdk/pull/3) ([barakpx](https://github.com/barakpx))

## [v.1.0.9](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v.1.0.9) (2016-09-22)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/v1.0.8...v.1.0.9)

## [v1.0.8](https://github.com/PerimeterX/perimeterx-java-sdk/tree/v1.0.8) (2016-09-21)
[Full Changelog](https://github.com/PerimeterX/perimeterx-java-sdk/compare/1.0.1...v1.0.8)

**Merged pull requests:**

- Dev less depends [\#2](https://github.com/PerimeterX/perimeterx-java-sdk/pull/2) ([pxaviad](https://github.com/pxaviad))

## [1.0.1](https://github.com/PerimeterX/perimeterx-java-sdk/tree/1.0.1) (2016-08-22)
**Merged pull requests:**

- Dev java17 [\#1](https://github.com/PerimeterX/perimeterx-java-sdk/pull/1) ([pxaviad](https://github.com/pxaviad))

\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*
