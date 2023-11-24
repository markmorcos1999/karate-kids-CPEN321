const admin = require('firebase-admin');

//Moved to a secure location, cannot be accessed without higher privileges.
const serviceAccount = require(process.env.FIREBASE_ACCOUNT_KEY_PATH);

class FCMNotifier {
    // ChatGPT usage: Partial
    constructor() {
        admin.initializeApp({
            credential: admin.credential.cert(serviceAccount)
        });
		console.log(admin);
		console.log(admin.messaging);
		console.log(admin.messaging());
        this.messaging = admin.messaging();
    }

    // ChatGPT usage: Yes
    async sendNotificationToDevice(deviceToken, title, body) {
        try {
            const message = {
                notification: {
                    title,
                    body,
                },
                token: deviceToken,
            };
			console.log("SENDING MESSAGE")
			console.log(message)
            const response = await this.messaging.send(message);
			console.log(response)
            return true;
        } catch (error) {
            console.error('Error sending notification:', error);
            return false;
        }
    }	
}

module.exports = FCMNotifier;
