package com.jpardogo.android.listbuddies.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.jpardogo.android.listbuddies.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by jpardogo on 22/02/2014.
 */
public class CustomizeFragment extends Fragment {
    private static final String TAG = CustomizeFragment.class.getSimpleName();
    @InjectView(R.id.seekBarGap)
    SeekBar mSeekBarGap;
    private OnSeekBarListener mOnSeekBarListener;

    public static Fragment newInstance() {
        return new CustomizeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customize, container, false);
        ButterKnife.inject(this, rootView);
        mSeekBarGap.setOnSeekBarChangeListener(mSeekBarGapListener);
        return rootView;
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarGapListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.i(TAG, "Progress: " + progress);
            mOnSeekBarListener.setGap(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnSeekBarListener = (OnSeekBarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement " + OnSeekBarListener.class.getSimpleName());
        }

    }

    public interface OnSeekBarListener {
        void setGap(int value);
    }
}
