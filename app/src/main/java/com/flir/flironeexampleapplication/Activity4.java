package com.flir.flironeexampleapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class Activity4 extends AppCompatActivity {

    private Button button8;
    String Click_on_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Click_on_button = bundle.getString("CLICK");

            if (Click_on_button == null || Click_on_button.isEmpty()) {
                finish();
            }

        } else {
            finish();
        }





        button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGLPreviewActivityFEV1();
            }
        });
    }

    public void openGLPreviewActivityFEV1(){
        Intent intent = new Intent(this, GLPreviewActivityFEV1.class);
        intent.putExtra("CLICK", Click_on_button);
        startActivity(intent);
    }
}