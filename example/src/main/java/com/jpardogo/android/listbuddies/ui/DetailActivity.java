package com.jpardogo.android.listbuddies.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.jpardogo.android.listbuddies.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    public static final String EXTRA_URL = "url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Picasso.with(this).load(imageUrl).fit().centerCrop().into((ImageView) findViewById(R.id.image));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}


