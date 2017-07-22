package com.example.mkz.dataz;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class DatazIntentService extends IntentService {

    public long rxbytes;
    public long txbytes;
    public long totalbytes;
    public long used;
    public long initial;
    public long DataPack;
    public long remaining;

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
            used = totalbytes-initial;          //          Used data from last recharge
            String s;
            if(rxbytes == TrafficStats.UNSUPPORTED || txbytes == TrafficStats.UNSUPPORTED)
            {
                s="Unsupported";
            }
            else
            {
                s=String.valueOf(totalbytes);
            }

            s = String.valueOf(used);

            if(s!="0")
            {
                savedata = s;
                try
                {
                    String FILENAME = "Used.txt";

                    FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);

                    byte buf[] = savedata.getBytes();

                    fileOutputStream.write(buf);
                    fileOutputStream.close();
                }
                catch (Exception e)
                {

                }
                try
                {
                    remaining = DataPack-used;
                    String s1 = String.valueOf(used);
                    String FILENAME = "Remaining.txt";

                    FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);

                    byte buf[] = s1.getBytes();

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

        try
        {
            String FILENAME = "Initial.txt";      // File contains Initial Rx+Tx value at the time of recharge
            FileInputStream fis = openFileInput(FILENAME);

            int read=-1;
            StringBuffer buffer = new StringBuffer();
            while((read=fis.read())!=-1)
            {
                buffer.append((char)read);
            }
            String remaining = (buffer.toString());     // Read value stored in this String
            initial = Long.parseLong(remaining);
            fis.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            String FILENAME = "RechargeData.txt";      // File contains Initial Rx+Tx value at the time of recharge
            FileInputStream fis = openFileInput(FILENAME);

            int read=-1;
            StringBuffer buffer = new StringBuffer();
            while((read=fis.read())!=-1)
            {
                buffer.append((char)read);
            }
            String pack = (buffer.toString());     // Read value stored in this String
            DataPack = Long.parseLong(pack);
            fis.close();
        }
        catch(Exception e)
        {

        }





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
