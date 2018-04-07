package com.nwagu.medmanager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PRIVATE;

public class AlarmService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private ServiceCallBacks serviceCallBacks;

    public AlarmService() {
    }

    class LocalBinder extends Binder {
        AlarmService getService() {
            return AlarmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        if((intent != null) &&
                (intent.getStringExtra(Constants.DUE_PILL_NAME) != null)) {
            if(serviceCallBacks == null) showNotification(intent.getStringExtra(Constants.DUE_PILL_NAME));
            else serviceCallBacks.updateUI();
        }
        return START_STICKY;
    }

    void showNotification(String duePillName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this/*, CHANNEL_ID*/)
                .setSmallIcon(R.drawable.not_icon)
                .setContentTitle("Med Manager reminder")
                .setContentText(duePillName)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Time to take " + duePillName))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVisibility(VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        /*if(Build.VERSION.SDK_INT >= 26) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }*/

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1, mBuilder.build());

    }

    public void setCallBacks(ServiceCallBacks callBacks) {
        serviceCallBacks = callBacks;
    }
}
