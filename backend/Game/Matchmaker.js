const ALLOWED_DIF_BASE = 50;
const DIFF_TIME_MULTIPLIER_EXPONENT = 0.002;
const MATCHING_INTERVAL = 500;
const MAX_WAIT_TIME = 7000;
const MAX_FRIEND_WAIT_TIME = 12000;

class MatchMaker {
	// ChatGPT usage: Partial
	constructor(manager) {
		this.waitingPlayers = [];
		this.matchingInProgress = false;
		this.friendList = {}
		this.gameManager = manager;
	}

	// Finds a match for a player with the given id and elo. Returns a promise that
	// resolves to the value of the id of the matched player and rejects if no match
	// could be found within the maximum wait time.
	// ChatGPT usage: Partial
	async findMatch(id, elo, waitStartTime = Date.now()) {
		var player = {
			id,
			elo,
			waitStartTime
		};

		player.matchPromise = new Promise(
			(resolve, reject) => { 
				player.matchPromiseResolve = resolve;
				player.matchPromiseReject = reject; 
			}
		);

		this.waitingPlayers.push(player);

		if (!this.matchingInProgress) {
			this.matchingInProgress = true;
			this.matchPlayers();
		}
		
		return player.matchPromise;
	}
	
	//Adds a friend to the list of friends waiting to be matched.
	//ChatGPT usage: no
	async friendWaiting(player) {
		
		var id = player.id;
		var friendId = player.friendId;
		
		this.friendList[id] = player;

		if (!this.matchingInProgress) {
			this.matchingInProgress = true;
			this.matchPlayers();
		}
		

		//Now the friends matching
		for(var i in this.friendList){
			var friend = this.friendList[i]
			if(friend.friendId == id && !this.friendList[i].done){
				var game = this.gameManager.startGame(id, friendId)
				this.friendList[id].matchPromiseResolve(game)
				this.friendList[i].matchPromiseResolve(game)
				this.friendList[id].done = true
				this.friendList[i].done = true
			} 
			
		}

		
	}



	// Attempts to match players on a set time interval until there are no players left
	// that need to be matched.
	// ChatGPT usage: Partial	
	async matchPlayers() {
		
		if (this.waitingPlayers.length == 0 && this.checkEmptyFriends()) {
			this.matchingInProgress = false;
			return
		}
		
		//matching for normal players
		if (this.waitingPlayers.length >= 2) {
			// Sort players in order of increasing elo
			this.waitingPlayers.sort((p1, p2) => p1.elo - p2.elo);

			for (var i = 1; i < this.waitingPlayers.length; i++) {
				const p1 = this.waitingPlayers[i];
				const p2 = this.waitingPlayers[i - 1];
				
				const diff = p1.elo - p2.elo;
				const allowedDiff = this.allowedEloDiff(p1.waitStartTime, p2.waitStartTime);
				
				
				if (diff <= allowedDiff) {
					var game = this.gameManager.startGame(p1.id, p2.id);

					p1.matchPromiseResolve(game);
					p2.matchPromiseResolve(game);

					this.waitingPlayers.splice(i - 1, 2);
					
				}
			}
		}
		
		//timeout for the normal players
		for (i = 0; i < this.waitingPlayers.length; i++) {
			const curPlayer = this.waitingPlayers[i];

			if (Date.now() - curPlayer.waitStartTime >= MAX_WAIT_TIME) {
				// If exceeded max wait time with no match, return null
				curPlayer.matchPromiseReject("604");
				this.waitingPlayers.splice(i, 1);
				i--;
			}
		}
		
	
		//Now the friendslist matching
		for(i in this.friendList){

			if(!this.friendList[i].done){
				this.matchingInProgress = true;
			}
			if(Date.now() - this.friendList[i].waitStartTime >= MAX_FRIEND_WAIT_TIME && !this.friendList[i].done){
				this.friendList[i].done = true
				
				this.friendList[i].matchPromiseReject("604")
			}
		}

		setTimeout(() => this.matchPlayers(), MATCHING_INTERVAL);
	}

	//Checks if the friends fucntion is empty.
	//ChatGPT useage: No
	checkEmptyFriends(){
		for(var i in this.friendList){
			if(!this.friendList[i].done){
				return false
			}
			
		}
		return true;
	}


	// Computes the maximum allowed elo diff between two waiting players. This increases exponentially
	// as a player's wait time increases.
	// ChatGPT usage: Partial
	allowedEloDiff(time1, time2) {
		const diff = Date.now() - Math.max(time1, time2);
		return ALLOWED_DIF_BASE + Math.pow(2, diff * DIFF_TIME_MULTIPLIER_EXPONENT);
	}
	

}

module.exports = MatchMaker;