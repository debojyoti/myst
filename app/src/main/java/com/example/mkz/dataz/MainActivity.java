package com.example.mkz.dataz;

/*

        Here the usage details will be shown to the user

         1) Used Data           (keep reading in the handler)
         2) Remaining Data      (keep reading in the handler)
         3) Total Data          (Read Once)
         4) Burn rate (per hour) from the time of recharge
         5) Estimated time remaining as per current burn rate

            @ Debojyoti

 */



import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class MainActivity extends AppCompatActivity {
    PieView animatedPie;
    PieView animatedPie2;
    TextView warningshow;

    public double rxbytes;
    public double txbytes;
    public double totalbytes;
    public double used;
    public double initial;
    public double DataPack;
    public double remaining;
    public String storedDay;
    public int stat_per;
    TextView txtt;
    PieAngleAnimation animation;
    public  int f;
    public int time;



    Handler hd1= new Handler()
    {
        @Override
        public void handleMessage(Message msg) {


            Date d = new Date(new Date().getTime());
            String sysDate  = (String) DateFormat.format("MM-dd-yyyy", d.getTime());
            String packDate = mystfileRead("ExpDate.txt");

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
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
            storedDay = String.valueOf(diff);       // Currently no use of "storedDay" var


/* ******************   Read used data from Used.txt  (Starts) ************************************** */
                String pack = mystfileRead("Used.txt");
                used = Double.parseDouble(pack);
/* ******************   Read datapack value once  (Ends) ************************************** */



/* ******************   Read remaining data from Remaining.txt  (Starts) ************************************** */
                String rem = mystfileRead("Remaining.txt");
                remaining = Double.parseDouble(rem);
       /* ******************   Read datapack value once  (Ends) ************************************** */



            TextView usedData = (TextView) findViewById(R.id.textView9);
            TextView dataRemaining = (TextView) findViewById(R.id.textView8);
            TextView dataPack = (TextView) findViewById(R.id.textView7);
            TextView dateReamining = (TextView) findViewById(R.id.textView3);
            TextView dailyburnrate = (TextView) findViewById(R.id.textView5);
            txtt = (TextView) findViewById(R.id.txtt);
            TextView textView5 = (TextView) findViewById(R.id.textView5);
            TextView textView6 = (TextView) findViewById(R.id.textView6);



                warningshow.setText("Next warning at "+bytesToHuman(Double.parseDouble(mystfileRead("Warning.txt"))));


            /*double MBused,MBremaining,MBpack;

            MBpack = ((DataPack/1024)/1024);
            MBremaining = ((remaining/1024)/1024);
            MBused = ((used/1024)/1024);*/

            usedData.setText("Used : "+bytesToHuman(used));
            if(remaining<=1000000)      // 1 mb
            {
                dataRemaining.setText("Remainingk : Exhausted!");
            }
            else
            {
                dataRemaining.setText("Remaining = "+bytesToHuman(remaining));
            }

            dataPack.setText("Data pack : "+bytesToHuman(DataPack));

            if(diff==0)
            {
                dateReamining.setText("Data pack expired!");
            }
            else
            {
                dateReamining.setText(diff+" Days Remaining");
            }
            textView5.setText("Daily burn rate : "+(bytesToHuman(DataPack/diff)));
            textView6.setText("Burned : "+((bytesToHuman(used))));
            stat_per=(int)(used*100/DataPack);
            txtt.setText((100-stat_per)+"%");

            animatedPie.setPercentage((float)100-stat_per);
            animatedPie2.setPercentage((float)100-(float)(used*100/(DataPack/diff)));



        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isMyServiceRunning(DatazIntentService.class))
        {
            Intent intent2 = new Intent(MainActivity.this,DatazIntentService.class);
            startService(intent2);
        }

        Date d = new Date(new Date().getTime());
        String sysDate  = (String) DateFormat.format("MM-dd-yyyy", d.getTime());
        String packDate = mystfileRead("ExpDate.txt");

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
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

        String pack = mystfileRead("Used.txt");
        used = Double.parseDouble(pack);

/* ******************   Read datapack value once  (Starts) ************************************** */
        String pack1 = mystfileRead("RechargeData.txt");
        DataPack = Double.parseDouble(pack1);
/* ******************   Read datapack value once  (Ends) ************************************** */

        stat_per=(int)(used*100/DataPack);

        animatedPie = (PieView) findViewById(R.id.animated_pie_view_1);

        animatedPie.setPercentage((float)100-stat_per);
        animation = new PieAngleAnimation(animatedPie);
        animatedPie.setMainBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.customColor5));


        animation.setDuration(1000); //This is the duration of the animation in millis
        animatedPie.startAnimation(animation);
        animatedPie.setPercentageTextSize(0);
        animatedPie.setPercentageBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.perecentcircle));
        animatedPie.setInnerBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.customColor5));
        animatedPie2 = (PieView) findViewById(R.id.animated_pie_view_2);

        animatedPie2.setPercentage((float)100-(float)(used*100/(DataPack/diff)));
        PieAngleAnimation animation2 = new PieAngleAnimation(animatedPie2);
        animatedPie2.setMainBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dailyperbg));


        animation2.setDuration(1000); //This is the duration of the animation in millis
        animatedPie2.startAnimation(animation2);
        animatedPie2.setPercentageTextSize(0);
        animatedPie2.setPercentageBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dailyper));
        animatedPie2.setInnerBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dailyperbg));

        warningshow = (TextView) findViewById(R.id.textView4);
        warningshow.setText("Next warning at "+bytesToHuman(Double.parseDouble(mystfileRead("Warning.txt"))));
        System.out.println("Next Warning at  "+mystfileRead("Warning.txt")+" usage");






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

    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
            return "0";
        }
        return savedDataFromFile;
    }

    /* ***************** (Ends)   File read and write functions   *************** */

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

/*                      Garaged Code




import android.app.usage.NetworkStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;

import static android.R.attr.enabled;
import static android.R.attr.start;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private double mStartRX = 0;
    private double mStartTX = 0;
    private String packval;


    private TextView remval;

    String savedDataFromFile;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            TextView total= (TextView) findViewById(R.id.total);
            TextView dataval = (TextView) findViewById(R.id.dataval);
            double rxBytes = TrafficStats.getMobileRxPackets()-mStartRX;
            double txBytes = TrafficStats.getMobileTxPackets()-mStartTX;

            double totalBytes = double.parsedouble(my_file_read());// reads totalbytes from file
            double sizeofpack = double.parsedouble(packval);
            double remainingdata = sizeofpack-totalBytes;


            total.setText("Total Data :"+bytesToHuman((totalBytes)));
            dataval.setText(bytesToHuman((totalBytes)));

            if(remainingdata>0)
            {
                remval.setText(bytesToHuman((remainingdata)));
            }
            else
            {
                Intent intent1 = new Intent("com.example.mkz.dataz.DatazIntentService");




                try {
                    Method dataConnSwitchmethod = null;
                    Class telephonyManagerClass;
                    Object ITelephonyStub;
                    Class ITelephonyClass;
                    boolean isEnabled;

                    TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext()
                            .getSystemService(Context.TELEPHONY_SERVICE);

                    if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED){
                        isEnabled = true;
                    }else{
                        isEnabled = false;
                    }


                    //turn off data starts
                    telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
                    Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
                    getITelephonyMethod.setAccessible(true);
                    ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
                    ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

                    if (isEnabled)
                    {
                        dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
                    }

                    dataConnSwitchmethod.setAccessible(true);
                    dataConnSwitchmethod.invoke(ITelephonyStub);
                    //turn off data ends

                    //code to launch settings starts
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$DataUsageSummaryActivity"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    stopService(intent1);
                    //code to launch settings ends





                } catch (Exception e) {

                    e.printStackTrace();
                }
                remval.setText("Limit Exceeded");
                remval.setTextColor(Color.RED);
            }
            handler.postDelayed(mRunnable,1000);

        }
    };

    //Reads data used value from file and returns string value [custom function]
   public String my_file_read()
   {

       try {

           String FILENAME = "Datazdata.txt";

           FileInputStream fis = openFileInput(FILENAME);
           int read = -1;

           StringBuffer buffer = new StringBuffer();
           while ((read=fis.read())!=-1)
           {
               buffer.append((char) read);
           }

           savedDataFromFile = buffer.toString();
           if (savedDataFromFile.equals(""))
           {
               // if file is blank return zero as string
               savedDataFromFile="0";
           }
           fis.close();
       }
       catch (Exception e) {
           e.printStackTrace();
       }

       return savedDataFromFile;
   }

// [custom function ] sets decimal format for bytesToHuman
    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
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
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //After adding services DatazIntentService.java starts

        Intent intent2 = new Intent(MainActivity.this,DatazIntentService.class);
        startService(intent2);



        //After adding services DatazIntentService.java ends

        String path = getApplicationContext().getFilesDir().getAbsolutePath()+"/"+"Datazdata.txt";


        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        TextView sizepack = (TextView) findViewById(R.id.sizepack);
        remval = (TextView) findViewById(R.id.remval);


        Bundle bundle = getIntent().getExtras();
        packval = bundle.getString("packval");
        double packval_in_double = double.parsedouble(packval);
        String packval_in_bytesToHuman = bytesToHuman(packval_in_double);
        sizepack.setText("Pack Size = "+packval_in_bytesToHuman);


        mStartRX= TrafficStats.getMobileRxPackets();
        mStartTX= TrafficStats.getMobileTxPackets();



        if(mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(" This service is unsupported ");
            alert.show();
        }
        else
        {
            handler.postDelayed(mRunnable, 1000);
        }

        //After adding services DatazIntentService.java starts



        final TextView showtv = (TextView) findViewById(R.id.showtv);
        Button showbttn = (Button) findViewById(R.id.showbttn);

        showbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    showtv.setText(bytesToHuman(double.parsedouble(my_file_read())));

            }
        });

        //After adding services DatazIntentService.java ends
    }


}*/
