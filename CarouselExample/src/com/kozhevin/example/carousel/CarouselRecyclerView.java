package com.kozhevin.example.carousel;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CarouselRecyclerView extends RecyclerView {

	private static final String		TAG			= "CarouselRecyclerView";
	private static final boolean	IS_DEBUG	= false;

	private int						mTouchSlop;


	public CarouselRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		final ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = vc.getScaledTouchSlop();

	}


	public CarouselRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = vc.getScaledTouchSlop();

	}


	public CarouselRecyclerView(Context context) {
		super(context);
		final ViewConfiguration vc = ViewConfiguration.get(context);
		mTouchSlop = vc.getScaledTouchSlop();

	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if (IS_DEBUG) {
			Log.i(TAG, "onInterceptTouchEvent started");
		}

		StateScrollStorage lStateScroll = StateScrollStorage.getInstance();

		final int action = MotionEventCompat.getActionMasked(e);
		final int actionIndex = MotionEventCompat.getActionIndex(e);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_DOWN");
			}
			lStateScroll.setScrollPointerId(MotionEventCompat.getPointerId(e, 0));

			lStateScroll.setInitialTouchX((int)(e.getX() + 0.5f));
			lStateScroll.setLastTouchX(lStateScroll.getInitialTouchX());

			lStateScroll.setInitialTouchY((int)(e.getY() + 0.5f));
			lStateScroll.setLastTouchY(lStateScroll.getInitialTouchY());

			if (lStateScroll.getScrollState() == SCROLL_STATE_SETTLING) {
				lStateScroll.setScrollState(SCROLL_STATE_DRAGGING);
			}
			StateScrollStorage.getInstance().setTouched(true);

			break;

		case MotionEventCompat.ACTION_POINTER_DOWN:
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_POINTER_DOWN");
			}

			lStateScroll.setInitialTouchX((int)(MotionEventCompat.getX(e, actionIndex) + 0.5f));
			lStateScroll.setLastTouchX(lStateScroll.getInitialTouchX());

			lStateScroll.setInitialTouchY((int)(MotionEventCompat.getY(e, actionIndex) + 0.5f));
			lStateScroll.setLastTouchY(lStateScroll.getInitialTouchY());

			lStateScroll.setScrollPointerId(MotionEventCompat.getPointerId(e, actionIndex));

			StateScrollStorage.getInstance().setTouched(true);

			break;

		case MotionEvent.ACTION_MOVE: {
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_MOVE");
			}

			final int index = MotionEventCompat.findPointerIndex(e, lStateScroll.getScrollPointerId());
	
			if (index < 0) {
				Log.e(TAG, "Error processing scroll; pointer index for id " +
						lStateScroll.getScrollPointerId() + " not found. Did any MotionEvents get skipped?");
				return false;
			}
			
			StateScrollStorage.getInstance().setTouched(true);

			final int x = (int)(MotionEventCompat.getX(e, index) + 0.5f);
			final int y = (int)(MotionEventCompat.getY(e, index) + 0.5f);
			final int dx = x - lStateScroll.getInitialTouchX();
			final int dy = y - lStateScroll.getInitialTouchY();
			boolean startScroll = false;
			if (Math.abs(dx) > mTouchSlop) {
				lStateScroll.setLastTouchX(lStateScroll.getInitialTouchX() + 1 * (dx < 0 ? -1 : 1));
				startScroll = true;
			}
			if (Math.abs(dy) > mTouchSlop) {
				lStateScroll.setLastTouchY(lStateScroll.getInitialTouchY() + 1 * (dy < 0 ? -1 : 1));
				startScroll = true;
			}
			if (startScroll) {
				lStateScroll.setScrollState(SCROLL_STATE_DRAGGING);
			}

		}
			break;

		case MotionEvent.ACTION_UP: {
			if (IS_DEBUG) {
				Log.v(TAG, "MotionEvent.ACTION_UP");
			}
			StateScrollStorage.getInstance().setTouched(false);

		}
			break;

		}

		if (IS_DEBUG) {
			Log.i(TAG, "onInterceptTouchEvent finished = ");
		}
		return super.onInterceptTouchEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		
		if (IS_DEBUG) {
			Log.i(TAG, "onTouchEvent started");
		}

		final int action = MotionEventCompat.getActionMasked(e);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_DOWN");
			}
			StateScrollStorage.getInstance().setWaitNextTouchEvent(false);
			break;

		case MotionEventCompat.ACTION_POINTER_DOWN:
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_POINTER_DOWN");
			}


			break;

		case MotionEvent.ACTION_MOVE: {
			if (IS_DEBUG) {
				Log.i(TAG, "MotionEvent.ACTION_MOVE");
			}

			StateScrollStorage.getInstance().setWaitNextTouchEvent(false);

		
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (IS_DEBUG) {
				Log.v(TAG, "MotionEvent.ACTION_UP");
			}
			StateScrollStorage.getInstance().setTouched(false);
			
			if (getLayoutManager() != null
					&& getLayoutManager() instanceof CarouselLayoutManager
					&& StateScrollStorage.getInstance().getScrollState() != RecyclerView.SCROLL_STATE_DRAGGING ) {
				((CarouselLayoutManager)getLayoutManager()).performSmoothScroll();
			}

			break;
			}
			

		}

		if (IS_DEBUG) {
			Log.i(TAG, "onTouchEvent finished");
		}
		
		return super.onTouchEvent(e);
	}
	
}
