package com.example.sriramramineni.routing_sample;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sri Ram Ramineni on 9/1/2015.
 */
public class GeoFenceTransitionsIntentService extends BroadcastReceiver {
    Context context;
    public static final String TAG = "GeoFenceService";
    Intent broadcastIntent = new Intent();


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);;
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }


        int geoFenceTrans = geofencingEvent.getGeofenceTransition();

        if(geoFenceTrans == Geofence.GEOFENCE_TRANSITION_ENTER || geoFenceTrans == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringPoints = geofencingEvent.getTriggeringGeofences();

            String geoFenceTransDetails = getGeoFenceTransDetails(context,geoFenceTrans,triggeringPoints);

            sendNotification(geoFenceTransDetails);
            rollup(geoFenceTransDetails);
        }
        else{
            Log.e(TAG, context.getString(R.string.geofence_transition_invalid_type, geoFenceTrans));
        }
    }


    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return context.getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return context.getString(R.string.geofence_transition_exited);
            default:
                return context.getString(R.string.unknown_geofence_transition);
        }
    }

    private String getGeoFenceTransDetails(Context context, int geoFenceTrans, List<Geofence> triggeringPoints) {

        String geoFenceTransitionString = getTransitionString(geoFenceTrans);

        List triggeringPointsIdsList = new ArrayList();
        for (Geofence geofence :triggeringPoints){
            triggeringPointsIdsList.add(geofence.getRequestId());
        }

        String triggeringPointsIdsString = TextUtils.join(":", triggeringPointsIdsList);


        return geoFenceTransitionString+": "+triggeringPointsIdsString;
    }
    private void rollup(String s){
        Log.i("Here","roll up");
        s = s.substring(18);
        Intent intent = new Intent("BroadCast");
        intent.putExtra("Current GeoFence",s);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_historyy)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_historyy))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(context.getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        this.context = context;
//
//        broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);
//
//        if (LocationClient.hasError(intent)) {
//            handleError(intent);
//        } else {
//            handleEnterExit(intent);
//        }
//    }
//
//    private void handleError(Intent intent){
//        // Get the error code
//        int errorCode = LocationClient.getErrorCode(intent);
//
//        // Get the error message
//        String errorMessage = LocationServiceErrorMessages.getErrorString(
//                context, errorCode);
//
//        // Log the error
//        Log.e(GeofenceUtils.APPTAG,
//                context.getString(R.string.geofence_transition_error_detail,
//                        errorMessage));
//
//        // Set the action and error message for the broadcast intent
//        broadcastIntent
//                .setAction(GeofenceUtils.ACTION_GEOFENCE_ERROR)
//                .putExtra(GeofenceUtils.EXTRA_GEOFENCE_STATUS, errorMessage);
//
//        // Broadcast the error *locally* to other components in this app
//        LocalBroadcastManager.getInstance(context).sendBroadcast(
//                broadcastIntent);
//    }
//
//
//    private void handleEnterExit(Intent intent) {
//        // Get the type of transition (entry or exit)
//        int transition = LocationClient.getGeofenceTransition(intent);
//
//        // Test that a valid transition was reported
//        if ((transition == Geofence.GEOFENCE_TRANSITION_ENTER)
//                || (transition == Geofence.GEOFENCE_TRANSITION_EXIT)) {
//
//            // Post a notification
//            List<Geofence> geofences = LocationClient
//                    .getTriggeringGeofences(intent);
//            String[] geofenceIds = new String[geofences.size()];
//            String ids = TextUtils.join(GeofenceUtils.GEOFENCE_ID_DELIMITER,
//                    geofenceIds);
//            String transitionType = GeofenceUtils
//                    .getTransitionString(transition);
//
//            for (int index = 0; index < geofences.size(); index++) {
//                Geofence geofence = geofences.get(index);
//                ...do something with the geofence entry or exit. I'm saving them to a local sqlite db
//
//            }
//            // Create an Intent to broadcast to the app
//            broadcastIntent
//                    .setAction(GeofenceUtils.ACTION_GEOFENCE_TRANSITION)
//                    .addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES)
//                    .putExtra(GeofenceUtils.EXTRA_GEOFENCE_ID, geofenceIds)
//                    .putExtra(GeofenceUtils.EXTRA_GEOFENCE_TRANSITION_TYPE,
//                            transitionType);
//
//            LocalBroadcastManager.getInstance(MyApplication.getContext())
//                    .sendBroadcast(broadcastIntent);
//
//            // Log the transition type and a message
//            Log.d(GeofenceUtils.APPTAG, transitionType + ": " + ids);
//            Log.d(GeofenceUtils.APPTAG,
//                    context.getString(R.string.geofence_transition_notification_text));
//
//            // In debug mode, log the result
//            Log.d(GeofenceUtils.APPTAG, "transition");
//
//            // An invalid transition was reported
//        } else {
//            // Always log as an error
//            Log.e(GeofenceUtils.APPTAG,
//                    context.getString(R.string.geofence_transition_invalid_type,
//                            transition));
//        }
//    }
//
//    /**
//     * Posts a notification in the notification bar when a transition is
//     * detected. If the user clicks the notification, control goes to the main
//     * Activity.
//     *
//     * @param transitionType
//     *            The type of transition that occurred.
//     *
//     */
//    private void sendNotification(String transitionType, String locationName) {
//
//        // Create an explicit content Intent that starts the main Activity
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//
//        // Construct a task stack
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//
//        // Adds the main Activity to the task stack as the parent
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack
//        PendingIntent notificationPendingIntent = stackBuilder
//                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions
//        // >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                context);
//
//        // Set the notification contents
//        builder.setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle(transitionType + ": " + locationName)
//                .setContentText(
//                        context.getString(R.string.geofence_transition_notification_text))
//                .setContentIntent(notificationPendingIntent);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//    }
}
