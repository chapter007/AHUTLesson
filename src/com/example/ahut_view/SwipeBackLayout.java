package com.example.ahut_view;

import java.util.LinkedList;
import java.util.List;

import com.example.ahut.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

/**
 * @author tim
 * @date 2014-12-19
 * @email tim_ding@qq.com
 */
public class SwipeBackLayout extends FrameLayout {
	/**
	 * 父容器
	 */
	private View mContentView;
	/**
	 * 有效touch距离
	 */
	private int mTouchSlop;
	/**
	 * 按下的X坐标
	 */
	private int downX;
	/**
	 * 按下的Y坐标
	 */
	private int downY;
	/**
	 * 临时的X坐标
	 */
	private int tempX;
	/**
	 * 滚动类
	 */
	private Scroller mScroller;
	/**
	 * 宽度
	 */
	private int viewWidth;
	private boolean isSilding;
	private boolean isFinish;
	private Drawable mShadowDrawable;
	private Activity mActivity;
	private List<ViewPager> mViewPagers = new LinkedList<ViewPager>();
	private List<WebView> mWebViews = new LinkedList<WebView>();
	private List<HorizontalScrollView> mHorScrollView = new LinkedList<HorizontalScrollView>();
	
	public SwipeBackLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeBackLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScroller = new Scroller(context);

		mShadowDrawable = getResources().getDrawable(R.drawable.shadow_left);
	}
	
	public void attachToActivity(Activity activity) {
		mActivity = activity;
		TypedArray a = activity.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.windowBackground });
		int background = a.getResourceId(0, 0);
		a.recycle();

		ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
		decorChild.setBackgroundResource(background);
		decor.removeView(decorChild);
		addView(decorChild);
		setContentView(decorChild);
		decor.addView(this);
	}

	private void setContentView(View decorChild) {
		mContentView = (View) decorChild.getParent();
	}

	/**
	 * 事件拦截操作
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//处理ViewPager冲突问题
		ViewPager mViewPager = getTouchViewPager(mViewPagers, ev);
		Log.e("tim", "mViewPager = " + mViewPager);
		if(mViewPager != null && mViewPager.getCurrentItem() != 0){
			return super.onInterceptTouchEvent(ev);
		}
		
		//处理Webview冲突问题
		WebView webView= getTouchWebView(mWebViews,ev);
		if(webView != null){
			Log.e("tim", "mwebView = " + webView.getHeight());
		}
		if(webView != null && webView.getScrollX() != 0){
			
			return super.onInterceptTouchEvent(ev);
		}
		HorizontalScrollView view = getTouchHorScrollerView(mHorScrollView,ev);
		Log.e("tim", "mHo = " + view);
		if(view != null && view.getScrollX() != 0){
			return super.onInterceptTouchEvent(ev);
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = tempX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getRawX();
			// 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
			if (moveX - downX > mTouchSlop
					&& Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
				return true;
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getRawX();
			int deltaX = tempX - moveX;
			tempX = moveX;
			if (moveX - downX > mTouchSlop
					&& Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
				isSilding = true;
			}
			if (moveX - downX >= 0 && isSilding) {
				mContentView.scrollBy(deltaX, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			isSilding = false;
			if (mContentView.getScrollX() <= -viewWidth / 2) {
				isFinish = true;
				scrollRight();
			} else {
				scrollOrigin();
				isFinish = false;
			}
			break;
		}

		return true;
	}
	
	/**
	 * 获取SwipeBackLayout里面的ViewPager的集合
	 * @param mViewPagers
	 * @param parent
	 */
	private void getAlLViewPager(List<ViewPager> mViewPagers, ViewGroup parent){
		int childCount = parent.getChildCount();
		for(int i=0; i<childCount; i++){
			View child = parent.getChildAt(i);
			if(child instanceof ViewPager){
				mViewPagers.add((ViewPager)child);
			}else if(child instanceof ViewGroup){
				getAlLViewPager(mViewPagers, (ViewGroup)child);
			}
		}
	}
	
	private void getAllHorizontalScrollView(List<HorizontalScrollView> mHor,ViewGroup parent){
		int childCount = parent.getChildCount();
		for(int i=0; i<childCount; i++){
			View child = parent.getChildAt(i);
			if(child instanceof HorizontalScrollView){
				mHorScrollView.add((HorizontalScrollView)child);
			}else if(child instanceof ViewGroup){
				getAllHorizontalScrollView(mHorScrollView, (ViewGroup)child);
			}
		}
	}

	/**
	 * 获取SwipeBackLayout里面的WebView的集合
	 * @param mViewPagers
	 * @param parent
	 */
	private void getAlLWebViews(List<WebView> mWebViews, ViewGroup parent){
		int childCount = parent.getChildCount();
		for(int i=0; i<childCount; i++){
			View child = parent.getChildAt(i);
			if(child instanceof WebView){
				mWebViews.add((WebView)child);
			}else if(child instanceof ViewGroup){
				getAlLWebViews(mWebViews, (ViewGroup)child);
			}
		}
	}
	
	
	/**
	 * 返回我们touch的WebView
	 * @param mWebViews
	 * @param ev
	 * @return
	 */
	private WebView getTouchWebView(List<WebView> mWebViews, MotionEvent ev){
		if(mWebViews == null || mWebViews.size() == 0){
			return null;
		}
		Rect mRect = new Rect();
		for(final WebView v : mWebViews){ 

	        v.getGlobalVisibleRect(mRect);
			Log.e("tim", "left:"+mRect.left+",x:"+ev.getX()+",right:"+mRect.right+",top:"+mRect.top+",y:"+ev.getY()+",bottom:"+mRect.bottom);
//	        v.getLocalVisibleRect(mRect);
//			Log.i(TAG, "left:"+mRect.left+",x:"+ev.getX()+",right:"+mRect.right+",top:"+mRect.top+",y:"+ev.getY()+",bottom:"+mRect.bottom);
			
			if(mRect.contains((int)ev.getX(), (int)ev.getY())){
				return v;
			}
		}

		return null;
	}
	
	/**
	 * 返回我们touch的ViewPager
	 * @param mViewPagers
	 * @param ev
	 * @return
	 */
	private ViewPager getTouchViewPager(List<ViewPager> mViewPagers, MotionEvent ev){
		if(mViewPagers == null || mViewPagers.size() == 0){
			return null;
		}
		Rect mRect = new Rect();
		for(ViewPager v : mViewPagers){
			v.getHitRect(mRect);
			
			if(mRect.contains((int)ev.getX(), (int)ev.getY())){
				return v;
			}
		}
		return null;
	}
	private HorizontalScrollView getTouchHorScrollerView(List<HorizontalScrollView> mScrollerView, MotionEvent ev){
		if(mScrollerView == null || mScrollerView.size() == 0){
			return null;
		}
		Rect mRect = new Rect();
		for(HorizontalScrollView v : mScrollerView){
			v.getGlobalVisibleRect(mRect);
			
			if(mRect.contains((int)ev.getX(), (int)ev.getY())){
				return v;
			}
		}
		return null;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		//Log.e("tim","change " + changed);
		if (changed) {
			viewWidth = this.getWidth();
			
			getAlLViewPager(mViewPagers, this);
			getAlLWebViews(mWebViews, this);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mShadowDrawable != null && mContentView != null) {

			int left = mContentView.getLeft()
					- mShadowDrawable.getIntrinsicWidth();
			int right = left + mShadowDrawable.getIntrinsicWidth();
			int top = mContentView.getTop();
			int bottom = mContentView.getBottom();

			mShadowDrawable.setBounds(left, top, right, bottom);
			mShadowDrawable.draw(canvas);
		}

	}


	/**
	 * 滚动出界面
	 */
	private void scrollRight() {
		final int delta = (viewWidth + mContentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		postInvalidate();
	}

	/**
	 * 滚动到起始位置
	 */
	private void scrollOrigin() {
		int delta = mContentView.getScrollX();
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (mScroller.computeScrollOffset()) {
			mContentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished() && isFinish) {
				mActivity.finish();
			}
		}
	}


}