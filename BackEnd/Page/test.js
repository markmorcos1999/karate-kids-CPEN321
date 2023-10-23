const PageManager = require('./PageManager.js');

let manager = new PageManager();
manager.getShortestPath('Batman', 'Joker').then(res => console.log(res));
manager.getShortestPath('Nuclear fusion', 'James Bond').then(res => console.log(res));
