const FCKNotifier = require('.././Notifications/NotificationManager.js');
const Game = require('./Game.js');
const Matchmaker = require('./Matchmaker.js');
const PageManager = require('./../Page/PageManager.js');
const Player = require('./Player.js');

module.exports = class GameManager{
	
	//To get the leaderboard and firebase stuff, using a constructor
	constructor(_leaderboardDB) {
        this.leaderboardDB = _leaderboardDB 
		this.firebaseNotifier = new FCKNotifier()
		this.playerList = {}
		this.friendList = {}
		this.sessionList = {}
		this.pageMan = new PageManager()
		this.matchmaker = new Matchmaker(this)
    }
	
	addPlayer(init, deviceToken){
		var id = init._id;
		//console.log(id)
		this.playerList[id] = new Player(init, deviceToken)
	}
	
	playerFindGame(id){
		console.log(id)
		
		return this.matchmaker.findMatch(id, this.playerList[id].elo)

	}
	
	playerPagePost(data){
		var sessionId = this.playerList[data.id].sessionId
		this.sessionList[sessionId].playerToPage(data.id, data.URL)
	}
	
	playerEndGame(id){
		var sessionId = this.playerList[id].sessionId
		return this.sessionList[sessionId].playerEndGame(id)
	}
	
	completeGame(playerOrder, sessionId){
		//Insert real elo logic here
		playerOrder[0].elo += playerOrder.length
		playerOrder[0].gamesWon += 1;
		
		for(let i = 1; i < playerOrder.length; i++){
			playerOrder[i].elo += (playerOrder.length - i)
			playerOrder[i].gamesLost += 1
			
		}
		
		for(var i in playerOrder){
			var pl = playerOrder[i]
			this.leaderboardDB.updatePlayer(pl.id, pl.elo, pl.gamesWon, pl.gamesLost, 0, 0)
		}
	}
	
	async startGame(p1Id, p2Id){
		var sessionId = Math.random()
		
		var players = []
		players.push(this.playerList[p1Id])
		players.push(this.playerList[p2Id])
		
		var pageList = await this.pageMan.getRandomPages()
		var path = this.pageMan.getshortestPath(pageList[0].title, pageList[1].title)
		
		//Check if there isnt a path
		console.log("Making new game!")
		var game = new Game(sessionId, players, pageList, path, this)
		//console.log(game)
		this.sessionList[sessionId] = game
		
		return game 
		
	}
	
	async startDaily(id){
		var sessionId = Math.random()
		
		var players = []
		players.push(this.playerList[id])
		var pageList = this.pageMan.getDailyPage()
		var path = this.pageMan.getshortestPath(pageList[0].title, pageList[1].title)
		
		var game = new Game(sessionId, players, pageList, path, this)
		
		
		this.sessionList[sessionId] = game
		
		return game 
	}
	
	async startSingle(id){
		var sessionId = Math.random()
		
		var players = []
		players.push(this.playerList[id])
		var pageList = await this.pageMan.getRandomPages()
		var path = this.pageMan.getShortestPath(pageList[0].title, pageList[1].title)
		var game = new Game(sessionId, players, pageList, path, this)
		
		
		this.sessionList[sessionId] = game
		
		return game 
	}
	
	//Some code taken from Matchmaker.js
	async friendSearch(id, friendId){
		
		var friend = {
			id: id,
			friendId: friendId,
			done: false
		};

		friend.matchPromise = new Promise(
			(res, rej) => { 
				friend.matchPromiseResolve = res;
				friend.matchPromiseReject = rej 
			}
		);
		
		for(var i in this.friendList){
			console.log(i)
			console.log(this.friendList[i].friendId)
			console.log(id)
			if(this.friendList[i].friendId == id && !this.friendList[i].done){
				console.log("PAIR FOUND")
				var game = this.startGame(id, friendId)
				friend.matchPromiseResolve(game)
				this.friendList[i].matchPromiseResolve(game)
				this.friendList[i].done = true
				friend.done = true
				
			}
		}
		
		this.friendList[id] = friend
		
		return friend.matchPromise
	}
	
	sendLoss(players, winner){
		for(var i in players){
			var pl = players[i]
			if(pl.id != winner){
				console.log("Token: " + pl.deviceToken)
				console.log(JSON.stringify(pl))
				this.firebaseNotifier.sendNotificationToDevice(pl.deviceToken, "loss", "You lost!").then((success) => console.log("successful: " + success));
			}
			
		}
	}
    
}