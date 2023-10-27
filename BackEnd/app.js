var express = require("express")
var http = require('http');
var https = require('https');

var fs = require('fs');

var LeaderBoardDB = require('./Player/PlayerManager.js')
var GameManager = require('./Game/GameManager.js');


const options = {
	key: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem', 'utf8'),
	cert: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem', 'utf8')
};

const leaderboardDB;
const gameManager;

async function run(){
	console.log("Running")
	try{//CHANGE THIS TO HTTPS after uncommenting the keys
 		leaderboardDB = new LeaderboardDB();
		gameManager = new GameManager(leaderboardDB);
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
						var id = message.data.id;
						var player;
						if(leaderboardDB.playerExists(id)){
							player = leaderboardDB.getPlayerInfo(id)
						}
						else{
							player = leaderboardDB.createNewPlayer(id, message.data.name)
						}
						
						gameManager.addPlayer(player, message.data.token)
					}
					else if(message.subject == "requestGame"){
						
						//Assumes "res" in this case is a game, and that game has a method "getMessage()" that returns a string in the right form
						gameManager.playerFindGame(message.data.id).then((res) => response.end(res.getMessage()))
						
						//response.end({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
					}
					else if(message.subject == "page"){
						gameManager.playerPagePost(message.data)
						response.end("200")
						
					}
					else if(message.subject == "endGame"){
						response.end(gameManager.playerEndGame(message.subject.id))
					}
					else if(message.subject == "leaderboard"){
					
						response.end(JSON.stringify(Instance.leaderboardDB.getPlayerData.id)));//Here add the "database get leaderboard"
					}
					else if(message.subject == "statsRequest"){
						
						response.end(JSON.stringify(Instance.leaderboardDB.getPlayerData.id));//here add the "database get playerinfo
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
	Instance.playerList[id] = new Player(id, data.name, data.deviceToken); 
	
  return String(id);
}



run()