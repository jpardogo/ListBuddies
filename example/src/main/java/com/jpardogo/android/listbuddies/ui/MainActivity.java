package com.jpardogo.android.listbuddies.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.provider.FragmentTags;
import com.jpardogo.android.listbuddies.ui.fragments.CustomizeFragment;
import com.jpardogo.android.listbuddies.ui.fragments.ListBuddiesFragment;


public class MainActivity extends ActionBarActivity implements CustomizeFragment.OnCustomizeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            manageFragment(ListBuddiesFragment.newInstance(), FragmentTags.LIST_BUDDIES, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_customize:
                manageFragment(CustomizeFragment.newInstance(), FragmentTags.CUSTOMIZE, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void manageFragment(Fragment newInstanceFragment, FragmentTags tag, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentIntanceFragment = findFragmentByTag(tag);
        if (currentIntanceFragment == null || (currentIntanceFragment != null && currentIntanceFragment.isHidden())) {
            if (currentIntanceFragment != null) {
                ft.show(currentIntanceFragment);
            } else {
                currentIntanceFragment = newInstanceFragment;
                ft.add(R.id.container, currentIntanceFragment, tag.toString());
                if (addToBackStack) {
                    ft.addToBackStack(null);
                }
            }
        } else {
            ft.hide(currentIntanceFragment);
        }
        ft.commit();
    }

    private Fragment findFragmentByTag(FragmentTags tag) {
        return getSupportFragmentManager().findFragmentByTag(tag.toString());
    }

    @Override
    public void setSpeed(int value) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setSpeed(value);
        }
    }

    @Override
    public void setGap(int value) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setGap(value);
        }
    }

    @Override
    public void fillGap(int color) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.fillGap(color);
        }
    }

    @Override
    public void setDivider(Drawable drawable) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setDivider(drawable);
        }
    }

    @Override
    public void setDividerHeight(int value) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setDividerHeight(value);
        }
    }

    @Override
    public void setAutoScrollFaster(int option) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setAutoScrollFaster(option);
        }
    }

    @Override
    public void setScrollFaster(int option) {
        ListBuddiesFragment fragment = (ListBuddiesFragment) findFragmentByTag(FragmentTags.LIST_BUDDIES);
        if (fragment != null) {
            fragment.setScrollFaster(option);
        }
    }
}
