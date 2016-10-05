package com.patr.radix.ui.settings;

import java.io.File;

import org.xutils.common.util.MD5;

import com.patr.radix.MyApplication;
import com.patr.radix.R;
import com.patr.radix.R.id;
import com.patr.radix.R.layout;
import com.patr.radix.R.string;
import com.patr.radix.bean.RequestResult;
import com.patr.radix.bll.ServiceManager;
import com.patr.radix.network.RequestListener;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.utils.BitmapUtil;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    /**
     * 拍摄图片的url地址
     */
    private String photoPath;

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
        phoneTv.setText(MyApplication.instance.getUserInfo().getMobile());
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
                startActivityForResult(intent, 2);
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
                        photoPath = FileSystemManager
                                .getTemporaryPath(context)
                                + photoPath;
                        File picture = new File(photoPath);
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri imageFileUri = Uri.fromFile(picture);
                        intent.putExtra(
                                android.provider.MediaStore.EXTRA_OUTPUT,
                                imageFileUri);
                        startActivityForResult(intent, 1);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "没有找到储存目录", 1)
                                .show();
                    }
                } else {
                    Toast.makeText(context, "没有储存卡", 1)
                            .show();
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
        String zoomPath = FileSystemManager
                .getPostPath(context)
                + System.currentTimeMillis() + ".jpg";
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case 1:
                BitmapUtil.getImageScaleByPath(new PicturePropertiesBean(
                        photoPath, zoomPath, IMG_SCALE, IMG_SCALE),
                        context);
//                paths.add(zoomPath);
                break;
            // 直接从相册获取
            case 2:
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
                                    new PicturePropertiesBean(photoPath,
                                            zoomPath, IMG_SCALE, IMG_SCALE),
                                    context);
//                            paths.add(zoomPath);
//                            refreshImageView();
                    } else {
                        Toast.makeText(context, "获取图片失败", 1)
                                .show();
                    }

                }
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.change_avatar_btn:
            break;
            
        case R.id.phone_rl:
            break;
            
        case R.id.pwd_rl:
            break;
        }
    }

}
