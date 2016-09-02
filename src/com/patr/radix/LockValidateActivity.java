/**
 * radix
 * LockValidateActivity
 * zhoushujie
 * 2016-7-21 上午10:56:59
 */
package com.patr.radix;

import java.util.List;

import org.xutils.x;
import org.xutils.common.util.LogUtil;

import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.lock.LockPatternUtils;
import com.patr.radix.view.LockPatternView;
import com.patr.radix.view.LockPatternView.Cell;
import com.patr.radix.view.LockPatternView.DisplayMode;
import com.patr.radix.view.LockPatternView.OnPatternListener;
import com.patr.radix.view.TitleBarView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * 手势密码设置验证界面
 * 
 * @author zhoushujie
 * 
 */
public class LockValidateActivity extends Activity implements OnPatternListener {

    Context context;

    private TitleBarView titleBarView;

    /** 手势密码界面 */
    private LockPatternView lockPatternView;

    /** 标题 */
    private TextView title;

    /** 密码数据 */
    private List<Cell> lockPattern;

    private int requestCode;

    /** 手势密码错误的次数 */
    private int wrongCount;

    private static final int LOCK_WRONG_MAX = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setup);
        context = this;
        requestCode = getIntent().getIntExtra("requestCode",
                Constants.LOCK_CHECK);
        String patternString = PrefUtil.getString(context,
                Constants.PREF_LOCK_KEY, null);
        if (TextUtils.isEmpty(patternString)) {
            finish();
            return;
        }
        lockPattern = LockPatternUtils.stringToPattern(patternString);

        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.lock_settings_titlebar);
        if (requestCode == Constants.LOCK_CLEAR) {
            titleBarView.setTitle(R.string.titlebar_lock_close);
        } else {
            titleBarView.setTitle(R.string.titlebar_lock_check);
        }
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        title = (TextView) findViewById(R.id.text_lock_hint);
        title.setText(R.string.lockpattern_enter_lockpattern);
    }

    @Override
    public void onBackPressed() {
        setResult(Constants.LOCK_CHECK_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onPatternStart() {
        LogUtil.e("onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        LogUtil.e("onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
        LogUtil.e("onPatternCellAdded");
        LogUtil.d(LockPatternUtils.patternToString(pattern));
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        LogUtil.e("onPatternDetected");

        if (pattern.equals(lockPattern)) {
            title.setTextColor(getResources().getColor(R.color.right_text));
            title.setText("手势密码正确");
            lockPatternView.clearPattern();
            wrongCount = 0;
            // 其他验证返回结果码
            setResult(Constants.LOCK_CHECK_OK);
            finish();
        } else {
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            title.setTextColor(getResources().getColor(R.color.error_text));
            title.setText(getString(R.string.lock_error, LOCK_WRONG_MAX
                    - ++wrongCount));

            if (wrongCount == LOCK_WRONG_MAX) {
                // 其他验证返回结果码
                setResult(Constants.LOCK_CHECK_WRONG);
                finish();
            }
        }
    }

    /**
     * 启动
     * 
     * @param activity
     * @param requestCode
     */
    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(),
                LockValidateActivity.class);
        intent.putExtra("requestCode", requestCode);
        fragment.startActivityForResult(intent, requestCode);
    }

}
