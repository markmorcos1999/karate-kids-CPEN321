const fetch = require('node-fetch');
const wiki = require('wikijs').default;


class PageManager {
    // ChatGPT usage: Yes
    async getRandomPages(limit = 2) {
        console.log('Getting random pages...');
        
        try {
            const randomTitles = await wiki().random(limit);
            const titlePromises = randomTitles.map(async (title) => {
                var url = await wiki().page(title).then(page => page.url());
				url = url.slice(0, 10) + ".m" + url.slice(10);
                const pageInfo = { 
                    title, 
                    url 
                };

                return pageInfo;
            });
            return Promise.all(titlePromises);
        } catch (error) {
            console.error(error);
            return [];
        }
    }
	
	getDailyPage(){
		return [{title: "Taco", url: "https://en.m.wikipedia.org/wiki/Taco"},{title: "Mexico", url: "https://en.m.wikipedia.org/wiki/Mexico"}]
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
            return data.paths[0].map(id => data.pages[id.toString()].title);
        })
        .catch((error) => {
            throw error;
        });
    }
}

module.exports = PageManager;