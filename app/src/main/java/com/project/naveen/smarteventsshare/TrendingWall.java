package com.project.naveen.smarteventsshare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TrendingWall extends AppCompatActivity {
    SlidingRootNav slidingRootNav;
    SessionManager sessionManager;
    String type;

    RecyclerView recyclerView;
    TrendingWallAdapter trendingWallAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<TrendingWallData> arrayList = new ArrayList<TrendingWallData>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_wall);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

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

        recyclerView = findViewById(R.id.recyclerView);
        trendingWallAdapter = new TrendingWallAdapter(arrayList);
        //vertical recyclerView
        layoutManager = new GridLayoutManager(this,2);
        //horizontal recyclerView
        //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        trendingWallAdapter.setClickListener(new TrendingWallAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent = new Intent(TrendingWall.this, MainActivity.class);
                startActivity(intent);
            }
        });
        type = "getTrendingWall";
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();

        getLocation();
        initNavi();
    }
    private void initNavi() {
        LinearLayout llAll = (LinearLayout) findViewById(R.id.llAll);
        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        LinearLayout llTrending = (LinearLayout) findViewById(R.id.llTrending);
        llTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendingWall.this, TrendActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llOurActivities = (LinearLayout) findViewById(R.id.llOurActivities);
        llOurActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendingWall.this, Uploadactivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendingWall.this, notification.class);
                startActivity(intent);
            }
        });
        LinearLayout llLogOut = (LinearLayout) findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TrendingWall.this, "Logged out successful",Toast.LENGTH_SHORT).show();
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
                        intent = new Intent(TrendingWall.this,TrendingWall.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_fav:
                        intent = new Intent(TrendingWall.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_search:
                        intent = new Intent(TrendingWall.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_profile:
                        intent = new Intent(TrendingWall.this, Profile.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    public void openSaveImage(View view) {
        Intent intent = new Intent(TrendingWall.this, PostImageActivity.class);
        startActivity(intent);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {

        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    public static boolean isLocationEnabled(Context context)
    {
        int locationMode = 0;
        String locationProviders;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    protected void getLocation() {
        if (isLocationEnabled(TrendingWall.this))
        {
            /*
            Log.w("TAG", "Location is enabled");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            ,10);
                }
                return;
            }
            Log.w("Provider", bestProvider);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
            if (location != null) {
                Log.w("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String time = DateFormat.getTimeInstance().format(location.getTime());
                String latlng =  "Latitude: " + latitude + "\nLongitude: " + longitude+"\nTime: "+time;
                Log.w("GEO Location", latlng);
            }
            else{
                Log.w("TAG", "Location is null");
                //This is what you need:
                //locationManager.requestLocationUpdates(bestProvider, 1000, 0, listener);
            }
            */
        }
        else
        {
            //prompt user to enable location....
            android.app.AlertDialog.Builder notifyLocationServices = new android.app.AlertDialog.Builder(TrendingWall.this);
            notifyLocationServices.setTitle("Switch on Location Services");
            notifyLocationServices.setMessage("Location Services must be turned on to complete this action. Also please take note that if on a very weak network connection,  such as 'E' Mobile Data or 'Very weak Wifi-Connections' it may take even 15 mins to load. If on a very weak network connection as stated above, location returned to application may be null or nothing and cause the application to crash.");
            notifyLocationServices.setPositiveButton("Ok, Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent openLocationSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    TrendingWall.this.startActivity(openLocationSettings);
                    finish();
                }
            });
            notifyLocationServices.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            notifyLocationServices.show();
        }
    }

    public class BackgroundWorker extends AsyncTask<String,Void,String> {
        String json_string;
        JSONArray jsonArray;
        JSONObject jsonObject;
        @Override
        protected String doInBackground(String... params) {
            try {
                String post_data = null;
                String login_url = null;

                if(type.equals("getTrendingWall")) {
                    login_url = "http://nutzindia.com/SES_Wall/getTrendingWall.php";

                    post_data = URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode("  ","UTF-8")
                            +"&"+ URLEncoder.encode("post_image_string","UTF-8")+"="+ URLEncoder.encode(" ","UTF-8")
                    ;
                }else if(type.equals("like")) {
                    login_url = "http://nutzindia.com/SES_Wall/like.php";

                    post_data = URLEncoder.encode("post_id","UTF-8")+"="+ URLEncoder.encode(" ","UTF-8")
                            +"&"+ URLEncoder.encode("likes","UTF-8")+"="+ URLEncoder.encode(" ","UTF-8")
                    ;
                }

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            if(type.equals("getTrendingWall")) {
                json_string = result;

                if(result != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("getTrendingWall");

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String user_name,img_url,profileImgUrl,comment,date,views,likes,post_id;
                            Bitmap bitmap;
                            user_name = jo.getString("user_name");
                            img_url = jo.getString("img_url");
                            profileImgUrl = jo.getString("profileImgUrl");
                            comment = jo.getString("comment");
                            date = jo.getString("date");
                            views = jo.getString("views");
                            likes = jo.getString("likes");
                            post_id = jo.getString("post_id");

                            TrendingWallData trendingWallData = new TrendingWallData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id,true);
                            arrayList.add(trendingWallData);
                        }
                        recyclerView.setAdapter(trendingWallAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e("getTrendingWall result","null");
                }
            }else if(type.equals("like")) {
                if(result != null){
                    
                }else {
                    Log.e("Like result","null");
                }
            }


        }
    }
}
