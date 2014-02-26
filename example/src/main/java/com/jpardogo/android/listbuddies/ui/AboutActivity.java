package com.jpardogo.android.listbuddies.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.jpardogo.android.listbuddies.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jpardogo on 23/02/2014.
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.about_body)
    TextView mTextView;
    @InjectView(R.id.image)
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);
        mBackground = mImageView;
        moveBackground();
        mTextView.setText(Html.fromHtml(getString(R.string.about_cody)));
        mTextView.setMovementMethod(new LinkMovementMethod());

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}

