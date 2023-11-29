const FCKNotifier = require('.././Notifications/NotificationManager.js');
const Game = require('./Game.js');
const Matchmaker = require('./Matchmaker.js');
const PageManager = require('./../Page/PageManager.js');
const Player = require('./Player.js');

module.exports = class GameManager{
	//ChatGPT usage: No
	//To get the leaderboard and firebase stuff, using a constructor
	constructor(_playerManager) {
        this.playerManager = _playerManager 
		this.firebaseNotifier = new FCKNotifier()
		this.playerList = {}
		this.sessionList = {}
		this.pageMan = new PageManager()
		this.matchmaker = new Matchmaker(this)
    }
	//ChatGPT usage: No
	addPlayer(init, deviceToken){
		
		var id = init._id;
		this.playerList[id] = new Player(init, deviceToken);
	}
	//ChatGPT usage: No
	//Because there is no gracefull way for the frontend to recover when it has lost its ID
	//We are instead going to allow the player to continue without their id.
	checkForPlayer(id){
		
		if(this.playerList[id]){
		
			return true

		}
		else{
			var newPlayer = {
				_id: id, 
				name: "Guest", 
				elo: 0, 
				gamesWon: 0, 
				gamesLost: 0
			};
			
			this.playerList[id] = new Player(newPlayer, "")
			
			return true
		}
	}
	//ChatGPT usage: No
	playerFindGame(id){

		return this.matchmaker.findMatch(id, this.playerList[id].elo);
	}
	//ChatGPT usage: No
	playerPagePost(data){
		var sessionId = this.playerList[data.id].sessionId;
		return this.sessionList[sessionId].playerToPage(data.id, data.URL);
	}
	//ChatGPT usage: No
	playerEndGame(id){
		var sessionId = this.playerList[id].sessionId
		return this.sessionList[sessionId].playerEndGame(id)
	}
	//ChatGPT usage: No
	completeGame(playerOrder, gaveUp, sessionId){
		//Insert real elo logic here
		playerOrder[0].elo += playerOrder.length
		playerOrder[0].gamesWon += 1;
		
		for(let i = 1; i < playerOrder.length; i++){
			playerOrder[i].elo += (playerOrder.length - i)
			playerOrder[i].gamesLost += 1
			
		}
		
		for(var i in playerOrder){
			var pl = playerOrder[i]
			this.playerManager.updatePlayer(pl.id, pl.elo, pl.gamesWon, pl.gamesLost, 0, 0)
		}
	}
	//ChatGPT usage: No
	async startGame(p1Id, p2Id){
		var sessionId = Math.random();
		
		var players = [];
		players.push(this.playerList[p1Id]);
		players.push(this.playerList[p2Id]);
		

		var pageList = this.pageMan.getRandomPages();
		var path = await this.pageMan.getShortestPath(pageList[0].title, pageList[1].title);		
		//Check if there isnt a path
		
		var game = new Game(sessionId, players, pageList, path, this);
		this.sessionList[sessionId] = game;
		
		return game ;
	}
	//ChatGPT usage: No
	async startDaily(id){
		var sessionId = Math.random()
		
		var players = []
		players.push(this.playerList[id])
		var pageList = this.pageMan.getDailyPage()
		var path = await this.pageMan.getShortestPath(pageList[0].title, pageList[1].title)
		

		var game = new Game(sessionId, players, pageList, path, this)
		
		this.sessionList[sessionId] = game
		
		return game 
	}
	//ChatGPT usage: No
	async startSingle(id){
		var sessionId = Math.random()
		
		var players = []
		players.push(this.playerList[id])
		
		var pageList = this.pageMan.getRandomPages()
		var path = await this.pageMan.getShortestPath(pageList[0].title, pageList[1].title)
		var game = new Game(sessionId, players, pageList, path, this)
		
		this.sessionList[sessionId] = game
		
		return game 
	}
	//ChatGPT usage: No
	//Some code taken from Matchmaker.js
	async friendSearch(id, friendId, waitStartTime = Date.now()){
		
		var player = {
			id,
			friendId,
			done: false,
			waitStartTime: waitStartTime
		};

		player.matchPromise = new Promise(
			(resolve, reject) => { 
				player.matchPromiseResolve = resolve;
				player.matchPromiseReject = reject;
			}
		)
		
		this.matchmaker.friendWaiting(player)
		
		return player.matchPromise
	}
	//ChatGPT usage: No
	sendLoss(players, winner){
		for(var i in players){
			var pl = players[i]
			if(pl.id != winner){
				this.firebaseNotifier.sendNotificationToDevice(pl.deviceToken, "loss", "You lost!")
			}
		}
	}
    
}