const request = require('supertest');
const https = require('https');
const fs = require('fs');
const { MongoClient } = require('mongodb');

jest.mock('https');
https.createServer.mockReturnValue({ 
    listen: jest.fn((port, callback) => {
        callback();
    }) 
});

originalReadFileSync = fs.readFileSync;
fs.readFileSync = jest.fn((filePath, encoding) => {
    if (filePath === '/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem'
        || filePath === '/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem') {
        return '';
    }
    
    return originalReadFileSync(filePath, encoding);
});

jest.mock('mongodb');
var mockCollection = {
    findOne: jest.fn(),
    find: jest.fn(),
    insertOne: jest.fn(),
    updateOne: jest.fn()
};
MongoClient.mockReturnValue({
    connect: jest.fn(),
    db: jest.fn().mockReturnValue({
        collection: jest.fn().mockReturnValue(mockCollection)
    })
});

const admin = require('firebase-admin');
jest.mock('firebase-admin');
admin.messaging = jest.fn().mockReturnValue('');
admin.credential.cert.mockReturnValue('');

const app = require('./app.js');
const { randomInt } = require('crypto');

// Interface GET /leaderboard
describe("Retrieve the leaderboard", () => {
    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Array of up to 10 player stats, including: id, name, elo, games won, games lost, average game duration, and average game path length
     */
    test("Successfully retrieve leaderboard with players", async () => {
        const player = mockPlayer();

        var toArray = jest.fn().mockReturnValueOnce([ player ]);
        var limit = jest.fn().mockReturnValueOnce({ toArray });
        var sort = jest.fn().mockReturnValueOnce({ limit });
        mockCollection.find.mockReturnValueOnce({ sort });

        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(1);
        expect(JSON.stringify(response.body[0])).toBe(JSON.stringify(player));
        expect(toArray).toHaveBeenCalled();
        expect(limit).toHaveBeenCalledWith(10);
        expect(sort).toHaveBeenCalled();
    });

    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Empty array when there are no players
     */
    test("Retrieve leaderboard with no players", async () => {
        var toArray = jest.fn().mockReturnValueOnce([]);
        var limit = jest.fn().mockReturnValueOnce({ toArray });
        var sort = jest.fn().mockReturnValueOnce({ limit });
        mockCollection.find.mockReturnValueOnce({ sort });

        // Perform actions to set up the scenario with no players (e.g., clear database)
        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(0);
        expect(toArray).toHaveBeenCalled();
        expect(limit).toHaveBeenCalledWith(10);
        expect(sort).toHaveBeenCalled();
    });

    /**
     * Input: N/A
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
        var toArray = jest.fn().mockRejectedValue(new Error('Test Error'));

        // Perform actions to set up the scenario with no players (e.g., clear database)
        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(500);
    });
});


// Interface GET /player/:id
describe("Retrieve a player's information", () => {
    /**
     * Input: A player's id
     * Expected status code: 200
     * Expected behaviour: The player's information should be retrieved
     * Expected output: The player's stats, including their id, name, elo, games won, games lost, average game duration, and average game path length
     */
    test("Retrieve a single player's information", async () => {
        const player = mockPlayer();

        mockCollection.findOne.mockReturnValue(player);

        const response = await request(app).get('/player/' + player._id);
        expect(response.status).toBe(200);
        expect(JSON.stringify(response.body)).toBe(JSON.stringify(player));
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id })
    });

    /**
     * Input: A nonexistent player id (300)
     * Expected status code: 404
     * Expected behaviour: The server should simply return a 404 error
     * Expected output: A 404 response
     */
    test("Retrieve a nonexistent player's information", async () => {
        mockCollection.findOne.mockReturnValue(null);

        const response = await request(app).get('/player/300');
        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: "300" })
    });

    /**
     * Input: N/A
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
        mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).get('/player/20');
        expect(response.status).toBe(500);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: "20" })
    });
});

// Interface POST /signIn/:id
describe("Testing player signIn", () => {
    /**
     * Input: A player's id
     * Expected status code: 200
     * Expected behaviour: The player should be "signed in" and can now enter games.
     * Expected output: Should return the players ID as signin confirmation
     */
    test("SignIn Player", async () => {
        const player = mockPlayer();

        mockCollection.findOne.mockReturnValue(player);

        const response = await request(app).post('/signIn/' + player._id).send({_id:player.id, username:player.name});
        expect(response.status).toBe(200);
        expect(JSON.stringify(response.body)).toBe(JSON.stringify(player._id));
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id })
    });

    /**
     * Input: A nonexistent player id (400)
     * Expected status code: 200
     * Expected behaviour: This should create a new database entry for  the new ID
     * Expected output: The given ID as confirmation
     */
    test("Signin a nonexistent player", async () => {
        
		var newPlayer = {
            _id: 400,
            name: "newPlayer",
            elo: 0,
            gamesWon: 0,
            gamesLost: 0,
            avgGameDuration: null,
            avgGamePathLength: null
        }

        const response = await request(app).post('/signIn/400').send({_id:400, username:"newPlayer"});
        expect(response.status).toBe(200);
        expect(mockCollection.insertOne).toHaveBeenCalledWith(newPlayer)
    });

});

// Interface POST /game
describe("Testing game requests", () => {
    /**
     * Input: A player's id who is not signed in and requested a game
     * Expected status code: 400
     * Expected behaviour: The server should return an error code and not crash
     * Expected output: An empty message with status 400
     */
    test("Invalid Request", async () => {
        const player = mockPlayer();

        const response = await request(app).post('/game').send({_id:0, username:player.name});
        expect(response.status).toBe(400);
        
    });

    /**
     * Input: Testing single player game.
     * Expected status code: 200
     * Expected behaviour: This should create a new game with valid pages for the user.
     * Expected output: The given ID as confirmation
     */
    test("Retrieve a nonexistent player's information", async () => {
        
		var newPlayer = {
            _id: 400,
            name: "newPlayer",
            elo: 0,
            gamesWon: 0,
            gamesLost: 0,
            avgGameDuration: null,
            avgGamePathLength: null
        }

        const response = await request(app).post('/signIn/400').send({_id:400, username:"newPlayer"});
        expect(response.status).toBe(200);
        expect(mockCollection.insertOne).toHaveBeenCalledWith(newPlayer)
    });

    /** @TODO remove this test?
     * Input: N/A
     * Expected status code: 400
     * Expected behaviour: The server not crash but send back an error.
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
         

        const response = await request(app).post('/signIn/0').send({_id:0, username:"newPlayer"});
        expect(response.status).toBe(400);
        expect(mockCollection.insertOne).toHaveBeenCalledTimes(0)
        expect(mockCollection.findOne).toHaveBeenCalledTimes(0)
    });
});


// Interface POST /player/{playerId}/friend/{friendId}
describe("Add a friend", () => {
    /**
     * Input: a player id and a friend id
     * Expected status code: 201
     * Expected behaviour: the server adds the friend id to the player's friends list
     * Expected output: An empty message with status 201
     */
    test("Add a friend", async () => {
        const player = mockPlayer("Player");
        const friend = mockPlayer("Friend");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == player._id) {
                return player;
            }
            else if (idObj._id == friend._id) {
                return friend;
            }

            return null;
        });
    
        const response = await request(app).post('/player/' + player._id + '/friend/' + friend._id);

        expect(response.status).toBe(201);
        expect(mockCollection.updateOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
    });

    /**
     * Input: a player id and a friend id that doesn't exist
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Add a nonexistent friend", async () => {
        const player = mockPlayer("Player");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == player._id) {
                return player;
            }

            return null;
        });
    
        const response = await request(app).post('/player/' + player._id + '/friend/' + (player._id + 1));

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id + 1 });
    });

    /**
     * Input: a player id that doesn't exist and a friend id
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Add a friend to a nonexistent player", async () => {
        const friend = mockPlayer("Player");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == friend._id) {
                return friend;
            }

            return null;
        });
    
        const response = await request(app).post('/player/' + (friend._id + 1) + '/friend/' + friend._id);

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id + 1 });
    });

    /**
     * Input: N/A
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
        mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).post('/player/20/friend/30');
        expect(response.status).toBe(500);
    });
});

// Interface DELETE /player/{playerId}/friend/{friendId}
describe("Remove a friend", () => {
    /**
     * Input: a player id and a friend id
     * Expected status code: 201
     * Expected behaviour: the server adds the friend id to the player's friends list
     * Expected output: An empty message with status 201
     */
    test("Remove a friend", async () => {
        const friend = mockPlayer("Friend");
        const player = mockPlayer("Player");
        player.friends = [friend._id];

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == player._id) {
                return player;
            }
            else if (idObj._id == friend._id) {
                return friend;
            }

            return null;
        });
    
        const response = await request(app).delete('/player/' + player._id + '/friend/' + friend._id);

        expect(response.status).toBe(204);
        expect(mockCollection.updateOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
    });

    /**
     * Input: a player id and a the id of another player who isn't a friend
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Remove a friend", async () => {
        const friend = mockPlayer("Friend");
        const player = mockPlayer("Player");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == player._id) {
                return player;
            }
            else if (idObj._id == friend._id) {
                return friend;
            }

            return null;
        });
    
        const response = await request(app).delete('/player/' + player._id + '/friend/' + friend._id);

        expect(response.status).toBe(404);
        expect(mockCollection.updateOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
    });

    /**
     * Input: a player id and a friend id that doesn't exist
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Add a nonexistent friend", async () => {
        const player = mockPlayer("Player");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == player._id) {
                return player;
            }

            return null;
        });
    
        const response = await request(app).delete('/player/' + player._id + '/friend/' + (player._id + 1));

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id + 1 });
    });

    /**
     * Input: a player id that doesn't exist and a friend id
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Add a friend to a nonexistent player", async () => {
        const friend = mockPlayer("Player");

        mockCollection.findOne.mockImplementation((idObj) => {
            if (idObj._id == friend._id) {
                return friend;
            }

            return null;
        });
    
        const response = await request(app).delete('/player/' + (friend._id + 1) + '/friend/' + friend._id);

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id + 1 });
    });

    /**
     * Input: N/A
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
        mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).delete('/player/20/friend/30');
        expect(response.status).toBe(500);
    });
});


function mockPlayer(
    name = "Julia Rubin", 
    _id = randomInt(1000000).toString(), 
    elo = randomInt(1000000), 
    gamesWon = randomInt(1000000), 
    gamesLost = randomInt(1000000), 
    avgGameDuration = randomInt(1000000), 
    avgGamePathLength = randomInt(1000000),
    friends = null
) {
    if (!friends) {
        friends = [];

        for (var i = 0; i < randomInt(5); i++) {
            friends.push(randomInt(1000000));
        }
    }

    return {
        _id,
        name,
        elo,
        gamesWon,
        gamesLost,
        avgGameDuration,
        avgGamePathLength,
        friends
    };
}