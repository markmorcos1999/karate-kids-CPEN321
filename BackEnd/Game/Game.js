module.exports = class Game{


    constructor(newPlayers, _manager) {
		this.manager = _manager
		this.id = Math.random();
		this.finishOrder = [];
		for(var pl in newPlayers){
			pl.sessionId = id;
			pl.pageList = {};
		}
		this.game = null;
		this.players = newPlayers;
    }
	
	getId()[
		return this.id;
    }
	
	addPlayers(newPlayers){
		for(var pl in newPlayers){
			pl.sessionId = id;
			pl.pageList = {};
			this.players.push(pl)
		}
		
	}
	
	playerToPage(id, page){
		for(var pl in this.players){
			if(pl.id == id){
				pl.pageList.push(page);
				//Consider: If player reaches end page, should it be done here?
				//Consider: Should server check for cheating here?
			}
		}
	}
	
	playerEndGame(id){
		for(var pl in this.players){
			if(pl.id == id){
				this.finishOrder.push(pl);
				if (this.finishOrder.length == this.players.length){
					gameOver()
				}
				return this.finishOrder.length;
			}
		}
		return 0;
	}
	
	gameOver(){
		manager.completeGame(this.finishOrder, this.id)
	}
    
}