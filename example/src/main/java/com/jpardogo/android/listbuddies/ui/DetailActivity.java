package com.jpardogo.android.listbuddies.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.jpardogo.android.listbuddies.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends BaseActivity {

    public static final String EXTRA_URL = "url";
    @InjectView(R.id.image)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.inject(this);
        mBackground = mImageView;
        String imageUrl = getIntent().getExtras().getString(EXTRA_URL);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Picasso.with(this).load(imageUrl).into((ImageView) findViewById(R.id.image), new Callback() {
            @Override
            public void onSuccess() {
                moveBackground();
            }

            @Override
            public void onError() {
            }
        });
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


