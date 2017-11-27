package org.resoft.bitcoin;

/**
 * Created by onuragtas on 15.07.2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by onuragtas on 5.02.2017.
 */

public class SessionManager {

    private final Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        editor = preferences.edit();
    }



    public void setBtc(float btc){
        editor.putFloat("btc", btc).commit();
    }
    public float getBtc(){
        return preferences.getFloat("btc", 0);
    }

    public void setAlarm(float btc){
        editor.putFloat("alarm", btc).commit();
    }
    public float getAlarm(){
        return preferences.getFloat("alarm", 0);
    }

    public void destroy(){
        editor.putFloat("btc", 0);
        editor.putInt("alarm", 0);
        editor.clear();
        editor.commit();
    }

}