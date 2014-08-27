package com.kozhevin.example.carousel;



public class StateScrollStorage  {

	private int mInitialTouchX;
	private int mInitialTouchY;
	
	private int mLastTouchX;
	private int mLastTouchY;
	
	private int mScrollState;
	
	private int mScrollPointerId;
	
	private boolean mIsTouched;
	

	private volatile boolean mIsWaitNextTouchEvent;
	
	private static class InstanceHolder {

		private static final StateScrollStorage INSTANCE = new StateScrollStorage();
	}

	public static StateScrollStorage getInstance() {
		return StateScrollStorage.InstanceHolder.INSTANCE;
	}

	private StateScrollStorage() {

	}

	public int getInitialTouchY() {
		return mInitialTouchY;
	}

	public void setInitialTouchY(int pInitialTouchY) {
		mInitialTouchY = pInitialTouchY;
	}

	public int getInitialTouchX() {
		return mInitialTouchX;
	}

	public void setInitialTouchX(int pInitialTouchX) {
		mInitialTouchX = pInitialTouchX;
	}

	public int getLastTouchX() {
		return mLastTouchX;
	}

	public void setLastTouchX(int pLastTouchX) {
		mLastTouchX = pLastTouchX;
	}

	public int getLastTouchY() {
		return mLastTouchY;
	}

	public void setLastTouchY(int pLastTouchY) {
		mLastTouchY = pLastTouchY;
	}

	public int getScrollState() {
		return mScrollState;
	}

	public void setScrollState(int pScrollState) {
		mScrollState = pScrollState;
	}

	public int getScrollPointerId() {
		return mScrollPointerId;
	}

	public void setScrollPointerId(int pScrollPointerId) {
		mScrollPointerId = pScrollPointerId;
	}

	public boolean isTouched() {
		return mIsTouched;
	}

	public void setTouched(boolean pIsTouched) {
		mIsTouched = pIsTouched;
	}

	public boolean isWaitNextTouchEvent() {
		return mIsWaitNextTouchEvent;
	}

	public void setWaitNextTouchEvent(boolean pIsWaitNextTouchEvent) {
		mIsWaitNextTouchEvent = pIsWaitNextTouchEvent;
	}

}
