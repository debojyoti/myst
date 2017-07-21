package com.example.mkz.dataz;

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

        final EditText pack = (EditText) findViewById(R.id.packsize);
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
        });
    }
}
