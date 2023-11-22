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

var node_fetch = require('node-fetch');

jest.mock('node-fetch');


node_fetch = jest.fn()

node_fetch.mockReturnValue(mockPath());

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
		
		
		//First, sign player in
		player = mockPlayer();		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});

		//Now start game
        const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "Single"});
        
		expect(JSON.stringify(response.body.startTitle)).toBe(JSON.stringify(pages[0].title));
		expect(JSON.stringify(response.body.startPage)).toBe(JSON.stringify(pages[0].url));
		expect(JSON.stringify(response.body.endTitle)).toBe(JSON.stringify(pages[1].title));
		expect(JSON.stringify(response.body.endPage)).toBe(JSON.stringify(pages[1].url));
		expect(response.status).toBe(200);
        expect(mocktitle.map).toHaveBeenCalledTimes(1)
        expect(mockWiki.random).toHaveBeenCalledTimes(1)
    });
	
	 /** 
     * Input: Daily Game Request
     * Expected status code: 200
     * Expected behaviour: The server should create a game using the Current Daily pages
     * Expected output: Valid Game information
     */
    test("Daily Game Request", async () => {	

		
		//First, sign player in
		player = mockPlayer();		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});

		//Now start game
        const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "Daily"});
        
		expect(JSON.stringify(response.body.startTitle)).toBe("Taco");
		expect(JSON.stringify(response.body.startPage)).toBe("https://en.m.wikipedia.org/wiki/Taco");
		expect(JSON.stringify(response.body.endTitle)).toBe("Mexico");
		expect(JSON.stringify(response.body.endPage)).toBe("https://en.m.wikipedia.org/wiki/Mexico");
		expect(response.status).toBe(200);
        
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
		
		//First, sign player in
		player = mockPlayer();	
		player2 = mockPlayer2();
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        request(app).post('/game').send({id:player2._id, name:player2.name, mode: "Multi"});
        const response = await request(app).post('/game').send({id:player._id, name:player.name, mode: "Multi"});

		expect(JSON.stringify(response.body.startTitle)).toBe("Taco");
		expect(JSON.stringify(response.body.startPage)).toBe("https://en.m.wikipedia.org/wiki/Taco");
		expect(JSON.stringify(response.body.endTitle)).toBe("Mexico");
		expect(JSON.stringify(response.body.endPage)).toBe("https://en.m.wikipedia.org/wiki/Mexico");
		expect(response.status).toBe(200);
        
    });
	
});

// Interface PUT /game
describe("Testing putting pages into games", () => {
	 /** 
     * Input: testing adding pages to a game in progress.
     * Expected status code: 200
     * Expected behaviour: The server should add the game to its internal page list
     * Expected output: A success response code
     */
    test("Page Put Message", async () => {
		
		
		//First, sign player in
		player = mockPlayer();		
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});

		//Now start a game
        await request(app).post('/game').send({id:player._id, name:player.name, mode: "Daily"});
        
		const response = await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.wikipedia.org/wiki/Mexican_cuisine"});
		
		expect(response.status).toBe(200);
        
    });
	
});

// Interface GET /game
describe("Testing completing a game", () => {
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
		
		
		//First, sign player in
		player = mockPlayer();	
		player2 = mockPlayer2();
		await request(app).post('/signIn/' + player._id).send({id:player._id, name:player.name});
		await request(app).post('/signIn/' + player2._id).send({id:player2._id, name:player2.name});

		//Now start game
        request(app).post('/game').send({id:player2._id, name:player2.name, mode: "Multi"});
        await request(app).post('/game').send({id:player._id, name:player.name, mode: "Multi"});
        
		const response = await request(app).put('/game').send({id:player._id, name:player.name, URL: "https://en.wikipedia.org/wiki/Mexican_cuisine"});
		
		expect(response.status).toBe(200);
        
    });
	
});

function mockPlayer(
    name = "Julia Rubin", 
    _id = randomInt(1000000).toString(), 
    elo = randomInt(1000000), 
    gamesWon = randomInt(1000000), 
    gamesLost = randomInt(1000000), 
    avgGameDuration = randomInt(1000000), 
    avgGamePathLength = randomInt(1000000)
) {
    return {
        _id,
        name,
        elo,
        gamesWon,
        gamesLost,
        avgGameDuration,
        avgGamePathLength
    };
}

function mockPlayer2(
    name = "John Robin", 
    _id = randomInt(1000000), 
    elo = randomInt(1000000), 
    gamesWon = randomInt(1000000), 
    gamesLost = randomInt(1000000), 
    avgGameDuration = randomInt(1000000), 
    avgGamePathLength = randomInt(1000000)
) {
    return {
        _id,
        name,
        elo,
        gamesWon,
        gamesLost,
        avgGameDuration,
        avgGamePathLength
    };
}

function mockPages(){
	return [{title: "Taco", url: "https://en.m.wikipedia.org/wiki/Taco"},{title: "Mexico", url: "https://en.m.wikipedia.org/wiki/Mexico"}]
}

function mockPath(){
	
	return ["Taco", "Mexican Food", "Mexico"]
}
