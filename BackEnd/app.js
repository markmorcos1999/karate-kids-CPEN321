var express = require("express")
var https = require('https');

var express = require('express');
var app = express();
var serv = require('http').Server(app);

var app = express();
var fs = require('fs');

app.use(express.json())


//Start the server on a port
serv.listen(8012);
console.log("Server started.");

var SOCKET_LIST = {};

//Creating the io object (to help us talk with the sockets) web sockets, fast connection 
var io = require('socket.io')(serv,{});


//Setting up what to do when someone connects
io.sockets.on('connection', function(socket){
    
    //Give it an ID and add it to the list
	//console.log('socket connection');
	socket.id = Math.random();
	SOCKET_LIST[socket.id] = socket;
	
	console.log("Connected");
	
    //Determine what to do when it disconnects
    socket.on('disconnect', function(){
        delete Player.list[socket.id];
		delete SOCKET_LIST[socket.id];
	});
	
	socket.on('test', function(data){
       console.log(data.message);
	   socket.emit('response', {});
    });
	
    
});
