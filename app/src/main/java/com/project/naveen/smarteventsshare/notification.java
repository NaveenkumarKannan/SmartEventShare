package com.project.naveen.smarteventsshare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
    }
    public void goBack(View view) {
        finish();
    }
}
