package com.jgkj.bxxc.activity;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jgkj.bxxc.R;
import com.jgkj.bxxc.bean.UserInfo;
import com.jgkj.bxxc.tools.BaseActivity;
import com.jgkj.bxxc.tools.OnBooleanListener;
import com.jgkj.bxxc.tools.StatusBarCompat;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 提交个人信息照片
 * 上传身份证正反面，上半身，和五指健全照片
 */
public class RegisterDetailActivity2 extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private Button remind;
    private Button back_forward;
    //上传imageView
    private ImageView pic1, pic2, pic3, pic4;
    //样式imageView
    private ImageView style1, style2, style3, style4;
    private Dialog dialog, look_dialog;
    private View inflate, look_inflate;
    private ImageView look_image;
    private Button choosePhoto;
    private Button takePhoto;
    private Button she_cancel;
    private boolean flag = false;
    private UserInfo.Result result;
    private Uri corpUri;
    private Context context;
    // 拍照
    private static int CAMERA_REQUEST_CODE = 1001;
    // 相册选图
    private static int CHOOSE_PICTIRE = 1002;
    // 裁剪
    private static int CROP_REQUEST_CODE = 1003;
    //4张照片的本地url
    private String url1, url2, url3, url4;
    private Intent intent;
    private int uid;
    private Uri corpnewUri;
    private long random, random1, random2, random3, random4, newrandom1, newrandom2, newrandom3, newrandom4;
    private File file1, file2, file3, file4;
    //上传照片
    private Button submit;
    private ProgressDialog proDialog;
    private String imageName = null;
    private Uri nowUri;
    private String token = "";
    private String oldupLoadUrls = "http://www.baixinxueche.com/index.php/Home/Api/get_files";
    private String upLoadUrl = "http://www.baixinxueche.com/index.php/Home/Api/get_filesAgain";
    private String upLoadUrls = "http://www.baixinxueche.com/index.php/Home/Api/get_filesAgainThree";

    //内部类，用来解析无result的json
    private class Result {
        private String code;
        private String reason;
        public String getCode() {
            return code;
        }
        public String getReason() {
            return reason;
        }
    }
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Gson gson = new Gson();
            Result res = gson.fromJson(msg.obj.toString(), Result.class);
            if (res.getCode().equals("200")) {
                Toast.makeText(RegisterDetailActivity2.this, res.getReason(), Toast.LENGTH_SHORT).show();
//                Intent login = new Intent();
//                login.setClass(RegisterDetailActivity2.this, HomeActivity.class);
//                login.putExtra("FromActivity", "MySetting");
//                startActivity(login);
                finish();
            } else {
                Toast.makeText(RegisterDetailActivity2.this, res.getReason(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerdetail);
        StatusBarCompat.compat(this, Color.parseColor("#37363C"));
        context = this;
        init();
        initData();
    }

    /**
     * 初始化数据，读取本地个人信息，和生成图片名
     */
    private void initData() {
        SharedPreferences sp = getSharedPreferences("USER",
                Activity.MODE_PRIVATE);
        String str = sp.getString("userInfo", null);
        Gson gson = new Gson();
        UserInfo userInfo = gson.fromJson(str, UserInfo.class);
        result = userInfo.getResult();
        SharedPreferences sp2 = getSharedPreferences("token",
                Activity.MODE_PRIVATE);
        token = sp2.getString("token", null);
        uid = result.getUid();
        random1 = System.currentTimeMillis();
        random2 = random1 + 1;
        random3 = random1 + 2;
        random4 = random1 + 3;
        newrandom1 = random1 + 4;
        newrandom2 = random1 + 5;
        newrandom3 = random1 + 6;
        newrandom4 = random1 + 7;

    }

    /**
     * 初始化控件
     */
    private void init() {
        File tmpDir = new File(Environment.getExternalStorageDirectory()
                + "/baixinxueche/image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        title = (TextView) findViewById(R.id.text_title);
        title.setText("完善信息");
        remind = (Button) findViewById(R.id.button_forward);
        remind.setOnClickListener(this);
        remind.setVisibility(View.VISIBLE);
        remind.setText("上传须知");

        back_forward = (Button) findViewById(R.id.button_backward);
        back_forward.setVisibility(View.VISIBLE);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
        //上传imageView实例化
        pic1 = (ImageView) findViewById(R.id.pic1);
        pic2 = (ImageView) findViewById(R.id.pic2);
        pic3 = (ImageView) findViewById(R.id.pic3);
        pic4 = (ImageView) findViewById(R.id.pic4);
        //样式imageView实例化
        style1 = (ImageView) findViewById(R.id.style1);
        style2 = (ImageView) findViewById(R.id.style2);
        style3 = (ImageView) findViewById(R.id.style3);
        style4 = (ImageView) findViewById(R.id.style4);
    }

    /**
     * 图片点击监听方法
     *
     * @param v 视图
     */
    public void showPic(View v) {
        switch (v.getId()) {
            case R.id.pic1:
                creatDialog();
                setTag(pic1, pic2, pic3, pic4);
                break;
            case R.id.pic2:
                creatDialog();
                setTag(pic2, pic1, pic3, pic4);
                break;
            case R.id.pic3:
                creatDialog();
                setTag(pic3, pic2, pic1, pic4);
                break;
            case R.id.pic4:
                creatDialog();
                setTag(pic4, pic3, pic2, pic1);
                break;
        }
    }

    /**
     * 上传照片
     *
     * @param http_url  请求地址
     * @param filepath1 图片地址
     * @param filepath2
     * @param filepath3
     * @param filepath4
     * @param uid       用户id
     */
    public void uploadImage(final String http_url, final String filepath1, final String filepath2
            , final String filepath3, final String filepath4, final String uid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file1 = new File(filepath1);
                    File file2 = new File(filepath2);
                    File file3 = new File(filepath3);
                    //File file4 = new File(filepath4);
                    if (!file1.exists() || !file2.exists() || !file3.exists()) {
                        Toast.makeText(RegisterDetailActivity2.this, "文件不存在", Toast.LENGTH_SHORT).show();
                    }

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(http_url);

                    FileBody fileBody1 = new FileBody(file1, "image/png");
                    FileBody fileBody2 = new FileBody(file2, "image/png");
                    FileBody fileBody3 = new FileBody(file3, "image/png");
                    //FileBody fileBody4 = new FileBody(file4, "image/png");
                    MultipartEntity entity = new MultipartEntity();

                    entity.addPart("idCard1", fileBody1);//uploadedfile是图片上传的键值名
                    entity.addPart("idCard2", fileBody2);
                    entity.addPart("upbody", fileBody3);
                    //entity.addPart("finger", fileBody4);
                    entity.addPart("uid", new StringBody(uid, Charset.forName("UTF-8")));//设置要传入的参数，key_app是键值名,此外传参时候需要指定编码格式
                    entity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));//设置要传入的参数，key_app是键值名,此外传参时候需要指定编码格式
                    post.setEntity(entity);
                    HttpResponse response = client.execute(post);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity httpEntity = response.getEntity();
                        final String result = EntityUtils.toString(httpEntity, "utf-8");
                        proDialog.dismiss();
                        Message msg = new Message();
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    } else {
                        Toast.makeText(RegisterDetailActivity2.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    proDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setTag(ImageView current, ImageView img2, ImageView img3, ImageView img4) {
        current.setTag("CHICK");
        img2.setTag("UNCHICK");
        img3.setTag("UNCHICK");
        img4.setTag("UNCHICK");
    }

    private void getTag() {
        if (pic1.getTag().toString().equals("CHICK")) {
            imageName = uid +""+ random1 ;
        } else if (pic2.getTag().toString().equals("CHICK")) {
            imageName = uid + ""+ random2 ;
        } else if (pic3.getTag().toString().equals("CHICK")) {
            imageName = uid + ""+ random3 ;
        } else if (pic4.getTag().toString().equals("CHICK")) {
            imageName = uid + ""+ random4 ;
        }
    }

    //裁剪后重新命名
    private void getTag2() {
        if (pic1.getTag().toString().equals("CHICK")) {
            imageName = uid  + ""+ newrandom1;
        } else if (pic2.getTag().toString().equals("CHICK")) {
            imageName = uid  + ""+ newrandom2;
        } else if (pic3.getTag().toString().equals("CHICK")) {
            imageName = uid + ""+ newrandom3 ;
        } else if (pic4.getTag().toString().equals("CHICK")) {
            imageName = uid + ""+ newrandom4 ;
        }

        File fileTemp = new File(Environment.getExternalStorageDirectory() + "/baixinxueche/image/" + imageName + ".png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //高版本一定要加上这两句话，做一下临时的Uri
            FileProvider.getUriForFile(this, "com.jgkj.bxxc.fileProvider",fileTemp);
        }
        corpnewUri = Uri.fromFile(fileTemp);
    }

    /**
     * 摄像头拍照或者调用图片库存
     */
    private void creatDialog() {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        inflate = LayoutInflater.from(this).inflate(R.layout.she_dialog, null);
        choosePhoto = (Button) inflate.findViewById(R.id.choosePhoto);
        takePhoto = (Button) inflate.findViewById(R.id.takePhoto);
        she_cancel = (Button) inflate.findViewById(R.id.she_cancel);
        she_cancel.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        // 将布局设置给Dialog
        dialog.setContentView(inflate);
        // 获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置dialog横向充满
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        // 获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 20;// 设置Dialog距离底部的距离
        // 将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();// 显示对话框，一定要设置
    }

    /**
     * 回调函数，处理头像设置
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param data        返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (requestCode == CAMERA_REQUEST_CODE) {//照相
            if (nowUri != null) {
                startImgZoom(nowUri);
            }
        } else if (requestCode == CHOOSE_PICTIRE) {//选择照片
            if (data == null) {
                return;
            } else {
                Uri uri;
                uri = data.getData();
                startImgZoom(uri);
            }
        } else if (requestCode == CROP_REQUEST_CODE) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), corpnewUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File tmpDir = new File(Environment.getExternalStorageDirectory() + "/baixinxueche/image/"+imageName + ".png");
            if (pic1.getTag().toString().equals("CHICK") &&tmpDir.exists()) {
                pic1.setImageBitmap(bitmap);
            } else if (pic2.getTag().toString().equals("CHICK") &&tmpDir.exists()) {
                pic2.setImageBitmap(bitmap);
            } else if (pic3.getTag().toString().equals("CHICK") &&tmpDir.exists()) {
                pic3.setImageBitmap(bitmap);
            } else if (pic4.getTag().toString().equals("CHICK") &&tmpDir.exists()) {
                pic4.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 调用手机自带的裁剪，图像裁剪
     *
     * @param uri 图片uri
     */
    private void startImgZoom(Uri uri) {
        getTag2();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, corpnewUri);
        intent.putExtra("scale", true);
        //裁剪框比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1.5 );
        //图片输出大小

        startActivityForResult(intent, CROP_REQUEST_CODE);
    }


    /**
     * 拍照，设置图片保存路径
     *
     * @return
     */
    private File getImageStoragePath(Context context) {
        File tmpDir = new File(Environment.getExternalStorageDirectory() + "/baixinxueche/image/");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        getTag();
        File img = new File(tmpDir.getAbsoluteFile() + "/" + imageName + ".png");
        try {
            return img;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_backward:
                finish();
                break;
            case R.id.takePhoto:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onPermissionRequests(Manifest.permission.CAMERA, new OnBooleanListener() {
                        @Override
                        public void onClick(boolean bln) {

//                                /*
//                                * 这里就是高版本需要注意的，需用使用FileProvider来获取Uri，同时需要注意getUriForFile
//                                * 方法第二个参数要与AndroidManifest.xml中provider的里面的属性authorities的值一致
//                                * */
//                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            imageUriFromCamera = FileProvider.getUriForFile(MainActivity.this,
//                                    "com.xuezj.fileproviderdemo.fileprovider", photoFile);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriFromCamera);
//                            startActivityForResult(intent, GET_IMAGE_BY_CAMERA_U);

                            nowUri = FileProvider.getUriForFile(context, "com.jgkj.bxxc.fileProvider", getImageStoragePath(context));
                            Intent intent_takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent_takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, nowUri);
                            intent_takePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(intent_takePhoto, CAMERA_REQUEST_CODE);
                        }
                    });
                }else{
                    nowUri = Uri.fromFile(getImageStoragePath(this));
                    Intent intent_takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, nowUri);
                    startActivityForResult(intent_takePhoto, CAMERA_REQUEST_CODE);
                }
                dialog.dismiss();
                break;
            case R.id.choosePhoto:
                Intent action_getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                action_getPhoto.setType("image/*");
                startActivityForResult(action_getPhoto, CHOOSE_PICTIRE);
                dialog.dismiss();
                break;
            case R.id.she_cancel:
                dialog.dismiss();
                break;
            case R.id.submit:
                proDialog = ProgressDialog.show(RegisterDetailActivity2.this, null, "上传中...");
                uploadImage(upLoadUrls,
                        Environment.getExternalStorageDirectory() + "/baixinxueche/image/" + uid  + ""+ newrandom1 + ".png",
                        Environment.getExternalStorageDirectory() + "/baixinxueche/image/" + uid  + ""+ newrandom2 + ".png",
                        Environment.getExternalStorageDirectory() + "/baixinxueche/image/" + uid  + ""+ newrandom3 + ".png",
                        "",
                        uid + "");
                break;
            case R.id.button_forward:
                Intent intent = new Intent();
                intent.setClass(this,WebViewActivity.class);
                intent.putExtra("url","http://www.baixinxueche.com/webshow/thing/pic.html");
                intent.putExtra("title","上传须知");
                startActivity(intent);
                break;
        }
    }
}
