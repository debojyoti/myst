package com.example.mkz.dataz;





import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Thread divert = new Thread();

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
    private long mStartRX = 0;
    private long mStartTX = 0;
    private String packval;


    private TextView remval;

    String savedDataFromFile;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            TextView total= (TextView) findViewById(R.id.total);
            TextView dataval = (TextView) findViewById(R.id.dataval);
            long rxBytes = TrafficStats.getMobileRxPackets()-mStartRX;
            long txBytes = TrafficStats.getMobileTxPackets()-mStartTX;

            long totalBytes = Long.parseLong(my_file_read());// reads totalbytes from file
            long sizeofpack = Long.parseLong(packval);
            long remainingdata = sizeofpack-totalBytes;


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
    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

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
        Long packval_in_long = Long.parseLong(packval);
        String packval_in_bytesToHuman = bytesToHuman(packval_in_long);
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

                    showtv.setText(bytesToHuman(Long.parseLong(my_file_read())));

            }
        });

        //After adding services DatazIntentService.java ends
    }


}*/
