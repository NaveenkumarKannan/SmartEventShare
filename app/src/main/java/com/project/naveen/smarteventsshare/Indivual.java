package com.project.naveen.smarteventsshare;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

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

import butterknife.ButterKnife;

public class Indivual extends AppCompatActivity {
    String type,post_id;
    ImageView ivView;
    TextView tvName,tvCaption,tvViews,tvHearts,tvPostId;
    SimpleDraweeView profileImg,imageView;
    LinearLayout llHearts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indivual);

        imageView = ButterKnife.findById(this,R.id.imageView);
        tvName = findViewById(R.id.tvName);
        profileImg = ButterKnife.findById(this,R.id.profileImg);

        tvCaption = findViewById(R.id.tvCaption);
        tvViews = findViewById(R.id.tvViews);
        tvHearts = findViewById(R.id.tvHearts);

        post_id = getIntent().getStringExtra("post_id");
        type = "getIndivual";
        BackgroundWorker backgroundWorker = new BackgroundWorker();
        backgroundWorker.execute();
    }
    public void goBack(View view) {
        finish();
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

                if(type.equals("getIndivual")) {
                    login_url = "http://nutzindia.com/SES_Wall/getIndivual.php";

                    post_data = URLEncoder.encode("post_id","UTF-8")+"="+ URLEncoder.encode(post_id,"UTF-8")
                            +"&"+ URLEncoder.encode("post_image_string","UTF-8")+"="+ URLEncoder.encode(" ","UTF-8")
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
            if(type.equals("getIndivual")) {
                json_string = result;

                if(result != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("getIndivual");

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

                            tvName.setText(user_name);
                            imageView.setImageURI(Uri.parse(img_url));
                            profileImg.setImageURI(Uri.parse(profileImgUrl));

                            tvCaption.setText(comment+" @"+date);
                            tvViews.setText(views);
                            tvHearts.setText(likes);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Log.e("getIndivual result","null");
                }
            }


        }
    }
}
