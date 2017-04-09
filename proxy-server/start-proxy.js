#!/usr/bin/env node

"use strict";

let proxyServer = require("./proxy-server");
let pjson = require('./package.json');
console.log( 'Analytics Proxy v' + pjson.version + '. Debug Disabled');
proxyServer.start(8080, false);