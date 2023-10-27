const FCKNotifier = require('.././Notifications/NotificationManager.js');
const serviceAccount = require('./service-account-key.json');
const Game = require('./Game.js');
const Matchmaker = require('./Matchmaker.js');

module.exports = class GameManager{
	
	//To get the leaderboard and firebase stuff, using a constructor
	constructor(_leaderboardDB) {
        this.leaderboardDB = _leaderboardDB //Is this a new one?
		this.firebaseNotifier = new FCMNotifier()
		this.playerList = {}
		this.sessionList = {}
		Matchmaker.setGameManager(this)
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
		
		for(int i = 1; i < playerOrder.length, i++){
			playerOrder[i].elo += (playerOrder.length - i)
			
		}
		
		for(var i in playerOrder){
			pl = playerOrder[i]
			leaderboardDB.updatePlayer(pl.id, pl.elo, pl.gamesWon, pl.gamesLost, 0, 0)
		}
	}
    
}