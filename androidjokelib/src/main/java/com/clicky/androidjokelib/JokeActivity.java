package com.clicky.androidjokelib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 *
 * Created by fabianrodriguez on 2/14/16.
 */
public class JokeActivity extends AppCompatActivity{

    public static final String EXTRA_JOKE = "joke";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke);

        String joke = getIntent().getExtras().getString(EXTRA_JOKE);
        if(joke != null)
            ((TextView)findViewById(R.id.label_joke)).setText(joke);
    }

}
