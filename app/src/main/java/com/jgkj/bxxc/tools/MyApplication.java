package com.jgkj.bxxc.tools;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.jgkj.bxxc.db.DBManager;
import com.jgkj.bxxc.db.DaoMaster;
import com.jgkj.bxxc.db.DaoSession;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.utils.Log;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    public static RequestQueue queue;
    private final static String dbName = "sub_db1";
    protected static MyApplication app;
    static DaoMaster daoMaster;
    static DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        UMShareAPI.get(this);
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);

        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //创建队列
        queue = Volley.newRequestQueue(getApplicationContext());

        // 置入一个不设防的VmPolicy（不设置的话 7.0以上一调用拍照功能就崩溃了）
        // 还有一种方式：manifest中加入provider然后修改intent代码
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        app = this;
        DBManager.getInstance(app);
    }
    {
        //QQ
        PlatformConfig.setQQZone("1106051977","yFJBgfD4DvYsdoma");
        //微信
        PlatformConfig.setWeixin("wx75b78ead0e64a547","16a2704c6eab845d6170899f201d2321");
        //新浪
        PlatformConfig.setSinaWeibo("6681699", "c2318d8d8d8bd0906297aad3394abeec","http://sns.whalecloud.com");
    }
    //暴漏一个方法返回请求队列
    public static RequestQueue getQueue() {
        return queue;
    }

    public static MyApplication getInstance() {
        return app;
    }

    /**
     * 取得DaoSession
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,dbName, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

}