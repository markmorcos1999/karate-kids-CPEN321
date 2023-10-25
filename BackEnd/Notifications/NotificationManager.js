const admin = require('firebase-admin');
const serviceAccount = require('./your-service-account-key.json'); // Replace with your own service account key

class FCMNotifier {
  constructor() {
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
      databaseURL: 'https://your-firebase-database-url.firebaseio.com',
    });

    this.messaging = admin.messaging();
  }

  // Send an FCM notification to a specific device token
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

      return true; // Notification sent successfully
    } catch (error) {
      console.error('Error sending notification:', error);
      return false; // Failed to send the notification
    }
  }
}

module.exports = FCMNotifier;
