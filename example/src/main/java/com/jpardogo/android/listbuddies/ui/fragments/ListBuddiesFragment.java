package com.jpardogo.android.listbuddies.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CircularAdapter;
import com.jpardogo.android.listbuddies.provider.ExtraArgumentKeys;
import com.jpardogo.android.listbuddies.provider.ImagesUrls;
import com.jpardogo.android.listbuddies.ui.DetailActivity;
import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ListBuddiesFragment extends Fragment implements ListBuddiesLayout.OnBuddyItemClickListener {
    private static final String TAG = ListBuddiesFragment.class.getSimpleName();
    int mMarginDefault;
    int[] mScrollConfig;
    private boolean isOpenActivities;
    private CircularAdapter mAdapterLeft;
    private CircularAdapter mAdapterRight;
    @InjectView(R.id.listbuddies)
    ListBuddiesLayout mListBuddies;
    private List<String> mImagesLeft = new ArrayList<String>();
    private List<String> mImagesRight = new ArrayList<String>();

    public static ListBuddiesFragment newInstance(boolean isOpenActivitiesActivated) {
        ListBuddiesFragment fragment = new ListBuddiesFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ExtraArgumentKeys.OPEN_ACTIVITES.toString(), isOpenActivitiesActivated);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOpenActivities = getArguments().getBoolean(ExtraArgumentKeys.OPEN_ACTIVITES.toString(), false);
        mMarginDefault = getResources().getDimensionPixelSize(com.jpardogo.listbuddies.lib.R.dimen.default_margin_between_lists);
        mScrollConfig = getResources().getIntArray(R.attr.scrollFaster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        //If we do this we need to uncomment the container on the xml layout
        //createListBuddiesLayoutDinamically(rootView);
        mImagesLeft.addAll(Arrays.asList(ImagesUrls.imageUrls_left));
        mImagesRight.addAll(Arrays.asList(ImagesUrls.imageUrls_right));
        mAdapterLeft = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_small), mImagesLeft);
        mAdapterRight = new CircularAdapter(getActivity(), getResources().getDimensionPixelSize(R.dimen.item_height_tall), mImagesRight);
        mListBuddies.setAdapters(mAdapterLeft, mAdapterRight);
        mListBuddies.setOnItemClickListener(this);
        return rootView;
    }

    private void createListBuddiesLayoutDinamically(View rootView) {
        mListBuddies = new ListBuddiesLayout(getActivity());
        resetLayout();
        //Once the container is created we can add the ListViewLayout into it
        //((FrameLayout)rootView.findViewById(R.id.<container_id>)).addView(mListBuddies);
    }

    @Override
    public void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id) {
        if (isOpenActivities) {
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

    public void setGap(int value) {
        mListBuddies.setGap(value);
    }

    public void setSpeed(int value) {
        mListBuddies.setSpeed(value);
    }

    public void setDividerHeight(int value) {
        mListBuddies.setDividerHeight(value);
    }

    public void setGapColor(int color) {
        mListBuddies.setGapColor(color);
    }

    public void setAutoScrollFaster(int option) {
        mListBuddies.setAutoScrollFaster(option);
    }

    public void setScrollFaster(int option) {
        mListBuddies.setManualScrollFaster(option);
    }

    public void setDivider(Drawable drawable) {
        mListBuddies.setDivider(drawable);
    }

    public void setOpenActivities(Boolean openActivities) {
        this.isOpenActivities = openActivities;
    }

    public void resetLayout() {
        mListBuddies.setGap(mMarginDefault)
                .setSpeed(ListBuddiesLayout.DEFAULT_SPEED)
                .setDividerHeight(mMarginDefault)
                .setGapColor(getResources().getColor(R.color.frame))
                .setAutoScrollFaster(mScrollConfig[ScrollConfigOptions.RIGHT.getConfigValue()])
                .setManualScrollFaster(mScrollConfig[ScrollConfigOptions.LEFT.getConfigValue()])
                .setDivider(getResources().getDrawable(R.drawable.divider));
    }
}