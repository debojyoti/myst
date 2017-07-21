package com.example.mkz.dataz;


/*      This is the main launching page which will divert user automatically based on data exhausted or not
        --- @Debojyoti  */



import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);



        /*  This is the thread which will redirect the launching page to another activity after 1 second


        * If data pack is exhausted, redirect to "Recharge.java" activity
        * Otherwise redirect to "MainActivity.java" - the page contains usage information
        *
        * @ Debojyoti


        * */

        Thread th = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(1000);

                    Intent i = new Intent(Launch.this,Recharge.class);

                    startActivity(i);
                }
                catch(Exception e)
                {

                }
            }
        };

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
