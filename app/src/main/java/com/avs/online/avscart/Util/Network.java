package com.avs.online.avscart.Util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SATHISH on 9/4/2017.
 */

public class Network extends Activity
{
    public Boolean isOnline(Context cont) {

        ConnectivityManager cm=(ConnectivityManager)cont.getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        NetworkInfo netInfo=cm.getActiveNetworkInfo();

        if(netInfo!=null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }

    }
}
