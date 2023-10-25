const { MongoClient } = require('mongodb');

class LeaderboardDB {
    constructor() {
        this.client = new MongoClient('mongodb://localhost:27017');
        this.collection = null;
    }

    async connect() {
        try {
            await this.client.connect();
            this.collection = this.client.db('user').collection('leaderboard');
            console.log("Successfully connected to the database");
        } catch (err) {
            console.error(err);
            await this.client.close();
        }
    }

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

    async getPlayerData(id) {
        try {
            return await this.collection.findOne({ 
                _id: id 
            });
        } catch (err) {
            console.error(err);
            return null;
        }
    }

    async createNewPlayer() {
        try {
            return await this.collection.insertOne({
                elo: 0
            });
        } catch (err) {
            console.error(err);
            return null;
        }
    }

    async updatePlayerElo(id, additionalElo) {
        try {
            const result = await this.collection.updateOne(
                { _id: id },
                { $inc: { elo: additionalElo } } // Increment elo by the specified amount
            );

            return result.modifiedCount === 1;
        } catch (err) {
            console.error(err);
            return false;
        }
    }
}

module.exports = LeaderboardDB;
