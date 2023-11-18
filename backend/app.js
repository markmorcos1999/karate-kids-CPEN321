var https = require('https');
var fs = require('fs');

const express = require('express');

var PlayerManager = require('./Player/PlayerManager.js');
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

app.post('/signIn/:id', async (req, res) => {
	const id = req.params.id;
	var player;

	if(await playerManager.playerExists(id)) {
		player = await playerManager.getPlayerInfo(id);			
	}
	else {
		playerManager.createNewPlayer(id, message.data.name);
		player = {
			_id: id, 
			name: message.data.name, 
			elo: 0, 
			gamesWon: 0, 
			gamesLost: 0
		};
		
	}
					
	gameManager.addPlayer(player, message.data.token)
	res.send({ id })
});

app.post('/game', async (req, res) => {
	var message = JSON.parse(req.body);

	const type = message.data.type;
	const id = message.data.id;
					
	//Assumes "res" in this case is a game, and that game has a method "getMessage()" that returns a string in the right form
	//gameManager.playerFindGame(message.data.id).then((res) => res.send(JSON.stringify(res.getMessage())))
	if(!gameManager.checkForPlayer(id)) {
		res.status(400);
		res.send();
		return;
	}

	if(type == "multi") {
		gameManager.playerFindGame(message.data.id).then((res) => res.send(res.getMessage()));
	}
	else if(type == "daily") {
		const game = await gameManager.startDaily(id);
		res.send(game.getMessage());
	}
	else if (type == "friend") {
		const friendId = message.data.friendId;
		const game = await gameManager.friendSearch(id, friendId);
		res.send(game.getMessage());
	}
	else {
		const game = await gameManager.startSingle(id);
		res.send(game.getMessage());
	}
	//res.send({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
});

app.put('/game', async (req, res) => {
	const message = JSON.parse(req.body);

	if(!gameManager.checkForPlayer(message.data.id)){
		res.status(400);
		res.send();
		return;
	}
					
	if (gameManager.playerPagePost(message.data)) {
		var result = gameManager.playerEndGame(message.data.id);
		res.send(result);
	}
	else {
		res.status(200);
		res.send();
	}
});

app.get('/leaderboard', async (req, res) => {
	res.send(await playerManager.getTopPlayers()); //Here add the "database get leaderboard"
});

app.get('/player/:id', async (req, res) => {
	const id = parseInt(req.params.id);

	if(!(await playerManager.playerExists(id))){
		res.status(404);
		res.send();
		return;
	}

	res.send(await playerManager.getPlayerInfo(id)); //here add the "database get playerinfo
});


const server = https.createServer(options, app);

server.listen(port, () => {
	console.log('Server has started')
});

module.exports = app;