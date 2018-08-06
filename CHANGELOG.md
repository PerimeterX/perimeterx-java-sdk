# Change Log

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
