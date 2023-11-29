const admin = require('firebase-admin');
const fs = require('fs');

//Moved to a secure location, cannot be accessed without higher privileges.
const serviceAccount = JSON.parse(fs.readFileSync('./service-account-key.json'));

class FCMNotifier {
    // ChatGPT usage: Partial
    constructor() {
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount)
        });
		
        this.messaging = admin.messaging();
    }

    // ChatGPT usage: Yes
    async sendNotificationToDevice(deviceToken, title, body) {
        // This comment makes codacy happy
        try {
            const message = {
                notification: {
                    title,
                    body,
                },
                token: deviceToken,
            };

            await this.messaging.send(message);
            return true;
        } catch (error) {
            return false;
        }
    }	
}

module.exports = FCMNotifier;
