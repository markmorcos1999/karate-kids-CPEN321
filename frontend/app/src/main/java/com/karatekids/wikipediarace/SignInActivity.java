package com.karatekids.wikipediarace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignInActivity extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private final static String TAG = "SignInActivity";
    private final int RC_SIGN_IN = 1;

    //ChatGPT usage: No
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        findViewById(R.id.guest_signin_bt).setOnClickListener(new View.OnClickListener() {
            //ChatGPT usage: No
            @Override
            public void onClick(View v) {
                guestSignIn();
            }
        });
    }

    //ChatGPT usage: No
    private void guestSignIn() {
        //https://firebase.google.com/docs/cloud-messaging/android/client
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                //ChatGPT usage: No
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        int id = (int) Math.floor(1000_0000 + (Math.random() * 9000_0000));
                        Networker.serverSignIn( Integer.toString(id), "Guest", "a",SignInActivity.this);
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, msg);
                    int id = (int) Math.floor(1000_0000 + (Math.random() * 9000_0000));
                    Networker.serverSignIn(Integer.toString(id), "Guest", token,SignInActivity.this);
                }
            });
    }

    //ChatGPT usage: No
    /**
     * Handle Signin when button is clicked
     */
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //ChatGPT usage: No
    /**
     * Checks if user is signed in. If so, proceed to handleSignInResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //ChatGPT usage: No
    /**
     * If google user signin details are accessible upddate ui with them.
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            //https://firebase.google.com/docs/cloud-messaging/android/client
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        //ChatGPT usage: No
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();

                            // Log and toast
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.d(TAG, msg);
                            int id = (int) Math.floor(1000_0000 + (Math.random() * 9000_0000));
                            Networker.serverSignIn(Integer.toString(id), account.getDisplayName(), token,SignInActivity.this);
                        }
                    });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    //ChatGPT usage: No
    /**
     * Transition into SignInActivity with userName
     * @param name
     */
    public void updateUI(String name) {

        Log.d(TAG, "Display Name: " + name);

            //move to SignInActivity to display client and server data
            Intent mainActivity = new Intent(SignInActivity.this, MainActivity.class);
            mainActivity.putExtra("userName", name);
            startActivity(mainActivity);
    }
}
