package com.mxth.mybanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerView banner = (BannerView) findViewById(R.id.banner);
        ArrayList<Object> list=new ArrayList<>();
        list.add(R.mipmap.ic_launcher);
        list.add(R.mipmap.ic_launcher);
        list.add("http://a4.att.hudong.com/38/47/19300001391844134804474917734_950.png");
        list.add(R.mipmap.ic_launcher);
        list.add("http://mvimg2.meitudata.com/53fdb48c9381e8750.jpg");
        banner.setImageResources(list, new BannerView.ImageCycleViewListener() {
            @Override
            public void displayImage(Object imageURL, ImageView imageView) {
                if(imageURL instanceof String){
                    //String类型的字符串，网络url 采用Glide或者Picasso框架加载
                    Glide.with(MainActivity.this).load((String)imageURL).into(imageView);
                }else if(imageURL instanceof Integer){
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    //int类型数据 resource id
                    imageView.setImageResource((Integer) imageURL);
                }
            }

            @Override
            public void onImageClick(int position, View imageView) {

            }
        });
    }
}
