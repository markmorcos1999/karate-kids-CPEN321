module.exports = class Player{

    constructor(init, deviceToken) {
		//Check if id exists in database
		//If no, then createNewDatabase entry
		//if Yes, get elo
		this.id = init._id;
		this.username = init.name;
		this.sessionId = 0;
		this.deviceToken = deviceToken;
		this.pageList = {};
    }
	
	

}