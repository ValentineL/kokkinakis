package com.kokkinakis.service;

import java.util.Calendar;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kokkinakis.service.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
 
public class GCMNotificationIntentService extends IntentService {
 
  public static final int NOTIFICATION_ID = 1;
  private NotificationManager mNotificationManager;
  NotificationCompat.Builder builder;
 
  public GCMNotificationIntentService() {
    super("GcmIntentService");
  }
 
  public static final String TAG = "GCMNotificationIntentService";
 
  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
 
    String messageType = gcm.getMessageType(intent);
 
    if (!extras.isEmpty()) {
      if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
          .equals(messageType)) {
        sendNotification("Send error: " + extras.toString());
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
          .equals(messageType)) {
        sendNotification("Deleted messages on server: "
            + extras.toString());
      } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
          .equals(messageType)) {
 
        for (int i = 0; i < 3; i++) {
          Log.i(TAG,
              "Working... " + (i + 1) + "/5 @ "
                  + SystemClock.elapsedRealtime());
          try {
            Thread.sleep(5000);
          } catch (InterruptedException e) {
          }
 
        }
        Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
        Log.i(TAG, "Received: " + extras.toString());
        
        String flag=new String(extras.get("flag").toString());
 
       if(flag.equals("0"))
          {//sendNotification("GCM: " + extras.get(profile.MESSAGE_KEY));
           sendNotification(extras.get("m").toString()); //το tag μπορει να αλλαξει απο τον server
          }
       else if(flag.equals("1"))
         {
        	String date=new String(extras.get("date").toString());
        	String time=new String(extras.get("time").toString());
        	String brand=new String(extras.get("brand").toString());
        	String model=new String(extras.get("model").toString());
        	String message=new String(extras.get("m").toString());
        	
        	Calendar begintime=Calendar.getInstance();
        	Calendar endtime=Calendar.getInstance();
      
        	String[] separ_time=time.split(":");
        	String[] separ_date=date.split("/");
        	
        	
        	begintime.set(Integer.parseInt(separ_date[2]), Integer.parseInt(separ_date[1]), Integer.parseInt(separ_date[0]), Integer.parseInt(separ_time[0]), Integer.parseInt(separ_time[1]));
        	endtime.set(Integer.parseInt(separ_date[2]), Integer.parseInt(separ_date[1]), Integer.parseInt(separ_date[0]), (Integer.parseInt(separ_time[0])+1), Integer.parseInt(separ_time[1]));

        	long beginmillis = begintime.getTimeInMillis();
        	long endmillis = endtime.getTimeInMillis();
        	
        	Intent cal_intent = new Intent(Intent.ACTION_INSERT);
        	cal_intent.setData(Events.CONTENT_URI);
        	cal_intent.putExtra(Events.TITLE, "Ραντεβου Κοκκινακης");
        	cal_intent.putExtra(Events.EVENT_LOCATION, "Συνεργειο Κοκκινακη");
        	cal_intent.putExtra(Events.DESCRIPTION, "Οχημα:"+brand+" "+model+"\n"+message);
        	cal_intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginmillis);
        	cal_intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endmillis); //μια ωρα σε χιλιοστα του δευτερολεπτου
        	cal_intent.putExtra(Events.ALL_DAY, false);
        	cal_intent.putExtra(Events.HAS_ALARM, true);
        	cal_intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        	cal_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//αλλιως κρασαρει με το μηνυμα:Calling startActivity() from outside of an Activity context requires the FLAG_ACTIVITY_NEW_TASK flag
        	
        	startActivity(cal_intent);
        	
        	
        	sendNotification("Το ραντεβου σας εγκριθηκε!"); 
         }
        
      }
    }
    GcmBroadcastReceiver.completeWakefulIntent(intent);
  }
 
  private void sendNotification(String msg) {
    Log.d(TAG, "Preparing to send notification...: " + msg);
    mNotificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);
 
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, MainActivity.class), 0);
 
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
        this).setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("Κοκκινακης")
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
        .setContentText(msg);
    
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    mBuilder.setSound(alarmSound);
    
    mBuilder.setContentIntent(contentIntent);
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    Log.d(TAG, "Notification sent successfully.");
  }
}