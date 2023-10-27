module.exports = class Game{


    constructor(newPlayers) {
		
		this.id = Math.random();
		this.finishOrder = 1;
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
	
	endGame(id){
		for(var pl in this.players){
			if(pl.id == id){
				finishOrder += 1;
				return finishOrder - 1;
			}
		}
		return 0;
	}
    
}