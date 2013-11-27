package com.jpardogo.listbuddies.lib.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jpardogo.listbuddies.lib.R;
import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapater;

public class ListBuddiesLayout extends LinearLayout implements View.OnTouchListener, ObservableListView.ListViewObserverDelegate {

    private static final String TAG = ListBuddiesLayout.class.getSimpleName();

    /**
     * Click listener callback
     */
    private OnBuddyItemClickListener mItemBuddyListener;

    /**
     * Controll the distance that the list will scroll per timer tick period
     */
    private static final int SCROLL_DISTANCE = 2;

    /**
     * Period between the timer ticks
     */
    private final int mScrollPeriod = 20;

    /**
     * When user click the list or scroll but finish the scroll stopping it at the end then we need to detect
     * this behavior to start the autoscroll again. A range from -1.0 to 1.0 is anough to detect that the list
     * is stop at the end of the interaction so we need to start it again
     */
    private static final double MAX_RANGE_CLICK = 5.0;
    private static final double MIN_RANGE_CLICK = -5.0;

    private ObservableListView mListViewLeft;
    private ObservableListView mListViewRight;

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

    private boolean isRightListEnabled = false;
    private boolean isLeftListEnabled = false;

    private int mLastViewTouchId;


    public ListBuddiesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.list_group, this, true);
        mListViewLeft = (ObservableListView) findViewById(R.id.list_left);
        mListViewRight = (ObservableListView) findViewById(R.id.list_right);

        //Initialize so on first touch the toogle works
        mLastViewTouchId = mListViewRight.getId();
        //Init listeners
        mListViewLeft.setOnItemClickListener(OnBuddyClicked);
        mListViewRight.setOnItemClickListener(OnBuddyClicked);

        mListViewLeft.setOnTouchListener(this);
        mListViewLeft.setObserver(this);
        mListViewLeft.setOnScrollListener(new AbsListView.OnScrollListener() {

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
                    onListScroll(view, 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });

        mListViewRight.setOnTouchListener(this);
        mListViewRight.setObserver(this);
        mListViewRight.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //The user is scrolling using touch, and their finger is still on the screen
                    isRightListEnabled = false;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    //The view is not scrolling.
                    isRightListEnabled = true;
                    onListScroll(view, 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });

        startAutoScroll();
    }

    public void setAdapters(CircularLoopAdapater adapter, CircularLoopAdapater adapter2) {
        mListViewLeft.setAdapter(adapter);
        mListViewRight.setAdapter(adapter2);
        mListViewLeft.setSelection(Integer.MAX_VALUE / 2);
        mListViewRight.setSelection(Integer.MAX_VALUE / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mActionDown = true;
                //Select the enable ListView
                toogleListView(v);
                mLastViewTouchId = v.getId();
                stopAutoscroll();
                break;

            case MotionEvent.ACTION_UP:
                mActionDown = false;
                //mDelta is really small means that the user either clicked or scroll but finish stopping the scroll
                // and the scroll is no moving anymore so onListScroll won´t be called and we have to force another
                // call to start the auto-scroll again.
                if (between(mDeltaY, MIN_RANGE_CLICK, MAX_RANGE_CLICK)) {
                    startAutoScroll();
                }
                break;
        }
        return false;
    }

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
            if (listView.getId() == mListViewLeft.getId() && !isLeftListEnabled) {
                mListViewRight.smoothScrollBy((int) -deltaY / 2, 0);

            } else if (listView.getId() == mListViewRight.getId() && !isRightListEnabled) {
                mListViewLeft.smoothScrollBy((int) -deltaY * 2, 0);
            }
        }
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
            mListViewLeft.smoothScrollBy(SCROLL_DISTANCE, 0);
            mListViewRight.smoothScrollBy(SCROLL_DISTANCE / 2, 0);
        }

        public void onFinish() {
            Log.d(TAG, "mAutoScrollTimer - onFinish");
        }
    };

    /**
     * Each time we touch the opposite ListView than the last one we have selected
     * we need to activate iT as the enable one
     *
     * @param v - The ListView touched
     */
    private void toogleListView(View v) {
        if (v.getId() != mLastViewTouchId) {
            isLeftListEnabled = !isLeftListEnabled;
            isRightListEnabled = !isRightListEnabled;

        }
    }

    /**
     * Check if a number is inside a range
     */
    private boolean between(float deltaY, double min, double max) {
        return deltaY >= min && deltaY <= max;
    }

    public void setOnItemClickListener(OnBuddyItemClickListener listener) {
        mItemBuddyListener = listener;
    }


    private AdapterView.OnItemClickListener OnBuddyClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Send the number of the list where the item was clicked
            int buddy = parent.getId() == mListViewLeft.getId() ? 0 : 1;
            //Callback with all the information (list selected and position in it)
            mItemBuddyListener.onBuddyItemClicked(parent, view, buddy, position, id);
        }
    };


    public interface OnBuddyItemClickListener {
        //Buddy corresponde with the list that the itemclick correspond to 0 for the left one, 1 for the right one
        void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id);
    }
}
