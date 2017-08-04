package com.example.mkz.dataz;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

public class AddData extends AppCompatActivity {

    Button addData;
    EditText getRechargeAmount;
    RadioGroup mbgb;
    TextView curRemaining;
    TextView totalPack;
    public  double dataBytes;

    public long data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);


        addData = (Button) findViewById(R.id.button7);
        getRechargeAmount = (EditText) findViewById(R.id.editText);
        mbgb = (RadioGroup) findViewById(R.id.dataType);
        curRemaining = (TextView) findViewById(R.id.textView11);
        totalPack = (TextView) findViewById(R.id.textView12);


        curRemaining.setText("Currently Remaining : "+bytesToHuman(Double.parseDouble(mystfileRead("Remaining.txt"))));
        totalPack.setText("Your Current Data Pack : "+bytesToHuman(Double.parseDouble(mystfileRead("RechargeData.txt"))));

        getRechargeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!getRechargeAmount.getText().toString().equals("") && !getRechargeAmount.getText().toString().equals(".")) {


                    if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1 && data==1) {

                        addData.setVisibility(View.VISIBLE);
                        if (data == 1) {
                            dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) ;

                        } else if (data == 0) {
                            dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 ) ;

                        }
                    } else if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 2 && data == 0) {

                        if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                            addData.setVisibility(View.VISIBLE);
                            if (data == 1) {
                                dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) ;

                            } else if (data == 0) {
                                dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 ) ;

                            }
                        }
                    } else

                    {
                        addData.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(AddData.this);

                alert.setTitle("Do you want to change data pack?");
                // alert.setMessage("Message");

                alert.setPositiveButton("Add Data", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        long prevData = Long.parseLong(mystfileRead("RechargeData.txt"));
                        long total = prevData + (long)dataBytes;
                        mystfileWrite("RechargeData.txt",String.valueOf(total));
                        long used,remaining;
                        used = Long.parseLong(mystfileRead("Used.txt"));
                        remaining = total - used;
                        mystfileWrite("Remaining.txt",String.valueOf(remaining));
                        Intent i = new Intent(AddData.this,MainActivity.class);

                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(i);
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                alert.show();


            }
        });
    }

    public void backToMainActivity(View v)
    {
        Intent i = new Intent(AddData.this,MainActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
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

                addData.setVisibility(View.VISIBLE);
                if (data == 1) {
                    dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) ;

                } else if (data == 0) {
                    dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 ) ;

                }
            } else if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 2 && data == 0) {

                if (Double.parseDouble(getRechargeAmount.getText().toString()) >= 1) {
                    addData.setVisibility(View.VISIBLE);
                    if (data == 1) {
                        dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 * 1024) ;

                    } else if (data == 0) {
                        dataBytes = (double) (Double.parseDouble(getRechargeAmount.getText().toString()) * 1024 * 1024 ) ;

                    }
                }
            } else

            {
                addData.setVisibility(View.INVISIBLE);
            }
        }
    }

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
