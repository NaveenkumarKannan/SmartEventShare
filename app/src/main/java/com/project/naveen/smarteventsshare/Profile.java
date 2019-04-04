package com.project.naveen.smarteventsshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

public class Profile extends AppCompatActivity {
    SessionManager sessionManager;
    SlidingRootNav slidingRootNav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(100) //140  Horizontal translation of a view. Default == 180dp
                .withRootViewScale(1f) //0.7Content view's scale will be interpolated between 1f and 0.7f. Default == 0.65f;
                .withRootViewElevation(0) //10Content view's elevation will be interpolated between 0 and 10dp. Default == 8.
                .withRootViewYTranslation(0) //4Content view's translationY will be interpolated between 0 and 4. Default == 0

                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        initNavi();
    }
    private void initNavi() {
        LinearLayout llAll = (LinearLayout) findViewById(R.id.llAll);
        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, AllActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llTrending = (LinearLayout) findViewById(R.id.llTrending);
        llTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, TrendActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llOurActivities = (LinearLayout) findViewById(R.id.llOurActivities);
        llOurActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Uploadactivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, notification.class);
                startActivity(intent);
            }
        });
        LinearLayout llLogOut = (LinearLayout) findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Profile.this, "Logged out successful",Toast.LENGTH_SHORT).show();
                sessionManager.logoutUser();
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        intent = new Intent(Profile.this,TrendingWall.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_fav:
                        intent = new Intent(Profile.this, TrendActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_search:
                        intent = new Intent(Profile.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_profile:

                        return true;
                }
                return false;
            }
        });
    }

    public void openSaveImage(View view) {
        Intent intent = new Intent(Profile.this, PostImageActivity.class);
        startActivity(intent);
    }

    public void goEditProfile(View view) {
        Intent intent = new Intent(Profile.this, editProfile.class);
        startActivity(intent);
    }

}
