var cordova = require('cordova'), exec = require('cordova/exec');
var KEY = "Plugin.FirstPhoneLauncher";

var FirstPhoneLauncher = function() {

};

FirstPhoneLauncher.prototype.startApp = function (argsJsonArray, successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'startApp', argsJsonArray);
};

FirstPhoneLauncher.prototype.getDevice = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'getDevice', []);
};

FirstPhoneLauncher.prototype.getUser = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'getUser', []);
};

FirstPhoneLauncher.prototype.exit = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'exit', []);
};

FirstPhoneLauncher.prototype.goHome = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'goHome', []);
};

FirstPhoneLauncher.prototype.dial = function(number, successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'dial', [number]);
};

FirstPhoneLauncher.prototype.getSettings = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'getSettings', []);
};

FirstPhoneLauncher.prototype.showKeyboard = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'showKeyboard', []);
};

FirstPhoneLauncher.prototype.hideKeyboard = function(successCallback, failureCallback) {
    exec(successCallback, failureCallback, KEY, 'hideKeyboard', []);
};

var launcher = new FirstPhoneLauncher();

module.exports = launcher;
