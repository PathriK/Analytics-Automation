var http = require('http');
var url = require('url');

var shouldRecord = true;
var analyticsData = [];

http.createServer((request, response) => {
	let targetHost = request.headers['host'];
	let parsed = url.parse(request.url,true);
		
	if(targetHost === '127.0.0.1:8080' || targetHost === 'localhost:8080'){ //If the request is for proxy, it needs to handled and not delegated
		if(request.url === '/start'){ //Handling 'start' request from Selenium
			console.log('starting capture');
			shouldRecord = true;
			analyticsData = [];
			request.on('data', (chunk) => {}); //No data is expected in 'start' request so ignoring
			request.on('end', (chunk) => { //Empty success response
				response.writeHead('200');
				response.end();
			});			
		}else{
			console.log('stoping capture'); //Handle for 'stop' request from Selenium
			shouldRecord = false;
			request.on('data', (chunk) => {}); //No data is expected in 'stop' request so ignoring
			request.on('end', (chunk) => {
				response.writeHead('200');
				response.write(JSON.stringify(analyticsData), 'binary'); //Writing the captured Analytics data as response
				response.end();
			});						
		}
		return;		
	}
	
	if("metrics.apple.com" === targetHost && shouldRecord){	//Capturing the Analytics data
		analyticsData.push(parsed.query);
	}

	//Rest is Node.js Proxy coding
	var options = {
		port: 80,
		host: request.headers['host'],
		method: request.method,
		path: request.url,
		headers: request.headers
	};
	var proxy_request = http.request(options , (proxy_response) =>{
		proxy_response.on('data', (chunk) => {
			response.write(chunk, 'binary');
		});
		proxy_response.on('end', () => {
			response.end();
		});
		response.writeHead(proxy_response.statusCode, proxy_response.headers);
	});
	request.on('error', (e) => {
		console.log('Request error:' + e.message);
	});
	request.on('data', (chunk) => {
		proxy_request.write(chunk, 'binary');
	});
	request.on('end', (chunk) => {
		proxy_request.end();
	});
	
}).listen(8080, () => {
	console.log('server listening on 8080');
});