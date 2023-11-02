const { MongoClient } = require('mongodb');

class PlayerManager {
    // ChatGPT usage: No
    constructor() {
        this.client = new MongoClient('mongodb://localhost:27017');
        this.collection = null;
    }

    // ChatGPT usage: Partial
    async connect() {
        try {
            await this.client.connect();
            this.collection = this.client.db('user').collection('leaderboard');
            console.log("Successfully connected to the leaderboard database");
        } catch (err) {
            console.error(err);
            await this.client.close();
        }
    }

    // ChatGPT usage: Yes
    async playerExists(id) {
        try {
            const player = await this.collection.findOne({ _id: id });
            return player !== null;
        } catch (err) {
            console.error(err);
        }
    }

    // ChatGPT usage: Partial
    async getTopPlayers(limit = 10) {
        try {
            const topPlayers = await this.collection
                .find()
                .sort({ elo: -1 }) // Assuming you have an 'elo' field in your documents
                .limit(limit)
                .toArray();

            return topPlayers;
        } catch (err) {
            console.error(err);
            return [];
        }
    }

    //ChatGPT usage: Partial
    async getPlayerInfo(id) {
        try {
			var x = await this.collection.findOne({ 
                _id: id,
            });
            return x
        } catch (err) {
            console.error(err);
            return null;
        }
    }

    // ChatGPT usage: Partial
    async createNewPlayer(id, username) {
        try {
            await this.collection.insertOne({
                _id: id,
                name: username,
                elo: 0,
                gamesWon: 0,
                gamesLost: 0,
                avgGameDuration: null,
                avgGamePathLength: null
            });
        } catch (err) {
            console.error(err);
        }
    }

    // ChatGPT usage: Partial
    async updatePlayer(id, elo, gamesWon, gamesLost, avgGameDuration, avgGamePathLength) {
        try {
            const result = await this.collection.updateOne(
                { _id: id },
                { 
                    $set: {
                        elo,
                        gamesWon,
                        gamesLost,
                        avgGameDuration,
                        avgGamePathLength
                    }
                } 
            );

            return result.modifiedCount === 1;
        } catch (err) {
            console.error(err);
            return false;
        }
    }
}

module.exports = PlayerManager;
