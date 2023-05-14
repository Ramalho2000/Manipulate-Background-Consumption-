package com.example.challenge1;

import static java.lang.String.*;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;


public class MyPeriodicTaskWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";

    private static final SimpleDateFormat formatter = new SimpleDateFormat( "HH:mm:ss.SSS", Locale.US);
    static {
        formatter.setTimeZone(TimeZone.getDefault());
    }
    private static final String APP_NAME = "App Name"; //Insert your app name

    public MyPeriodicTaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        System.out.println("TAGS: " + getTags());

        //get Max Background Time from SharedPreferences
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("maxTimeMillis", Context.MODE_PRIVATE);
        long TaskEndTime = prefs.getLong("maxTimeMillis", 0);

        SendNotification(APP_NAME + ": Background Activity", "Current time: " + formatter.format(new Date(System.currentTimeMillis()))
                + "\nMax Background Time: " + formatter.format(new Date(TaskEndTime))
                + "\nIs App Running: " + isAppRunning());

        //IF (The Background Time is exceeded)
        if(System.currentTimeMillis() > TaskEndTime) {
            //Return a failure result "Time Exceeded"
            SendNotification(APP_NAME + ": No more notifications", "WorkRequest deleted");

            WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag(getClass().getName());

            Data failureData = new Data.Builder().putString("failure_reason", "Time Exceeded").build();
            return Result.failure(failureData);
        }

        //IF (App is Running)
        if(isAppRunning()) {
            //Return a failure result "App Running"
            Data failureData = new Data.Builder().putString("failure_reason", "App Running").build();
            return Result.failure(failureData);
        }


        //get the start time and Notify
        long startTask = System.currentTimeMillis();
        SendNotification(APP_NAME + ": Start Task", "Time: " + formatter.format(new Date(startTask)));

        //do the Work
        while(System.currentTimeMillis() < TaskEndTime) {
            bubbleSort();
        }

        //cancel all workers with this tag
        WorkManager.getInstance(getApplicationContext()).cancelAllWorkByTag(getClass().getName());

        //get the end time, the duration and Notify
        long endTask = System.currentTimeMillis();
        long durationInMillis = endTask - startTask;
        String FormattedDuration = format(Locale.US, "%02d:%02d:%02d.%03d", durationInMillis / (60 * 60 * 1000), (durationInMillis / (60 * 1000)) % 60, (durationInMillis / 1000) % 60, durationInMillis % 1000);
        SendNotification(APP_NAME + ": End Task", "Time: " + formatter.format(new Date(endTask)) + "\nTime Elapsed: " + FormattedDuration);

        // Return the result of the work "Success"
        Data outputData = new Data.Builder().putString(WORK_RESULT, "Success").build();
        return Result.success(outputData);

    }

    public void bubbleSort(){
        int i, n = 10000; //lenght of arr
        int[] arr = new int[n];
        int min = 0;
        int max = 100000;

        //fill array "arr" with random numbers
        for (i = 0; i < arr.length; i++)
            arr[i] = ThreadLocalRandom.current().nextInt(min, max + 1);

        for (i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr[j] > arr[j + 1]) {
                    // swap arr[j+1] and arr[j]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
    }

    private boolean isAppRunning() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setVisible", Context.MODE_PRIVATE);
        return prefs.getBoolean("setVisible", false);

    }

    private void SendNotification(String NotificationTitle, String NotificationText){

        //get context
        Context context = this.getApplicationContext();

        // Create an intent to open the app when the notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and higher
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("My Notification Channel Description");
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        notificationManager.createNotificationChannel(channel);

        // Create a notification with a title, message, and icon
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.frog)
                .setContentTitle(NotificationTitle)
                .setContentText(NotificationText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(NotificationText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // Show the notification
        int notificationId = (int)(Math.random()*1000 + 1);
        notificationManager.notify(notificationId, builder.build());

        System.out.println("Title: " + NotificationTitle + "\nText: " + NotificationText);
    }
}
