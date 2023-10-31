const playerManager = require('./PlayerManager');

db = new playerManager();
db.connect().then(async () => {
    await db.createNewPlayer(300, 'Julian');

    if (!(await db.playerExists(300))) {
        console.log("FAILURE");
    }

    if (await db.playerExists(34234123)) {
        console.log("FAILURE");
    }

    if ((await db.getPlayerInfo(300)).elo != 0) {
        console.log("FAILURE");
    }

    await db.updatePlayer(300, 100, 1, 0, 20, 29);

    if ((await db.getPlayerInfo(300)).avgGameDuration != 20) {
        console.log("FAILURE");
    }

    if ((await db.getTopPlayers()).length != 1) {
        console.log("FAILURE");
    }

    console.log("Test completed");
});