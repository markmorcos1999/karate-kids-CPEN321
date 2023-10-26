const DIFF_TIME_MULTIPLIER_EXPONENT = 0.002;

class MatchMaker{
    static waitingPlayers = [];

	// Matches two players. Players should be matched by similar elo, with greater elo
	// differences being allowed the longer a player has been waiting without another 
	// player of similar elo.
	static matchPlayers() {
		// Sort players in order of increasing elo
		this.waitingPlayers.sort((p1, p2) => p1.elo - p2.elo);

		for (var i = 1; i < this.waitingPlayers.length; i++) {
			const p1 = this.waitingPlayers[i];
			const p2 = this.waitingPlayers[i - 1];

			const diff = p1.elo - p2.elo;
			const allowedDiff = this.allowedEloDiff(p1.waitStartTime, p2.waitStartTime);
			
			if (diff <= allowedDiff) {
				p1.matchPromiseResolve(p2.id);
				p2.matchPromiseResolve(p1.id);

				this.waitingPlayers.splice(i - 1, 2);
				i++; // We need to ensure that the current player is no longer considered because they have been matched up
			}
		}
	}
	
	// Adds a player to the list of players waiting for a match.
	static addToGameWaitingList(playerId, playerElo, waitStartTime = Date.now()) {
		var player = {
			id: playerId,
			elo: playerElo,
			waitStartTime: waitStartTime,
			matchPromise: null
		};

		player.matchPromise = new Promise((res) => { player.matchPromiseResolve = res })

		this.waitingPlayers.push(player);

		return player.matchPromise;
	}

	static allowedEloDiff(time1, time2) {
		const diff = Date.now() - Math.max(time1, time2);
		return Math.pow(2, diff * DIFF_TIME_MULTIPLIER_EXPONENT);
	}
}

module.exports = MatchMaker;