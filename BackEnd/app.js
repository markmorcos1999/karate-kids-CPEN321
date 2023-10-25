var express = require("express")
var http = require('http');
var https = require('https');

var fs = require('fs');

var Player = require('./player.js');
var Instance = require('./instance.js');
var Session = require('./session.js');

var Matchmaker = require('./matchmaker.js');

const options = {
	key: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem', 'utf8'),
	cert: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem', 'utf8')
};



async function run(){
	console.log("Running")
	try{//CHANGE THIS TO HTTPS after uncommenting the keys
 		https.createServer(options, handleRequest).listen(8081)
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
						
						response.end(playerId);
					}
					else if(message.subject == "requestGame"){
						//Now we must make the player wait a bit for a response.
						Matchmaker.lookForGame(Instance.playerList[message.data.id])
						response.end({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
					}
					else if(message.subject == "endGame"){
						response.end({gamePosition: "0"})
					}
					else if(message.subject == "leaderboard"){
						response.end(JSON.stringify([{name:"Kyle", ELO: "1000"}, {name:"Mark", ELO: "1001"}]));//Here add the "database get leaderboard"
					}
					else if(message.subject == "statsRequest"){
						response.end(JSON.stringify({gamesWon:"100", gamesLost: "1000", averageLength:"2", averageTime:"2", MostVistedPage:"a"}));//here add the "database get playerinfo
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
	id = data.id;
	Instance.playerList[id] = new Player(id, data.name); 
  return String(id);
}



run()