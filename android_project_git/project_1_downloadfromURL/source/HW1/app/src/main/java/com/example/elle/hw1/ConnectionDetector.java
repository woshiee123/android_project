package com.example.elle.hw1;

/**
 * Created by Elle on 9/28/14.
 */
//This class can help user to check if there is net connected;
import  android.content.Context;
import  android.net.ConnectivityManager;
import  android.net.NetworkInfo;

public class ConnectionDetector {
    private Context _context;
    public ConnectionDetector(Context context){
        this. _context=context;
    }
    public  boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
         return false;


        }


    }




