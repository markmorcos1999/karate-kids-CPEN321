module.exports = class Session{



    constructor(id, newPlayers) {
		
		this.id = id;
		
		for(var pl in newPlayers){
			pl.sessionId = id;
		}
		this.game = null;
		this.players = newPlayers;
    }
	
	function addPlayers(newPlayers){
		for(var pl in newPlayers){
			pl.sessionId = id;
			this.players.push(pl)
		}
		
	}
	
	function startGame(){
		game = new Game(this);
	}
	
    
}