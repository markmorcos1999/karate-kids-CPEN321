const MatchMaker = require('./Matchmaker');

MatchMaker.addToGameWaitingList(2, 200).then((res) => console.log(res));
MatchMaker.addToGameWaitingList(3, 200).then((res) => console.log(res));
MatchMaker.addToGameWaitingList(1, 100).then((res) => console.log(res));
MatchMaker.addToGameWaitingList(4, 400).then((res) => console.log(res));
MatchMaker.addToGameWaitingList(5, 500).then((res) => console.log(res));
MatchMaker.addToGameWaitingList(6, 10000).then((res) => console.log(res));

setInterval(
    () => console.log(MatchMaker.matchPlayers()), 
    500
);


