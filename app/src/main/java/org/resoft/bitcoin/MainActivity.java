package org.resoft.bitcoin;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.resoft.bitcoin.callbacks.GeneralCallbacks;
import org.resoft.bitcoin.helpers.DBHelper;
import org.resoft.bitcoin.models.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements GeneralCallbacks {

    private SessionManager session;
    private TextView tlview;
    private Api api;
    private double last;

    private boolean bound;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MusicBinder binder = (MyService.MusicBinder) service;
            //get service
            DataManager.service = binder.getService();
            bound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
    private Intent playIntent;
    private EditText alarmedittext;
    private EditText startprice;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        api = new Api(getApplicationContext());
        db = new DBHelper(getApplicationContext());

        tlview = (TextView) findViewById(R.id.tl);

        final EditText edittext = (EditText) findViewById(R.id.edittext);
        Button save = (Button) findViewById(R.id.buton);

        alarmedittext = (EditText) findViewById(R.id.editalarm);
        Button savealarm = (Button) findViewById(R.id.alarmbuton);

        startprice = (EditText) findViewById(R.id.startprice);
        Button savestart = (Button) findViewById(R.id.savestart);


        edittext.setText(session.getBtc()+"");
        alarmedittext.setText(session.getAlarm()+"");
        startprice.setText(session.getStart()+"");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setBtc(Float.parseFloat(edittext.getText().toString()));
                api.get("https://www.paribu.com/ticker", new HashMap<String, String>(), MainActivity.this);
            }
        });

        savealarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setAlarm(Float.parseFloat(alarmedittext.getText().toString()));
            }
        });

        savestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.setStart(Float.parseFloat(startprice.getText().toString()));
            }
        });

        api.get("https://www.paribu.com/ticker", new HashMap<String, String>(), MainActivity.this);

        LineChart mChart = (LineChart) findViewById(R.id.chart);

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<>();

        ArrayList<Data> datas = db.getData();
        int i = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#");
        for (Data data : datas) {

            // turn your data into Entry objects
            xVals.add(data.getDate());
            yVals.add(new Entry((float)data.getData(),i));
            i++;
        }
    }


    @Override
    public void VolleyResponse(JSONObject data) throws JSONException {
        last = data.getJSONObject("BTC_TL").getDouble("last");
        tlview.setText(last+" - "+session.getBtc()*last+"");
    }

    @Override
    public void VolleyError() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(MainActivity.this, MyService.class);
            playIntent.setAction(DataManager.STARTFOREGROUND_ACTION);
            getApplicationContext().bindService(playIntent, connection, Context.BIND_AUTO_CREATE);
            getApplicationContext().startService(playIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getApplicationContext().unbindService(connection);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
