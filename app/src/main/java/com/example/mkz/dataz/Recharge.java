package com.example.mkz.dataz;


/*
        This activity will take data amount, data type(gb/mb) and validity (in days) from user as a new recharge
        and will save it into the file "RechargeData.txt"

        The stored data (in the file) will be in bytes

        The file storing operation is done in the method : addValidDataToFile()

        : @ Debojyoti


 */







import android.content.Intent;
import android.net.TrafficStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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






/* *************************   Getting and checking valid inputs (Starts)  ******************************* */

    RadioGroup mbgb;
    Button addData;
    TextView errorDisp;
    TextView showwarning;
    SeekBar setSeek;
    EditText getRechargeAmount;
    EditText validity;
    int data = -1;
    String dSize;
    String date;
    double dataSize;
    int warn_level=85;
    double warndata;        //      Contains warning data amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
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
        mbgb = (RadioGroup) findViewById(R.id.dataType);
        setSeek = (SeekBar) findViewById(R.id.setSeek);
        showwarning = (TextView) findViewById(R.id.warning_level);
        addData = (Button) findViewById(R.id.rechargeBtn);
        errorDisp = (TextView) findViewById(R.id.errorDisp);
        getRechargeAmount = (EditText) findViewById(R.id.getRechargeAmount);
        validity = (EditText) findViewById(R.id.validity);

        //mbgb.setVisibility(View.INVISIBLE);
        setSeek.setVisibility(View.INVISIBLE);
        showwarning.setVisibility(View.INVISIBLE);
        addData.setVisibility(View.INVISIBLE);
        validity.setVisibility(View.INVISIBLE);

        setSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = 60+((i*30)/6);
                showwarning.setText("Current warning level = "+progress+" %");
                warn_level=progress;

                if (!getRechargeAmount.getText().toString().equals("") && !getRechargeAmount.getText().toString().equals(".")) {
                    if ((Double.parseDouble(getRechargeAmount.getText().toString()) > 1 && data==0) || (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data==1)) {
                        if (data == 1) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024*1024) * ((double)warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                        } else if (data == 0) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024) * ((double)warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
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

        validity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSeek.setVisibility(View.VISIBLE);
                showwarning.setVisibility(View.VISIBLE);
                addData.setVisibility(View.VISIBLE);
                if (!getRechargeAmount.getText().toString().equals("")  && !getRechargeAmount.getText().toString().equals(".")) {
                    if (!validity.getText().toString().equals("") && !validity.getText().toString().equals("0"))
                    {
                        if (Double.parseDouble(getRechargeAmount.getText().toString()) <= 1 && (data == 0 || data == -1)) {
                            errorDisp.setText("Enter Amount Greater than 1 MB");
                            validity.setVisibility(View.INVISIBLE);
                            setSeek.setVisibility(View.INVISIBLE);
                            addData.setVisibility(View.INVISIBLE);
                            showwarning.setVisibility(View.INVISIBLE);
                        } else if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data == 1) {
                            validity.setVisibility(View.VISIBLE);
                            addData.setVisibility(View.VISIBLE);;
                            if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                                if (data == 1) {
                                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                                    String warn = bytesToHuman(warndata);
                                    showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                                } else if (data == 0) {
                                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                                    String warn = bytesToHuman(warndata);
                                    showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                                }
                            }
                        }
                        else if(Double.parseDouble(getRechargeAmount.getText().toString()) > 1 && data == 0) {
                            validity.setVisibility(View.VISIBLE);
                            addData.setVisibility(View.VISIBLE);;
                            if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                                if (data == 1) {
                                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                                    String warn = bytesToHuman(warndata);
                                    showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                                } else if (data == 0) {
                                    warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                                    String warn = bytesToHuman(warndata);
                                    showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                                }
                            }
                        }
                }
                else
                    {
                        addData.setVisibility(View.INVISIBLE);;
                    }
                }
                else
                {
                    errorDisp.setText("Enter Amount Greater than 1 MB");
                    validity.setVisibility(View.INVISIBLE);
                    setSeek.setVisibility(View.INVISIBLE);
                    addData.setVisibility(View.INVISIBLE);
                    showwarning.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getRechargeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // mbgb.setVisibility(View.VISIBLE);
                if (!getRechargeAmount.getText().toString().equals("")  && !getRechargeAmount.getText().toString().equals(".")) {
                    if(Double.parseDouble(getRechargeAmount.getText().toString())<=1 && (data==0 || data==-1))
                    {
                        errorDisp.setText("Enter Amount Greater than 1 MB");
                        validity.setVisibility(View.INVISIBLE);
                        setSeek.setVisibility(View.INVISIBLE);
                        addData.setVisibility(View.INVISIBLE);
                        showwarning.setVisibility(View.INVISIBLE);
                    }
                    else if((Double.parseDouble(getRechargeAmount.getText().toString())>=1 && data==1) || (Double.parseDouble(getRechargeAmount.getText().toString())>1 && data==0))
                    {

                        validity.setVisibility(View.VISIBLE);
                        setSeek.setVisibility(View.VISIBLE);
                        showwarning.setVisibility(View.VISIBLE);
                        if(validity.getText().toString().equals("") || validity.getText().toString().equals("0"))
                        {
                            addData.setVisibility(View.INVISIBLE);
                        }
                        if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                            if (data == 1) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024*1024) * ((double)warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            } else if (data == 0) {
                                warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString())*1024*1024) * ((double)warn_level / 100);
                                String warn = bytesToHuman(warndata);
                                showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                            }
                        }
                    }
                    else
                    {
                        errorDisp.setText("Enter Amount Greater than 1 MB");
                    }


                }
                else
                {
                    errorDisp.setText("Enter Amount Greater than 1 MB");
                    validity.setVisibility(View.INVISIBLE);
                    setSeek.setVisibility(View.INVISIBLE);
                    addData.setVisibility(View.INVISIBLE);
                    showwarning.setVisibility(View.INVISIBLE);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getRechargeAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorDisp.setText("");
            }
        });
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dSize = getRechargeAmount.getText().toString();
                date = validity.getText().toString();
                if (dSize.equals("") || dSize.equals("0") || Double.parseDouble(dSize)<1) {
                    errorDisp.setText("Enter a valid Amount of Data");

                } else {
                    errorDisp.setText("");
                    if(date.equals("") || date.equals("0"))
                    {
                        errorDisp.setText("Validity must be greater than 0");
                    }
                    else
                    {
                        if (data == -1) {
                            errorDisp.setText("Enter data type : GB/MB?");
                        } else {
                            addValidDataToFile();
                        }
                    }
                }
            }
        });





    }
    public void onRadioButtonClicked(View view) {



        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        errorDisp.setText("");


        // Check which radio button was clicked
        switch(view.getId()) {
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
        if (!getRechargeAmount.getText().toString().equals("")  && !getRechargeAmount.getText().toString().equals(".")) {
            if(Double.parseDouble(getRechargeAmount.getText().toString())<=1 && (data==0 || data==-1))
            {
                errorDisp.setText("Enter Amount Greater than 1 MB");
                validity.setVisibility(View.INVISIBLE);
                setSeek.setVisibility(View.INVISIBLE);
                addData.setVisibility(View.INVISIBLE);
                showwarning.setVisibility(View.INVISIBLE);
            }
            else if((Double.parseDouble(getRechargeAmount.getText().toString())>=1 && data==1) || (Double.parseDouble(getRechargeAmount.getText().toString())>1 && data==0)) {
                validity.setVisibility(View.VISIBLE);
                if (!validity.getText().toString().equals("") && !validity.getText().toString().equals("0"))
                {
                    setSeek.setVisibility(View.VISIBLE);
                    addData.setVisibility(View.VISIBLE);
                    showwarning.setVisibility(View.VISIBLE);
                    if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                        if (data == 1) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) * ((double) warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                        } else if (data == 0) {
                            warndata = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024) * ((double) warn_level / 100);
                            String warn = bytesToHuman(warndata);
                            showwarning.setText("Current warning level = " + warn_level + " % (" + warn + ")");
                        }
                    }
            }
            }

        }
        else
        {
            errorDisp.setText("Enter Amount Greater than 1 MB");
            validity.setVisibility(View.INVISIBLE);
            setSeek.setVisibility(View.INVISIBLE);
            addData.setVisibility(View.INVISIBLE);
            showwarning.setVisibility(View.INVISIBLE);
        }

    }
    public void showUsage(View v)
    {

        Intent i = new Intent(Recharge.this,MainActivity.class).putExtra("TYPE",0);  // TYPE = 0 means no recharge
        startActivity(i);
    }

/* *************************   Getting and checking valid inputs (Ends)  ******************************* */










/* *************************   Storing and initializing files as per user given data (Starts)  ******************************* */

    public void addValidDataToFile()
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

/* *************************   Storing and initializing files as per user given data (Ends)  ******************************* */
