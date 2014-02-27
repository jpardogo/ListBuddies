package com.jpardogo.android.listbuddies.ui.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.Utils.SharePreferences;
import com.jpardogo.android.listbuddies.adapters.CustomizeSpinnersAdapter;
import com.jpardogo.android.listbuddies.models.KeyValuePair;
import com.jpardogo.android.listbuddies.provider.SharedPrefKeys;
import com.jpardogo.listbuddies.lib.provider.ScrollConfigOptions;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

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
    Spinner mManualScrollSpinner;
    @InjectView(R.id.autoScrollSpinner)
    Spinner mAutoScrollSpinner;
    @InjectView(R.id.dividerSpinner)
    Spinner mDividerSpinner;

    private OnCustomizeListener mOnCustomizeListener;
    private CustomizeSpinnersAdapter mSpinnerAdapter;
    private List<KeyValuePair> mColorSpinnerSections;
    private List<KeyValuePair> mScrollScrollSpinnerSections;
    private int[] mScrollSpinnerValues;

    private int mFillGapSpinnerPosition;
    private int mAutoScrollSpinnerPosition;
    private int mManualScrollSpinnerPosition;
    private int mDividerSpinnerPosition;

    private int mGapSeekBarProgress;
    private int mSpeedSeekBarProgress;
    private int mDivHeightSeekBarProgress;

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
        startConfig();
        return rootView;
    }

    private void startConfig() {
        restartLastConfig();
        setProgressText();
        initSeekBars();
        initSpinners();
    }

    private void initSeekBars() {
        mSeekBarGap.setProgress(mGapSeekBarProgress);
        mSeekBarGap.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBarSpeed.setProgress(mSpeedSeekBarProgress);
        mSeekBarSpeed.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBarDivHeight.setProgress(mDivHeightSeekBarProgress);
        mSeekBarDivHeight.setOnSeekBarChangeListener(mSeekBarListener);
    }

    private void setProgressText() {
        seekBarGapValue.setText(String.valueOf(mGapSeekBarProgress));
        seekBarSpeedValue.setText(String.valueOf(mSpeedSeekBarProgress));
        seekBarDivHeightValue.setText(String.valueOf(mDivHeightSeekBarProgress));
    }

    private void restartLastConfig() {
        mGapSeekBarProgress = SharePreferences.getValue(SharedPrefKeys.GAP_PROGRESS);
        mSpeedSeekBarProgress = SharePreferences.getValue(SharedPrefKeys.SPEED_PROGRESS);
        mDivHeightSeekBarProgress = SharePreferences.getValue(SharedPrefKeys.DIV_HEIGHT_PROGRESS);
    }

    private void initSpinners() {
        initFillGatSpinner();
        initScrollSpinner();
        initAutoScrollSpinner();
        initDividerSpinner();
    }

    private void initFillGatSpinner() {
        mColorSpinnerSections = getFillGapSpinnerItems();
        setSpinner(mFillGapSpinner, mColorSpinnerSections, getString(R.string.fillgap)).setSelection(SharePreferences.getValue(SharedPrefKeys.FILL_GAP_POSITION));
    }

    private void initScrollSpinner() {
        mScrollScrollSpinnerSections = getScrollItems();
        setSpinner(mManualScrollSpinner, mScrollScrollSpinnerSections, getString(R.string.manual_fast_scroll)).setSelection(SharePreferences.getValue(SharedPrefKeys.MANUAL_SCROLL_POSITION));

    }

    private void initAutoScrollSpinner() {
        mScrollScrollSpinnerSections = getScrollItems();
        setSpinner(mAutoScrollSpinner, mScrollScrollSpinnerSections, getString(R.string.auto_fast_scroll)).setSelection(SharePreferences.getValue(SharedPrefKeys.AUTO_SCROLL_POSITION));
    }

    private void initDividerSpinner() {
        mColorSpinnerSections = getFillGapSpinnerItems();
        setSpinner(mDividerSpinner, mColorSpinnerSections, getString(R.string.dividers)).setSelection(SharePreferences.getValue(SharedPrefKeys.DIVIDERS_POSITION));
    }

    private Spinner setSpinner(Spinner spinner, List<KeyValuePair> items, String mainTitle) {
        mSpinnerAdapter = new CustomizeSpinnersAdapter(getActivity(), items, mainTitle);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(mSpinnerListener);
        return spinner;
    }

    private List<KeyValuePair> getScrollItems() {
        return new ArrayList<KeyValuePair>() {{
            add(new KeyValuePair(getString(R.string.right), mScrollSpinnerValues[ScrollConfigOptions.RIGHT.getConfigValue()]));
            add(new KeyValuePair(getString(R.string.left), mScrollSpinnerValues[ScrollConfigOptions.LEFT.getConfigValue()]));
        }};
    }

    private List<KeyValuePair> getFillGapSpinnerItems() {
        return new ArrayList<KeyValuePair>() {{
            add(new KeyValuePair(getString(R.string.black), CustomizeSpinnersAdapter.OptionTypes.BLACK));
            add(new KeyValuePair(getString(R.string.transparency), CustomizeSpinnersAdapter.OptionTypes.INSET));
            add(new KeyValuePair(getString(R.string.empty), CustomizeSpinnersAdapter.OptionTypes.EMPTY));

        }};
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.seekBarGap:
                    mGapSeekBarProgress = progress;
                    seekBarGapValue.setText(String.valueOf(progress));
                    mOnCustomizeListener.setGap(progress);
                    break;
                case R.id.seekBarSpeed:
                    mSpeedSeekBarProgress = progress;
                    seekBarSpeedValue.setText(String.valueOf(progress));
                    mOnCustomizeListener.setSpeed(progress);
                    break;
                case R.id.seekBarDivHeight:
                    mDivHeightSeekBarProgress = progress;
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
                    mFillGapSpinnerPosition = position;
                    value = mColorSpinnerSections.get(position).getColor(getActivity());
                    mOnCustomizeListener.setGapColor(value);
                    break;
                case R.id.autoScrollSpinner:
                    mAutoScrollSpinnerPosition = position;
                    value = mScrollScrollSpinnerSections.get(position).getScrollOption();
                    mOnCustomizeListener.setAutoScrollFaster(value);
                    break;
                case R.id.scrollSpinner:
                    mManualScrollSpinnerPosition = position;
                    value = mScrollScrollSpinnerSections.get(position).getScrollOption();
                    mOnCustomizeListener.setScrollFaster(value);
                    break;
                case R.id.dividerSpinner:
                    mDividerSpinnerPosition = position;
                    int color = mColorSpinnerSections.get(position).getColor(getActivity());
                    color = color == ListBuddiesLayout.ATTR_NOT_SET ? getResources().getColor(android.R.color.transparent) : color;
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

    public void reset() {
        startConfig();
    }

    public interface OnCustomizeListener {
        void setSpeed(int value);

        void setGap(int value);

        void setGapColor(int color);

        void setDivider(Drawable drawable);

        void setDividerHeight(int value);

        void setAutoScrollFaster(int option);

        void setScrollFaster(int option);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharePreferences.saveCustomization(SharedPrefKeys.GAP_PROGRESS, mGapSeekBarProgress);
        SharePreferences.saveCustomization(SharedPrefKeys.SPEED_PROGRESS, mSpeedSeekBarProgress);
        SharePreferences.saveCustomization(SharedPrefKeys.DIV_HEIGHT_PROGRESS, mDivHeightSeekBarProgress);
        SharePreferences.saveCustomization(SharedPrefKeys.FILL_GAP_POSITION, mFillGapSpinnerPosition);
        SharePreferences.saveCustomization(SharedPrefKeys.MANUAL_SCROLL_POSITION, mManualScrollSpinnerPosition);
        SharePreferences.saveCustomization(SharedPrefKeys.AUTO_SCROLL_POSITION, mAutoScrollSpinnerPosition);
        SharePreferences.saveCustomization(SharedPrefKeys.DIVIDERS_POSITION, mDividerSpinnerPosition);
    }
}
