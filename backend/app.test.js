const request = require('supertest');
const app = require('./app');

// Interface GET /leaderboard
describe("Retrieve the leaderboard", () => {
    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Array of up to 10 player stats, including: id, name, elo, games won, games lost, average game duration, and average game path length
     */
    test("Successfully retrieve leaderboard with players", async () => {
        const response = await request(app).get('/leaderboard');

        expect(response.status).toBe(200);
        expect(response.body).toBeInstanceOf(Array);
        expect(response.body.length).toBeGreaterThanOrEqual(0);
        // Add more specific expectations for the response body structure
    });

    /**
     * Input: N/A
     * Expected status code: 200
     * Expected behaviour: The stats of the 10 players with the highest elo are retrieved
     * Expected output: Empty array when there are no players
     */
    test("Retrieve leaderboard with no players", async () => {
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

