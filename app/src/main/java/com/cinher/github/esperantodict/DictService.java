package com.cinher.github.esperantodict;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DictService extends Service {
    public DictService() {

    }

    @Override
    public void onCreate(){
        Log.i("DictService","Dict Service Started");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId){
        Log.i("DictService","Dict Service Started");
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
