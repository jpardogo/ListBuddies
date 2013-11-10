package com.jpardogo.android.listbuddies.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CircularLoopAdapater;
import com.jpardogo.android.listbuddies.provider.ImagesUrls;
import com.jpardogo.android.listbuddies.widgets.ObservableListView;


public class ListBuddiesFragment extends Fragment implements View.OnTouchListener, AdapterView.OnItemClickListener, ObservableListView.ListViewObserverDelegate {

    private static final String TAG = ListBuddiesFragment.class.getSimpleName();

    /**Controll the distance that the list will scroll per timer tick period*/
    private static final int SCROLL_DISTANCE = 5;
    /**Period between the timer ticks*/
    private final int mScrollPeriod = 50;

    /**When user click the list or scroll but finish the scroll stopping it at the end then we need to detect
     * this behavior to start the autoscroll again. A range from -1.0 to 1.0 is anough to detect that the list
     * is stop at the end of the interaction so we need to start it again*/
    private static final double MAX_RANGE_CLICK = 1.0;
    private static final double MIN_RANGE_CLICK = -1.0;


    private ObservableListView mListView_left;
    private ObservableListView mListView_right;

    /**Adapter that will loop through the list items again and again*/
    private CircularLoopAdapater mAdapter;

    /**Time that the timer willl run - 300 000 000 years scrolling will be enough to make an infinite auto-scroll*/
    private long mTotalScrollTime = Long.MAX_VALUE;

    /**Flag that detects if the user is touching the screen*/
    private boolean mActionDown = true;

    /**Amount of pixel scrolled for the user on the Y axe*/
    private float mDeltaY;

    /**Flag that control a double call to the timer, in case the timer is running it is true, false otherwise*/
    private boolean timerRunning = false;


    public ListBuddiesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mListView_left = (ObservableListView) rootView.findViewById(R.id.list_left);
        mListView_right = (ObservableListView) rootView.findViewById(R.id.list_right);

        mAdapter = new CircularLoopAdapater(getActivity(), ImagesUrls.imageUrls_left);
        mListView_left.setAdapter(mAdapter);

        mAdapter = new CircularLoopAdapater(getActivity(), ImagesUrls.imageUrls_right);
        mListView_right.setAdapter(mAdapter);

        //Need to start list in the middle to make it infinite
        mListView_left.setSelection(Integer.MAX_VALUE / 2);
        mListView_left.setOnTouchListener(this);
        mListView_left.setObserver(this);

        //Need to start list in the middle to make it infinite
        mListView_right.setSelection(Integer.MAX_VALUE / 2);
        mListView_right.setOnTouchListener(this);
        mListView_right.setObserver(this);

        //Start auto-scroll
        startAutoScroll();

        return rootView;
    }


    /**
     * Stops the timer that manage the auto-scroll in case it is running
     */
    private void stopAutoscroll() {
        if (timerRunning) {
            timerRunning = false;
            mAutoScrollTimer.cancel();
        }
    }


    /**Starts timer for both lists*/
    private void startAutoScroll() {

        if (!timerRunning) {
            timerRunning = true;
            mListView_left.post(new Runnable() {
                @Override
                public void run() {
                    mAutoScrollTimer.start();
                }
            });

            mListView_right.post(new Runnable() {
                @Override
                public void run() {
                    mAutoScrollTimer.start();
                }
            });
        }


    }

    /**
     * Timer that will be call each {@link mScrollPeriod} to scroll {@link SCROLL_DISTANCE}*
     * during {@link mTotalScrollTime}
     */
    private final CountDownTimer mAutoScrollTimer = new CountDownTimer(mTotalScrollTime, mScrollPeriod) {


        public void onTick(long millisUntilFinished) {

            mListView_left.smoothScrollBy(SCROLL_DISTANCE, mScrollPeriod);
            mListView_right.smoothScrollBy(SCROLL_DISTANCE / 2, mScrollPeriod);
        }

        public void onFinish() {

        }

    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    /**
     * Detect when the user touch any of the lists
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDown = true;
                stopAutoscroll();
                break;
            case MotionEvent.ACTION_UP:
                mActionDown = false;

                //mDelta is really small means that the user either clicked or scroll but finish stopping the scroll
                // and the scroll is no moving anymore so onScroll won´t be called and we have to force another
                // call to start the auto-scroll again.
                if (between(mDeltaY,MIN_RANGE_CLICK, MAX_RANGE_CLICK)) {
                    onScroll(0);
                }
                break;

        }

        return false;
    }

    /**Check if a number is inside a range*/
    private boolean between(float x, double min, double max) {
        return x >= min && x <= max;
    }

    /**
     * Starts the auto scroll when the Y value is cero , so the scroll is not moving
     * @param deltaY
     */
    @Override
    public void onScroll(float deltaY) {

        //Sabe delta in global variable to use it on onTouch
        mDeltaY = deltaY;
        if (mDeltaY == 0 && !mActionDown) {
            startAutoScroll();
        }

    }

}