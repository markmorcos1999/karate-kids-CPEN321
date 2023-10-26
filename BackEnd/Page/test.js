const PageManager = require('./PageManager.js');

let manager = new PageManager();
manager.getRandomPages().then(res => {
    manager.getShortestPath(res[0].title, res[1].title).then(res3 => {
        console.log(res3);
    });
});