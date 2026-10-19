package com.doyou.app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import org.json.JSONObject;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL="do_you_tasks", PREF="reminder_registry";
    static void ensureChannel(Context c){
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel ch=new NotificationChannel(CHANNEL,"Task reminders",NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Due tasks and upcoming reminders");
            NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(ch);
        }
    }
    static PendingIntent pending(Context c,String id,String title){
        Intent i=new Intent(c,AlarmReceiver.class);i.putExtra("id",id);i.putExtra("title",title);
        return PendingIntent.getBroadcast(c,id.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void schedule(Context c,String id,String title,long millis){
        if(millis<=System.currentTimeMillis())return;
        try{
            AlarmManager alarm=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
            alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,millis,pending(c,id,title));
            JSONObject record=new JSONObject();record.put("title",title);record.put("at",millis);
            c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString(id,record.toString()).apply();
        }catch(Exception ignored){}
    }
    static void cancelAll(Context c){
        SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        AlarmManager alarm=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        for(String id:p.getAll().keySet())alarm.cancel(pending(c,id,""));
        p.edit().clear().apply();
    }
    static void restore(Context c){
        Map<String,?> data=c.getSharedPreferences(PREF,Context.MODE_PRIVATE).getAll();
        for(Map.Entry<String,?> entry:data.entrySet()){
            try{JSONObject obj=new JSONObject(String.valueOf(entry.getValue()));
                if(obj.getLong("at")>System.currentTimeMillis())schedule(c,entry.getKey(),obj.getString("title"),obj.getLong("at"));
            }catch(Exception ignored){}
        }
    }
    @Override public void onReceive(Context c,Intent intent){
        if(intent==null)return;
        String id=intent.getStringExtra("id"),title=intent.getStringExtra("title");
        if(id==null)return;
        c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().remove(id).apply();
        ensureChannel(c);
        if(Build.VERSION.SDK_INT>=33&&c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)return;
        Intent launch=new Intent(c,MainActivity.class);launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent content=PendingIntent.getActivity(c,0,launch,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,CHANNEL):new Notification.Builder(c);
        b.setSmallIcon(com.doyou.app.R.drawable.ic_launcher).setContentTitle("Do You · Reminder").setContentText(title==null?"Time for your task":title).setAutoCancel(true).setContentIntent(content).setColor(Color.rgb(103,87,232));
        NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id.hashCode(),b.build());
    }
}
