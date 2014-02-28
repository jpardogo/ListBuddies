package com.jpardogo.android.listbuddies.ui;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.Utils.Utils;

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
        mTextView.setText(Html.fromHtml(getString(R.string.about_cody, Utils.getVersionName(this))));
        mTextView.setMovementMethod(new LinkMovementMethod());

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (Utils.hasHoneycomb()) {
            View demoContainerView = findViewById(R.id.image);
            demoContainerView.setAlpha(0);
            ViewPropertyAnimator animator = demoContainerView.animate();
            animator.alpha(1);
            if (Utils.hasICS()) {
                animator.setStartDelay(250);
            }
            animator.setDuration(1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}

