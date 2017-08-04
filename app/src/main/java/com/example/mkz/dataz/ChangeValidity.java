package com.example.mkz.dataz;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ChangeValidity extends AppCompatActivity {
    Button changeVal;
    TextView remain;
    TextView totalData;
    TextView curExp;
    EditText getVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_validity);

        changeVal = (Button) findViewById(R.id.button7);
        remain = (TextView) findViewById(R.id.textView11);
        totalData = (TextView) findViewById(R.id.textView12);
        curExp = (TextView) findViewById(R.id.textView15);
        getVal = (EditText) findViewById(R.id.editText);

        remain.setText("Currently Remaining : "+bytesToHuman(Double.parseDouble(mystfileRead("Remaining.txt"))));
        totalData.setText("Your Current data Pack : "+bytesToHuman(Double.parseDouble(mystfileRead("RechargeData.txt"))));

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
        curExp.setText(" "+diff+" Days");

        getVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!getVal.getText().toString().equals("") && !getVal.getText().toString().equals("0"))
                {
                    changeVal.setVisibility(View.VISIBLE);
                }
                else
                {
                    changeVal.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void backToMainActivity(View v)
    {


        Intent i = new Intent(ChangeValidity.this,MainActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(i);
    }

    public void changeVal(View v)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Are you sure to change validity?");
        // alert.setMessage("Message");

        alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String date=getVal.getText().toString();

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



                Intent i = new Intent(ChangeValidity.this,MainActivity.class);

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
