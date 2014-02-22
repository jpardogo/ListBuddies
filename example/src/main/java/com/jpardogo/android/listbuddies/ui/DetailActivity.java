package com.jpardogo.android.listbuddies.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.jpardogo.android.listbuddies.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    public static final String EXTRA_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_activity);
        String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
        Picasso.with(this).load(imageUrl).fit().centerCrop().into((ImageView) findViewById(R.id.image));

    }
}
