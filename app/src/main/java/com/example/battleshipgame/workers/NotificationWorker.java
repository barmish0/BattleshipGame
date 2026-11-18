package com.example.battleshipgame.workers;import android.content.Context;import androidx.annotation.NonNull;import androidx.work.Worker;import androidx.work.WorkerParameters;
public class NotificationWorker extends Worker {public NotificationWorker(@NonNull Context c,@NonNull WorkerParameters p){super(c,p);}@NonNull public Result doWork(){return Result.success();}}
