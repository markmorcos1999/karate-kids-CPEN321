const fetch = require('node-fetch');
const wiki = require('wikijs');

const SHORTEST_PATH_BASE_URL = 'https://api.sixdegreesofwikipedia.com/paths'; // Replace with the URL you want to request


class PageManager {
    constructor() {
        this.pageCache = new Map();
    }

    async getRandomTitle() {
        return await wiki().random();
    }

    /**
     * Find the shortest path between two pages.
     *
     * @param {string} startTitle - The title of the starting page
     * @param {string} endTitle - The title of the ending page
     * 
     * @return {Array} - An array of page titles representing the shortest path
     * 
     * @throws {Error} - If no path is found
     */
    async getShortestPath(startTitle, endTitle) {
        return await fetch(SHORTEST_PATH_BASE_URL, {
            method: 'POST', // Use the POST method
            body: JSON.stringify({source: startTitle, target: endTitle}), // Include the request body
            headers: {
              'Content-Type': 'application/json', // Specify the content type of the request body
            },
        })
        .then(response => response.json())
        .then((data) => {
            return data.paths[0].map(id => data.pages[id.toString()].title);
        })
        .catch((error) => {
            throw error;
        });
    }
}

module.exports = PageManager;