package org.resoft.bitcoin;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.resoft.bitcoin.callbacks.GeneralCallbacks;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by onuragtas on 15.07.2017.
 */

public class Api {

    private final RequestQueue volley;
    private Context context;
    public Api(Context context){
        context = context;
        volley = Volley.newRequestQueue(context);
    }

    public void post(String api_url, final HashMap<String, String> params, final GeneralCallbacks callback){
        final StringRequest request_json = new StringRequest(Request.Method.POST,api_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.VolleyResponse(new JSONObject(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                callback.VolleyError();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volley.add(request_json);
    }
    public void get(String api_url, final HashMap<String, String> params, final GeneralCallbacks callback){
        StringRequest request_json = new StringRequest(Request.Method.GET,api_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.VolleyResponse(new JSONObject(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        request_json.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        volley.add(request_json);
    }
}
