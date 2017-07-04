package com.jgkj.bxxc.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import com.bigkoo.convenientbanner.holder.Holder;
import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.activity.InviteFriendsActivity;
import com.jgkj.bxxc.activity.LoginActivity;
import com.jgkj.bxxc.activity.PrivateActivity;
import com.jgkj.bxxc.activity.WebViewActivity;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.bean.entity.BannerEntity.BannerEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 网络图片加载例子
 */
public class NetworkImageHolderView implements Holder<BannerEntity> {
    private ImageView imageView;
    private UserInfo userInfo;
    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(final Context context, final int position, final BannerEntity data) {
        //imageView.setImageResource(R.mipmap.ic_launcher);
        ImageLoader.getInstance().displayImage(data.getPic(),imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = context.getSharedPreferences("USER", Activity.MODE_PRIVATE);
                String str = sp.getString("userInfo", null);
                Gson gson = new Gson();
                if(str != null){
                    userInfo = gson.fromJson(str, UserInfo.class);
                }
                //点击事件
                Intent intent = new Intent();
                if(data.getKey().equals("1")){
                    intent.setClass(context,WebViewActivity.class);
                    intent.putExtra("url",data.getUrl());
                    intent.putExtra("title",data.getTitle());
                    context.startActivity(intent);
                }
                if(data.getKey().equals("2")){
                    if (userInfo == null){
                        intent.setClass(context, LoginActivity.class);
                        intent.putExtra("message","lunbotu");
                        context.startActivity(intent);
                    }else{
                        intent.setClass(context,InviteFriendsActivity.class);
                        context.startActivity(intent);
                    }
                }
                if(data.getKey().equals("3")){
                    if (userInfo == null){
                        intent.setClass(context, LoginActivity.class);
                        intent.putExtra("message","privateCoach");
                        context.startActivity(intent);
                    }else{
                        intent.setClass(context, PrivateActivity.class);
                        context.startActivity(intent);
                    }
                }
                if (data.getKey().equals("4")){
                    new InvitedCouponDialog(context).call();
                }

            }
        });
    }
}
