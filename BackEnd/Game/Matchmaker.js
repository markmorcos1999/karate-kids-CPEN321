const DIFF_TIME_MULTIPLIER_EXPONENT = 0.002;
const ALLOWED_DIF_BASE = 50;
const MAX_WAIT_TIME = 10000;

class MatchMaker{
    static waitingPlayers = [];
	static matchingInProgress = false;

	static gameManager;
	
	static setGameManager(manager){
		gameManger = manager;
	}

	// Finds a match for a player with the given id and elo. Returns a promise that
	// resolves to the value of the id of the matched player and rejects if no match
	// could be found within the maximum wait time.
	static async findMatch(playerId, playerElo, waitStartTime = Date.now()) {
		var player = {
			id: playerId,
			elo: playerElo,
			waitStartTime: waitStartTime
		};

		player.matchPromise = new Promise(
			(res, rej) => { 
				player.matchPromiseResolve = res;
				player.matchPromiseReject = rej 
			}
		);

		this.waitingPlayers.push(player);

		if (!this.matchingInProgress) {
			this.matchingInProgress = true;
			this.matchPlayers();
		}

		return player.matchPromise;
	}

	static matchPlayers() {
		if (this.waitingPlayers.length == 0) {
			this.matchingInProgress = false;
			return;
		}

		if (this.waitingPlayers.length >= 2) {
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
					
					if (i-- > 1) {
						i--;
					}
				}
			}
		}
		
		for (var i = 0; i < this.waitingPlayers.length; i++) {
			const curPlayer = this.waitingPlayers[i];

			if (Date.now() - curPlayer.waitStartTime >= MAX_WAIT_TIME) {
				// If exceeded max wait time with no match, return null
				curPlayer.matchPromiseReject();
				this.waitingPlayers.splice(i, 1);
				i--;
			}
		}

		setTimeout(() => this.matchPlayers(), 500);
	}

	static allowedEloDiff(time1, time2) {
		const diff = Date.now() - Math.max(time1, time2);
		return ALLOWED_DIF_BASE + Math.pow(2, diff * DIFF_TIME_MULTIPLIER_EXPONENT);
	}
}

module.exports = MatchMaker;