package com.mxth.mybanner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/23.
 */

public class BannerView extends LinearLayout {
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 图片轮播视图
     */
    private ViewPager mAdvPager = null;

    /**
     * 滚动图片视图适配器
     */
    private ImageCycleAdapter mAdvAdapter;

    /**
     * 图片轮播指示器控件
     */
    private LinearLayout mGroup;

    /**
     * 图片轮播指示器-个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示器-视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 图片滚动当前图片下标
     */
    private int mImageIndex = 0;


    /**
     * @param context
     */
    public BannerView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_banner, this);
        mAdvPager = (ViewPager) findViewById(R.id.adv_pager);
        mAdvPager.addOnPageChangeListener(new GuidePageChangeListener());
        mAdvPager.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageTimerTask();
                        break;
                    default:
                        // 停止图片滚动
                        stopImageTimerTask();
                        break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视图
        mGroup = (LinearLayout) findViewById(R.id.viewGroup);
    }

    /**
     * 设置图片数据源
     * @param imageUrlList 图片集合(数据类型：String->URL or  Integer->ResourceID)
     * @param imageCycleViewListener
     */
    public void setImageResources(ArrayList<Object> imageUrlList, ImageCycleViewListener imageCycleViewListener) {
        // 清除所有子视图
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = imageUrlList.size();
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = MeasureUtil.dip2px(mContext,7);// XP与DP转换，适应不同分辨率
            int imagePadding = MeasureUtil.dip2px(mContext,5);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.width = imageParams;
            lp.height = imageParams;

            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i].setBackgroundResource(R.drawable.banner_selected_dot);
            } else {
                lp.leftMargin = imagePadding;
                mImageViews[i].setBackgroundResource(R.drawable.banner_defaule_dot);
            }
            mGroup.addView(mImageViews[i], i, lp);
        }
        mAdvAdapter = new ImageCycleAdapter(mContext, imageUrlList, imageCycleViewListener);
        if(imageCount>1){
        mImageIndex=100*imageCount;
        }
        mAdvPager.setAdapter(mAdvAdapter);
        mAdvPager.setCurrentItem(mImageIndex,false);
        startImageTimerTask();
    }

    /**
     * 开始轮播(手动控制自动轮播与否，便于资源控制)
     */
    public void startImageCycle() {
        startImageTimerTask();
    }

    /**
     * 暂停轮播——用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 开始图片滚动任务
     */
    private void startImageTimerTask() {
        stopImageTimerTask();
        // 图片每3秒滚动一次
        mHandler.postDelayed(mImageTimerTask, 3000);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        mHandler.removeCallbacks(mImageTimerTask);
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {

        @Override
        public void run() {
            if (mImageViews != null) {
                mAdvPager.setCurrentItem(++mImageIndex);
            }
        }
    };

    /**
     * 轮播图片状态监听器
     *
     * @author minking
     */
    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {


        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }


        @Override
        public void onPageSelected(int index) {
            mImageIndex =index;
            index=index%mAdvAdapter.getmAdList().size();
            startImageTimerTask(); // 开始下次计时
            // 设置图片滚动指示器背景
            for (int i = 0; i < mImageViews.length; i++) {
                if (index == i) {
                    mImageViews[i].setBackgroundResource(R.drawable.banner_selected_dot);
                }else{
                    mImageViews[i].setBackgroundResource(R.drawable.banner_defaule_dot);
                }
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        /**
         * 图片资源列表
         */
        private ArrayList<Object> mAdList = new ArrayList<>();

        /**
         * 广告图片点击监听器
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, ArrayList<Object> adList, ImageCycleViewListener imageCycleViewListener) {
            mContext = context;
            mAdList = adList;
            mImageCycleViewListener = imageCycleViewListener;

        }

        @Override
        public int getCount() {
            if (mAdList != null) {
                if (mAdList.size() > 1) {
                    return Integer.MAX_VALUE;
                } else {
                    return mAdList.size();
                }
            }
            return 0;

        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % mAdList.size();
            Object item = mAdList.get(position);

            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            // 设置图片点击监听
            final int finalPosition = position;
            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mImageCycleViewListener.onImageClick(finalPosition, v);
                }
            });

            imageView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        // 停止轮播
                        case MotionEvent.ACTION_DOWN:
                            stopImageTimerTask();
                            break;
                        // 开始轮播
                        case MotionEvent.ACTION_UP:
                            startImageTimerTask();
                            break;
                        default:
                            startImageTimerTask();
                            break;
                    }
                    return false;
                }
            });
            container.addView(imageView);
            mImageCycleViewListener.displayImage(item, imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            container.removeView(view);

        }

        public ArrayList<Object> getmAdList() {
            return mAdList;
        }
    }

    /**
     * 轮播控件的监听事件
     */
    public interface ImageCycleViewListener {

        /**
         * 加载图片资源
         */
         void displayImage(Object imageURL, ImageView imageView);

        /**
         * 单击图片事件
         */
         void onImageClick(int position, View imageView);
    }
}
