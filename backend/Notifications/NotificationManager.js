const admin = require('firebase-admin');
const serviceAccount = require('./service-account-key.json');

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
    try {
      const message = {
        notification: {
          title,
          body,
        },
        token: deviceToken,
      };

      const response = await this.messaging.send(message);
      console.log('Successfully sent notification:', response);

      return true;
    } catch (error) {
      console.error('Error sending notification:', error);
      return false;
    }
  }
}

module.exports = FCMNotifier;
