const fetch = require('node-fetch');
const fs = require('fs');
const { randomInt } = require('crypto');


class PageManager {
    constructor() {
        const pages = fs.readFileSync("pages.json")
        this.pages = JSON.parse(pages);
    }

    // ChatGPT usage: Yes
    getRandomPages() { 
        while(true) {
            const page1 = this.getRandomPage();
            const page2 = this.getRandomPage();

            if (page1.title !== page2.title) {
                return [page1, page2];
            }
        }
    }

    getRandomPage() {
        return this.pages[randomInt(this.pages.length)];
    }
	
    
	getDailyPage(){
		return [ this.pages[0], this.pages[1] ]
	}
	
    /// ChatGPT usage: Partial
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
            return data.paths[0].map(id => data.pages[id.toString()].title); //Statement here
        })
        .catch((error) => {
            throw error;
        });
    }
}

module.exports = PageManager;