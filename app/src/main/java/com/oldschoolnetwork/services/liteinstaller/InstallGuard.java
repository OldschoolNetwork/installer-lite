package com.oldschoolnetwork.services.liteinstaller;
import android.app.*;import android.content.*;import android.os.*;import com.oldschoolnetwork.lite.ProgressNotice;
public final class InstallGuard extends Service {
 public IBinder onBind(Intent i){return null;}
 public int onStartCommand(Intent i,int flags,int id){startForeground(829,ProgressNotice.build(this,"Preparing installation…",0,0));return START_NOT_STICKY;}
 public static void update(Context c,String text){((NotificationManager)c.getSystemService(NOTIFICATION_SERVICE)).notify(829,ProgressNotice.build(c,text,0,0));}
 public void onDestroy(){stopForeground(true);super.onDestroy();}
}
