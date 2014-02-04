package com.jpardogo.listbuddies.lib.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.AutoScrollHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jpardogo.listbuddies.lib.R;
import com.jpardogo.listbuddies.lib.adapters.CircularLoopAdapter;
import com.jpardogo.listbuddies.lib.helpers.ListBuddiesAutoScrollHelper;

/**
 * LinerLayout that contains 2 ListViews. This ListViews auto-scroll while the user is not interacting with them.
 * When the user interact with one of the ListViews a parrallax effect is created during its scroll.
 */
public class ListBuddiesLayout extends LinearLayout implements View.OnTouchListener, ObservableListView.ListViewObserverDelegate {

    private static final String TAG = ListBuddiesLayout.class.getSimpleName();
    private static final long PRESS_DELAY = 100;
    private static final float CANCEL_CLICK_LIMIT = 8;

    private OnBuddyItemClickListener mItemBuddyListener;
    private static final int SCROLL_DISTANCE = 2;
    private int[] mListViewCoords = new int[2];
    private int mLastViewTouchId;
    private int mDownPosition;
    private int mChildCount;
    private float mDownEventY;
    private ObservableListView mListViewLeft;
    private ObservableListView mListViewRight;
    private boolean mActionDown;
    private boolean isRightListEnabled = false;
    private boolean isLeftListEnabled = false;
    private boolean isUserInteracting = true;
    private View mDownView;
    private Rect mRect = new Rect();
    private ListBuddiesAutoScrollHelper mScrollHelper;


    public ListBuddiesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.listbuddies, this, true);
        mListViewLeft = (ObservableListView) findViewById(R.id.list_left);
        mListViewRight = (ObservableListView) findViewById(R.id.list_right);
        mLastViewTouchId = mListViewRight.getId();
        mListViewLeft.setOnTouchListener(this);
        mListViewLeft.setObserver(this);
        setOnListScrollListener(mListViewLeft, true);
        mListViewRight.setOnTouchListener(this);
        mListViewRight.setObserver(this);
        setOnListScrollListener(mListViewRight, false);
        setScrollHelpers();
        startAutoScroll();
    }

    private void setOnListScrollListener(ObservableListView list, final boolean isLeftList) {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int state) {
                switch (state) {
                    case SCROLL_STATE_IDLE:
                        setListState(true, isLeftList);
                        forceScrollIfNeeded(isOtherListEnable(isLeftList));
                        break;

                    case SCROLL_STATE_TOUCH_SCROLL:
                        setListState(false, isLeftList);
                        isUserInteracting = true;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }
        });
    }

    private boolean isOtherListEnable(boolean isLeftList) {
        boolean result;
        if(isLeftList){
            result=isRightListEnabled;
        }else{
            result=isLeftListEnabled;
        }
        return result;
    }


    private void setScrollHelpers() {
        mScrollHelper = new ListBuddiesAutoScrollHelper(mListViewLeft) {
            @Override
            public void scrollTargetBy(int deltaX, int deltaY) {
                mListViewLeft.smoothScrollBy(SCROLL_DISTANCE, 0);
                mListViewRight.smoothScrollBy(SCROLL_DISTANCE / 2, 0);
            }

            @Override
            public boolean canTargetScrollHorizontally(int i) {
                return false;
            }

            @Override
            public boolean canTargetScrollVertically(int i) {
                return true;
            }
        };

        mScrollHelper.setEnabled(true);
        mScrollHelper.setEdgeType(AutoScrollHelper.EDGE_TYPE_OUTSIDE);
    }

    private void setListState(boolean isEnabled, boolean isLeftList) {
        if (isLeftList) {
            isLeftListEnabled = isEnabled;
        } else {
            isRightListEnabled = isEnabled;
        }
    }

    private void startAutoScroll() {
        mListViewLeft.post(new Runnable() {
            @Override
            public void run() {
                forceScroll();
            }
        });
    }

    private void forceScrollIfNeeded(boolean isListEnabled) {
        if (isUserInteracting && isListEnabled) {
            isUserInteracting = false;
            if (!mActionDown) {
                forceScroll();
            }
        }
    }

    public void setAdapters(CircularLoopAdapter adapter, CircularLoopAdapter adapter2) {
        mListViewLeft.setAdapter(adapter);
        mListViewRight.setAdapter(adapter2);
        mListViewLeft.setSelection(Integer.MAX_VALUE / 2);
        mListViewRight.setSelection(Integer.MAX_VALUE / 2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ListView list = (ListView) v;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDown(list,event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(list);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
        }
        return mScrollHelper.onTouch(v, event);
    }

    private void actionDown(ListView list, MotionEvent event) {
        mActionDown = true;
        toogleListView(list);
        mLastViewTouchId = list.getId();
        startClickSelection(event, list, event.getY());
    }

    private void actionUp(ListView list) {
        mActionDown = false;
        performClick(list);
    }

    private void actionMove(MotionEvent event) {
        cancelClick(event.getY());
    }

    private void cancelClick(float eventY) {
        if (mDownView != null && (Math.abs(mDownEventY - eventY) > CANCEL_CLICK_LIMIT)) {
            mDownView.setPressed(false);
            mDownView = null;
        }
    }

    private void performClick(ListView list) {
        if (mDownView != null && isUserInteracting) {
            mDownView.setPressed(false);
            if (mItemBuddyListener != null) {
                int buddy = list.getId() == mListViewLeft.getId() ? 0 : 1;
                mItemBuddyListener.onBuddyItemClicked(list, mDownView, buddy, mDownPosition, mDownView.getId());
            }
        }
    }

    private void startClickSelection(MotionEvent event, ListView list, float eventY) {
        if (!isUserInteracting) {
            findViewClicked(event, eventY, list);

            setSelectorPressed(list);
        }
    }

    private void findViewClicked(MotionEvent event, float eventY, ListView list) {
        mChildCount = list.getChildCount();
        mListViewCoords = new int[2];
        list.getLocationOnScreen(mListViewCoords);
        int x = (int) event.getRawX() - mListViewCoords[0];
        int y = (int) event.getRawY() - mListViewCoords[1];
        View child;
        for (int i = 0; i < mChildCount; i++) {
            child = list.getChildAt(i);
            child.getHitRect(mRect);
            if (mRect.contains(x, y)) {
                mDownView = child;
                mDownEventY = eventY;
                break;
            }
        }
    }

    private void setSelectorPressed(ListView list) {
        if (mDownView != null) {
            mDownPosition = list.getPositionForView(mDownView);
            mDownView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isUserInteracting) {
                        if (mDownView != null) {
                            mDownView.setPressed(true);
                        }
                    }
                }
            }, PRESS_DELAY);
        }
    }

    /**
     * Receives the distance scroll on listView.
     */
    @Override
    public void onListScroll(View view, float deltaY) {
        if (view.getId() == mListViewLeft.getId() && !isLeftListEnabled) {
            mListViewRight.smoothScrollBy((int) -deltaY / 2, 0);

        } else if (view.getId() == mListViewRight.getId() && !isRightListEnabled) {
            mListViewLeft.smoothScrollBy((int) -deltaY * 2, 0);
        }
    }

    /**
     * Each time we touch the opposite ListView than the last one we have selected
     * we need to activate it as the enable one
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
        //Buddy corresponde with the list (0-left, 1-right)
        void onBuddyItemClicked(AdapterView<?> parent, View view, int buddy, int position, long id);
    }

    private void forceScroll() {
        MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_MOVE, 570, -1, 0);
        mScrollHelper.onTouch(mListViewLeft, event);
    }
}
