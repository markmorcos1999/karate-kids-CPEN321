module.exports = class Game{

//ChatGPT usage: No
    constructor(id, newPlayers, pages, _shortestPath, _manager) {
		this.manager = _manager
		this.id = Math.random();
		this.finishOrder = [];
		for(var i in newPlayers) {
			var pl = newPlayers[i]
			pl.sessionId = id;
			pl.pageList = [];
		}
		this.game = null;
		this.players = newPlayers;
		
		this.start = pages[0]
		this.end = pages[1]
		
		this.shortestPath = _shortestPath
		return this;
    }

	//ChatGPT usage: No
	addPlayers(newPlayers){
		for(var i in newPlayers){
			var pl = newPlayers[i]
			pl.sessionId = this.id;
			pl.pageList = {};
			this.players.push(pl)
		}
		
	}
	//ChatGPT usage: No
	playerToPage(id, page){
		for(var i in this.players){
			var pl = this.players[i]
			if(pl.id == id){
				pl.pageList.push(page);

				if (page == this.end) {
					return true;
				}
				//Consider: If player reaches end page, should it be done here?
				//Consider: Should server check for cheating here?
			}
		}

		return false;
	}
	//ChatGPT usage: No
	playerEndGame(id){
		if(this.finishOrder.length == 0){
			this.manager.sendLoss(this.players, id)
		}
		
		for(var i in this.players){
			var pl = this.players[i]
			if(pl.id == id){
				this.finishOrder.push(pl);
				if (this.finishOrder.length == this.players.length){
					this.gameOver();
				}

				const gameInfo = {
					gamePosition: this.finishOrder.length, 
					shortestPath: this.shortestPath
				};
				return gameInfo;
			}
		}
		
		return 0;
	}
	//ChatGPT usage: No
	gameOver(){
		this.manager.completeGame(this.finishOrder, this.id)
	}
	//ChatGPT usage: No
	getMessage(){
		const messageInfo = {
			startPage: this.start.url, 
			startTitle: this.start.title,
			endPage: this.end.url, 
			endTitle: this.end.title,
			players: this.players
		}
		return messageInfo;
	}
    
}