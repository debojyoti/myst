package com.example.mkz.dataz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Recharge extends AppCompatActivity {

    EditText getRechargeAmount;
    EditText validity;
    Button validdatabtn;
    Button addvalidity;
    Button rechargeBtn;
    Button backtovalidity;
    Button backtoadddatabtn;
    TextView noteValidity;
    TextView noteadddata;
    TextView warning_level;
    RadioGroup mbgb;
    RelativeLayout datalayout, validitylayout;
    SeekBar setSeek;

    int data = 0;
    String dSize;
    String date;
    double dataSize;
    int warn_level = 85;
    double warndata;        //      Contains warning data amount


    Animation slideUpAnimation, slideDownAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        validitylayout = (RelativeLayout) findViewById(R.id.validitylayout);
        datalayout = (RelativeLayout) findViewById(R.id.datalayout);
        getRechargeAmount = (EditText) findViewById(R.id.getRechargeAmount);
        validity = (EditText) findViewById(R.id.validity);
        validdatabtn = (Button) findViewById(R.id.validdatabtn);
        backtovalidity = (Button) findViewById(R.id.backtovalidity);
        addvalidity = (Button) findViewById(R.id.addvalidity);
        rechargeBtn = (Button) findViewById(R.id.rechargeBtn);
        backtoadddatabtn = (Button) findViewById(R.id.backtoadddatabtn);
        noteValidity = (TextView) findViewById(R.id.textView13);
        noteadddata = (TextView) findViewById(R.id.textView23);
        warning_level = (TextView) findViewById(R.id.warning_level);
        mbgb = (RadioGroup) findViewById(R.id.dataType);
        setSeek = (SeekBar) findViewById(R.id.setSeek);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);
        validdatabtn.setVisibility(View.INVISIBLE);
        if (!validity.getText().toString().equals("") && !validity.getText().toString().equals("0"))
        {
            addvalidity.setVisibility(View.VISIBLE);
        }
        else
        {
            addvalidity.setVisibility(View.INVISIBLE);
        }
        try
        {
            FileInputStream fis = openFileInput("RechargeData.txt");
            int read = -1;

            StringBuffer buffer = new StringBuffer();
            while ((read=fis.read())!=-1)
            {
                buffer.append((char) read);
            }

            fis.close();
        }
        catch (FileNotFoundException e) {
            ((Button) findViewById(R.id.showUsage)).setVisibility(View.INVISIBLE);
        }
        catch (Exception e)
        {

        }
        mystfileWrite("Warnflag.txt","0");



        setSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = 60+((i*30)/6);
                warning_level.setText("Current warning level = "+progress+" %");
                warn_level=progress;

                if (!getRechargeAmount.getText().toString().equals("") && !getRechargeAmount.getText().toString().equals(".")) {
                    if ((Double.parseDouble(getRechargeAmount.getText().toString()) > 1 && data==0) || (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data==1)) {
                        if (data == 1) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024*1024) * ((double)warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                        } else if (data == 0) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024) * ((double)warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                        }
                    }
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

/* *********************** (starts)    validdata button             *********************************         */
        validdatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Hide */
                getRechargeAmount.setVisibility(View.INVISIBLE);
                noteadddata.setVisibility(View.INVISIBLE);
                validdatabtn.setVisibility(View.INVISIBLE);
                mbgb.setVisibility(View.INVISIBLE);
                datalayout.setVisibility(View.INVISIBLE);
                /*  Show    */
                validity.setVisibility(View.VISIBLE);
                noteValidity.setVisibility(View.VISIBLE);
                if (!validity.getText().toString().equals("") && !validity.getText().toString().equals("0"))
                {
                    addvalidity.setVisibility(View.VISIBLE);
                }
                else
                {
                    addvalidity.setVisibility(View.INVISIBLE);
                }
                //addvalidity.setVisibility(View.VISIBLE);
                backtoadddatabtn.setVisibility(View.VISIBLE);

            }
        });
 /* ***********************  (starts)   back to data button             *********************************         */
        backtoadddatabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Hide */
                validity.setVisibility(View.INVISIBLE);
                noteValidity.setVisibility(View.INVISIBLE);
                addvalidity.setVisibility(View.INVISIBLE);
                backtoadddatabtn.setVisibility(View.INVISIBLE);
                /*  Show    */
                getRechargeAmount.setVisibility(View.VISIBLE);
                noteadddata.setVisibility(View.VISIBLE);
                validdatabtn.setVisibility(View.VISIBLE);
                mbgb.setVisibility(View.VISIBLE);
                datalayout.setVisibility(View.VISIBLE);

            }
        });
/* ***********************  (starts)   validity button             *********************************         */
        addvalidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Hide */
                validity.setVisibility(View.INVISIBLE);
                noteValidity.setVisibility(View.INVISIBLE);
                addvalidity.setVisibility(View.INVISIBLE);
                backtoadddatabtn.setVisibility(View.INVISIBLE);
                validitylayout.setVisibility(View.INVISIBLE);
                /*  Show    */
                setSeek.setVisibility(View.VISIBLE);
                warning_level.setVisibility(View.VISIBLE);
                rechargeBtn.setVisibility(View.VISIBLE);
                backtovalidity.setVisibility(View.VISIBLE);

            }
        });
/* ***********************  (starts) back to  validity button             *********************************         */
        backtovalidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Hide */
                validity.setVisibility(View.VISIBLE);
                noteValidity.setVisibility(View.VISIBLE);
                addvalidity.setVisibility(View.VISIBLE);
                backtoadddatabtn.setVisibility(View.VISIBLE);
                validitylayout.setVisibility(View.VISIBLE);
                /*  Show    */
                setSeek.setVisibility(View.INVISIBLE);
                warning_level.setVisibility(View.INVISIBLE);
                rechargeBtn.setVisibility(View.INVISIBLE);
                backtovalidity.setVisibility(View.INVISIBLE);

            }
        });

/* *********************** (starts)   add data button depends on valid data amount           *********************************         */
        getRechargeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!getRechargeAmount.getText().toString().equals("") && !getRechargeAmount.getText().toString().equals(".")) {
                    dSize = getRechargeAmount.getText().toString();
                    if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data == 1) {
                        validdatabtn.setVisibility(View.VISIBLE);
                        if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                            if (data == 1) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            } else if (data == 0) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            }
                        }
                    } else if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 2 && data == 0) {
                        validdatabtn.setVisibility(View.VISIBLE);
                        if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                            if (data == 1) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            } else if (data == 0) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            }
                        }
                    }
                    else {
                        validdatabtn.setVisibility(View.INVISIBLE);
                    }

                } else {
                    validdatabtn.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        validity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {



                if (!validity.getText().toString().equals("") && !validity.getText().toString().equals("0"))
                {
                    addvalidity.setVisibility(View.VISIBLE);
                }
                else
                {
                    addvalidity.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();
        //errorDisp.setText("");


        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.gb:
                if (checked)
                    System.out.println("Data selected as : GB");
                data = 1;
                break;
            case R.id.mb:
                if (checked)
                    System.out.println("Data selected as : MB");
                data = 0;
                break;
        }

        /* *********************** (starts)   add data button depends on valid data type           *********************************         */
        if (!getRechargeAmount.getText().toString().equals("") && !getRechargeAmount.getText().toString().equals(".")) {


            if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data==1) {

                validdatabtn.setVisibility(View.VISIBLE);
                if (data == 1) {
                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                    String warn = bytesToHuman(warndata);
                    warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                } else if (data == 0) {
                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                    String warn = bytesToHuman(warndata);
                    warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                }
            } else if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 2 && data == 0) {
                validdatabtn.setVisibility(View.VISIBLE);
                if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                    if (data == 1) {
                        warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                        String warn = bytesToHuman(warndata);
                        warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                    } else if (data == 0) {
                        warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                        String warn = bytesToHuman(warndata);
                        warning_level.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                    }
                }
            } else

            {
                validdatabtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addValidDataToFile(View v)
    {


        double newData = Double.parseDouble(dSize);
        if(data==0)      // It signifies that MB is selected by the user
        {
            newData = newData*1024*1024;
            System.out.println("Entered Data in bytes = "+newData);
        }
        else             // It signifies that GB is selected by the user
        {
            newData = newData*1024*1024*1024;
            System.out.println("Entered Data in bytes = "+newData);
        }
        dataSize = newData;
        String data = String.valueOf((long)newData);

        mystfileWrite("RechargeData.txt",data);
        System.out.println("\n\nFrom recharge, string data = "+data);
        date = validity.getText().toString();



            /*Date d = new Date(new Date().getTime());
            String cDate  = (String) DateFormat.format("dd", d.getTime());
            cDate = String.valueOf(Integer.parseInt(cDate)+Integer.parseInt(date));
            mystfileWrite("ExpDate.txt",cDate); */

/* ******************* (Starts) Generating and saving the corresponding Expiry date  ********************* */
        Date d = new Date(new Date().getTime());
        String dt = (String) DateFormat.format("MM-dd-yyyy", d.getTime());  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (Exception e) {
        }
        c.add(Calendar.DATE, Integer.parseInt(date));  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
        String updatedDate = sdf1.format(c.getTime());
        System.out.println("Updated Date = "+updatedDate);
        mystfileWrite("ExpDate.txt",updatedDate);
/* ******************* (Ends) Generating and saving the corresponding Expiry date  ********************* */


        mystfileWrite("Warning.txt",String.valueOf((long)warndata));
        mystfileWrite("Remaining.txt",data);
        mystfileWrite("Initial.txt","0");
        mystfileWrite("Used.txt","0");
        mystfileWrite("Lastbootdata.txt","0");

        /* **************** Taking rx+tx at the time of recharge  Starts******************* */

        mystfileWrite("Flag.txt","0");
        /* **************** Taking rx+tx at the time of recharge  Ends******************* */



        /*           Send to the usage page  (to MainActivity.java)          */
        Intent i = new Intent(Recharge.this,MainActivity.class).putExtra("TYPE",1);  // TYPE = 1 means new recharge
        startActivity(i);
        finish();
    }

    public void showUsage(View v)
    {

        Intent i = new Intent(Recharge.this,MainActivity.class).putExtra("TYPE",0);  // TYPE = 0 means no recharge
        startActivity(i);
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
