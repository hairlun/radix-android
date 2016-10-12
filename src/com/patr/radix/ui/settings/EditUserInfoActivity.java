package com.patr.radix.ui.settings;

import java.io.File;

import org.xutils.x;
import org.xutils.common.Callback.Cancelable;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.bean.Community;
import com.patr.radix.bean.MobileUploadResult;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.LoadingDialog;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.BitmapUtil;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.FileSystemManager;
import com.patr.radix.utils.PicturePropertiesBean;
import com.patr.radix.utils.ToastUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserInfoActivity extends Activity implements OnClickListener {

    private Context context;

    private TitleBarView titleBarView;

    private ImageView avatarIv;

    private Button changeAvatarBtn;

    private RelativeLayout phoneRl;

    private RelativeLayout pwdRl;

    private TextView phoneTv;
    
    private LoadingDialog loadingDialog;

    /**
     * 拍摄图片的url地址
     */
    private String photoPath;

    private Cancelable submitHttpHandler;

    /**
     * 缩放尺寸
     */
    protected static final float IMG_SCALE = 640f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        context = this;
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneTv.setText(MyApplication.instance.getUserInfo().getMobile());
        String pic = MyApplication.instance.getUserInfo().getUserPic();
        if (!TextUtils.isEmpty(pic)) {
            x.image().bind(avatarIv, pic);
        }
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.user_info_titlebar);
        avatarIv = (ImageView) findViewById(R.id.avatar_iv);
        changeAvatarBtn = (Button) findViewById(R.id.change_avatar_btn);
        phoneRl = (RelativeLayout) findViewById(R.id.phone_rl);
        pwdRl = (RelativeLayout) findViewById(R.id.pwd_rl);
        phoneTv = (TextView) findViewById(R.id.phone_tv);

        changeAvatarBtn.setOnClickListener(this);
        phoneRl.setOnClickListener(this);
        pwdRl.setOnClickListener(this);

        titleBarView.setTitle(R.string.titlebar_edit_user_info);
        loadingDialog = new LoadingDialog(context);
    }

    private void showPhotoDiaLog() {
        photoPath = System.currentTimeMillis() + ".jpg";
        final Dialog imageSelectDialog = new Dialog(context,
                R.style.image_select_dialog);
        imageSelectDialog.setContentView(R.layout.view_image_select_dialog);
        imageSelectDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
        imageSelectDialog.getWindow().getAttributes().height = LayoutParams.WRAP_CONTENT;
        imageSelectDialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        Button camera = (Button) imageSelectDialog.findViewById(R.id.camera);
        Button gallery = (Button) imageSelectDialog.findViewById(R.id.gallery);
        Button cancel = (Button) imageSelectDialog.findViewById(R.id.cancel);
        imageSelectDialog.show();
        /**
         * 从相册选取照片
         */
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                imageSelectDialog.dismiss();
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constants.GALLERY);
            }
        });
        /**
         * 从相机拍摄照片
         */
        camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 先验证手机是否有sdcard
                imageSelectDialog.dismiss();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    try {
                        photoPath = FileSystemManager.getTemporaryPath(context)
                                + photoPath;
                        File picture = new File(photoPath);
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageFileUri = Uri.fromFile(picture);
                        intent.putExtra(
                                android.provider.MediaStore.EXTRA_OUTPUT,
                                imageFileUri);
                        startActivityForResult(intent, Constants.CAMERA);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "没有找到储存目录", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(context, "没有储存卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                imageSelectDialog.dismiss();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filename = System.currentTimeMillis() + ".jpg";
        String zoomPath = FileSystemManager.getPostPath(context) + filename;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case Constants.CAMERA:
                BitmapUtil.getImageScaleByPath(new PicturePropertiesBean(
                        photoPath, zoomPath, IMG_SCALE, IMG_SCALE), context);
                uploadAvatar(filename, zoomPath);
                break;
            // 直接从相册获取
            case Constants.GALLERY:
                if (data != null) {
                    Uri originalUri = data.getData();
                    // 从媒体db中查询图片路径
                    String[] proj = { MediaStore.Images.Media.DATA };
                    Cursor cursor = managedQuery(originalUri, proj, null, null,
                            null);
                    if (cursor != null) {
                        int col = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        photoPath = cursor.getString(col);
                        cursor.close();
                        BitmapUtil.getImageScaleByPath(
                                new PicturePropertiesBean(photoPath, zoomPath,
                                        IMG_SCALE, IMG_SCALE), context);
                        uploadAvatar(filename, zoomPath);
                    } else {
                        Toast.makeText(context, "获取图片失败", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
                break;
            default:
                break;
            }
        }
    }

    private void uploadAvatar(String filename, String filepath) {
        submitHttpHandler = ServiceManager.mobileUpload(filename, new File(
                filepath), new RequestListener<MobileUploadResult>() {

            @Override
            public void onStart() {
                loadingDialog.show("正在上传…");
            }

            @Override
            public void onSuccess(int stateCode, MobileUploadResult result) {
                if (result.isSuccesses()) {
                    String userPic = result.getUserPic();
                    updateAvatar(userPic);
                }
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, R.string.connect_exception);
                loadingDialog.dismiss();
            }

        });
    }
    
    private void updateAvatar(final String userPic) {
        ServiceManager.updateUserPortrait(userPic, new RequestListener<RequestResult>() {

            @Override
            public void onSuccess(int stateCode, RequestResult result) {
                String pic = userPic;
                if (!TextUtils.isEmpty(userPic) && !userPic.startsWith("http")) {
                    Community community = MyApplication.instance
                            .getSelectedCommunity();
                    if (userPic.contains("surpass")) {
                        pic = String.format("%s:%s/%s", community.getHost(), community.getPort(),
                            userPic);
                    } else {
                        pic = String.format("%s:%s/surpass/%s",
                                community.getHost(), community.getPort(),
                                userPic);
                    }
                }
                MyApplication.instance.getUserInfo().setUserPic(pic);
                x.image().bind(avatarIv, pic);
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(Exception error, String content) {
                ToastUtil.showShort(context, R.string.connect_exception);
                loadingDialog.dismiss();
            }
            
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.change_avatar_btn:
            showPhotoDiaLog();
            break;

        case R.id.phone_rl:
            startActivity(new Intent(context, UpdateUserPhoneActivity.class));
            break;

        case R.id.pwd_rl:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        if (submitHttpHandler != null) {
            submitHttpHandler.cancel();
        }
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }

}
