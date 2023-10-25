module.exports = class MatchMaker{

	//@TODO are these instances the same? If not, how do we fix this?
	var Instance = require('./instance.js');
	var Session = require('./session.js');

    static playersLooking = {};

	//We should keep a list of players sorted by elo
	//Then pair up close entries in the list.
	//For now we just pair up any players

	static matchPlayers(){
		if(playersLooking.length > 1){
			newSesh = {};
			for(var i in playersLooking){
				newSesh.push(playersLooking[i]);
				if(newSesh.length > 2){
					id = Math.random();
					session = new Session(id, newSesh);
					Instance.sessionList[id] = session;
					
				}
			}
		}
	}
	
	static lookForGame(player){
		playersLooking[]
		
	}
    
}