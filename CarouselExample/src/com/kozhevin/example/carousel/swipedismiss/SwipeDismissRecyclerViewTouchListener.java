package com.kozhevin.example.carousel.swipedismiss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kozhevin.example.carousel.StateScrollStorage;

public class SwipeDismissRecyclerViewTouchListener implements View.OnTouchListener, OnItemTouchListener {

	private static final String			LOG_TAG						= "SwipeDismiss";

	private static final boolean		IS_DEBUG					= false;

	// Cached ViewConfiguration and system-wide constant values
	private int							mSlop;
	private int							mMinFlingVelocity;
	private int							mMaxFlingVelocity;
	private long						mAnimationTime;

	// Fixed properties
	private RecyclerView				mRecycleView;
	private DismissCallbacks			mCallbacks;
	private int							mViewHeight					= 1;									// 1 and not 0 to prevent dividing by zero

	// Transient properties
	private List<PendingDismissData>	mPendingDismisses			= new ArrayList<PendingDismissData>();
	private int							mDismissAnimationRefCount	= 0;
	private float						mDownY;
	private boolean						mSwiping;
	private VelocityTracker				mVelocityTracker;
	private int							mDownPosition;
	private View						mDownView;
	private boolean						mPaused;

	private StringBuilder				mStrBuild;
	private Rect						mRectViewCoord;
	private boolean						mIsAlreadyDissmissed;

	/**
	 * The callback interface used by {@link SwipeDismissRecyclerViewTouchListener} to inform its client
	 * about a successful dismissal of one or more list item positions.
	 */
	public interface DismissCallbacks {

		/**
		 * Called to determine whether the given position can be dismissed.
		 */
		boolean canDismiss(int position);


		/**
		 * Called when the user has indicated they she would like to dismiss one or more list item
		 * positions.
		 *
		 * @param pRecyclerView
		 *            The originating {@link RecyclerView}.
		 * @param reverseSortedPositions
		 *            An array of positions to dismiss, sorted in descending
		 *            order for convenience.
		 */
		void onDismiss(RecyclerView pRecyclerView, int[] pReverseSortedPositions);
	}


	/**
	 * Constructs a new swipe-to-dismiss touch listener for the given list view.
	 *
	 * @param mRecyclerView
	 *            The RecyclerView whose items should be dismissable.
	 * @param callbacks
	 *            The callback to trigger when the user has indicated that she would like to
	 *            dismiss one or more list items.
	 */
	public SwipeDismissRecyclerViewTouchListener(RecyclerView mRecyclerView, DismissCallbacks callbacks) {
		ViewConfiguration vc = ViewConfiguration.get(mRecyclerView.getContext());
		mSlop = vc.getScaledTouchSlop();
		mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
		mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
		mAnimationTime = mRecyclerView.getContext().getResources().getInteger(
				android.R.integer.config_shortAnimTime);
		mRecycleView = mRecyclerView;
		mCallbacks = callbacks;
		if (IS_DEBUG) {
			mStrBuild = new StringBuilder(100);
		}
	}


	/**
	 * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
	 *
	 * @param enabled
	 *            Whether or not to watch for gestures.
	 */
	public void setEnabled(boolean enabled) {
		mPaused = !enabled;
	}


	/**
	 * Returns an {@link RecyclerView.OnScrollListener} to be added to the {@link RecyclerView} using
	 * {@link RecyclerView#setOnScrollListener(RecyclerView.OnScrollListener)}.
	 * If a scroll listener is already assigned, the caller should still pass scroll changes through
	 * to this listener. This will ensure that this {@link SwipeDismissRecyclerViewTouchListener} is
	 * paused during list view scrolling.</p>
	 *
	 * @see SwipeDismissRecyclerViewTouchListener
	 */
	public RecyclerView.OnScrollListener makeScrollListener() {

		return new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(int pScrollState) {

				StateScrollStorage.getInstance().setScrollState(pScrollState);

				setEnabled(pScrollState != RecyclerView.SCROLL_STATE_DRAGGING);
				
				if (IS_DEBUG) {
					mStrBuild.delete(0, mStrBuild.length());
					mStrBuild.append("ScrollState is ");
					switch (pScrollState) {

					case RecyclerView.SCROLL_STATE_IDLE:
						mStrBuild.append("SCROLL_STATE_IDLE");
						break;

					case RecyclerView.SCROLL_STATE_DRAGGING:
						mStrBuild.append("SCROLL_STATE_DRAGGING");
						break;

					case RecyclerView.SCROLL_STATE_SETTLING:
						mStrBuild.append("SCROLL_STATE_SETTLING");
						break;

					default:
						mStrBuild.append("undefined");

					}
					mStrBuild.append(" now").append('\n');
					Log.d(LOG_TAG, mStrBuild.toString());
				}
			}


			@Override
			public void onScrolled(int pDx, int pDy) {
				
				if (IS_DEBUG) {
					mStrBuild.delete(0, mStrBuild.length());
					mStrBuild.append("onScrolled dx=").append(pDx).append(", dy=").append(pDy);
					Log.d(LOG_TAG, mStrBuild.toString());
				}
				/*
				if (mRecycleView.getLayoutManager() != null
						&& mRecycleView.getLayoutManager() instanceof CarouselLayoutManager
						&& !StateScrollStorage.getInstance().isTouched()) {
					((CarouselLayoutManager)mRecycleView.getLayoutManager()).performSmoothScroll();
				}
				*/

			}
		};
	}


	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (IS_DEBUG) {
			mStrBuild.delete(0, mStrBuild.length());
			Log.w(LOG_TAG, "onTouch started");
		}
		if (mViewHeight < 2) {
			mViewHeight = mRecycleView.getHeight();
		}

		switch (motionEvent.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: {
			mIsAlreadyDissmissed = false;
			return false;

		}

		case MotionEvent.ACTION_UP: {
			if (IS_DEBUG) {
				Log.v(LOG_TAG, "MotionEvent.ACTION_UP");
			}
			if (mVelocityTracker == null) {
				if (IS_DEBUG) {
					Log.v(LOG_TAG, "mVelocityTracker == null");
				}

				break;
			}

			float deltaY = motionEvent.getRawY() - mDownY;
			mVelocityTracker.addMovement(motionEvent);
			mVelocityTracker.computeCurrentVelocity(1000);
			float velocityX = mVelocityTracker.getYVelocity();
			float absVelocityX = Math.abs(velocityX);
			float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
			boolean dismiss = false;
			boolean dismissRight = false;
			if (Math.abs(deltaY) > mViewHeight / 2) {
				dismiss = true;
				dismissRight = deltaY > 0;
			}
			else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
					&& absVelocityY < absVelocityX) {
				// dismiss only if flinging in the same direction as dragging
				dismiss = (velocityX < 0) == (deltaY < 0);
				dismissRight = mVelocityTracker.getYVelocity() > 0;
			}

			if (IS_DEBUG) {
				Log.v(LOG_TAG, "mIsAlreadyDissmissed == " + (mIsAlreadyDissmissed == true));
			}

			if (dismiss && !mIsAlreadyDissmissed) {
				// dismiss

				final View downView = mDownView; // mDownView gets null'd before animation ends
				final int downPosition = mDownPosition;
				++mDismissAnimationRefCount;
				ViewCompat.animate(downView).translationY(dismissRight ? mViewHeight : -mViewHeight).alpha(0)
						.setDuration(mAnimationTime).setListener(new ViewPropertyAnimatorListener() {

							@Override
							public void onAnimationStart(View arg0) {

							}


							@Override
							public void onAnimationEnd(View arg0) {
								if (!mIsAlreadyDissmissed) {
									mIsAlreadyDissmissed = true;
									performDismiss(downView, downPosition);
								}
							}


							@Override
							public void onAnimationCancel(View arg0) {}
						}).start();
			}
			else {
				// cancel
				ViewCompat.animate(mDownView).translationY(0).alpha(1).setDuration(mAnimationTime).start();

			}
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			mDownY = 0;
			mDownView = null;
			mDownPosition = ListView.INVALID_POSITION;
			mSwiping = false;
			break;
		}

		case MotionEvent.ACTION_MOVE: {

			if (IS_DEBUG) {
				Log.v(LOG_TAG, "MotionEvent.ACTION_MOVE");
			}
			mIsAlreadyDissmissed = false;

			if (mVelocityTracker == null) {
				if (IS_DEBUG) {
					Log.v(LOG_TAG, "mVelocityTracker == null, creating his ");
				}
				if (mRectViewCoord == null) {
					mRectViewCoord = new Rect();
				}
				int childCount = mRecycleView.getChildCount();
				int[] listViewCoords = new int[2];
				mRecycleView.getLocationOnScreen(listViewCoords);
				if (IS_DEBUG) {
					mStrBuild.append("Array listViewCoords :");
					boolean lIsFirstItem = true;
					for (int lItem : listViewCoords) {
						if (!lIsFirstItem) {
							mStrBuild.append(", ");
						}
						else {
							lIsFirstItem = false;
						}
						mStrBuild.append(lItem);
					}
					mStrBuild.append(";");
					mStrBuild.append('\n');

					mStrBuild.append("motionEvent.getRawX() = ").append((int)motionEvent.getRawX());
					mStrBuild.append('\n');
					mStrBuild.append("motionEvent.getRawY() = ").append((int)motionEvent.getRawY());
					mStrBuild.append('\n');
				}
				int x = (int)motionEvent.getRawX() - listViewCoords[0];
				int y = (int)motionEvent.getRawY() - listViewCoords[1];
				if (IS_DEBUG) {
					mStrBuild.append("delta X = ").append(x);
					mStrBuild.append(" delta Y = ").append(y);
					mStrBuild.append('\n');
					mStrBuild.append("Child view total count = ").append(childCount).append('\n');
				}
				View child;

				for (int i = 0; i < childCount; i++) {
					child = mRecycleView.getChildAt(i);
					mRectViewCoord.setEmpty();

					mRectViewCoord.left = (int)child.getX();
					mRectViewCoord.right = mRectViewCoord.left + child.getWidth();

					mRectViewCoord.top = (int)child.getY();
					mRectViewCoord.bottom = mRectViewCoord.top + child.getHeight();

					if (IS_DEBUG) {
						mStrBuild.append("ChildView[").append(i).append("]").append('\n').
								append("left(x)=").append(mRectViewCoord.left).
								append(" right(x)=").append(mRectViewCoord.right).
								append(" top(y)=").append(mRectViewCoord.top).
								append(" bottom(y)=").append(mRectViewCoord.bottom).append(";").append('\n');
					}
					if (mRectViewCoord.contains(x, y)) {
						if (IS_DEBUG) {
							mStrBuild.append("contains coord");
						}
						mDownView = child;
						break;
					}
				}

				if (mDownView != null) {

					mDownY = motionEvent.getRawY();
					//FIXME position view at adapter
					mDownPosition = mRecycleView.getChildPosition(mDownView);
					if (mCallbacks.canDismiss(mDownPosition)) {
						mVelocityTracker = VelocityTracker.obtain();
						mVelocityTracker.addMovement(motionEvent);
					}
					else {
						mDownView = null;
					}
				}

			}

			if (IS_DEBUG) {
				Log.v(LOG_TAG, mStrBuild.toString());
			}

			if (mVelocityTracker == null || mPaused) {
				if (IS_DEBUG) {
					Log.v(LOG_TAG, "mVelocityTracker == null || mPaused");
				}
				break;
			}

			mVelocityTracker.addMovement(motionEvent);
			float deltaY = motionEvent.getRawY() - mDownY;
			if (Math.abs(deltaY) > mSlop) {
				mSwiping = true;
				mRecycleView.requestDisallowInterceptTouchEvent(true);

				// Cancel RecyclerView's touch (un-highlighting the item)
				MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
				cancelEvent.setAction(MotionEvent.ACTION_CANCEL
						| (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
				mRecycleView.onTouchEvent(cancelEvent);
				cancelEvent.recycle();
			}

			if (mSwiping) {
				mDownView.setTranslationY(deltaY);
				mDownView.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaY) / mViewHeight)));

				if (IS_DEBUG) {
					Log.w(LOG_TAG, "onTouch finished");
				}

				return true;
			}
			break;
		}
		}
		if (IS_DEBUG) {
			Log.w(LOG_TAG, "onTouch finished");
		}
		return false;
	}

	class PendingDismissData implements Comparable<PendingDismissData> {

		public int	position;
		public View	view;


		public PendingDismissData(int position, View view) {
			this.position = position;
			this.view = view;
		}


		@Override
		public int compareTo(PendingDismissData other) {
			// Sort by descending position
			return other.position - position;
		}
	}


	private void performDismiss(final View dismissView, final int dismissPosition) {
		// Animate the dismissed list item to zero-height and fire the dismiss callback when
		// all dismissed list item animations have completed. This triggers layout on each animation
		// frame; in the future we may want to do something smarter and more performant.

		final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
		final int originalHeight = dismissView.getHeight();

		ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

		animator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				--mDismissAnimationRefCount;
				if (mDismissAnimationRefCount == 0) {
					// No active animations, process all pending dismisses.
					// Sort by descending position
					Collections.sort(mPendingDismisses);

					int[] dismissPositions = new int[mPendingDismisses.size()];
					for (int i = mPendingDismisses.size() - 1; i >= 0; i--) {
						dismissPositions[i] = mPendingDismisses.get(i).position;
					}
					mCallbacks.onDismiss(mRecycleView, dismissPositions);

					ViewGroup.LayoutParams lp;
					for (PendingDismissData pendingDismiss : mPendingDismisses) {
						// Reset view presentation
						pendingDismiss.view.setAlpha(1f);
						pendingDismiss.view.setTranslationX(0);
						lp = pendingDismiss.view.getLayoutParams();
						lp.height = originalHeight;
						pendingDismiss.view.setLayoutParams(lp);
					}

					mPendingDismisses.clear();
				}
			}
		});

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				lp.height = (Integer)valueAnimator.getAnimatedValue();
				dismissView.setLayoutParams(lp);
			}
		});

		mPendingDismisses.add(new PendingDismissData(dismissPosition, dismissView));
		animator.start();
	}


	@Override
	public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

		if (IS_DEBUG) {
			Log.w(LOG_TAG, "onInterceptTouchEvent started");
		}

		StateScrollStorage lStateScroll = StateScrollStorage.getInstance();
		boolean lResult = (Math.abs(lStateScroll.getInitialTouchX() - lStateScroll.getLastTouchX()) < Math
				.abs(lStateScroll
						.getInitialTouchY() - lStateScroll.getLastTouchY()));
		if (IS_DEBUG) {
			Log.w(LOG_TAG, "onInterceptTouchEvent finished " + lResult);
		}
		return lResult;
	}


	@Override
	public void onTouchEvent(RecyclerView rv, MotionEvent e) {

		if (IS_DEBUG) {
			Log.w(LOG_TAG, "onTouchEvent started");
		}

		if (IS_DEBUG) {
			Log.w(LOG_TAG, "onTouchEvent finished ");
		}
	}

}