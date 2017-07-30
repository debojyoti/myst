package com.example.mkz.dataz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.FileOutputStream;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    public void getStarted(View v){


         /*   ******* Creating file "Remaining.txt" = 0  starts   ************************ */
        try
        {
            String FILENAME = "Remaining.txt";
            FileOutputStream fileOutputStream2 = openFileOutput(FILENAME,MODE_PRIVATE);
            byte[] buf = "0".getBytes();
            fileOutputStream2.write(buf);
            fileOutputStream2.close();
        }
        catch (Exception e)
        {

        }
           /*   ******* Creating file "Reamining.txt" = 0  ends   ************************ */
        mystfileWrite("Reboot.txt","0");

        Intent i = new Intent(Intro.this,Recharge.class);
        startActivity(i);
        finish();
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
}
