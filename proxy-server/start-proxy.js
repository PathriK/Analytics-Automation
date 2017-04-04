#!/usr/bin/env node

var proxyServer = require("./proxy-server");
proxyServer.start(8080, false);