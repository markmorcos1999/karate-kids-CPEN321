const MatchMaker = require('./Matchmaker');

const mm = new MatchMaker()

mm.findMatch(2, 200).then((res) => console.log(res));
mm.findMatch(3, 200).then((res) => console.log(res));
mm.findMatch(1, 100).then((res) => console.log(res));
mm.findMatch(4, 400).then((res) => console.log(res));
mm.findMatch(6, 3000).then((res) => console.log(res), (rej) => console.log("Could not find a match :("));

setTimeout(() => {
    mm.findMatch(2, 200).then((res) => console.log(res));
    mm.findMatch(3, 200).then((res) => console.log(res));
    mm.findMatch(1, 100).then((res) => console.log(res));
    mm.findMatch(4, 400).then((res) => console.log(res));
    mm.findMatch(5, 500).then((res) => console.log(res));
    mm.findMatch(6, 10000).then((res) => console.log(res));
}, 12000);


