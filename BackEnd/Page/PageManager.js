const fetch = require('node-fetch');
const wiki = require('wikijs');


class PageManager {
    constructor() {
        this.pageCache = new Map();
    }

    async getRandomTitle() {
        return await wiki().random();
    }

    // Find the shortest path between two pages.
    async getShortestPath(startTitle, endTitle) {
        return await fetch(
            'https://api.sixdegreesofwikipedia.com/paths', 
            {
                method: 'POST',
                body: JSON.stringify({source: startTitle, target: endTitle}),
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        )
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