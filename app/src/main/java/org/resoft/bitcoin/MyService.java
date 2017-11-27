package org.resoft.bitcoin;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;
import org.resoft.bitcoin.callbacks.GeneralCallbacks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service implements GeneralCallbacks {
    private final IBinder musicBind = new MusicBinder();
    private RemoteViews bigViews;
    private Notification status;
    private Api api;
    private SessionManager session;
    private double last;
    private Timer timer;


    public void onCreate(){
        super.onCreate();
        session = new SessionManager(getApplicationContext());
        api = new Api(getApplicationContext());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                api.get("https://www.paribu.com/ticker", new HashMap<String, String>(), MyService.this);
            }

        }, 0, 1000*60);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent.getAction().equals(DataManager.STARTFOREGROUND_ACTION)) {
                showNotification();
            }else if (intent.getAction().equals(DataManager.close)) {
                onDestroy();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return START_STICKY;
    }


    public class MusicBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        //player.stop();
        //player.release();
        return false;
    }

    public void showNotification() {
        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);


        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(DataManager.main);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        if(last<session.getAlarm()){
            bigViews.setTextViewText(R.id.btc1, "1 BTC: "+last+" TL , Alarm: "+session.getAlarm());
        }else{
            bigViews.setTextViewText(R.id.btc1, "1 BTC: "+last+" TL");
        }

        bigViews.setTextViewText(R.id.btc2, session.getBtc()+" BTC: "+session.getBtc()*last+" TL");
        bigViews.setTextViewText(R.id.date, timeStamp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            status = new Notification.Builder(getApplicationContext()).build();



            status.contentView = bigViews;
            status.bigContentView = bigViews;
            status.flags = Notification.FLAG_ONGOING_EVENT;
            status.icon = R.drawable.ic_launcher_background;
            status.contentIntent = pendingIntent;
            status.priority = Notification.PRIORITY_MAX;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }

    }

    @Override
    public void VolleyResponse(JSONObject data) throws JSONException {
        last = data.getJSONObject("BTC_TL").getDouble("last");
        showNotification();
    }

    @Override
    public void VolleyError() {

    }
}
