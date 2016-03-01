package com.uva.inertia.besilite;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by Ben on 2/27/2016.
 */
public class NetworkErrorHandlers {

    public static Response.ErrorListener toastHandler(final Context context) {
        return new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String err_msg = new String(error.networkResponse.data);
                    Log.e("ERROR", err_msg);
                    Toast toast = Toast.makeText(context, err_msg, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(context, "Unable to connect to server. Please check your internet connection", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };
    }
}
