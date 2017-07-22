package com.example.mkz.dataz;


/*


    *****       This is the "Myst Data Engine", it is basically a sticky service(a service that can't be killed, even when
    the application is not running. It also starts automatically when the device boots up.     ******
    
    This is the main thread which will be running all the time to trace data usage and keep updating the 
    following files (marked with *) :-
    
            1) *Initial.txt      =       It stores device's totalTxBytes from when myst starts data counting
            2) Recharge.txt      =       It stores the data pack's value (entered by user)
            3) *Used.txt         =       It stores used data since last recharge (Current system's TotalRxBytes - Initial)
            4) *Remaining.txt    =       It stores the remaining data (Recharge - Used)
            
            *** Note : All these files contains values in terms of bytes
             
                                                                                ---- @ Debojyoti





 */




import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Date;


public class DatazIntentService extends IntentService {

    public double rxbytes;
    public double txbytes;
    public double totalbytes;
    public double used;
    public double initial;
    public double DataPack;
    public double remaining;
    public double lastBootData;
    public String expday;

    String savedata;

    public DatazIntentService() {
        super("DatazIntentService");
    }

    Handler hd1= new Handler()
    {
        @Override
        public void handleMessage(Message msg) {




            Date d = new Date(new Date().getTime());
            String sysDate  = (String) DateFormat.format("dd", d.getTime());
            String curDate = "";
            try
            {
                String FILENAME22 = "CurrentDate.txt";      // File contains Initial Rx+Tx value at the time of recharge
                FileInputStream fis22 = openFileInput(FILENAME22);

                int read=-1;
                StringBuffer buffer = new StringBuffer();
                while((read=fis22.read())!=-1)
                {
                    buffer.append((char)read);
                }
                curDate = (buffer.toString());     // Read value stored in this String

                fis22.close();
            }
            catch (Exception e)
            {

            }
            if(!sysDate.equals(curDate))
            {
                int sd,cd,diff;
                String savedDays="";
                sd = Integer.parseInt(sysDate);
                cd = Integer.parseInt(curDate);

                diff=sd-cd;

                try
                {
                    String FILENAME = "ExpDate.txt";      // File contains Initial Rx+Tx value at the time of recharge
                    FileInputStream fis = openFileInput(FILENAME);

                    int read=-1;
                    StringBuffer buffer = new StringBuffer();
                    while((read=fis.read())!=-1)
                    {
                        buffer.append((char)read);
                    }
                    savedDays = (buffer.toString());     // Read value stored in this String

                    fis.close();
                }
                catch (Exception e)
                {

                }

                try
                {

                    String FILENAME33 = "ExpDate.txt";

                    FileOutputStream fileOutputStream33 = openFileOutput(FILENAME33,MODE_PRIVATE);

                    byte buf[] = String.valueOf(Integer.parseInt(savedDays)-diff).getBytes();

                    fileOutputStream33.write(buf);
                    fileOutputStream33.close();
                }
                catch (Exception e)
                {

                }

            }
            Date d1 = new Date(new Date().getTime());
            String sysTime  = (String) DateFormat.format("HHmmss", d1.getTime());


            if(sysTime.equals("000000"))
            {
                String storedDay="";
                try
                {
                    String FILENAME = "ExpDate.txt";      // File contains Initial Rx+Tx value at the time of recharge
                    FileInputStream fis = openFileInput(FILENAME);

                    int read=-1;
                    StringBuffer buffer = new StringBuffer();
                    while((read=fis.read())!=-1)
                    {
                        buffer.append((char)read);
                    }
                    storedDay = (buffer.toString());     // Read value stored in this String
                    expday = storedDay;
                    fis.close();
                }
                catch (Exception e)
                {

                }

                try
                {

                    String FILENAME33 = "ExpDate.txt";

                    FileOutputStream fileOutputStream33 = openFileOutput(FILENAME33,MODE_PRIVATE);

                    byte buf[] = String.valueOf(Integer.parseInt(storedDay)-1).getBytes();

                    fileOutputStream33.write(buf);
                    fileOutputStream33.close();
                }
                catch (Exception e)
                {

                }
            }

            try
            {
                String FILENAME = "ExpDate.txt";      // File contains Initial Rx+Tx value at the time of recharge
                FileInputStream fis = openFileInput(FILENAME);

                int read=-1;
                StringBuffer buffer = new StringBuffer();
                while((read=fis.read())!=-1)
                {
                    buffer.append((char)read);
                }
                expday = (buffer.toString());     // Read value stored in this String
                fis.close();
            }
            catch (Exception e)
            {

            }

            boolean dataEnabled = isMobileDataEnabled();
            System.out.println("\nservice running, data = "+String.valueOf(dataEnabled)+"\n");
            System.out.println("\nservice running, data reamining = "+String.valueOf(remaining)+"\n");

            if(dataEnabled && (((int)remaining)>=1200) && !expday.equals("0"))
            {
                rxbytes = TrafficStats.getMobileRxBytes();
                txbytes = TrafficStats.getMobileTxBytes();
                totalbytes = rxbytes+txbytes;

                if(totalbytes<initial || totalbytes<1024)
                {

                    try
                    {
                        String FILENAME22 = "Used.txt";      // File contains Initial Rx+Tx value at the time of recharge
                        FileInputStream fis22 = openFileInput(FILENAME22);

                        int read=-1;
                        StringBuffer buffer = new StringBuffer();
                        while((read=fis22.read())!=-1)
                        {
                            buffer.append((char)read);
                        }
                        String pack = (buffer.toString());     // Read value stored in this String
                        lastBootData = Double.parseDouble(pack);
                        fis22.close();
                    }
                    catch (Exception e)
                    {

                    }


                    try
                    {
                        String FILENAME11 = "Initial.txt";
                        /*double rxbytes1 = TrafficStats.getMobileRxBytes();
                        double txbytes1 = TrafficStats.getMobileTxBytes();
                        double totalbytes1 = rxbytes1+txbytes1;*/
                        FileOutputStream fileOutputStream21 = openFileOutput(FILENAME11,MODE_PRIVATE);
                        byte[] buf = "0".getBytes();
                        fileOutputStream21.write(buf);
                        fileOutputStream21.close();
                    }
                    catch (Exception e)
                    {

                    }
                }
                used = lastBootData+(totalbytes-initial);          //          Used data from last recharge
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
                        String s1 = String.valueOf(remaining);
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



        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {


        try
        {
            String FILENAME = "Remaining.txt";      // File contains Initial Rx+Tx value at the time of recharge
            FileInputStream fis = openFileInput(FILENAME);

            int read=-1;
            StringBuffer buffer = new StringBuffer();
            while((read=fis.read())!=-1)
            {
                buffer.append((char)read);
            }
            String pack = (buffer.toString());     // Read value stored in this String
            remaining = Double.parseDouble(pack);
            fis.close();
        }
        catch (Exception e)
        {

        }

        lastBootData=0;


        rxbytes = TrafficStats.getMobileRxBytes();
        txbytes = TrafficStats.getMobileTxBytes();
        totalbytes = rxbytes+txbytes;

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
            initial = Double.parseDouble(remaining);
            fis.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            String FILENAME = "RechargeData.txt";      // The amount of data recharged by the user
            FileInputStream fis = openFileInput(FILENAME);

            int read=-1;
            StringBuffer buffer = new StringBuffer();
            while((read=fis.read())!=-1)
            {
                buffer.append((char)read);
            }
            String pack = (buffer.toString());     // Read value stored in this String
            DataPack = Double.parseDouble(pack);
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

    public Boolean isMobileDataEnabled(){
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            return (Boolean)m.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
