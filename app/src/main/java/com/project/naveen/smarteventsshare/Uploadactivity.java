package com.project.naveen.smarteventsshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Uploadactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadactivity);
    }

    public void goBack(View view) {
        finish();
    }
}
