package com.project.naveen.smarteventsshare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

public class AllActivity extends AppCompatActivity {
    SlidingRootNav slidingRootNav;
    String type;

    ArrayList<AllActivityData> birthDayList,anniversaryList,otherList;

    RecyclerView rvBirthday,rvAnniversary,rvOther;
    AllAdapter allAdapterBirthday,allAdapterAnniversary,allAdapterOther;
    RecyclerView.LayoutManager blayoutManager,alayoutManager,olayoutManager;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);

        sessionManager = new SessionManager(this);

        birthDayList = new ArrayList<>();
        anniversaryList = new ArrayList<>();
        otherList = new ArrayList<>();

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

        rvBirthday = findViewById(R.id.rvBirthday);
        rvAnniversary = findViewById(R.id.rvAnniversary);
        rvOther = findViewById(R.id.rvOther);

        allAdapterBirthday = new AllAdapter(birthDayList);
        allAdapterAnniversary = new AllAdapter(anniversaryList);
        allAdapterOther = new AllAdapter(otherList);

        //vertical recyclerView
        blayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        alayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        olayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        //horizontal recyclerView
        //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvBirthday.setLayoutManager(blayoutManager);
        rvAnniversary.setLayoutManager(alayoutManager);
        rvOther.setLayoutManager(olayoutManager);

        rvBirthday.setHasFixedSize(true);
        rvAnniversary.setHasFixedSize(true);
        rvOther.setHasFixedSize(true);

        allAdapterBirthday.setClickListener(new AllAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                AllActivityData allActivityData = birthDayList.get(pos);
                String post_id = allActivityData.getPost_id();
                Intent intent = new Intent(AllActivity.this, Indivual.class);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
                /*
                Intent intent = new Intent(AllActivity.this, AllCategoryActivity.class);
                intent.putExtra("category","Birthday" );
                startActivity(intent);
                */
            }
        });
        allAdapterAnniversary.setClickListener(new AllAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                AllActivityData allActivityData = anniversaryList.get(pos);
                String post_id = allActivityData.getPost_id();
                Intent intent = new Intent(AllActivity.this, Indivual.class);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
                /*
                Intent intent = new Intent(AllActivity.this, AllCategoryActivity.class);
                intent.putExtra("category","Anniversary" );
                startActivity(intent);
                */
            }
        });
        allAdapterOther.setClickListener(new AllAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                AllActivityData allActivityData = otherList.get(pos);
                String post_id = allActivityData.getPost_id();
                Intent intent = new Intent(AllActivity.this, Indivual.class);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
                /*
                Intent intent = new Intent(AllActivity.this, AllCategoryActivity.class);
                intent.putExtra("category","Other" );
                startActivity(intent);
                */
            }
        });
        type = "getAll";
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();
        initNavi();
    }
    private void initNavi() {
        LinearLayout llAll = (LinearLayout) findViewById(R.id.llAll);
        llAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu();
            }
        });
        LinearLayout llTrending = (LinearLayout) findViewById(R.id.llTrending);
        llTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllActivity.this, TrendActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llOurActivities = (LinearLayout) findViewById(R.id.llOurActivities);
        llOurActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllActivity.this, Uploadactivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllActivity.this, notification.class);
                startActivity(intent);
            }
        });
        LinearLayout llLogOut = (LinearLayout) findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AllActivity.this, "Logged out successful",Toast.LENGTH_SHORT).show();
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
                        intent = new Intent(AllActivity.this,TrendingWall.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_fav:
                        intent = new Intent(AllActivity.this, TrendActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_search:
                        intent = new Intent(AllActivity.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_profile:
                        intent = new Intent(AllActivity.this, Profile.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    public void openSaveImage(View view) {
        Intent intent = new Intent(AllActivity.this, PostImageActivity.class);
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

                if(type.equals("getAll")) {
                    login_url = "http://nutzindia.com/SES_Wall/getAll.php";

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
            if(type.equals("getAll")) {
                json_string = result;

                if(result != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);

                        jsonArray = jsonObject.getJSONArray("Birthday");

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

                            AllActivityData allActivityData = new AllActivityData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id);
                            birthDayList.add(allActivityData);
                        }
                        rvBirthday.setAdapter(allAdapterBirthday);

                        jsonArray = jsonObject.getJSONArray("Anniversary");

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

                            AllActivityData allActivityData = new AllActivityData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id);
                            anniversaryList.add(allActivityData);
                        }
                        rvAnniversary.setAdapter(allAdapterAnniversary);

                        jsonArray = jsonObject.getJSONArray("Other");

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

                            AllActivityData allActivityData = new AllActivityData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id);
                            otherList.add(allActivityData);
                        }
                        rvOther.setAdapter(allAdapterOther);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e("getAll result","null");
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
