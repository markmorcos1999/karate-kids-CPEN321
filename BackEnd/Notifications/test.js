const notificationManager = require('./NotificationManager');

let nm = new notificationManager();

nm.sendNotificationToDevice('dhr-frYDT16wu0AlIPDknV:APA91bHKNT3iywnREAfeeUPukbudiWSfwY-V1LKCzNg4NS0iTlJB1tU40nQM7HPzDdIIsi08_JrI3IRsohFw7y8ZRks4aavycJsYGTfBS5i0uYBr8Hpy8NmOkgj5AaVI_GyHU9W5u_A2', 'test', 'Hi again, other Mark')
    .then((success) => console.log("successful: " + success));
