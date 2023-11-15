var https = require('https');
var fs = require('fs');

const express = require('express');

var PlayerManager = require('./Player/PlayerManager.js')
var GameManager = require('./Game/GameManager.js');

const options = {
	key: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem', 'utf8'),
	cert: fs.readFileSync('/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem', 'utf8')
};

var playerManager = new PlayerManager();
playerManager.connect();
var gameManager = new GameManager(playerManager);

var app = express();
var port = 8081;

app.use(express.json());

app.post('/signIn', async (req, res) => {
	const id = req.body.id;
	var player;

	if(await playerManager.playerExists(id)) {
		player = await playerManager.getPlayerInfo(id);
		console.log("Exists");
						
	}
	else {
		playerManager.createNewPlayer(id, message.name);
		player = {
			_id: id, 
			name: message.name, 
			elo: 0, 
			gamesWon: 0, 
			gamesLost: 0
		};
		console.log("New");
	}
					
	gameManager.addPlayer(player, message.token)
	res.send({ id })
});

app.post('/game', async (req, res) => {
	var message = JSON.parse(req.body);

	const type = message.type;
	const id = message.id;
					
	//Assumes "res" in this case is a game, and that game has a method "getMessage()" that returns a string in the right form
	//gameManager.playerFindGame(message.data.id).then((res) => res.send(JSON.stringify(res.getMessage())))
	if(!gameManager.checkForPlayer(id)) {
		res.statusCode(400);
		res.send();
	}
	if(type == "multi") {
		gameManager.playerFindGame(id).then((res) => res.send(res.getMessage()));
	}
	else if(type == "daily") {
		const game = await gameManager.startDaily(id);
		console.log(game);
		console.log(JSON.stringify(game.getMessage()));
		res.send(game.getMessage());
	}
	else if (type == "friend") {
		const friendId = message.friendId;
		const game = await gameManager.friendSearch(id, friendId);
		res.send(game.getMessage());
	}
	else {
		const game = await gameManager.startSingle(id);
		console.log(game);
		res.send(game.getMessage());
	}
	//res.send({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
});

app.put('/game', async (req, res) => {
	
	const message = JSON.parse(req.body);

	if(!gameManager.checkForPlayer(message.id)){
		res.statusCode(400);
		res.send();
		
	}
					
	if (gameManager.playerPagePost(message)) {
		
		//In this case we need to send back player position, shortest path, ect
		
		//var result = gameManager.playerEndGame(message.id);
		console.log(result);
		res.send(result);
	}
	else {
		res.statusCode(200);
		//In this case we only send back a status code`
		res.end();
	}
});

app.get('/game', async(req,res) =>{
	
	if(!gameManager.checkForPlayer(message.id)){
		res.statusCode(400);
		res.send();
	
	}
	
	const message = JSON.parse(req.body);
	var result = gameManager.playerEndGame(message.id);
	res.send(result);
	
}

app.get('/leaderboard', async (req, res) => {
	res.send(await playerManager.getTopPlayers());//Here add the "database get leaderboard"
});

app.get('/player', async (req, res) => {
	const id = req.body.id;

	if(!gameManager.checkForPlayer(id)){
		res.statusCode(400);
		res.send();
	}

	res.send(await playerManager.getPlayerInfo(id));//here add the "database get playerinfo
});

app.get('/',(req, res) =>{
	res.end("Node server active")
});


const server = https.createServer(options, app);

server.listen(port, () => {
	console.log('Server has started')
});

module.exports = app;