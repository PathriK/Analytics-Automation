var http = require('http');

http.createServer((request, response) => {
	console.log(request.connection.remoteAddress + ": " + request.method + " " + request.url);

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