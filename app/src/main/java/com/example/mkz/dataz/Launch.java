package com.example.mkz.dataz;


/*      This is the main launching page which will divert user automatically based on data exhausted or not

        * If data pack is exhausted, redirect to "Recharge.java" activity
        * Otherwise redirect to "MainActivity.java" - the page contains usage information

        --- @Debojyoti  */



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Launch extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Thread th = new Thread()   //  This is the thread which will redirect the launching page to another activity after 1 second
        {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(1000);     // Wait for 1 second






/* ******************************   File read operation Starts ****************************** */
                    String FILENAME = "Remaining.txt";      // File contains remaining data
                    FileInputStream fis = openFileInput(FILENAME);

                    int read=-1;
                    StringBuffer buffer = new StringBuffer();
                    while((read=fis.read())!=-1)
                    {
                        buffer.append((char)read);
                    }
                    String remaining = (buffer.toString());     // Read value stored in this String
                    fis.close();
/* ******************************   File read operation Ends **************************** */








/* *****************************   Redirection code Starts ************************** */
                    Intent i;
                    if(remaining.equals("0"))
                    {
                        i = new Intent(Launch.this,Recharge.class);
                    }
                    else
                    {
                        i = new Intent(Launch.this,MainActivity.class);
                    }
                    startActivity(i);
                    finish();   //  while it redirect to new activity , this (current) activity will be destroyed : @ Debojyoti
                }



                /* **********  For first launch(Newly installed) : Redirect to Intro.java   Starts ********** */
                catch(FileNotFoundException e)
                {
                    Intent i = new Intent(Launch.this,Intro.class);
                    startActivity(i);
                    finish();   //  while it redirect to new activity , this (current) activity will be destroyed : @ Debojyoti
                }
                /* **********  For first launch(Newly installed) : Redirect to Intro.java   Ends ********** */
                catch (Exception e)
                {

                }
            }
        };
        th.start();
/* *****************************   Redirection code Ends ************************** */








    }
}
















/*                          abandoned code              */

/* final EditText pack = (EditText) findViewById(R.id.packsize);
        Button proceed = (Button) findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Launch.this,MainActivity.class);

                String packval = pack.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("packval",packval);

                intent.putExtras(bundle);

                startActivity(intent);

            }
        });*/
