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

const mockPages = [
    { "title": "Cat", "url": "https://en.m.wikipedia.org/wiki/Cat" },
    { "title": "France", "url": "https://en.m.wikipedia.org/wiki/France" }
];
const originalReadFileSync = fs.readFileSync;
fs.readFileSync = jest.fn((filePath, encoding) => {
    if (filePath === '/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/privkey.pem'
        || filePath === '/etc/letsencrypt/live/milestone1.canadacentral.cloudapp.azure.com/fullchain.pem') {
        return '';
    }
    else if (filePath === './service-account-key.json') {
        return '{}'
    }
    else if (filePath === 'pages.json') {
        return JSON.stringify(mockPages);
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
admin.messaging = jest.fn()

var mockMessenger = {
	send: jest.fn()	
	
}

admin.messaging.mockReturnValue(mockMessenger)

admin.credential.cert.mockReturnValue('');

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
     * ChatGPT usage: Partial
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
        expect(mockCollection.find).toHaveBeenCalled();
        expect(mockCollection.find).toHaveBeenCalledTimes(1);
        expect(toArray).toHaveBeenCalled();
        expect(limit).toHaveBeenCalledWith(10);
        expect(sort).toHaveBeenCalled();
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.find).toHaveBeenCalled();
        expect(mockCollection.find).toHaveBeenCalledTimes(1);
        expect(toArray).toHaveBeenCalled();
        expect(limit).toHaveBeenCalledWith(10);
        expect(sort).toHaveBeenCalled();
    });

    /**
     * ChatGPT usage: Partial
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
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledTimes(2);
        expect(mockCollection.findOne).toHaveBeenCalledWith({_id: player._id})
    });

    /**
     * ChatGPT usage: Partial
     * Input: A nonexistent player id (300)
     * Expected status code: 404
     * Expected behaviour: The server should simply return a 404 error
     * Expected output: A 404 response
     */
    test("Retrieve a nonexistent player's information", async () => {
        mockCollection.findOne.mockReturnValue(null);

        const response = await request(app).get('/player/300');
        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledTimes(1);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: "300" })
    });

    /**
     * ChatGPT usage: Partial
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
     * ChatGPT usage: No
     * Input: A player's id
     * Expected status code: 200
     * Expected behaviour: The player should be "signed in" and can now enter games.
     * Expected output: Should return the players ID as signin confirmation
     */
    test("SignIn Player", async () => {
        const player = mockPlayer();

        mockCollection.findOne.mockReturnValue(player);
	
        const response = await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
        expect(response.status).toBe(200);
        expect(response.body._id).toBe(player._id);
        expect(mockCollection.findOne).toHaveBeenCalledWith({_id: player._id })
    });

    /**
     * ChatGPT usage: No
     * Input: A player ID
     * Expected status code: 500
     * Expected behaviour: The server should error out
     * Expected output: An internal server error response
    */
    test("Database error", async () => {

		mockCollection.findOne.mockRejectedValue(new Error("Test error"));

        const response = await request(app).post('/signIn/30').send({id:"30", name:"none"});
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
     * ChatGPT usage: No
     * Input: Single Game Request
     * Expected status code: 200
     * Expected behaviour: The server should check on the game and send back details
     * Expected output: Game information
     */
    test("Single Game Request", async () => {
		
        var player = mockPlayer();
		
        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "single"});
        const responsePages = [response.body.startPage, response.body.endPage]
		
		expect(response.status).toBe(200);
		expect(responsePages.sort()).toEqual([mockPages[0].url, mockPages[1].url]);
    });
	
	/** 
     * ChatGPT usage: No
     * Input: Single Game Request
     * Expected status code: 500
     * Expected behaviour: The server should run into a fetch error and return error
     * Expected output: error code 500
     */
    test("Single Player Fetch path error", async () => {
		
        var player = mockPlayer();
		
		node_fetch.mockRejectedValueOnce(new Error("Test error"));
		
        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
        
		const response = await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name})
		.then(response => {
			return request(app).post('/game').send({id:player._id, name:player.name, mode: "single"});
		});
                 
		expect(response.status).toBe(500);
		
		
    });
	
	/**
     * ChatGPT usage: No
     * Input: Daily Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game using the Current Daily mockPages
     * Expected output: Valid Game information
     */
    test("Daily Game Request", async () => {	
		var player = mockPlayer();
		
		jest.mock('node-fetch');

        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
        
		const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "daily"});
                 
		expect(response.status).toBe(200);
		expect(response.body.startPage).toBe(mockPages[0].url);
		expect(response.body.endPage).toBe(mockPages[1].url);
    });
	
    /**
     * ChatGPT usage: No 
     * Input: Multi Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game with the two players
     * Expected output: Valid Game information
     */
	test("Multi Game Request", async () => {	
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);	
		const player2 = mockPlayer("Player2", "2000", 12);
		
		
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});
		
		const [response, response2] = await Promise.all([promise1, promise2]);
		const responsePages = [response.body.startPage, response.body.endPage]
		
		expect(response.status).toBe(200);
        expect(response.body).toEqual(response2.body);
		expect(responsePages.sort()).toEqual([mockPages[0].url, mockPages[1].url]);
		
		//Sleeping to let the player matching stop for the next test
        await sleep(1000);
    });
	
	/**
     * ChatGPT usage: No 
     * Input: Multi Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game with the two players
     * Expected output: Valid Game information
     */
	test("Multi Game Request with high elo difference", async () => {	
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 5);	
		const player2 = mockPlayer("Player2", "2000", 90);

        mockCollection.findOne.mockReturnValue(player);
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		
		mockCollection.findOne.mockReturnValue(player2);
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});
		
		const [response, response2] = await Promise.all([promise1, promise2]);
		const responsePages = [response.body.startPage, response.body.endPage]
		
		expect(response.status).toBe(200);
        expect(response.body).toEqual(response2.body);
		expect(responsePages).toContain(mockPages[0].url);
        expect(responsePages).toContain(mockPages[1].url);
		
		//Sleeping to let the player matching stop for the next test
        await sleep(1000);
    });
	
	/**
     * ChatGPT usage: No 
     * Input: Two Friend Game Requests
     * Expected status code: 200
     * Expected behaviour: The server should create a game with the two players, as friends
     * Expected output: Valid Game information
     */
	test("Friend Game Request", async () => {	
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);	
		const player2 = mockPlayer("Player2", "2000", 12);
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "friend", friendId:player._id});
        var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "friend", friendId:player2._id});
		
		const [response, response2] = await Promise.all([promise1, promise2]);
		const responsePages = [response.body.startPage, response.body.endPage]
		
		expect(response.status).toBe(200);
        expect(response.body).toEqual(response2.body);
        expect(responsePages.sort()).toEqual([mockPages[0].url, mockPages[1].url]);
		
        //Sleeping to let the player matching stop for the next test
        await sleep(1000);
    });	
	
	
	/**
     * ChatGPT usage: No
     * Input: One Multi Game Request
     * Expected status code: 604
     * Expected behaviour: The server should return a "604" message, signalling there are no other players online
     * Expected output: Valid Game information
     */
	test("Multi Game Request timeout", async () => {	
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});

		//Now send game request to player 2 (who isnt signed in or online)
		const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});
		
		expect(response.status).toBe(604);

		
        
    }, 15000);
	
	
	
	/**
     * ChatGPT usage: No 
     * Input: One Friend Game Request
     * Expected status code: 200
     * Expected behaviour: The server should return a "604" message, signalling there are no other players online
     * Expected output: Valid Game information
     */
	test("Friend Game Request timeout", async () => {
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);	
		const player2 = mockPlayer("Player2", "2000", 12);
		
		await request(app).post('/signIn/' + player._id).send({ id: player._id, name: player.name });

		//Now send game request to player 2 (who isnt signed in or online)
		const response = await request(app).post('/game').send({id: player._id, name: player.name, mode: "friend", friendId: player2._id});
		
		expect(response.status).toBe(604);
    }, 15000);
	
	
	
});


// Interface GET /game
describe("Testing completing a game", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

	/**
     * ChatGPT usage: No
     * Input: Completing a game after mockPages have been aded
     * Expected status code: 200
     * Expected behaviour: The server should create a game with both players.
     * Expected output: Valid Game information
     */
    test("Completing multiplayer game", async () => {	
		
		mockMessenger.send.mockReturnValue("Succsess!");
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);	
		const player2 = mockPlayer("Player2", "2000", 12);

		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});
		
		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        
		var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});

		await Promise.all([promise1, promise2]);


		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.m.wikipedia.org/wiki/Cat"});
		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.m.wikipedia.org/wiki/France"});

		await request(app).put('/game').send({id:player2._id, name:player2.name, URL: "https://en.m.wikipedia.org/wiki/France"});
		await request(app).put('/game').send({id:player2._id, name:player2.name, URL: "https://en.m.wikipedia.org/wiki/Cat"});


        const response = await request(app).get('/game/' + player._id);
		const response2 = await request(app).get('/game/' + player2._id);
		
		expect(response.status).toBe(200);
		
		expect(mockMessenger.send).toHaveBeenCalled();
		expect(response.body.gamePosition).toBe(JSON.stringify(1));
		expect(JSON.stringify(response.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		
		
		expect(response2.body.gamePosition).toBe(JSON.stringify(2));
		expect(JSON.stringify(response2.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		

		
    });
	
	/**
     * ChatGPT usage: No 
     * Input: Completing a complete game, but with a message failure
     * Expected status code: 200
     * Expected behaviour: The server should continue as normal, despite the failure
     * Expected output: Valid Game information
     */
	test("Completing multiplayer game with messenger failure", async () => {	
		
		mockMessenger.send.mockRejectedValueOnce(new Error("Test error"));
		mockCollection.findOne.mockReturnValue(null);
		
		//First, sign player in
		const player = mockPlayer("Player1", "1000", 11);	
		const player2 = mockPlayer("Player2", "2000", 12);
		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});
		
		//Now start game
        var promise1 = request(app).post('/game').send({id:player2._id, name:player2.name, mode: "multi"});
        
		var promise2 = request(app).post('/game').send({id:player._id, name:player.name, mode: "multi"});

		await Promise.all([promise1, promise2]);

	

		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.m.wikipedia.org/wiki/France"});
		await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.m.wikipedia.org/wiki/Cat"});

        const response = await request(app).get('/game/' + player._id);
		const response2 = await request(app).get('/game/' + player2._id);
		
		expect(response.status).toBe(200);

		expect(mockMessenger.send).toHaveBeenCalled();
		expect(response.body.gamePosition).toBe(JSON.stringify(1));
		expect(JSON.stringify(response.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		

		expect(response2.body.gamePosition).toBe("NA");
		expect(JSON.stringify(response2.body.shortestPath)).toBe(JSON.stringify(["Taco", "Mexican Food", "Mexico"]));		
		
    });
	
	/**
     * ChatGPT usage: No 
     * Input: Completing a complete game, but with a message failure
     * Expected status code: 200
     * Expected behaviour: The server should continue as normal, despite the failure
     * Expected output: Valid Game information
     */
	test("Game completion message while not in a game.", async () => {	
		
		mockCollection.findOne.mockReturnValue(null);
		
		//First, no signin even
		const player = mockPlayer();	
		
        const response = await request(app).get('/game/' + player._id);
		
		expect(response.status).toBe(500);

				
    });

	
	
});

// Interface POST /player/{playerId}/friend/{friendId}
describe("Add a friend", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.updateOne).toHaveBeenCalledTimes(1);
        expect(mockCollection.updateOne).toHaveBeenCalledWith(
            { _id: player._id },
            { 
                $set: {
                    elo: player.elo,
                    gamesWon: player.gamesWon,
                    gamesLost: player.gamesLost,
                    avgGameDuration: player.avgGameDuration,
                    avgGamePathLength: player.avgGamePathLength,
                    friends
                } 
            }
        );
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledTimes(3);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id + 1 });
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id + 1 });
    });

    /**
     * ChatGPT usage: Partial
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
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
        expect(mockCollection.updateOne).toHaveBeenCalled();
        expect(mockCollection.updateOne).toHaveBeenCalledTimes(1);
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
     * ChatGPT usage: Partial
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
        expect(mockCollection.updateOne).not.toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.updateOne).not.toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id + 1 });
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.updateOne).not.toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id + 1 });
    });

    /**
     * ChatGPT usage: Partial
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
     * ChatGPT usage: Partial
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
            const id = idObj._id;

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
        expect(mockCollection.findOne).toHaveBeenCalledTimes(2 + player.friends.length);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });

        for (const friend of friends) {
            expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: friend._id });
        }
    });

    /**
     * ChatGPT usage: Partial
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
        expect(mockCollection.findOne).toHaveBeenCalledTimes(2);
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: player._id });
    });

    /**
     * ChatGPT usage: Partial
     * Input: a nonexistend player id
     * Expected status code: 404
     * Expected behaviour: server responds with an error
     * Expected output: an empty 404 response
     */
    test("Retrieve friends of a player that doesn't exist", async () => {
        mockCollection.findOne.mockReturnValue(null);

        const response = await request(app).get('/player/20/friend');

        expect(response.status).toBe(404);
        expect(mockCollection.findOne).toHaveBeenCalled();
        expect(mockCollection.findOne).toHaveBeenCalledWith({ _id: '20'});
    });

    /**
     * ChatGPT usage: Partial
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
describe("Testing putting mockPages into games", () => {
    beforeEach(() => {
        resetConnectionMocks();
    });

	/**
     * ChatGPT usage: No
     * Input: testing adding mockPages to a game in progress.
     * Expected status code: 200
     * Expected behaviour: The server should add the game to its internal page list
     * Expected output: A success response code
     */
    test("Page Put Message", async () => {
		var player = mockPlayer();
		
		jest.mock('node-fetch');

        mockCollection.findOne.mockReturnValue(player);

        await request(app).post('/signIn/' + player._id).send(JSON.stringify({id:player._id, name:player.name}));
        await request(app).post('/game').send({id:player._id, name:player.name, mode: "single"});

   		const response = await request(app).put('/game').send({ id: player._id, name: player.name, URL: "https://en.m.wikipedia.org/wiki/Mexican_cuisine" });
		
		expect(response.status).toBe(200);
    });
	
	/**
     * ChatGPT usage: No
     * Input: testing adding mockPages to a game in progress.
     * Expected status code: 500
     * Expected behaviour: The server should error because of incorrect inputs
     * Expected output: A 500 response code
     */
    test("Page Put Message error", async () => {
		var player = mockPlayer();
		
		jest.mock('node-fetch');

        mockCollection.findOne.mockReturnValue(player);
     
   	    const response = await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.m.wikipedia.org/wiki/Mexican_cuisine"});
		
		expect(response.status).toBe(500);
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
        name,
        _id,
        elo,
        gamesWon,
        gamesLost,
        avgGameDuration,
        avgGamePathLength,
        friends
    };
}

function sleep(milliseconds) {
  return new Promise((resolve) => {
    setTimeout(resolve, milliseconds);
  });
}

var sampleFetchData = {
  isSourceRedirected: false,
  isTargetRedirected: false,
  pages: {
    '1': {
      description: 'Indo-European language',
      thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Idioma_Griego.PNG/160px-Idioma_Griego.PNG',
      title: 'Taco',
      url: 'https://en.wikipedia.org/wiki/Greek_language'
    },
    '2': {
      description: 'Premature cell death',
      thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/6/69/Structural_changes_of_cells_undergoing_necrosis_or_apoptosis.png/160px-Structural_changes_of_cells_undergoing_necrosis_or_apoptosis.png',
      title: 'Mexican Food',
      url: 'https://en.wikipedia.org/wiki/Necrosis'
    },
    '3': {
      description: 'Pandemic in the Byzantine Empire, later northern Europe',
      thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Plaguet03.jpg/160px-Plaguet03.jpg',
      title: 'Mexico',
      url: 'https://en.wikipedia.org/wiki/Plague_of_Justinian'
    },
    '5982415': {
      description: 'Genus of arachnids',
      thumbnailUrl: 'https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Long-legged_Sac_Spider_-_Cheiracanthium_sp.%2C_Pateros%2C_Washington.jpg/160px-Long-legged_Sac_Spider_-_Cheiracanthium_sp.%2C_Pateros%2C_Washington.jpg',
      title: 'Cheiracanthium',
      url: 'https://en.wikipedia.org/wiki/Cheiracanthium'
    },
    '8611983': {
      description: 'Wikimedia list article',
      title: 'List of medical roots, suffixes and prefixes',
      url: 'https://en.wikipedia.org/wiki/List_of_medical_roots,_suffixes_and_prefixes'
    },
    '19352117': {
      description: 'Ancient Greek philosopher',
      title: 'Asclepiades of Phlius',
      url: 'https://en.wikipedia.org/wiki/Asclepiades_of_Phlius'
    },
    '42147537': {
      description: 'Species of arachnid',
      title: 'Cheiracanthium campestre',
      url: 'https://en.wikipedia.org/wiki/Cheiracanthium_campestre'
    }
  },
  paths: [
    [ 1, 2, 3],
    [ 19352117, 11887, 548536, 39936, 5982415, 42147537 ]
  ],
  sourcePageTitle: 'Asclepiades of Phlius',
  targetPageTitle: 'Cheiracanthium campestre'
}

//creating a response object
//Sample code from stack overflow
var mockResponse = {
	json: () => {
		return new Promise((resolve, reject) => {
			setTimeout(() => {
				//Creating a map object
				resolve(sampleFetchData);
				//resolve("test")
			}, 200);
		});
	}
}