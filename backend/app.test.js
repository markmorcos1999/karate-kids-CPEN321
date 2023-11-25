const request = require('supertest');
const https = require('https');
const fs = require('fs');
const { MongoClient } = require('mongodb');
const Wikijs = require('wikijs');

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

jest.mock('wikijs');
var mockWiki = {
	page: jest.fn(),
	random: jest.fn()
}
var mockTitle = {
	map: jest.fn()
}
Wikijs.default.mockReturnValue(mockWiki)

//Wikijs.default = mockWiki;

var node_fetch = require('node-fetch');
jest.mock('node-fetch');

node_fetch.mockReturnValue(
	new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve(mockResponse);
        }, 1000);
    })
);

const app = require('./app.js');
const { randomInt } = require('crypto');

// Interface GET /leaderboard
describe("Retrieve the leaderboard", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

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
        mockCollection.find.mockRejectedValue(new Error("Test Error"));

        // Perform actions to set up the scenario with no players (e.g., clear database)
        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(500);
    });
});


// Interface GET /player/:id
describe("Retrieve a player's information", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

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
        expect(mockCollection.findOne).toHaveBeenCalledWith({_id: player._id})
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

        const response = await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}));
        expect(response.status).toBe(200);
        expect(response.body._id).toBe(player._id);
        expect(mockCollection.findOne).toHaveBeenCalledWith({_id: player._id })
    });

    /**
     * Input: A player ID
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
    */
    test("Database error", async () => {

		mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).post('/signIn/30').send(JSON.stringify({id:"30", name:"none"}));
        expect(response.status).toBe(500);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: "30" })
		
    });
	

});

// Interface POST /game
describe("Testing game requests", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

    /** 
     * Input: Single Game Request
     * Expected status code: 200
     * Expected behaviour: The server should check on the game and send back details
     * Expected output: Game information
     */
    test("Single Game Request", async () => {
		
        var player = mockPlayer();
		
		
		var pages = mockPages();
		mockTitle.map.mockReturnValue(pages);
		mockWiki.random.mockReturnValue(mockTitle);

        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}));
        
		const response = await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}))
		.then(response => {
			return request(app).post('/game').send({id:player._id, name:player.name, mode: "single"});
		});
                 
		expect(response.status).toBe(200);
		
		expect(JSON.stringify(response.body.startTitle)).toBe(JSON.stringify(pages[0].title));
		expect(JSON.stringify(response.body.startPage)).toBe(JSON.stringify(pages[0].url));
		expect(JSON.stringify(response.body.endTitle)).toBe(JSON.stringify(pages[1].title));
		expect(JSON.stringify(response.body.endPage)).toBe(JSON.stringify(pages[1].url));
		
        expect(mockTitle.map).toHaveBeenCalledTimes(1)
        expect(mockWiki.random).toHaveBeenCalledTimes(1)
    });
	
	 /** 
     * Input: Daily Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game using the Current Daily pages
     * Expected output: Valid Game information
     */
    test("Daily Game Request", async () => {	

		var player = mockPlayer();
		
		
		jest.mock('node-fetch');

		var pages = mockPages();
		
		mockWiki = {
			page: jest.fn(),
			random: jest.fn()
		}
		
		mockTitle = {
			map: jest.fn()
		}
		mockTitle.map.mockReturnValue(pages);
		mockWiki.random.mockReturnValue(mockTitle);

        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}));
        
		const response = await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}))
		.then(response => {
			return request(app).post('/game').send({id:player._id, name:player.name, mode: "daily"});
		});
                 
		expect(response.status).toBe(200);
		
		expect(JSON.stringify(response.body.startTitle)).toBe(JSON.stringify(pages[0].title));
		expect(JSON.stringify(response.body.startPage)).toBe(JSON.stringify(pages[0].url));
		expect(JSON.stringify(response.body.endTitle)).toBe(JSON.stringify(pages[1].title));
		expect(JSON.stringify(response.body.endPage)).toBe(JSON.stringify(pages[1].url));
		
        expect(mockTitle.map).toHaveBeenCalledTimes(0)
        expect(mockWiki.random).toHaveBeenCalledTimes(0)
        
    });
	
	 /** 
     * Input: Multi Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game with the two players
     * Expected output: Valid Game information
     */
	test("Multi Game Request", async () => {	
		var pages = mockPages();
		mockTitle.map.mockReturnValue(pages);
		mockWiki.random.mockReturnValue(mockTitle);
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		player = mockPlayer(elo = 11);	
		player2 = mockPlayer(elo = 12);
		
		
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});
		
		const[response, r2] = await Promise.all([promise1, promise2]);
		
		
		expect(response.status).toBe(200);

		expect(JSON.stringify(response.body.startTitle)).toBe(JSON.stringify(pages[0].title));
		expect(JSON.stringify(response.body.startPage)).toBe(JSON.stringify(pages[0].url));
		expect(JSON.stringify(response.body.endTitle)).toBe(JSON.stringify(pages[1].title));
		expect(JSON.stringify(response.body.endPage)).toBe(JSON.stringify(pages[1].url));
		
        
    });
});


// Interface GET /game
describe("Testing completing a game", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

	 /** 
     * Input: Completing a game after pages have been aded
     * Expected status code: 200
     * Expected behaviour: The server should create a game using the Current Daily pages
     * Expected output: Valid Game information
     */
    test("Completing multiplayer game", async () => {	
		
		var pages = mockPages();
		mockTitle.map.mockReturnValue(pages);
		mockWiki.random.mockReturnValue(mockTitle);
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		player = mockPlayer(_id = "1000", elo = 10);	
		player2 = mockPlayer(_id = "2000", elo = 11);
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});
		
		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        
		var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});

		await Promise.all([promise1, promise2]);

		console.log("No longer awaiting games")

		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.wikipedia.org/wiki/Mexican_cuisine"});
		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.wikipedia.org/wiki/Mexico"});

        const response = await request(app).get('/game/' + player._id);
		const response2 = await request(app).get('/game/' + player2._id);
		
		expect(response.status).toBe(200);

		
		expect(JSON.stringify(response.body.gamePosition)).toBe(JSON.stringify(1));
		expect(JSON.stringify(response.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		

		expect(JSON.stringify(response2.body.gamePosition)).toBe(JSON.stringify(2));
		expect(JSON.stringify(response2.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		

		
    });
	
});

// Interface POST /player/{playerId}/friend/{friendId}
describe("Add a friend", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

    /**
     * Input: a player id and a friend id
     * Expected status code: 201
     * Expected behaviour: the server adds the friend id to the player's friends list
     * Expected output: An empty message with status 201
     */
    test("Add a friend", async () => {
        const player = mockPlayer("Player");
        const friend = mockPlayer("Friend");
        const friends = [...player.friends, friend._id];

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
        expect(mockCollection.updateOne).toHaveBeenCalledWith(
            { _id: player._id },
            { 
                $set: {
                    elo: player.elo,
                    gamesWon: player.gamesWon,
                    gamesLost: player.gamesLost,
                    avgGameDuration: player.avgGameDuration,
                    avgGamePathLength: player.avgGamePathLength,
                    friends: friends
                } 
            }
        );
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
    });

    /**
     * Input: a player id and the id of an existing friend
     * Expected status code: 409
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 409
     */
    test("Add a friend who is already a friend", async () => {
        const player = mockPlayer("Player");
        const friend = mockPlayer("Friend");
        
        player.friends.push(friend._id);

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

        expect(response.status).toBe(409);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
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
    beforeEach(() => {
        resetConnectionMocks();
    });

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
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
        expect(mockCollection.updateOne).toHaveBeenCalled();
        expect(mockCollection.updateOne).toHaveBeenCalledWith(
            { _id: player._id },
            { 
                $set: {
                    elo: player.elo,
                    gamesWon: player.gamesWon,
                    gamesLost: player.gamesLost,
                    avgGameDuration: player.avgGameDuration,
                    avgGamePathLength: player.avgGamePathLength,
                    friends: []
                } 
            }
        );
    });

    /**
     * Input: a player id and a the id of another player who isn't a friend
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Remove a friend who isn't a friend", async () => {
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
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
    });

    /**
     * Input: a player id and a friend id that doesn't exist
     * Expected status code: 404
     * Expected behaviour: the server responds with an error
     * Expected output: An empty message with status 404
     */
    test("Remove a nonexistent friend", async () => {
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
    test("Remove a friend from a nonexistent player", async () => {
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


// Interface GET /player/{id}/friend
describe("Retrieve a player's friend list", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

    /**
     * Input: a player's id
     * Expected status code: 200
     * Expected behaviour: server responds with the friends of the player
     * Expected output: A response containing an array with the ids of the player's friends
     */
    test("Retrieve player friends", async () => {
        const player = mockPlayer("Player");
        const friends = [ mockPlayer("Friend1"), mockPlayer("Friend2"), mockPlayer("Friend3") ];
        player.friends = friends.map((friend) => friend._id);

        mockCollection.findOne.mockImplementation((idObj) => {
            id = idObj._id;

            if (id == player._id) {
                return player;
            }
            else {
                for (const friend of friends) {
                    if (friend._id == id) {
                        return friend;
                    }
                }
            }

            throw new Error("Test error");
        });

        const response = await request(app).get('/player/' + player._id + '/friend');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(friends.length);
        expect(JSON.stringify(response.body)).toBe(JSON.stringify(friends));
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });

        for (const friend of friends) {
            expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
        }
    });

    /**
     * Input: a player's id
     * Expected status code: 200
     * Expected behaviour: server responds with an empty list
     * Expected output: A response containing an array with the ids of the player's friends
     */
    test("Retrieve friends of a player with no friends", async () => {
        const player = mockPlayer("Player");
        player.friends = [];

        mockCollection.findOne.mockReturnValue(player);

        const response = await request(app).get('/player/' + player._id + '/friend');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(0);
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
    });

    /**
     * Input: a nonexistend player id
     * Expected status code: 404
     * Expected behaviour: server responds with an error
     * Expected output: an empty 404 response
     */
    test("Retrieve friends of a player that doesn't exist", async () => {
        mockCollection.findOne.mockReturnValue(null);

        const response = await request(app).get('/player/20/friend');

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: '20'});
    });

    /**
     * Input: N/A
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
     */
    test("Database retrieval error", async () => {
        mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).get('/player/20/friend');
        expect(response.status).toBe(500);
    });
});

// Interface PUT /game
describe("Testing putting pages into games", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

	 /** 
     * Input: testing adding pages to a game in progress.
     * Expected status code: 200
     * Expected behaviour: The server should add the game to its internal page list
     * Expected output: A success response code
     */
    test("Page Put Message", async () => {
		var player = mockPlayer();
	
		
		jest.mock('node-fetch');

		var pages = mockPages();
		
		mockWiki = {
			page: jest.fn(),
			random: jest.fn()
		}
		
		mockTitle = {
			map: jest.fn()
		}
		mockTitle.map.mockReturnValue(pages);
		mockWiki.random.mockReturnValue(mockTitle);

        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}));
        
		await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}))
		.then(response => {
			return request(app).post('/game').send({id:player._id, name:player.name, mode: "single"});
		});
                 
   		const response = await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.wikipedia.org/wiki/Mexican_cuisine"});
		
		expect(response.status).toBe(200);
				
        expect(mockTitle.map).toHaveBeenCalledTimes(0)
        expect(mockWiki.random).toHaveBeenCalledTimes(0)
        
    });
});


function resetConnectionMocks() {
    mockCollection.findOne = jest.fn();
    mockCollection.find = jest.fn();
    mockCollection.insertOne = jest.fn();
    mockCollection.updateOne = jest.fn();
}

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
            friends.push(randomInt(1000000).toString());
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

function mockPages(){
	return [{title: "Taco", url: "https://en.m.wikipedia.org/wiki/Taco"},{title: "Mexico", url: "https://en.m.wikipedia.org/wiki/Mexico"}]
}

//creating a response object
var mockResponse = {
	json: () => {
		return new Promise((resolve, reject) => {
        setTimeout(() => {
			//Creating a map object
            resolve({paths:[{ map:()=>{return ["Taco", "Mexican Food", "Mexico"]} }]});
			//resolve("test")
		}, 200);
    });
	}
}

function mockPath(){
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve(mockResponse);
        }, 1000);
    });
}
