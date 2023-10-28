const PageManager = require('./../Page/PageManager.js');

module.exports = class Game{


    constructor(id, newPlayers, pages, _manager) {
		this.manager = _manager
		this.id = Math.random();
		this.finishOrder = [];
		for(var i in newPlayers){
			var pl = newPlayers[i]
			pl.sessionId = id;
			pl.pageList = [];
		}
		this.game = null;
		this.players = newPlayers;
		
		
		
		this.start = pages[0]
		this.end = pages[1]
		return this;
    }
	
	getId(){
		return this.id;
    }
	
	addPlayers(newPlayers){
		for(var i in newPlayers){
			var pl = newPlayers[i]
			pl.sessionId = id;
			pl.pageList = {};
			this.players.push(pl)
		}
		
	}
	
	playerToPage(id, page){
		for(var i in this.players){
			var pl = this.players[i]
			if(pl.id == id){
				pl.pageList.push(page);
				//Consider: If player reaches end page, should it be done here?
				//Consider: Should server check for cheating here?
			}
		}
	}
	
	playerEndGame(id){
		if(this.finishOrder.length == 0){
			this.manager.sendLoss(this.players, id)
		}
		
		for(var i in this.players){
			var pl = this.players[i]
			if(pl.id == id){
				this.finishOrder.push(pl);
				if (this.finishOrder.length == this.players.length){
					this.gameOver()
				}
				return {gamePosition: this.finishOrder.length};
			}
		}
		return 0;
	}
	
	gameOver(){
		this.manager.completeGame(this.finishOrder, this.id)
	}
	
	getMessage(){
		
		return {startPage:this.start.url, startTitle:this.start.title,
		endPage:this.end.url, endTitle:this.end.title,
		players:this.players}
	}
    
}