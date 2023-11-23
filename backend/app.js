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
	

	
	try {
		
		const id = req.params.id;
		var player;

		const message = req.body;
		
		if(await playerManager.playerExists(id)) {
			player = await playerManager.getPlayerInfo(id);			
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
			
		}
						
		gameManager.addPlayer(player, message.token)
		res.status(200);
		res.send({_id: id})
	}
	catch {
		res.status(500);
		res.send();
	}
});

app.post('/game', async (req, res) => {
	try {
		const message = req.body;

		const type = message.mode;
		const id = message.id;
		
		if(!gameManager.checkForPlayer(id)) {
			res.status(400);
			res.send();
			return;
		}
		
		//Here, we use "604" as a message code to say "try again later", letting the front end know
		//that no other player was found.
		if(type == "multi") {
			console.log("MultiGame with:")
			console.log(id) //error here, I believe @TODO
			gameManager.playerFindGame(message.id).then(
			(resolve) => res.send(resolve.getMessage()), 
			(reject) => res.send("604"));
		}
		else if(type == "daily") {
			const game = await gameManager.startDaily(id);
			res.send(game.getMessage());
		}
		else if (type == "friend") {
			const friendId = message.friendId;
			gameManager.friendSearch(id, friendId).then(
			(resolve) => res.send(resolve.getMessage()),
			(reject) => res.send("604")
			)
			const game = await gameManager.friendSearch(id, friendId);
			res.send(game.getMessage());
		}
		else {
			const game = await gameManager.startSingle(id);
			res.send(game.getMessage());
		}
	}
	catch (e){
		console.error(e);
		res.status(500);
		res.send();
	}
	
	//res.send({startPage:"https://en.m.wikipedia.org/wiki/Taco", endPage: "https://en.m.wikipedia.org/wiki/Mexico", players: [{name:"Mark", ELO: "1001"}, {name:"Kyle", ELO: "1001"}]})
});

app.put('/game', async (req, res) => {
	try {
		const message = req.body;

		if(!gameManager.checkForPlayer(message.id)){
			res.status(400);
			res.send();
			return;
		}
						
		if (gameManager.playerPagePost(message)) {
			var result = gameManager.playerEndGame(message.id);
			res.send(result);
		}
		else {
			res.status(200);
			res.send();
		}
	}
	catch {
		res.status(500);
		res.send();
		
	}
});

app.get('/game/:id', async(req,res) =>{
	
	const id = req.params.id;
	
	gameManager.checkForPlayer(id);
	
	var result = gameManager.playerEndGame(id);
	res.send(result);
	
});

app.get('/leaderboard', async (req, res) => {
	try {
		res.send(await playerManager.getTopPlayers()); //Here add the "database get leaderboard"
	}
	catch {
		res.status(500);
		res.send();
	}
});

app.get('/player/:id', async (req, res) => {
	try {
		const id = req.params.id;
		
		if(!(await playerManager.playerExists(id))){
			res.status(404);
			res.send();
			return;
		}
	
		res.send(await playerManager.getPlayerInfo(id)); //here add the "database get playerinfo
	}
	catch {
		res.status(500);
		res.send();
	}
});


const server = https.createServer(options, app);

server.listen(port, () => {
	console.log('Server has started on port ' + port);
});

module.exports = app;