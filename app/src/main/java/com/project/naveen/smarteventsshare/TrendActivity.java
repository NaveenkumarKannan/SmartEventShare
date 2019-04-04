package com.project.naveen.smarteventsshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.HashMap;

public class TrendActivity extends AppCompatActivity {
    SlidingRootNav slidingRootNav;
    SessionManager sessionManager;
    String type,district;

    RecyclerView rvTrend;
    TrendAdapter trendAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<TrendData> trendList = new ArrayList<TrendData>();
    TextView tvTrend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.getUserDetails();
        // name
        district = user.get(SessionManager.KEY_DISTRICT);

        tvTrend = findViewById(R.id.tvTrend);
        tvTrend.setText("Trending in "+district);
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

        rvTrend = findViewById(R.id.rvTrend);
        trendAdapter = new TrendAdapter(trendList);
        //vertical rvTrend
        layoutManager = new GridLayoutManager(this,2);
        //horizontal rvTrend
        //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvTrend.setLayoutManager(layoutManager);
        rvTrend.setHasFixedSize(true);

        trendAdapter.setClickListener(new TrendAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                TrendData trendData= trendList.get(pos);
                String post_id = trendData.getPost_id();
                Intent intent = new Intent(TrendActivity.this, Indivual.class);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
            }
        });
        type = "getTrend";
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();

        initNavi();
    }
    private void initNavi() {
        LinearLayout llAll = (LinearLayout) findViewById(R.id.llAll);
        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendActivity.this, AllActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llTrending = (LinearLayout) findViewById(R.id.llTrending);
        llTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
            }
        });
        LinearLayout llOurActivities = (LinearLayout) findViewById(R.id.llOurActivities);
        llOurActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendActivity.this, Uploadactivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrendActivity.this, notification.class);
                startActivity(intent);
            }
        });
        LinearLayout llLogOut = (LinearLayout) findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TrendActivity.this, "Logged out successful",Toast.LENGTH_SHORT).show();
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
                        intent = new Intent(TrendActivity.this,TrendingWall.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_fav:
                        Toast.makeText(TrendActivity.this,"You're already in Trending section." ,Toast.LENGTH_SHORT ).show();
                        return true;
                    case R.id.navigation_search:
                        intent = new Intent(TrendActivity.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_profile:
                        intent = new Intent(TrendActivity.this, Profile.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    public void openSaveImage(View view) {
        Intent intent = new Intent(TrendActivity.this, PostImageActivity.class);
        startActivity(intent);
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

                if(type.equals("getTrend")) {
                    login_url = "http://nutzindia.com/SES_Wall/getTrend.php";

                    post_data = URLEncoder.encode("district","UTF-8")+"="+ URLEncoder.encode(district,"UTF-8")
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
            if(type.equals("getTrend")) {
                json_string = result;

                if(result != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("getTrend");

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

                            TrendData trendData = new TrendData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id,true);
                            trendList.add(trendData);
                        }
                        rvTrend.setAdapter(trendAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e("getTrend result","null");
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
