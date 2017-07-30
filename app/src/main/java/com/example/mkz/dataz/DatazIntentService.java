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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.NotificationCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;


public class DatazIntentService extends IntentService {

    NotificationCompat.Builder notification;    //  Sticky notification
    private static final int uID = 123456;      //  Sticky notification's id


    /*  ************ (Starts) values available globally *********** */
    public long totalbytes;                     //  at any instant of time (current rx + tx values)
    public long used;                           //  data used out of total datapack
    public long DataPack;                       //  total data amount
    public long remaining;                      //  datapack-used
    /*  ************ (Ends) values available globally *********** */

    /*  ************ (Starts) internal variables *********** */
    public long initial;                        //  reference from current rx+tx , updated only after each recharge or reboots
    public long lastBootData;                   //  backup the used data amount before a reboot
    public long expDate;
    public int not_flag=0;                      //  notification will  rebuild if service restarts only
    public int curProgress,totalProgress;       //  holds data to display in sticky notification
    public long prevBytes;                      //  to display speed
    public long exh_flag=-1,exp_flag=-1,dis_flag=0; //  flags to control notification building
    public int stat_per;                        //  calculates percentage for notification's progress bar
    public long reboot;                         //  it stores current rx+tx (total) to check whether service is restarted or device reboots!
    public long rxbytes;                        // current rx
    public long txbytes;                        // current tx
    String savedata;
    /*  ************ (Ends) internal variables *********** */

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
            /*Date d = new Date(new Date().getTime());
            String sysDate  = (String) DateFormat.format("dd", d.getTime());
            String packDate = mystfileRead("ExpDate.txt");
            if(dataEnabled && ((remaining)>=1200) && (Integer.parseInt(sysDate)<Integer.parseInt(packDate)) && totalbytes!=0)
            */

            Date d = new Date(new Date().getTime());
            String sysDate  = (String) DateFormat.format("MM-dd-yyyy", d.getTime());
            String packDate = mystfileRead("ExpDate.txt");

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            /*

            String dt = (String) DateFormat.format("MM-dd-yyyy", d.getTime());  // sysdate
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dt));
            } catch (Exception e) {
            }

            c.add(Calendar.DATE, 33);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
            String output = sdf1.format(c.getTime());
            System.out.println("Updated Date = "+output);*/
            long diff=1;
            String days="";
            try {
                Date date1 = sdf.parse(sysDate);
                Date date2 = sdf.parse(packDate);
                diff = date2.getTime() - date1.getTime();
                diff = TimeUnit.DAYS.convert(diff, TimeUnit.DAYS) ;
                diff/= (1000*60*60*24);
            } catch (Exception e) {
                e.printStackTrace();
            }


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
            //System.out.println("\nservice running, totalP = "+totalProgress+" curP = "+curProgress+"\n");
            System.out.println("\nservice running, flag = "+mystfileRead("Flag.txt")+"\n");
            System.out.println("\nservice running, day rem = "+(diff)+"\n\n");
/* ****************************(Ends) Testing output (for personal use : Debojyoti) ******************************** */












/* **********************(Starts) If data pack exists,then only do works here  ************************ */
            if(dataEnabled && ((remaining)>=1024000) && (diff>0) && totalbytes!=0)   // stop at 1 mb
            {

                mystfileWrite("Reboot.txt",String.valueOf(totalbytes));     // recoreds current total

/*  *****************(Starts) Updating notification content   *************** */
                //notification.setSmallIcon(R.drawable.mystlogo);
                notification.setContentTitle("Remaining = "+bytesToHuman(remaining));
                notification.setContentText("Used =" +bytesToHuman(used)+" (Data is enabled) "+bytesToHuman((double)(totalbytes-prevBytes))+"/s");
                curProgress=((int)used);
                totalProgress=(int)DataPack;
                notification.setProgress(totalProgress, curProgress, false);

                if(not_flag==0)
                {
                    notification.setSmallIcon(R.drawable.mystlogo);
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(uID,notification.build());
                    not_flag=1;
                }
/*  *******************(Ends) Updating notification content******************  */


                System.out.println("\nservice running, cur total bytes = "+String.valueOf(totalbytes)+"\n");
                if(mystfileRead("Flag.txt").equals("0"))    // flag will be 0 if new recharge is done
                {
                    mystfileWrite("Initial.txt",String.valueOf(totalbytes));
                    initial=totalbytes;
                    mystfileWrite("Flag.txt","1");
                }

                if(totalbytes<reboot && totalbytes>0)       // if device reboots
                {

/* (Starts) backup the previous used data from the "Used.txt" file and store it in the variable lastBootData */
                        String pack = mystfileRead("Used.txt");
                        lastBootData = Long.parseLong(pack);
                        reboot=totalbytes-1;
/* (Ends) backup the previous used data from the "Used.txt" file and store it in the variable lastBootData */
                        initial=0;
                        mystfileWrite("Initial.txt","0");  //  Assign initial value = 0
                        mystfileWrite("Lastbootdata.txt", String.valueOf(lastBootData));
                }
                else if(totalbytes>(reboot-1))      // id service restarts
                {
                    lastBootData=Long.parseLong(mystfileRead("Lastbootdata.txt"));
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

                 prevBytes=totalbytes;
                 exh_flag=exp_flag=dis_flag=0;


/*  ***********************(Starts)    Dynamic status bar      ********************************* */
                stat_per=(int)(used*100/DataPack);

                if(stat_per>87)
                {
                    notification.setSmallIcon(R.drawable.wheel7);

                }
                else if(stat_per<=87 && stat_per>75)
                {
                    notification.setSmallIcon(R.drawable.wheel6);
                }
                else if(stat_per<=75 && stat_per>62)
                {
                    notification.setSmallIcon(R.drawable.wheel5);
                }
                else if(stat_per<=62 && stat_per>50)
                {
                    notification.setSmallIcon(R.drawable.wheel4);
                }
                else if(stat_per<=50 && stat_per>37)
                {
                    notification.setSmallIcon(R.drawable.wheel3);
                }
                else if(stat_per<=37 && stat_per>25)
                {
                    notification.setSmallIcon(R.drawable.wheel2);
                }
                else if(stat_per<=25 && stat_per>12)
                {
                    notification.setSmallIcon(R.drawable.wheel1);
                }
                else if(stat_per<=12 && stat_per>=0)
                {
                    notification.setSmallIcon(R.drawable.wheel0);
                }
                //NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
               // nm.notify(uID,notification.build());
                System.out.println("Service running stat_per = "+stat_per);

/*  ********************(Ends)    Dynamic status bar      ****************************************** */

            }
            else if(((remaining)<1024000) && exh_flag==0)
            {
                notification.setSmallIcon(R.drawable.mystlogo);
                notification.setContentTitle("Exhausted!");
                notification.setContentText("Data Pack = "+bytesToHuman(DataPack));

                notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(uID,notification.build());

                exh_flag=1;
                dis_flag=1;


            }
            else if((diff<=0) && exp_flag==0)
            {
                notification.setSmallIcon(R.drawable.mystlogo);
                notification.setContentTitle("Validity Expired!");
                notification.setContentText("Data Pack = "+bytesToHuman(DataPack));

                notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(uID,notification.build());

                exh_flag=1;
                dis_flag=1;


            }
            else if(!dataEnabled && totalbytes==0 && dis_flag==0)
            {

                notification.setSmallIcon(R.drawable.mystlogo);
                notification.setContentTitle("Rem = "+bytesToHuman(remaining));
                notification.setContentText("Used =" +bytesToHuman(used)+" (Data is disabled)");
                curProgress=((int)used);
                totalProgress=(int)DataPack;
                notification.setProgress(0, 0, false);

                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(uID,notification.build());

                    dis_flag=1;


            }


        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        lastBootData=0;
        prevBytes=TrafficStats.getMobileTxBytes()+TrafficStats.getMobileRxBytes();

/*  ***************************(Starts) Building notification object ****************************    */
        notification = new NotificationCompat.Builder(DatazIntentService.this);
        notification.setSmallIcon(R.drawable.mystlogo);
        Intent i = new Intent(DatazIntentService.this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(DatazIntentService.this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pi);
        notification.setOngoing(true);
/*  ***************************(Ends) Building notification object ****************************    */






/*  ******************** (Starts)  At the start of this service, read 3 files  *************************    */
        String pack = mystfileRead("Remaining.txt");
        remaining = Long.parseLong(pack);
        String remaining1 = mystfileRead("Initial.txt");
        initial = Long.parseLong(remaining1);
        String packData = mystfileRead("RechargeData.txt");
        DataPack = Long.parseLong(packData);
        reboot = Long.parseLong(mystfileRead("Reboot.txt"));
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


        Intent i = new Intent(this,MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uID,notification.build());

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
            return "0";
        }
        return savedDataFromFile;
    }

    /* ***************** (Ends)   File read and write functions   *************** */



    public void notify(View v)
    {
        notification.setSmallIcon(R.drawable.mystlogo);
        notification.setTicker("Myst Running!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Myst");
        notification.setContentText("hello");

        Intent i = new Intent(this,MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setContentIntent(pi);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uID,notification.build());
    }
    // [custom function] converts bytes To Human readable form
    public static String bytesToHuman (double size)
    {
        double Kb = 1  * 1024;
        double Mb = Kb * 1024;
        double Gb = Mb * 1024;
        double Tb = Gb * 1024;
        double Pb = Tb * 1024;
        double Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "???";
    }
    // [custom function ] sets decimal format for bytesToHuman
    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }
}
