const FCKNotifier = require('.././Notifications/NotificationManager.js');
const serviceAccount = require('./service-account-key.json');
const Game = require('./Game.js');
const Matchmaker = require('./Matchmaker.js');

module.exports = class GameManager{

//Import those classes here maybe?
    static playerList = {};
	static sessionList = {};
	static leaderboardDB;
	static firebaseNotifier;
	static matchmaker;
	
	//To get the leaderboard and firebase stuff, using a constructor
	constructor(_leaderboardDB) {
        leaderboardDB = _leaderboardDB //Is this a new one?
		firebaseNotifier = new FCMNotifier()
		matchmaker = new Matchmaker();
    }
	
	addPlayer(init, deviceToken){
		id = init._id;
		playerList[id] = new Player(init, deviceToken)
	}
	
	findGame(id){
		return matchmaker.findMatch(id, playerList[id].elo)
	}
	
	pagePost(data){
		sessionId = playerList[data.id].sessionId
		sessionList[sessionId].playerToPage(data.id, data.URL)
	}
	
	endGame(id){
		sessionId = playerList[data.id].sessionId
		return sessionList[sessionId].endGame(data.id)
	}
    
}