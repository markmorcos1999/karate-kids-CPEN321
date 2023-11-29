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
		this.gaveUp = [];
		
		this.start = pages[0]
		this.end = pages[1]
		
		this.shortestPath = _shortestPath
		return this;
    }

	//ChatGPT usage: No
	playerToPage(id, page){
		
		for(var i in this.players){
			var pl = this.players[i]
			if(pl.id == id){
				pl.pageList.push(page);
				
				if (page == this.end.url) {
					
					
							
					if(this.finishOrder.length == 0){
						this.manager.sendLoss(this.players, id)
					}
					this.finishOrder.push(pl);

					return true;	
						
				}
					
				
			}
				//Consider: If player reaches end page, should it be done here?
				//Consider: Should server check for cheating here?
			
		}

		return false;
	}
	//ChatGPT usage: No
	playerEndGame(id){
		
		
		var gameInfo = {
			gamePosition: "NA",
			shortestPath: this.shortestPath,
			done: true
		}
		
		for(var i in this.finishOrder){
			if(this.finishOrder[i].id == id){
				gameInfo = {
					gamePosition: String(Number(i) + 1), 
					shortestPath: this.shortestPath,
					done: true
				};
			}
		}
		
		if(gameInfo.gamePosition == 'NA'){
			this.gaveUp.push(this.players[id])
		}

		if (this.finishOrder.length + this.gaveUp.length >= this.players.length){
			this.gameOver();
		}		
		
		return gameInfo;
		
		
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