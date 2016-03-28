package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.clicky.androidjokelib.JokeActivity;
import com.clicky.jokes.backend.myApi.MyApi;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.udacity.gradle.builditbigger.R;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                new EndpointsAsyncTask().execute(MainActivity.this);
            }
        });

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle(R.string.dialog_loading);
        loadingDialog.setMessage("Hang on tight");
        loadingDialog.setCancelable(false);

        requestNewInterstitial();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("14CD27A43C86BE199CCB8A908F05242B")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void tellJoke(View view){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        /*
        else {

            new EndpointsAsyncTask().execute(this);
        }
        /*
        Joker joke = new Joker();
        Intent i = new Intent(MainActivity.this, JokeActivity.class);
        i.putExtra(JokeActivity.EXTRA_JOKE, joke.getJoke());
        startActivity(i);
        */
        //Toast.makeText(this, joke.getJoke(), Toast.LENGTH_SHORT).show();
    }

    class EndpointsAsyncTask extends AsyncTask<Context, Void, String> {
        private MyApi myApiService = null;
        private Context context;

        @Override
        protected void onPreExecute(){
            if(!loadingDialog.isShowing()){
                loadingDialog.show();
            }
        }

        @Override
        protected String doInBackground(Context... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://udacity-1222.appspot.com/_ah/api/");
                // end options for devappserver

                myApiService = builder.build();
            }

            context = params[0];

            try {
                return myApiService.sayHi().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
            if(result != null) {
                Intent i = new Intent(context, JokeActivity.class);
                i.putExtra(JokeActivity.EXTRA_JOKE, result);
                startActivity(i);
            }else
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
        }
    }


}
