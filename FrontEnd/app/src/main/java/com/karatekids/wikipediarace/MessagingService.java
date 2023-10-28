package com.karatekids.wikipediarace;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    final static String TAG = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            ContextCompat.getMainExecutor(InGameActivity.gameContext).execute(() -> {
                AlertDialog.Builder  builder = new AlertDialog.Builder(InGameActivity.gameContext);
                builder.setTitle("Game Over! You lost.");
                builder.setMessage("Do you want to end the game now and see your results?");
                builder.setCancelable(false);
                builder.setPositiveButton("End Game", (DialogInterface.OnClickListener) (dialog, which) -> {
                    InGameActivity.endGame(InGameActivity.gameContext);
                });
                builder.setNegativeButton("Continue playing current game for second", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });
        }
    }
}
