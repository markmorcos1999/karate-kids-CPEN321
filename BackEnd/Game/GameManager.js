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
	
	
	
	
    
}