const leaderboardDB = require('./PlayerManager');

db = new leaderboardDB();
db.connect().then(() => {
    db.createNewPlayer(300, 'Julian');
    db.getTopPlayers().then(players => console.log(players));
});