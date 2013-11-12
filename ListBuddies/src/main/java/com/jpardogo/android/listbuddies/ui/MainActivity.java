package com.jpardogo.android.listbuddies.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.ui.fragments.ListBuddiesFragment;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ListBuddiesFragment())
                    .commit();
        }
    }

}
