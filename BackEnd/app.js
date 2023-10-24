var express = require("express")
var http = require('http');
var https = require('https');

var fs = require('fs');

var Player = require('./player.js');
var Instance = require('./instance.js');
var Session = require('./instance.js');

const options = {
	//key: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem', 'utf8'),
	//cert: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem', 'utf8')
};



async function run(){
	console.log("Running")
	try{//CHANGE THIS TO HTTPS after uncommenting the keys
		http.createServer(options, handleRequest).listen(8081)
	}
	catch(err){
		console.log(err)
	}
}

//Code for making post requests from stackoverflow: https://stackoverflow.com/questions/12006417/node-js-server-that-accepts-post-requests			
function handleRequest(request, response){
	if(request.method == 'POST'){
				console.log('POST')
				var body = ''
				request.on('data', function(data) {
					body += data
					console.log('Partial body: ' + body)
				})
				request.on('end', function() {
					message = JSON.parse(body)
					console.log('Body: ' + message)
					response.writeHead(200, {'Content-Type': 'text/html'})
					if(message.subject == "connect"){
						playerId = connectPlayer(message.data)
						
						sessionId = Math.random();
						Instance.sessionList[id] = new Session(id, [Instance.PlayerList[playerId]])
						
						response.end(playerId);
					}
					else if(message.subject == "join"){
						
					}
					else{
						response.end("unknown subject")
					}
				})
			}
			else{
				response.writeHead(200);
				response.end("Welcome to Node.js HTTPS servern");
			}	
	
}

function connectPlayer(data){
	id = Math.random();
	Instance.playerList[id] = new Player(id, data.name); 
  return String(id);
}



run()