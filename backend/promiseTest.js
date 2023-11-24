

var player1 = {
			id:1,
			elo:20,
			waitStartTime:30
		};

player1.matchPromise = new Promise(
	(resolve, reject) => { 
	player1.matchPromiseResolve = resolve;
	player1.matchPromiseReject = reject; 
	}
);

async function doPromise(player){
	//player.matchPromiseResolve.
}

async function rejectPromise(player){
	player.matchPromiseReject("rejected")
}
var obj = {
	json: () => {
		return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve("some bullshit");
        }, 1000);
    });
	}
}

function promise1() {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve(obj);
        }, 1000);
    });
}

function promise2(message) {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            resolve(message + ', Promise 2 resolved');
        }, 1000);
    });
}

promise1()
    .then(result => result.json())
    .then(finalResult => console.log(finalResult))
    .catch(error => console.log(error));

async function doThings(player){
	
	
	doPromise(player)
	player.matchPromise.then(
	(resolve) => console.log(resolve),
	).then(
	(resolve) => console.log(resolve)
	)
	
}

//doThings(player1)