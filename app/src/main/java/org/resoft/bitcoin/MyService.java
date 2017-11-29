package org.resoft.bitcoin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
    private double last,prev = 0;
    private Timer timer;
    MediaPlayer m = new MediaPlayer();

    public void onCreate(){
        super.onCreate();
        initPlay();
        session = new SessionManager(getApplicationContext());
        api = new Api(getApplicationContext());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                api.get("https://www.paribu.com/ticker", new HashMap<String, String>(), MyService.this);
            }

        }, 0, 1000*30);
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
            bigViews.setTextViewText(R.id.btc1, last+" TL, Alarm: "+session.getAlarm());
        }else{
            bigViews.setTextViewText(R.id.btc1, last+" TL, Kar: "+(last*session.getBtc()-session.getStart())+" TL");
        }

        bigViews.setTextViewText(R.id.btc2, session.getBtc()+" BTC: "+session.getBtc()*last+" TL");
        bigViews.setTextViewText(R.id.date, timeStamp);

        if(last > prev){
            beep2();
        }else{
            beep();
        }
        prev = last;

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

    public void initPlay(){
        try{
            AssetFileDescriptor descriptor = getAssets().openFd("beep.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.setLooping(true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void beep() {
        try {
            if(!m.isPlaying()) {
                m.start();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(m.isPlaying()) {
                m.pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beep2(){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        for (int i = 0; i<2; i++){

            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
