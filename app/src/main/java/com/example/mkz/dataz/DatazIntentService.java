package com.example.mkz.dataz;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.FileOutputStream;


public class DatazIntentService extends IntentService {

    public long rxbytes;
    public long txbytes;
    public long totalbytes;

    String savedata;

    public DatazIntentService() {
        super("DatazIntentService");
    }

    Handler hd1= new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            rxbytes = TrafficStats.getMobileRxBytes();
            txbytes = TrafficStats.getMobileTxBytes();
            totalbytes = rxbytes+txbytes;

            String s;
            if(rxbytes == TrafficStats.UNSUPPORTED || txbytes == TrafficStats.UNSUPPORTED)
            {
                s="Unsupported";
            }
            else
            {
                s=String.valueOf(totalbytes);
            }

            s = String.valueOf(totalbytes);

            if(s!="0")
            {
                savedata = s;
                try
                {
                    String FILENAME = "Datazdata.txt";

                    FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);

                    byte buf[] = savedata.getBytes();

                    fileOutputStream.write(buf);
                    fileOutputStream.close();
                }
                catch (Exception e)
                {

                }
            }

        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    try {
                        Thread.sleep(1000);
                        hd1.sendEmptyMessage(0);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread th = new Thread(r);
        th.start();

        return START_STICKY;
    }

}
