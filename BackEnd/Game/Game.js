module.exports = class Game{


    constructor(newPlayers) {
		
		this.id = Math.random();
		this.finishOrder = 0;
		for(var pl in newPlayers){
			pl.sessionId = id;
			pl.pageList = {};
		}
		this.game = null;
		this.players = newPlayers;
    }
	
	getID()[
		return this.id;
    }
	
	addPlayers(newPlayers){
		for(var pl in newPlayers){
			pl.sessionId = id;
			pl.pageList = {};
			this.players.push(pl)
		}
		
	}
	
	playerToPage(page, id){
		for(var pl in this.players){
			if(pl.id = id){
				pl.pageList.push(page);
				//Check if win game
			}
		}
	}
    
}