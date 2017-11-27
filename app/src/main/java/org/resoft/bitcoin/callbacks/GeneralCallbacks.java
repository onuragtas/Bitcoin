package org.resoft.bitcoin.callbacks;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by onuragtas on 15.07.2017.
 */

public interface GeneralCallbacks {
    void VolleyResponse(JSONObject data) throws JSONException;

    void VolleyError();
}

