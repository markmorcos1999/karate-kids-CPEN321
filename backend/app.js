var https = require('https');
var fs = require('fs');

var PlayerManager = require('./Player/PlayerManager.js')
var GameManager = require('./Game/GameManager.js');

const options = {
	key: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem', 'utf8'),
	cert: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem', 'utf8')
};

var playerManager;
var gameManager;

//ChatGPT usage: No
async function run() {
	console.log("Running")
	try {
		playerManager = new PlayerManager();
		playerManager.connect();
		gameManager = new GameManager(playerManager);
		https.createServer(options, handleRequest).listen(8081)
	}
	catch(err) {
		console.log(err)
	}
}

//ChatGPT usage: No
//Code for making post requests from stackoverflow: https://stackoverflow.com/questions/12006417/node-js-server-that-accepts-post-requests			
function handleRequest(request, response){
	if(request.method == 'POST'){
				console.log('POST')
				var body = ''
				request.on('data', function(data) {
					body += data
					console.log('Partial body: ' + body)
				})
				request.on('end', async function() {
					
					var message = JSON.parse(body)
					console.log('Body: ' + message)
					response.writeHead(200, {'Content-Type': 'text/html'})

					if(message.subject == "signIn"){
						var id = message.data.id;
						var player;
						if(await playerManager.playerExists(id)){
							player = await playerManager.getPlayerInfo(id)
							console.log("Exists")
							
						}
						else{
							playerManager.createNewPlayer(id, message.data.name)
							player = {_id:id, name:message.data.name, elo:0, gamesWon:0, gamesLost:0}
							console.log("New")
						}
						
						gameManager.addPlayer(player, message.data.token)
						response.end(id)
					}
					else if(message.subject == "requestGame"){
						var mode = message.data.mode
						id = message.data.id
						
						
						//Assumes "res" in this case is a game, and that game has a method "getMessage()" that returns a string in the right form
						//gameManager.playerFindGame(message.data.id).then((res) => response.end(JSON.stringify(res.getMessage())))
						if(!gameManager.checkForPlayer(id)){
							response.end("400")
						}
						if(mode == "multi"){
							gameManager.playerFindGame(message.data.id).then((res) => response.end(JSON.stringify(res.getMessage())))
						}
						else if(mode == "daily"){
							const game = await gameManager.startDaily(id)
							console.log(game)
							console.log(JSON.stringify(game.getMessage()))
							response.end(JSON.stringify(game.getMessage()));
						}
						else{
							const game = await gameManager.startSingle(id)
							console.log(game)
							response.end(JSON.stringify(game.getMessage()));
						}
						//response.end({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
					}
					else if(message.subject == "friendGame"){
						
						if(!gameManager.checkForPlayer(message.data.id)){
							response.end("400")
						}
						
						const game = await gameManager.friendSearch(message.data.id, message.data.friendId)
						response.end(JSON.stringify(game.getMessage()));
					}
					else if(message.subject == "page"){
						
						if(!gameManager.checkForPlayer(message.data.id)){
							response.end("400")
						}
						
						gameManager.playerPagePost(message.data)
						response.end("200")
						
					}
					else if(message.subject == "endGame"){
						
						if(!gameManager.checkForPlayer(message.data.id)){
							response.end("400")
						}
						
						var result = gameManager.playerEndGame(message.data.id)
						console.log(result)
						response.end(JSON.stringify(result))
					}
					else if(message.subject == "leaderboard"){
					
						response.end(JSON.stringify(await playerManager.getTopPlayers()));//Here add the "database get leaderboard"
					}
					else if(message.subject == "statsRequest"){
						
						response.end(JSON.stringify(await playerManager.getPlayerInfo(message.data.id)));//here add the "database get playerinfo
					}
					else{
						response.end("unknown subject")
					}
				})
			}
			else{
				response.writeHead(200);
				response.end("Node.js server is currently online.");
			}	
	
}

run()