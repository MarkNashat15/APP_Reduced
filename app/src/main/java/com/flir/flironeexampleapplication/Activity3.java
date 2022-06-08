package com.flir.flironeexampleapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class Activity3 extends AppCompatActivity {

    private Button button4;
    String Click_on_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Click_on_button = bundle.getString("CLICK");

            if (Click_on_button == null || Click_on_button.isEmpty()) {
                finish();
            }

        } else {
            finish();
        }





        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGLPreviewActivity();
            }
        });
    }

    public void openGLPreviewActivity(){
        Intent intent = new Intent(this, GLPreviewActivity.class);
        intent.putExtra("CLICK", Click_on_button);
        startActivity(intent);
    }
}