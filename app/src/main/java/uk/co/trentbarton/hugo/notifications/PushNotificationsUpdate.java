package uk.co.trentbarton.hugo.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import uk.co.trentbarton.hugo.dataholders.HttpDataParams.SendPushTokenParams;
import uk.co.trentbarton.hugo.datapersistence.HugoPreferences;
import uk.co.trentbarton.hugo.tasks.DataRequestTask;

public class PushNotificationsUpdate extends FirebaseMessagingService {

    private static final String TAG = PushNotificationsUpdate.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sendRegistrationToServer(s);
    }



    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]


    /*
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]*/

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    public void sendRegistrationToServer(String token) {

        Log.d(TAG, "Server token:" + token);
        HugoPreferences.setPushToken(this,token);
        HugoPreferences.setPushSent(PushNotificationsUpdate.this, false);
        SendPushTokenParams params = new SendPushTokenParams(this);
        params.setPushToken(token);
        DataRequestTask task = new DataRequestTask(params);
        task.setOnTaskCompletedListener(result -> {
            if(result){
                Log.d(TAG, "Token successfully sent to server");
                HugoPreferences.setPushSent(PushNotificationsUpdate.this, true);
            }else{
                Log.d(TAG, "Token update to hugo servers failed because " + task.getErrorMessage());
            }
        });
        task.execute(this);

    }


}
