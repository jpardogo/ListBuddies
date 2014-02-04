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
import com.jpardogo.android.listbuddies.ui.ExampleActivity;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;


public class ListBuddiesFragment extends Fragment implements ListBuddiesLayout.OnBuddyItemClickListener {
    private static final String TAG = ListBuddiesFragment.class.getSimpleName();

    private MenuItem mStartActivityOnItemClick;

    public ListBuddiesFragment() {
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
        ListBuddiesLayout listBuddies = (ListBuddiesLayout) rootView.findViewById(R.id.listbuddies);
        CircularAdapter adapter = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), ImagesUrls.imageUrls_left);
        CircularAdapter adapter2 = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), ImagesUrls.imageUrls_right);
        listBuddies.setAdapters(adapter, adapter2);
        listBuddies.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id) {
        if (mStartActivityOnItemClick.isChecked()) {
            Intent intent = new Intent(getActivity(), ExampleActivity.class);
            startActivity(intent);
        } else {
            Resources resources = getResources();
            Toast.makeText(getActivity(), resources.getString(R.string.list) + ": " + buddy + " " + resources.getString(R.string.position) + ": " + position, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        mStartActivityOnItemClick = menu.findItem(R.id.item1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
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
}