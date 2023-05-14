package com.example.challenge1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;

import androidx.work.WorkManager;


import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
        Your code here
        */
      
        initBackgroundTasks();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when the app is paused, the app can start the background tasks
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setVisible", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("setVisible", false).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //when the app is in the foreground, the background tasks must stop
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setVisible", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("setVisible", true).apply();
    }

    public void initBackgroundTasks(){
        Context context = getApplicationContext();

        int backgroundMaxTimeMinutes = 30;  //Max time that the app can stay in the background
        long minutesToMillis = 60 * 1000;
        long maxTimeMillis = System.currentTimeMillis() + (backgroundMaxTimeMinutes * minutesToMillis);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("maxTimeMillis", Context.MODE_PRIVATE);

        prefs.edit().putLong("maxTimeMillis", maxTimeMillis).apply();
        System.out.println(prefs.getLong("maxTimeMillis", 0));

        long repeatIntervalMinutes = 15L;
        PeriodicWorkRequest myTaskRequest =
                new PeriodicWorkRequest.Builder(MyPeriodicTaskWorker.class, repeatIntervalMinutes, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(context).enqueue(myTaskRequest);
    }
}
