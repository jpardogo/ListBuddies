package com.jpardogo.android.listbuddies.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CircularLoopAdapater;
import com.jpardogo.android.listbuddies.provider.ImagesUrls;
import com.jpardogo.android.listbuddies.widgets.ObservableListView;


public class ListBuddiesFragment extends Fragment implements View.OnTouchListener, AdapterView.OnItemClickListener, ObservableListView.ListViewObserverDelegate {

    private static final String TAG = ListBuddiesFragment.class.getSimpleName();

    /**
     * Controll the distance that the list will scroll per timer tick period
     */
    private static final int SCROLL_DISTANCE = 5;
    /**
     * Period between the timer ticks
     */
    private final int mScrollPeriod = 50;

    /**
     * When user click the list or scroll but finish the scroll stopping it at the end then we need to detect
     * this behavior to start the autoscroll again. A range from -1.0 to 1.0 is anough to detect that the list
     * is stop at the end of the interaction so we need to start it again
     */
    private static final double MAX_RANGE_CLICK = 1.0;
    private static final double MIN_RANGE_CLICK = -1.0;


    private ObservableListView mListView_left;
    private ObservableListView mListView_right;

    /**
     * Adapter that will loop through the list items again and again
     */
    private CircularLoopAdapater mAdapter;

    /**
     * Time that the timer willl run - 300 000 000 years scrolling will be enough to make an infinite auto-scroll
     */
    private long mTotalScrollTime = Long.MAX_VALUE;

    /**
     * Flag that detects if the user is touching the screen
     */
    private boolean mActionDown = true;

    /**
     * Amount of pixel scrolled for the user on the Y axe
     */
    private float mDeltaY;

    /**
     * Flag that control a double call to the timer, in case the timer is running it is true, false otherwise
     */
    private boolean timerRunning = false;


    private boolean isRightListEnabled = true;
    private boolean isLeftListEnabled = true;

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
        mListView_left.setOnItemClickListener(this);
        mListView_left.setObserver(this);
        mListView_left.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // onListScroll will be called for both list when they scroll to make
                // scroll the other one so we need to avoid the infite loop setting the list
                // that is enable on the moment of the scroll
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //The user is scrolling using touch, and their finger is still on the screen
                    isLeftListEnabled = false;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    //The view is not scrolling.
                    isLeftListEnabled = true;
                    onListScroll(mListView_left, 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });

        //Need to start list in the middle to make it infinite
        mListView_right.setSelection(Integer.MAX_VALUE / 2);
        mListView_right.setOnTouchListener(this);
        mListView_right.setOnItemClickListener(this);
        mListView_right.setObserver(this);

        mListView_right.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //The user is scrolling using touch, and their finger is still on the screen
                    isRightListEnabled = false;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    //The view is not scrolling.
                    isRightListEnabled = true;
                    onListScroll(mListView_right, 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });

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


    /**
     * Starts timer for both lists
     */
    private void startAutoScroll() {

        if (!timerRunning) {
            timerRunning = true;
            mAutoScrollTimer.start();
        }


    }

    /**
     * Timer that will be call each {@link mScrollPeriod} to scroll {@link SCROLL_DISTANCE}*
     * during {@link mTotalScrollTime}
     */
    private final CountDownTimer mAutoScrollTimer = new CountDownTimer(mTotalScrollTime, mScrollPeriod) {


        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "mAutoScrollTimer - onTick");
            mListView_left.smoothScrollBy(SCROLL_DISTANCE, 0);
            mListView_right.smoothScrollBy(SCROLL_DISTANCE / 2, 0);
        }

        public void onFinish() {
            Log.d(TAG, "mAutoScrollTimer - onFinish");
        }

    };


    /**
     * Detect when the user touch any of the lists
     *
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
                // and the scroll is no moving anymore so onListScroll won´t be called and we have to force another
                // call to start the auto-scroll again.
                if (between(mDeltaY, MIN_RANGE_CLICK, MAX_RANGE_CLICK)) {
                    onListScroll(v, 0);
                }
                break;

        }

        return false;
    }

    /**
     * Check if a number is inside a range
     */
    private boolean between(float deltaY, double min, double max) {
        return deltaY >= min && deltaY <= max;
    }

    /**
     * Callback for our ListViewObserver informing of the distance scrolled
     *
     * @param deltaY - distance scrolled
     */
    @Override
    public void onListScroll(View view, float deltaY) {

        ListView listView = (ListView) view;
        //Sabe delta in global variable to use it on onTouch
        mDeltaY = deltaY;


        if (mDeltaY == 0 && !mActionDown && isLeftListEnabled && isRightListEnabled) {
            startAutoScroll();
        } else {


            //Make the other list scroll half speed in one case or double in the other
            // so we achieve the parallax effect

            if (listView.getId() == mListView_left.getId() && !isLeftListEnabled) {
                mListView_right.smoothScrollBy((int) -deltaY / 2, 0);

            } else if (listView.getId() == mListView_right.getId() && !isRightListEnabled) {

                mListView_left.smoothScrollBy((int) -deltaY * 2, 0);

            }
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Position: " + position, Toast.LENGTH_LONG).show();
        onListScroll((View) view.getParent(), 0);
    }
}