const FCKNotifier = require('.././Notifications/NotificationManager.js');
const Game = require('./Game.js');
const Matchmaker = require('./Matchmaker.js');
const PageManager = require('./../Page/PageManager.js');

module.exports = class GameManager{
	
	//To get the leaderboard and firebase stuff, using a constructor
	constructor(_leaderboardDB) {
        this.leaderboardDB = _leaderboardDB //Is this a new one?
		this.firebaseNotifier = new FCKNotifier()
		this.playerList = {}
		this.sessionList = {}
		this.pageMan = new PageManager()
		this.matchmaker = new Matchmaker(this)
    }
	
	addPlayer(init, deviceToken){
		id = init._id;
		this.playerList[id] = new Player(init, deviceToken)
	}
	
	playerFindGame(id){
		return Matchmaker.findMatch(id, playerList[id].elo)
	}
	
	playerPagePost(data){
		sessionId = this.playerList[data.id].sessionId
		this.sessionList[sessionId].playerToPage(data.id, data.URL)
	}
	
	playerEndGame(id){
		sessionId = this.playerList[data.id].sessionId
		return this.sessionList[sessionId].playerEndGame(data.id)
	}
	
	completeGame(playerOrder, sessionId){
		//Insert real elo logic here
		playerOrder[0].elo += playerOrder.length
		playerOrder[0].gamesWon += 1;
		
		for(let i = 1; i < playerOrder.length; i++){
			playerOrder[i].elo += (playerOrder.length - i)
			
		}
		
		for(var i in playerOrder){
			pl = playerOrder[i]
			leaderboardDB.updatePlayer(pl.id, pl.elo, pl.gamesWon, pl.gamesLost, 0, 0)
		}
	}
	
	async startGame(p1Id, p2Id){
		sessionId = Math.random()
		
		players = []
		players.push(this.playerList[p1Id])
		players.push(this.playerList[p2Id])
		
		pageList = await pageMan.getRandomPages()
		
		//Check if there isnt a path
		
		game = new Game(sessionId, players, pageList, this)
		this.sessionList[sessionId] = game
		
		return game 
		
	}
	
	sendLoss(players, winner){
		for(var pl in players){
			if(pl.id != winner){
				firebaseNotifier.sendNotificationToDevice(pl.token, "loss", "You lost!").then((success) => console.log("successful: " + success));
			}
			
		}
	}
    
}