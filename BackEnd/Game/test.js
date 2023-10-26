const MatchMaker = require('./Matchmaker');

MatchMaker.findMatch(2, 200).then((res) => console.log(res));
MatchMaker.findMatch(3, 200).then((res) => console.log(res));
MatchMaker.findMatch(1, 100).then((res) => console.log(res));
MatchMaker.findMatch(4, 400).then((res) => console.log(res));
MatchMaker.findMatch(6, 3000).then((res) => console.log(res), (rej) => console.log("Could not find a match :("));

setTimeout(() => {
    MatchMaker.findMatch(2, 200).then((res) => console.log(res));
    MatchMaker.findMatch(3, 200).then((res) => console.log(res));
    MatchMaker.findMatch(1, 100).then((res) => console.log(res));
    MatchMaker.findMatch(4, 400).then((res) => console.log(res));
    MatchMaker.findMatch(5, 500).then((res) => console.log(res));
    MatchMaker.findMatch(6, 10000).then((res) => console.log(res));
}, 12000);


