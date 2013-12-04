package com.jpardogo.listbuddies.lib.views;

import android.content.Context;
import android.graphics.Rect;
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
import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapter;

/**
 * LinerLayout that contains 2 ListViews. This ListViews auto-scroll while the user is not interacting with them.
 * When the user interact with one pf the ListViews these created a parallax effect due they are connected.
 */
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
    private final int mScrollPeriod = 10;

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

    /**
     * 2 flags that avoid the scroll in both directions at the same time when we scroll one of the listviews.
     */
    private boolean isRightListEnabled = false;
    private boolean isLeftListEnabled = false;

    /**
     * Parameter for the last listview that we interacted with.
     */
    private int mLastViewTouchId;


    public ListBuddiesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.listbuddies, this, true);
        mListViewLeft = (ObservableListView) findViewById(R.id.list_left);
        mListViewRight = (ObservableListView) findViewById(R.id.list_right);
        //Initialize with a default list as last touched
        mLastViewTouchId = mListViewRight.getId();

        mListViewLeft.setOnTouchListener(this);
        mListViewLeft.setObserver(this);
        mListViewLeft.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Because the left list scrolls the right when it scrolls.
                // And the left scrolls the right one when it scrolls. To avoid an infinite loop between them we
                // need 2 flag that avoid the scroll in both directions at the same time.
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    //The user is scrolling the left list using touch, and their finger is still on the screen
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
                    //The user is scrolling the right list using touch, and their finger is still on the screen
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

    }

    /**
     * Sets the adapters of both ListView son the ListBuddiesLayout.
     *
     * @param adapter  - adapter for the left list
     * @param adapter2 - adapter for the right list
     */
    public void setAdapters(CircularLoopAdapter adapter, CircularLoopAdapter adapter2) {
        mListViewLeft.setAdapter(adapter);
        mListViewRight.setAdapter(adapter2);
        mListViewLeft.setSelection(Integer.MAX_VALUE / 2);
        mListViewRight.setSelection(Integer.MAX_VALUE / 2);

    }

    //The viewItem touch on the ListView
    private View mDownView;
    /**
     * The position of my {@link mDownView}
     */
    private int mDownPosition;
    // Area use to detect the boundaries of the item clicked
    private Rect mRect = new Rect();
    //The number of children in the group.
    private int mChildCount;
    //Coordenates on of the screen in the screen
    private int[] mListViewCoords = new int[2];

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ListView list = (ListView) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Stopped the timer so the lsit stop scrolling
                stopAutoscroll();
                //Activate flag advising the aciton down started
                mActionDown = true;
                //Select the enable ListView
                toogleListView(v);
                //Save the id of the last listview touch
                mLastViewTouchId = v.getId();
                //Due problems with click a item in an auto scroll list, we need to
                //handle the click manually
                finsItemListClicked(event, list);
                //In case the item has been found we get its position on the list and set it state to pressed
                if (mDownView != null) {
                    mDownPosition = list.getPositionForView(mDownView);
                    //It would be much better to activate the press stated of the list item with its default
                    // selector but I could find a way to change it so I need a custom layout and change the
                    // stated of the item itself with my own selector
                    mDownView.setPressed(true);
                }
                break;

            case MotionEvent.ACTION_UP:
                ////Activate flag advising the finger is not touching the screen anymore
                mActionDown = false;
                //mDelta is really small means that the user either clicked or scroll but finish stopping the scroll
                // and the scroll is no moving anymore so onListScroll won´t be called and we have to force another
                // call to start the auto-scroll again.
                if (mDownView != null) {
                    //Always change the state of the item pressed after release the finger form the screen.
                    mDownView.setPressed(false);
                    if (mItemBuddyListener != null) {
                        //ListId of the list where the item was selected
                        int buddy = list.getId() == mListViewLeft.getId() ? 0 : 1;
                        //Only in case action was a single tap (user didnt scroll after selected the list item)
                        // we need to perform this click (callback to the activity).
                        //Send the number of the list where the item was clicked
                        mItemBuddyListener.onBuddyItemClicked(list, mDownView, buddy, mDownPosition, mDownView.getId());
                        //Start again the scroll,
                        // In case this click on the item start another screen it will be handle on onVisibilityChanged
                        startAutoScroll();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //Cancel click and set new state of the view selected when list scroll again
                if (mDownView != null) {
                    mDownView.setPressed(false);
                    mDownView = null;
                }
                break;
        }
        return false;
    }

    /**
     * Finds the item clicked on a ListView using the coordinates of a motion event.
     *
     * @param event - MotionEvent of the finger of the screen
     * @param list  - list where we want to find the item clicked
     */
    private void finsItemListClicked(MotionEvent event, ListView list) {
        mChildCount = list.getChildCount();
        mListViewCoords = new int[2];
        list.getLocationOnScreen(mListViewCoords);
        //X click point coordenate
        int x = (int) event.getRawX() - mListViewCoords[0];
        //Y click point coordenate
        int y = (int) event.getRawY() - mListViewCoords[1];
        View child;
        for (int i = 0; i < mChildCount; i++) {
            child = list.getChildAt(i);
            child.getHitRect(mRect);
            //If the rec contains the click coordenate, we found our onItemclick
            if (mRect.contains(x, y)) {
                mDownView = child; // This is your down view
                break;
            }
        }
    }

    /**
     * Receives the distance scroll on listView.
     *
     * @param view   - view scrolled
     * @param deltaY - Y distance scrolles
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
    private CountDownTimer mAutoScrollTimer = new CountDownTimer(mTotalScrollTime, mScrollPeriod) {
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
        if (mLastViewTouchId != v.getId()) {
            if (mLastViewTouchId == mListViewLeft.getId()) {
                isLeftListEnabled = true;
                isRightListEnabled = false;
            } else {
                isLeftListEnabled = false;
                isRightListEnabled = true;
            }
        }
    }

    public void setOnItemClickListener(OnBuddyItemClickListener listener) {
        mItemBuddyListener = listener;
    }

    public interface OnBuddyItemClickListener {
        //Buddy corresponde with the list that the itemclick correspond to 0 for the left one, 1 for the right one
        void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id);
    }

    /**
     * In case the view is not visible the timer must stop,
     * and in case it comes to the foreground again we want to start the scrolling again
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.INVISIBLE) {
            stopAutoscroll();
        } else {
            startAutoScroll();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAutoScrollTimer.cancel();
        mAutoScrollTimer = null;
    }
}
