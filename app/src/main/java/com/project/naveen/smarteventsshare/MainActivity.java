package com.project.naveen.smarteventsshare;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

public class MainActivity extends AppCompatActivity {
    SlidingRootNav slidingRootNav;
    RecyclerView recyclerView;
    MainActivityAdapter mainActivityAdapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<MainActivityData> arrayList = new ArrayList<MainActivityData>();
    String type;
    String likes,post_id,views;
    Boolean like;
    MainActivityData mainActivityData;
    int position;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        mainActivityAdapter = new MainActivityAdapter(arrayList);
        //vertical recyclerView
        layoutManager = new LinearLayoutManager(this);
        //horizontal recyclerView
        //layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        mainActivityAdapter.setClickListener(new MainActivityAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                position = pos;
                mainActivityData = arrayList.get(position);
                post_id = mainActivityData.getPost_id();
                switch (view.getId()){
                    case R.id.llHearts:
                        int hearts = Integer.parseInt(mainActivityData.getLikes());
                        like = mainActivityData.getLike();
                        if(like){
                            likes = String.valueOf(hearts+1);
                            Log.e("onItemClick", "likes="+likes);
                            like = false;
                        }else{
                            likes = String.valueOf(hearts-1);
                            Log.e("onItemClick", "likes="+likes);
                            like = true;
                        }

                        type = "like";
                        break;
                    case R.id.ivView:
                        view.setVisibility(View.GONE);

                        int getViews = Integer.parseInt(mainActivityData.getViews());
                        views = String.valueOf(getViews+1);
                        type = "views";
                        break;
                    default:
                        Intent intent = new Intent(MainActivity.this,Indivual.class);
                        intent.putExtra("post_id", post_id);
                        startActivity(intent);

                }
                BackgroundWorker backgroundWorker = new BackgroundWorker();
                backgroundWorker.execute();
            }
        });

        type = "getPost";
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();

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
                Intent intent = new Intent(MainActivity.this, AllActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llTrending = (LinearLayout) findViewById(R.id.llTrending);
        llTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrendActivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llOurActivities = (LinearLayout) findViewById(R.id.llOurActivities);
        llOurActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Uploadactivity.class);
                startActivity(intent);
            }
        });
        LinearLayout llNotification = (LinearLayout) findViewById(R.id.llNotification);
        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, notification.class);
                startActivity(intent);
            }
        });
        LinearLayout llLogOut = (LinearLayout) findViewById(R.id.llLogOut);
        llLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Logged out successful",Toast.LENGTH_SHORT).show();
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
                        intent = new Intent(MainActivity.this,TrendingWall.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_fav:
                        intent = new Intent(MainActivity.this, TrendActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_search:
                        intent = new Intent(MainActivity.this, Uploadactivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_profile:
                        intent = new Intent(MainActivity.this, Profile.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });
    }
    public void openSaveImage(View view) {
        Intent intent = new Intent(MainActivity.this, PostImageActivity.class);
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

                if(type.equals("getPost")) {
                    login_url = "http://nutzindia.com/SES_Wall/getPost.php";

                    post_data = URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode("  ","UTF-8")
                            +"&"+ URLEncoder.encode("post_image_string","UTF-8")+"="+ URLEncoder.encode(" ","UTF-8")
                    ;
                }else if(type.equals("like")) {
                    login_url = "http://nutzindia.com/SES_Wall/like.php";

                    post_data = URLEncoder.encode("post_id","UTF-8")+"="+ URLEncoder.encode(post_id,"UTF-8")
                            +"&"+ URLEncoder.encode("likes","UTF-8")+"="+ URLEncoder.encode(likes,"UTF-8")
                    ;
                }else if(type.equals("views")) {
                    login_url = "http://nutzindia.com/SES_Wall/view.php";

                    post_data = URLEncoder.encode("post_id","UTF-8")+"="+ URLEncoder.encode(post_id,"UTF-8")
                            +"&"+ URLEncoder.encode("views","UTF-8")+"="+ URLEncoder.encode(views,"UTF-8")
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
            if(type.equals("getPost")) {
                json_string = result;

                if(result != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("getPost");

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

                            MainActivityData mainActivityData = new MainActivityData(user_name,img_url,profileImgUrl,comment,date,views,likes,post_id,true);
                            arrayList.add(mainActivityData);
                        }
                        recyclerView.setAdapter(mainActivityAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(type.equals("like")) {
                if(result != null){
                    Log.e("Like result",likes);
                    if(result.charAt(0)=='L'){
                        mainActivityData.setLikes(likes);
                        mainActivityData.setLike(like);
                        arrayList.set(position,mainActivityData);
                        mainActivityAdapter.notifyDataSetChanged();
                    }else {
                        Log.e("Like not added","Error like"+result );
                    }
                }else {
                    Log.e("Like result","null");
                }
            }else if(type.equals("views")) {
                if(result != null){
                    Log.e("Views result",result);
                    mainActivityData.setViews(views);
                    arrayList.set(position,mainActivityData);
                    mainActivityAdapter.notifyDataSetChanged();
                }else {
                    Log.e("Views result","null");
                }
            }


        }
    }
    //new DownloadImage().execute("http://nutzindia.com/SES_Wall/uploads/naveen/naveen25102018201958.jpg");
    private void setImage(Bitmap drawable)
    {
        //viewImage.setImageBitmap(drawable);
    }
    public class DownloadImage extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0]);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Bitmap image) {
            setImage(image);
        }


        /**
         * Actually download the Image from the _url
         *
         * @param _url
         * @return
         */
        private Bitmap downloadImage(String _url) {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            //BufferedInputStream buf;
            try {
                url = new URL(_url);
                in = url.openStream();

            /*
             * THIS IS NOT NEEDED
             *
             * YOU TRY TO CREATE AN ACTUAL IMAGE HERE, BY WRITING
             * TO A NEW FILE
             * YOU ONLY NEED TO READ THE INPUTSTREAM
             * AND CONVERT THAT TO A BITMAP
            out = new BufferedOutputStream(new FileOutputStream("testImage.jpg"));
            int i;

             while ((i = in.read()) != -1) {
                 out.write(i);
             }
             out.close();
             in.close();
             */

                // Read the inputstream
                buf = new BufferedInputStream(in);

                // Convert the BufferedInputStream to a Bitmap
                Bitmap bMap = BitmapFactory.decodeStream(buf);
                if (in != null) {
                    in.close();
                }
                if (buf != null) {
                    buf.close();
                }

                return bMap;

            } catch (Exception e) {
                Log.e("Error reading file", e.toString());
            }

            return null;
        }
    }

}
