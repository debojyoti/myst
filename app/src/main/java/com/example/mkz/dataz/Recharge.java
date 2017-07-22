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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import java.io.FileOutputStream;
import java.util.Date;

public class Recharge extends AppCompatActivity {






/* *************************   Getting valid input (Starts)  ******************************* */


    Button addData;
    TextView errorDisp;
    EditText getRechargeAmount;
    EditText validity;
    int data = -1;
    String dSize;
    String date;
    long dataSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        addData = (Button) findViewById(R.id.rechargeBtn);
        errorDisp = (TextView) findViewById(R.id.errorDisp);
        getRechargeAmount = (EditText) findViewById(R.id.getRechargeAmount);
        validity = (EditText) findViewById(R.id.validity);
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
                if (dSize.equals("") || dSize.equals("0")) {
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
    }
    public void showUsage(View v)
    {
        Intent i = new Intent(Recharge.this,MainActivity.class).putExtra("TYPE",0);  // TYPE = 0 means no recharge
        startActivity(i);
    }

/* *************************   Getting valid input (Ends)  ******************************* */










/* *************************   Storing the valid input in the file "RechargeData.txt" (Starts)  ******************************* */

    public void addValidDataToFile()
    {
        try
        {
            String FILENAME = "RechargeData.txt";
            FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);
            long newData = (long) Integer.parseInt(dSize);
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
            String data = String.valueOf(newData);
            byte buf[] = data.getBytes();
            fileOutputStream.write(buf);
            fileOutputStream.close();








        }
        catch (Exception e)
        {

        }
        try
        {

            String FILENAME33 = "ExpDate.txt";

            FileOutputStream fileOutputStream33 = openFileOutput(FILENAME33,MODE_PRIVATE);

            byte buf[] = date.getBytes();

            fileOutputStream33.write(buf);
            fileOutputStream33.close();
        }
        catch (Exception e)
        {

        }
        try
        {

            String FILENAME44 = "CurrentDate.txt";

            FileOutputStream fileOutputStream44 = openFileOutput(FILENAME44,MODE_PRIVATE);
            Date d = new Date(new Date().getTime());
            String cDate  = (String) DateFormat.format("dd", d.getTime());
            byte buf[] = cDate.getBytes();

            fileOutputStream44.write(buf);
            fileOutputStream44.close();
        }
        catch (Exception e)
        {

        }

        try
        {
            String s1 = String.valueOf(dataSize);
            String FILENAME = "Remaining.txt";

            FileOutputStream fileOutputStream = openFileOutput(FILENAME,MODE_PRIVATE);

            byte buf[] = s1.getBytes();

            fileOutputStream.write(buf);
            fileOutputStream.close();
        }
        catch (Exception e)
        {

        }



        /* **************** Taking rx+tx at the time of recharge  Starts******************* */
        try
        {
            String FILENAME = "Initial.txt";
            long rxbytes = TrafficStats.getMobileRxBytes();
            long txbytes = TrafficStats.getMobileTxBytes();
            long totalbytes = rxbytes+txbytes;
            FileOutputStream fileOutputStream2 = openFileOutput(FILENAME,MODE_PRIVATE);
            byte[] buf = String.valueOf(totalbytes).getBytes();
            fileOutputStream2.write(buf);
            fileOutputStream2.close();
        }
        catch (Exception e)
        {

        }
        /* **************** Taking rx+tx at the time of recharge  Ends******************* */



        /*           Send to the usage page  (to MainActivity.java)          */
        Intent i = new Intent(Recharge.this,MainActivity.class).putExtra("TYPE",1);  // TYPE = 1 means new recharge
        startActivity(i);
    }


}

/* *************************   Storing the valid input in the file "RechargeData.txt" (Ends)  ******************************* */
