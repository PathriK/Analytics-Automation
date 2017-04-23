"use strict";

const http = require('http');
const net = require('net');
const url = require('url');
const querystring = require('querystring');

 
let debugging = 0;
 
let regex_hostport = /^([^:]+)(:([0-9]+))?$/;

let shouldRecord = true;
let analyticsData = [];
let metricsDomain = "";

 
let getHostPortFromString = function ( hostString, defaultPort ) {
  let host = hostString;
  let port = defaultPort;
 
  let result = regex_hostport.exec( hostString );
  if ( result != null ) {
    host = result[1];
    if ( result[2] != null ) {
      port = result[3];
    }
  }
 
  return( [ host, port ] );
}
 
// handle a HTTP proxy request
let httpUserRequest = function ( userRequest, userResponse ) {
  if ( debugging ) {
    console.log( '  > request: %s', userRequest.url );
  }
  const chunks = [];
  let httpVersion = userRequest['httpVersion'];
  let hostport = getHostPortFromString( userRequest.headers['host'], 80 );
 
  // have to extract the path from the requested URL
  let path = userRequest.url;
  let result = /^[a-zA-Z]+:\/\/[^\/]+(\/.*)?$/.exec( userRequest.url );
  if ( result ) {
    if ( result[1].length > 0 ) {
      path = result[1];
    } else {
      path = "/";
    }
  }
  	let parsed = url.parse(userRequest.url,true);

    if ( debugging ) {
    console.log( '  > before filter: %s', hostport[0], path );
  }

  if(hostport[0]=== '127.0.0.1' || hostport[0]=== 'localhost'){
	if(path === '/start'){ //Handling 'start' request from Selenium
		console.log('starting capture');
		if( debugging )
			console.log(' headers: ', userRequest.headers);
		shouldRecord = true;
		analyticsData = [];
		metricsDomain = userRequest.headers['metrics-domain'];
		userRequest.on('data', (chunk) => {}); //No data is expected in 'start' request so ignoring
		userRequest.on('end', (chunk) => { //Empty success response
			userResponse.writeHead('200');
			userResponse.end();
		});			
	}else{
		console.log('stoping capture'); //Handle for 'stop' request from Selenium
		shouldRecord = false;
		userRequest.on('data', (chunk) => {}); //No data is expected in 'stop' request so ignoring
		userRequest.on('end', (chunk) => {
			userResponse.writeHead('200');
			userResponse.write(JSON.stringify(analyticsData), 'binary'); //Writing the captured Analytics data as response
			analyticsData = [];
			userResponse.end();
		});						
	}
	return;		
  }	  
  
  if(shouldRecord && userRequest.method === "GET" && metricsDomain != "" && metricsDomain === hostport[0]){	//Capturing the Analytics data
	analyticsData.push(parsed.query);
  }
 
  let options = {
    'host': hostport[0],
    'port': hostport[1],
    'method': userRequest.method,
    'path': path,
    'agent': userRequest.agent,
    'auth': userRequest.auth,
    'headers': userRequest.headers
  };
 
  if ( debugging ) {
    //console.log( '  > options: %s', JSON.stringify( options, null, 2 ) );
  }
 
  let proxyRequest = http.request(
    options,
    function ( proxyResponse ) {
      if ( debugging ) {
        console.log( '  > request headers: %s', JSON.stringify( options['headers'], null, 2 ) );
      }
 
      if ( debugging ) {
        console.log( '  < response %d headers: %s', proxyResponse.statusCode, JSON.stringify( proxyResponse.headers, null, 2 ) );
      }
 
      userResponse.writeHead(
        proxyResponse.statusCode,
        proxyResponse.headers
      );
 
      proxyResponse.on(
        'data',
        function (chunk) {
          if ( debugging ) {
            console.log( '  < chunk = %d bytes', chunk.length );
          }
          userResponse.write( chunk );
        }
      );
 
      proxyResponse.on(
        'end',
        function () {
          if ( debugging ) {
            console.log( '  < END' );
          }
          userResponse.end();
        }
      );
    }
  );
 
  proxyRequest.on(
    'error',
    function ( error ) {
      userResponse.writeHead( 500 );
      userResponse.write(
        "<h1>500 Error</h1>\r\n" +
        "<p>Error was <pre>" + error + "</pre></p>\r\n" +
        "</body></html>\r\n"
      );
      userResponse.end();
    }
  );
 
  userRequest.addListener(
    'data',
    function (chunk) {
      if ( debugging ) {
        console.log( '  > chunk = %d bytes', chunk.length );
      }
	  if(shouldRecord && userRequest.method === "POST" && metricsDomain != "" && metricsDomain === hostport[0]){		
		chunks.push(chunk);
	  }
      proxyRequest.write( chunk );
    }
  );
 
  userRequest.addListener(
    'end',
    function () {
      proxyRequest.end();
	  if(shouldRecord && userRequest.method === "POST" && metricsDomain != "" && metricsDomain === hostport[0]){		
		const queryData = Buffer.concat(chunks);
		analyticsData.push(querystring.parse(queryData.toString()));
	  }
    }
  );
}
 
let main = function (argPort, argDebug) {
  let port = 8080; // default port if none on command line

 if(argPort)
	port = argPort;
 if(argDebug)
	debugging = 1

    console.log( 'server listening on port ' + port );
 
  // start HTTP server with custom request handler callback function
  let server = http.createServer( httpUserRequest ).listen(port);
 
  // add handler for HTTPS (which issues a CONNECT to the proxy)
  server.addListener(
    'connect',
    function ( request, socketRequest, bodyhead ) {
      let url = request['url'];
      let httpVersion = request['httpVersion'];
 
      let hostport = getHostPortFromString( url, 443 );
 
      if ( debugging )
        console.log( '  = will connect to %s:%s', hostport[0], hostport[1] );
 
      // set up TCP connection
      let proxySocket = new net.Socket();
      proxySocket.connect(
        parseInt( hostport[1] ), hostport[0],
        function () {
          if ( debugging )
            console.log( '  < connected to %s/%s', hostport[0], hostport[1] );
 
          //if ( debugging )
            //console.log( '  > writing head of length %d', bodyhead.length );
 
          proxySocket.write( bodyhead );
 
          // tell the caller the connection was successfully established
          socketRequest.write( "HTTP/" + httpVersion + " 200 Connection established\r\n\r\n" );
        }
      );
 
      proxySocket.on(
        'data',
        function ( chunk ) {
          //if ( debugging )
            //console.log( '  < data length = %d', chunk.length );
 
          socketRequest.write( chunk );
        }
      );
 
      proxySocket.on(
        'end',
        function () {
          if ( debugging )
            console.log( '  < end' );
 
          socketRequest.end();
        }
      );
 
      socketRequest.on(
        'data',
        function ( chunk ) {
          //if ( debugging )
            //console.log( '  > data length = %d', chunk.length );
 
          proxySocket.write( chunk );
        }
      );
 
      socketRequest.on(
        'end',
        function () {
          if ( debugging )
            console.log( '  > end' );
 
          proxySocket.end();
        }
      );
 
      proxySocket.on(
        'error',
        function ( err ) {
          socketRequest.write( "HTTP/" + httpVersion + " 500 Connection error\r\n\r\n" );
          if ( debugging ) {
            console.log( '  < ERR: %s', err );
          }
          socketRequest.end();
        }
      );
 
      socketRequest.on(
        'error',
        function ( err ) {
          if ( debugging ) {
            console.log( '  > ERR: %s', err );
          }
          proxySocket.end();
        }
      );
    }
  ); // HTTPS connect listener
}
 
//main();

module.exports = {
	start: main
}