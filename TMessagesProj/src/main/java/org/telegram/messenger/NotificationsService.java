package org.telegram.messenger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class NotificationsService
  extends Service
{
  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }
  
  public void onCreate()
  {
    FileLog.e("tmessages", "service started");
    ApplicationLoader.postInitApplication();
  }
  
  public void onDestroy()
  {
    FileLog.e("tmessages", "service destroyed");
    if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true)) {
      sendBroadcast(new Intent("org.telegram.start"));
    }
  }
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    return 1;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\NotificationsService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */