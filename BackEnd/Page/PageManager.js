const wiki = require('wikijs').default;


class PageManager {
    constructor() {
        this.pageCache = new Map();
    }

    async getRandomPage() {
        return await wiki().random();
    }

    /**
     * Get a page from the cache or from wikipedia.
     *
     * @param {string} title - The title of the page to get
     * 
     * @return {Page} - The page object
     * 
     * @throws {Error} - If the page does not exist
     */
    async getPage(title) {
        if (this.pageCache.has(title)) {
            return this.pageCache.get(title);
        } else {
            let page = new Page(
                title, 
                await wiki().page(title).then(page => page.links())
            );

            console.log(page);

            this.pageCache.set(title, page);
            return page;
        }
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
        let pathQueue = [new SearchNode(await this.getPage(startTitle), null)];
        let visited = new Set();

        while (pathQueue.length > 0) {
            let currentNode = pathQueue.shift();

            if (currentNode.page.title === endTitle) {
                return this.backtrack(currentNode);
            }

            visited.add(currentNode.page.title);

            for (let childTitle of currentNode.page.children) {
                if (!visited.has(childTitle)) {
                    let childNode = new SearchNode(await this.getPage(childTitle), currentNode);
                    pathQueue.push(childNode);
                }
            }
        }

        throw new Error('No path found');
    }

    /**
     * Returns an array of page titles representing the path from the given node to the root node.
     * 
     * @param {Node} node - The node to start the backtrack from.
     * 
     * @returns {string[]} - An array of page titles representing the path from the given node to the root node.
     */
    backtrack(node) {
        let path = [];
        let currentNode = node;

        while (currentNode) {
            path.push(currentNode.page.title);
            currentNode = currentNode.parent;
        }

        return path.reverse();
    }
}

class Page {
    constructor(title, children) {
        this.title = title;
        this.children = children;
    }
}

class SearchNode {
    constructor(Page, parent) {
        this.page = Page;
        this.parent = parent;
    }
}

module.exports = PageManager;