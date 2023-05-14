# Manipulate-Background-Consumption-
This repository presents how to manipulate an app to consume in the background.

To reach our main objective, which is to consume battery in the background, we used the PeriodicWorkRequest.

## The PeriodicWorkerRequest

The class which allows all is named MyPeriodicTaskWorker.java, which can be inserted into any app with little effort. Here you can modify the job performed in the background; we choose the BubbleSort algorithm, known for its high consumption.

## The MainActivity

The MainActivity presented in MainActivity.java is capable of scheduling the periodic worker class. The maximum background time can be changed by altering a variable. Moreover, we changed the methods OnPause() and OnResume() to allow the app to only consume in the background. Those methods inform the PeriodicTaskRequest that it should stop or resume, depending on whether the app is in the foreground.
