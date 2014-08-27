package com.kozhevin.example.carousel.listeners;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kozhevin.example.carousel.StateScrollStorage;

public class OnLappingItemListener implements IOnLappingItemListener {

	private static final String			TAG			= "OnLappingItemListener";

	private static final boolean		IS_DEBUG	= false;

	private RecyclerView.LayoutManager	mCarouselLayoutManager;
	private RecyclerView				mRecyclerView;


	@SuppressWarnings("unused")
	private OnLappingItemListener() {};


	public OnLappingItemListener(RecyclerView pRecyclerView, RecyclerView.LayoutManager pCarouselLayoutManager) {
		mRecyclerView = pRecyclerView;
		mCarouselLayoutManager = pCarouselLayoutManager;
	}


	@Override
	public void onItemLapping(final int pPositionForLapping) {

		if (mCarouselLayoutManager == null) {
			throw new NullPointerException("CarouselLayoutManager is not setted.");
		}

		if (mRecyclerView == null) {
			throw new NullPointerException("RecyclerView is not setted.");
		}

		if (StateScrollStorage.getInstance().isWaitNextTouchEvent()) {
			if (IS_DEBUG) {
				Log.d(TAG, "Prevent to scroll RecyclerView, need to wait next action of touch");
			}
			mRecyclerView.invalidate();
			return;
		}
		else {
			StateScrollStorage.getInstance().setWaitNextTouchEvent(true);
		}

		// FIXME Will need to enable our animation when scrolling list
		// we'll remove this bugfix later when our animation will enabled
		// A bugfix's temporary
		ThreadHolder.finishTread();
		ThreadHolder.setThread(new ThreadLapping(pPositionForLapping, new Runnable() {

			@Override
			public void run() {

				try {
					for (int i = 0; i < 10; ++i) {
						if (ThreadHolder.getThread().isInterrupted()) {
							return;
						}
						Thread.sleep(50);
					}
				}catch(InterruptedException e) {

					e.printStackTrace();
				}
				if (ThreadHolder.getThread().isInterrupted()) {
					return;
				}

				if (IS_DEBUG) {
					Log.d(TAG, " trying scroll to: " + ThreadHolder.getThread().getPosition());
				}
				if (mCarouselLayoutManager != null) {
					mRecyclerView.smoothScrollToPosition(ThreadHolder.getThread().getPosition());
				}
			}
		}));
		ThreadHolder.getThread().start();

	}

	private static class ThreadHolder {

		private static ThreadLapping	mThread;


		public static ThreadLapping getThread() {
			return mThread;
		}


		public static void setThread(ThreadLapping pThread) {
			mThread = pThread;
		}


		public static void finishTread() {
			if (mThread != null && mThread.getState() != Thread.State.TERMINATED) {
				if (IS_DEBUG) {
					Log.w(TAG, "Thread interrupt()");
				}
				mThread.interrupt();
				while (mThread.getState() != Thread.State.TERMINATED) {

				}
			}

		}

	}

	private static class ThreadLapping extends Thread {

		private final int	mPosition;


		ThreadLapping(int pPosition, Runnable pRunnable) {
			super(pRunnable);
			mPosition = pPosition;
		}


		public int getPosition() {
			return mPosition;
		}

	}

}
