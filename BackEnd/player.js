module.exports = class Player{

    constructor(id, username) {
		//Check if id exists in database
		//If no, then createNewDatabase entry
		//if Yes, get elo
		this.id = id;
		this.username = username;
		this.sessionId = 0;
		this.elo = 0;
		this.gamesWon = 0;
		this.gamesPlayed = 0;
    }
	
	

}