# FIRST Phone Launcher Plugin

## Installation

### Add to Cordova Project

Add the plugin to your cordova project:

```
$ cordova plugin add https://github.homedepot.com/Back-Office-And-In-Aisle-Systems/firstphone-launcher-cordova-plugin.git
```

## Using the Plugin

In your application's javascript code, use

```
    window.plugin.firstphone.launcher.[FUNCTION]
```

where the available functions are:
* getDevice
* getUser
* getSettings
* exit
* goHome
* showKeyboard
* hideKeyboard

### getDevice(successCallback, errorCallback)

Returns device information.

#### Properties Available from Successful Callback

* `deviceId`: alpha-numeric unique identifier for device (e.g. "C06FE9A4-4E3FF2AC018CAF860EF01398-4680C55F")
* `deviceModelNumber`: device model (e.g. "tc70")
* `extensionNumber`: extension number of device (e.g. "101"); set to 0 if no extension is assigned
* `ipAddress`: ip address of the device (e.g. 172.16.218.111)
* `storeNumber`: store number (e.g. "5603")

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        function getDeviceSuccess(deviceInfo) {
            alert('Your store is st' + deviceInfo.storeNumber);
        };

        function getDeviceError(err) {
            // do something with the error
        };

        window.plugin.firstphone.launcher.getDevice(getDeviceSuccess, getDeviceError);
    }
```

### getUser(successCallback, errorCallback)

Returns user information. Will call the errorCallback if no user is logged into the device.

#### Properties Available from Successful Callback

* `associateUserID`: ldap id (e.g. "ASM001")
* `departmentNumber`: department number (e.g. "21")
* `firstName`: associate's first name (e.g. "Joe")
* `middleName`: associate's middle name (e.g. "J")
* `lastName`: associate's last name (e.g. "Smith")
* `imsUserLevel`: inventory management app IMS user level (e.g. 10)
* `ldapGroups`: array of ldap groups (e.g. ["All Store Associates", "Storewalk Store", ...])
* `locationNumber`: user's location number (e.g. "5603")
* `locationType`: location type of store, DC, etc. (e.g. "STR")
* `thdSsoToken`: single sign-on token (e.g. "PZappcHI0WEORdbep2iRMKHjZZSVgYZGRE0yyvGZmIqLg0lY4FjsWMhOiUshXGiOLba3FYNKqdidocTQ19fAh0niT58aZJl1UkZnuWW3lkmuhgv9gPYacJkh512nmEAUYhUNXLzjhsI4FMF8pSZ6cA3IWKrgu89uMSbO2VDOeHGgkAVfToZRKKPhWPRs8klkGCxG2E5lPrEKZefE0aaSGHya62SN5o6ePicSuikYlBLOBxjRA8e2VDEDNPuCqt6QJWTzyH68wIOl4teAftLlMadc2XtyxWc87ByRhDTW0rT17sKbY5ivc6MQFz8E4DsnHpct7UA48iQQWyEHwXNwPGTnEvNyIDz0LFCgba1CzzUUsb2VGPZYTfw8y3uIndq2roFO0rVcYh5x66pWZmkENfY1uQeILWa7LHw6ZynWfTR4T2L24T5kU37fFsmOLiynXVdy1z473bCPYOXMssq9f9TLkV52yBR7QhX1pqZTyjj6I6LHDCarwHs7JZiK7xcegFl8KN2tOrmQWqMGQQ06JHUl1W7V7nCn4YhuaUOiGKJGokZ3AVaR2oqvK6g3VeOJ2U47qu1R5XYmc4jF50lSWZEtujIY0LTFwfRagFTClR953GOwjDzy5gh9lFPWr2TJ1rnDkg7MgoNrbW4cUvbA7TI8a692yIoPzHS7Fq5pcLquiHNQFkZWJN4zSHd86bpJh1zU29k10BIX7GEbuYZCnrJY2QqqD7pjBd1yfbmojaNgupEq")
* `userType`: ??? (e.g. "0")

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        function getUserSuccess(userInfo) {
            alert('Hi ' + userInfo.firstName + ' ' + userInfo.lastName);
        };

        function getUserError(err) {
            // do something with the error
        };

        window.plugin.firstphone.launcher.getUser(getUserSuccess, getUserError);
    }
```

### getSettings(successCallback, errorCallback)

Returns device, location, and user information.

#### Properties Available from Successful Callback

* `device`:
    * `isQA`: whether the device is a QA device (e.g. "true")
    * `languageCode`: language of device (e.g. "en_US")
    * `name`: device name (e.g. "tc70")
    * `version`: version of the first phone launcher (e.g. "1.1.22")
* `location`:
    * `locationName`: full name of the location (e.g. "Store 5603")
    * `storeNumber`: store number (e.g. "5603")
    * `storeServer`: url of the ISP (e.g. "http://st5603.homedepot.com")
* `services`:
    * `rootUrl`: root url for store services
* `user`: set to null if not logged in
    * `associateUserId`: ldap id (e.g. ASM001")
    * `imsUserLevel`: inventory management app IMS user level (e.g. 10)
    * `ldapGroups`: array of ldap groups (e.g. ["All Store Associates", "Storewalk Store", ...])
    * `userType`: ??? (e.g. "0")

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        function getSettingsSuccess(settingsInfo) {
            alert('The launcher version is: ' + settingsInfo.device.version);
        };

        function getSettingsError(err) {
            // do something with the error
        };

        window.plugin.firstphone.launcher.getSettings(getSettingsSuccess, getSettingsError);
    }
```

### exit()

Exits the application.

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        window.plugin.firstphone.launcher.exit();
    }
```

### goHome()

Minimizes the application without exiting.

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        window.plugin.firstphone.launcher.goHome();
    }
```

### showKeyboard()

Shows the soft keyboard.

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        window.plugin.firstphone.launcher.showKeyboard();
    }
```

### hideKeyboard()

Hides the soft keyboard.

#### Example

```
    if (window.plugin && window.plugin.firstphone && window.plugin.firstphone.launcher) {
        window.plugin.firstphone.launcher.hideKeyboard();
    }
```
