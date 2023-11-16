const request = require('supertest');
const https = require('https');
const fs = require('fs');
const { MongoClient } = require('mongodb');

jest.mock('https');
https.createServer.mockReturnValue({ listen: jest.fn(() => {}) });

originalReadFileSync = fs.readFileSync;
fs.readFileSync = jest.fn((filePath, encoding) => {
    // TODO: Fix this
    if (filePath !== "hello") {
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

// Interface GET /leaderboard
describe("Retrieve the leaderboard", () => {
    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Array of up to 10 player stats, including: id, name, elo, games won, games lost, average game duration, and average game path length
     */
    test("Successfully retrieve leaderboard with players", async () => {
        mockCollection.find.mockReturnValueOnce({
            sort: jest.fn().mockReturnValueOnce({
                limit: jest.fn().mockReturnValueOnce({
                    toArray: jest.fn().mockReturnValueOnce([
                        { _id: "test" }
                    ])
                })
            })
        });

        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(1);
        // Add more specific expectations for the response body structure
    });

    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Empty array when there are no players
     */
    test("Retrieve leaderboard with no players", async () => {
        mockCollection.find.mockReturnValueOnce({
            sort: jest.fn().mockReturnValueOnce({
                limit: jest.fn().mockReturnValueOnce({
                    toArray: jest.fn().mockReturnValueOnce([])
                })
            })
        });

        // Perform actions to set up the scenario with no players (e.g., clear database)
        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBe(0);
    });

    /**
     * Add more tests as needed, for example, testing different scenarios, edge cases, or error cases.
     */
});

