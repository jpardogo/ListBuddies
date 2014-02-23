package com.jpardogo.android.listbuddies.ui.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CustomizeSpinnersAdapter;
import com.jpardogo.android.listbuddies.models.KeyValuePair;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jpardogo on 22/02/2014.
 */
public class CustomizeFragment extends Fragment {
    private static final String TAG = CustomizeFragment.class.getSimpleName();
    @InjectView(R.id.seekBarGap)
    SeekBar mSeekBarGap;
    @InjectView(R.id.seekBarGapValue)
    TextView seekBarGapValue;

    @InjectView(R.id.seekBarSpeed)
    SeekBar mSeekBarSpeed;
    @InjectView(R.id.seekBarSpeedValue)
    TextView seekBarSpeedValue;

    @InjectView(R.id.seekBarDivHeight)
    SeekBar mSeekBarDivHeight;
    @InjectView(R.id.seekBarDivHeightValue)
    TextView seekBarDivHeightValue;

    @InjectView(R.id.fillGapSpinner)
    Spinner mFillGapSpinner;
    @InjectView(R.id.scrollSpinner)
    Spinner mScrollSpinner;
    @InjectView(R.id.autoScrollSpinner)
    Spinner mAutoScrollSpinner;
    @InjectView(R.id.dividerSpinner)
    Spinner mDividerSpinner;

    private OnCustomizeListener mOnCustomizeListener;
    private CustomizeSpinnersAdapter mSpinnerAdapter;
    private List<KeyValuePair> mColorSpinnerSections;
    private List<KeyValuePair> mScrollSpinnerSections;
    private int[] mScrollSpinnerValues;

    public static Fragment newInstance() {
        return new CustomizeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScrollSpinnerValues = getActivity().getResources().getIntArray(R.attr.scrollFaster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customize, container, false);
        ButterKnife.inject(this, rootView);
        mSeekBarGap.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBarSpeed.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBarDivHeight.setOnSeekBarChangeListener(mSeekBarListener);
        initFillSpinners();
        return rootView;
    }

    private void initFillSpinners() {
        initFillGatSpinner();
        initScrollSpinner();
        initAutoScrollSpinner();
        initDividerSpinner();
    }

    private void initFillGatSpinner() {
        mColorSpinnerSections = getFillGapSpinnerItems();
        mSpinnerAdapter = new CustomizeSpinnersAdapter(getActivity(), mColorSpinnerSections, "Fill gap");
        mFillGapSpinner.setAdapter(mSpinnerAdapter);
        mFillGapSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    private void initScrollSpinner() {
        mScrollSpinnerSections = getScrollItems();
        mSpinnerAdapter = new CustomizeSpinnersAdapter(getActivity(), mScrollSpinnerSections, "Manual scroll");
        mScrollSpinner.setAdapter(mSpinnerAdapter);
        mScrollSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    private void initAutoScrollSpinner() {
        mScrollSpinnerSections = getScrollItems();
        mSpinnerAdapter = new CustomizeSpinnersAdapter(getActivity(), mScrollSpinnerSections, "Fast auto scroll");
        mAutoScrollSpinner.setAdapter(mSpinnerAdapter);
        mAutoScrollSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    private void initDividerSpinner() {
        mColorSpinnerSections = getFillGapSpinnerItems();
        mSpinnerAdapter = new CustomizeSpinnersAdapter(getActivity(), mColorSpinnerSections, "Dividers");
        mDividerSpinner.setAdapter(mSpinnerAdapter);
        mDividerSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    private List<KeyValuePair> getScrollItems() {
        return new ArrayList<KeyValuePair>() {{
            add(new KeyValuePair("Right", mScrollSpinnerValues[1]));
            add(new KeyValuePair("Left", mScrollSpinnerValues[2]));
        }};
    }

    private List<KeyValuePair> getFillGapSpinnerItems() {
        return new ArrayList<KeyValuePair>() {{
            add(new KeyValuePair("Black", CustomizeSpinnersAdapter.OptionTypes.BLACK));
            add(new KeyValuePair("Empty", CustomizeSpinnersAdapter.OptionTypes.EMPTY));
            add(new KeyValuePair("Random", CustomizeSpinnersAdapter.OptionTypes.RANDOM));
        }};
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i(TAG, "Progress: " + progress);
            switch (seekBar.getId()) {
                case R.id.seekBarGap:
                    seekBarGapValue.setText(String.valueOf(progress));
                    mOnCustomizeListener.setGap(progress);
                    break;
                case R.id.seekBarSpeed:
                    seekBarSpeedValue.setText(String.valueOf(progress));
                    mOnCustomizeListener.setSpeed(progress);
                    break;
                case R.id.seekBarDivHeight:
                    seekBarDivHeightValue.setText(String.valueOf(progress));
                    mOnCustomizeListener.setDividerHeight(progress);
                    break;
            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private AdapterView.OnItemSelectedListener mSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View spinner, int position, long id) {
            int value;
            switch (adapterView.getId()) {
                case R.id.fillGapSpinner:
                    value = mColorSpinnerSections.get(position).getColor(getActivity());
                    mOnCustomizeListener.fillGap(value);
                    break;
                case R.id.autoScrollSpinner:
                    value = mScrollSpinnerSections.get(position).getScrollOption();
                    mOnCustomizeListener.setAutoScrollFaster(value);
                    break;
                case R.id.scrollSpinner:
                    value = mScrollSpinnerSections.get(position).getScrollOption();
                    mOnCustomizeListener.setScrollFaster(value);
                    break;
                case R.id.dividerSpinner:
                    int color = mColorSpinnerSections.get(position).getColor(getActivity());
                    color = color == -1 ? getResources().getColor(android.R.color.transparent) : color;
                    Drawable drawable = new ColorDrawable(color);
                    mOnCustomizeListener.setDivider(drawable);
                    break;
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnCustomizeListener = (OnCustomizeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnCustomizeListener.class.getSimpleName());
        }

    }

    public interface OnCustomizeListener {
        void setSpeed(int value);

        void setGap(int value);

        void fillGap(int color);

        void setDivider(Drawable drawable);

        void setDividerHeight(int value);

        void setAutoScrollFaster(int option);

        void setScrollFaster(int option);
    }
}
