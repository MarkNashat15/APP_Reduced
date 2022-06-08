package com.flir.flironeexampleapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class Activity2 extends AppCompatActivity {

    private Button button6;
    String Click_on_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Click_on_button = bundle.getString("CLICK");

            if (Click_on_button == null || Click_on_button.isEmpty()) {
                finish();
            }

        } else {
            finish();
        }



        button6 = (Button) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGLPreviewAcitvity();
            }
        });
    }

    public void openGLPreviewAcitvity() {
        Intent intent = new Intent(this, GLPreviewActivity.class);
        intent.putExtra("CLICK", Click_on_button);
        startActivity(intent);
    }
}