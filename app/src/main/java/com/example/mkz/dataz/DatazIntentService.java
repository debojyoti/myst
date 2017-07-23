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
            5) ExpDate.txt       =       It stores
            
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

    public long rxbytes;
    public long txbytes;
    public long totalbytes;
    public long used;
    public long initial;
    public long DataPack;
    public long remaining;
    public long lastBootData;
    public String expday;

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
        totalbytes = rxbytes+txbytes;   // current rx+tx


/* **********************(Starts) Checking whether systemDate is lesser than data pack exp date  ************************ */
            Date d = new Date(new Date().getTime());
            String sysDate  = (String) DateFormat.format("dd", d.getTime());
            String packDate = mystfileRead("ExpDate.txt");
/* **********************(Ends) Checking whether systemDate is lesser than data pack exp date  ************************ */



/* **********************(Starts) Checking whether data connection is enabled  ************************ */
            boolean dataEnabled = isMobileDataEnabled();
/* **********************(Ends) Checking whether data connection is enabled ************************ */



/* ****************************(Starts) Testing output (for personal use : Debojyoti) ******************************** */
            System.out.println("\n\nservice running, data = "+String.valueOf(dataEnabled)+"\n");
            System.out.println("\nservice running, data reamining = "+remaining+"\n");
            System.out.println("\nservice running, data pack = "+DataPack+"\n");
            System.out.println("\nservice running, used = "+used+"\n");
            System.out.println("\nservice running, initial = "+initial+"\n");
            System.out.println("\nservice running, lastbootdata = "+lastBootData+"\n");
            System.out.println("\nservice running, flag = "+mystfileRead("Flag.txt")+"\n\n");
/* ****************************(Ends) Testing output (for personal use : Debojyoti) ******************************** */


/* **********************(Starts) If data pack exists,then only do works here  ************************ */
            if(dataEnabled && (((int)remaining)>=1200) && (Integer.parseInt(sysDate)<Integer.parseInt(packDate)) && totalbytes!=0)
            {

                System.out.println("\nservice running, cur total bytes = "+String.valueOf(totalbytes)+"\n");

                if(mystfileRead("Flag.txt").equals("0"))    // flag will be 0 if new recharge is done
                {
                    mystfileWrite("Initial.txt",String.valueOf(totalbytes));
                    initial=totalbytes;
                    mystfileWrite("Flag.txt","1");
                }

                if(totalbytes<initial || totalbytes<1024)       // if device reboots
                {

/* (Starts) backup the previous used data from the "Used.txt" file and store it in the variable lastBootData */
                        String pack = mystfileRead("Used.txt");
                        lastBootData = Long.parseLong(pack);
/* (Ends) backup the previous used data from the "Used.txt" file and store it in the variable lastBootData */

                        mystfileWrite("Initial.txt","0");  //  Assign initial value = 0
                }
                used = lastBootData+(totalbytes-initial);          //          Used data from last recharge
                String s;



                s = String.valueOf(used);

/* *************** (Starts)    Keep updating used data and remaining data ****************************** */
                        savedata = s;
                        mystfileWrite("Used.txt",savedata);
                        remaining = DataPack-used;
                        String s1 = String.valueOf(remaining);
                        mystfileWrite("Remaining.txt",s1);
/* *************** (Ends)    Keep updating used data and remaining data ****************************** */

            }
        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        lastBootData=0;


/*  ******************** (Starts)  At the start of this service, read 3 files  *************************    */
        String pack = mystfileRead("Remaining.txt");
        remaining = Long.parseLong(pack);
        String remaining1 = mystfileRead("Initial.txt");
        initial = Long.parseLong(remaining1);
        String packData = mystfileRead("RechargeData.txt");
        DataPack = Long.parseLong(packData);
/*  ******************** (Starts)  At the start of this service, read 3 files  *************************    */


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




    /* ***************** (Starts)   File read and write functions   *************** */
    public void mystfileWrite(String FILENAME,String data_to_write)
    {

        try
        {
            FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);


            byte buf[] = data_to_write.getBytes();

            fileOutputStream.write(buf);
            fileOutputStream.close();

        }
        catch (Exception e)
        {

        }
    }

    public String mystfileRead(String FILENAME)
    {
        String savedDataFromFile="";
        try
        {
            FileInputStream fis = openFileInput(FILENAME);
            int read = -1;

            StringBuffer buffer = new StringBuffer();
            while ((read=fis.read())!=-1)
            {
                buffer.append((char) read);
            }
            savedDataFromFile = buffer.toString();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return savedDataFromFile;
    }

    /* ***************** (Ends)   File read and write functions   *************** */
}
