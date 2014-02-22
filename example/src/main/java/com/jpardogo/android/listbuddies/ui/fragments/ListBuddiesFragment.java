package com.jpardogo.android.listbuddies.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CircularAdapter;
import com.jpardogo.android.listbuddies.provider.ImagesUrls;
import com.jpardogo.android.listbuddies.ui.DetailActivity;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ListBuddiesFragment extends Fragment implements ListBuddiesLayout.OnBuddyItemClickListener {
    private static final String TAG = ListBuddiesFragment.class.getSimpleName();

    private MenuItem mOpenActivities;
    private CircularAdapter mAdapterLeft;
    private CircularAdapter mAdapterRight;
    @InjectView(R.id.listbuddies)
    ListBuddiesLayout mListBuddies;

    public static Fragment newInstance() {
        return new ListBuddiesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);
        mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), ImagesUrls.imageUrls_left);
        mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), ImagesUrls.imageUrls_right);
        mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
        mListBuddies.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id) {
        if (mOpenActivities.isChecked()) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_URL, getImage(buddy, position));
            startActivity(intent);
        } else {
            Resources resources = getResources();
            Toast.makeText(getActivity(), resources.getString(R.string.list) + ": " + buddy + " " + resources.getString(R.string.position) + ": " + position, Toast.LENGTH_SHORT).show();
        }
    }

    private String getImage(int buddy, int position) {
        return buddy == 0 ? ImagesUrls.imageUrls_left[position] : ImagesUrls.imageUrls_right[position];
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_listbuddies, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mOpenActivities = menu.findItem(R.id.checkbox_open_activities);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkbox_open_activities:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setGap(int value) {
//        mListBuddies.setGap(value);
    }
}