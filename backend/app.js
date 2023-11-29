var https = require('https');
var fs = require('fs');

const express = require('express');

const bodyParser = require('body-parser');




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


//app.use(express.json());
app.use(bodyParser.json());

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
	catch (e){
    
		res.status(500);
		res.send();
	}
});

app.post('/game', async (req, res) => {
	try {
	  const message = req.body;

		const type = message.mode;
		const id = message.id;
		
		gameManager.checkForPlayer(id)

		//Here, we use "604" as a message code to say "try again later", letting the front end know
		//that no other player was found.
		if(type == "multi") {
			
			gameManager.playerFindGame(message.id).then(
			(resolve) => res.send(resolve.getMessage()),
			(reject) => {
				res.status(604)
				res.send()
				
			});
		}
		else if(type == "daily") {
			const game = await gameManager.startDaily(id);
			res.send(game.getMessage());
		}
		else if (type == "friend") {
			
			
			
			const friendId = message.friendId;
			
			gameManager.friendSearch(id, friendId).then(
			(resolve) => res.send(resolve.getMessage()),
			(reject) => {
				res.status(604)
				res.send()
				
			});

			//const game = await gameManager.friendSearch(id, friendId);
			//res.send(game.getMessage());
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

		gameManager.checkForPlayer(message.id)			
		if (gameManager.playerPagePost(message)) {
			//Later, here verify player actually wins
			//var result = gameManager.playerEndGame(message.id);
			res.send(200);
		}
		else {
			res.status(200);
			res.send();
		}
	}
	catch (e) {
		console.error(e);
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
	catch (e) {
		console.error(e);
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
	catch (e){
		console.error(e);
		res.status(500);
		res.send();
	}
});


app.post('/player/:playerId/friend/:friendId', async (req, res) => {
	try {
		const playerId = req.params.playerId;
		const friendId = req.params.friendId;

		if(!((await playerManager.playerExists(playerId)) && (await playerManager.playerExists(friendId)))){
			res.status(404);
			res.send();
			return;
		}

		const playerInfo = await playerManager.getPlayerInfo(playerId);
		
		let friends = playerInfo.friends;
		if (friends.includes(friendId)) {
			res.status(409)
			res.send();
			return;
		}
		friends.push(friendId);

		await playerManager.updatePlayer(
			playerId,
			playerInfo.elo,
			playerInfo.gamesWon,
			playerInfo.gamesLost,
			playerInfo.avgGameDuration,
			playerInfo.avgGamePathLength,
			friends
		);
	
		res.status(201);
		res.send();
	} 
	catch (e) {
		console.error(e);
		res.status(500);
		res.send();
	}
});

app.delete('/player/:playerId/friend/:friendId', async (req, res) => {
	try {
		const playerId = req.params.playerId;
		const friendId = req.params.friendId;

		if(!(await playerManager.playerExists(playerId) && await playerManager.playerExists(friendId))){
			res.status(404);
			res.send();
			return;
		}

		const playerInfo = await playerManager.getPlayerInfo(playerId);
		
		let friends = playerInfo.friends;
		const friendIndex = friends.indexOf(friendId);

		if (friendIndex < 0) {
			res.status(404)
			res.send();
			return;
		}
		delete friends[friendIndex];

		await playerManager.updatePlayer(
			playerId,
			playerInfo.elo,
			playerInfo.gamesWon,
			playerInfo.gamesLost,
			playerInfo.avgGameDuration,
			playerInfo.avgGamePathLength,
			friends
		);
	
		res.status(204);
		res.send();
	} 
	catch (e) {
		console.error(e);
		res.status(500);
		res.send();
	}
});

app.get('/player/:id/friend', async (req, res) => {
	try {
		const id = req.params.id;

		if(!(await playerManager.playerExists(id))) {
			res.status(404);
			res.send();
			return;
		}

		const friends = (await playerManager.getPlayerInfo(id)).friends;
		let friendInfo = await Promise.all(friends.map(async (friendId) => await playerManager.getPlayerInfo(friendId)));
	
		res.status(200);
		res.send(friendInfo);
	} 
	catch (e) {
		console.error(e);
		res.status(500);
		res.send();
	}
});


const server = https.createServer(options, app);

server.listen(port, () => {
	console.log('Server has started on port ' + port);
});

module.exports = app;