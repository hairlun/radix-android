package com.patr.radix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xutils.common.util.LogUtil;

import com.patr.radix.ui.view.LockPatternView;
import com.patr.radix.ui.view.TitleBarView;
import com.patr.radix.ui.view.LockPatternView.Cell;
import com.patr.radix.ui.view.LockPatternView.DisplayMode;
import com.patr.radix.ui.view.LockPatternView.OnPatternListener;
import com.patr.radix.utils.Constants;
import com.patr.radix.utils.PrefUtil;
import com.patr.radix.utils.ToastUtil;
import com.patr.radix.utils.lock.LockPatternUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class LockSetupActivity extends Activity implements OnPatternListener {

    Context context;

    private TitleBarView titleBarView;

    /** 手势密码界面 */
    private LockPatternView lockPatternView;

    /** 标题 */
    private TextView title;

    /** 步骤开始 */
    private static final int STEP_1 = 1;

    /** 步骤第一次设置手势完成 */
    private static final int STEP_2 = 2;

    /** 步骤按下继续按钮 */
    private static final int STEP_3 = 3;

    /** 步骤第二次设置手势完成 */
    private static final int STEP_4 = 4;

    // /** 步骤按确认按钮 */
    // private static final int SETP_5 = 4;

    /** 当前步骤 */
    private int step;

    /** 手势密码数据 */
    private List<Cell> choosePattern;

    private boolean confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_setup);
        context = this;
        initView();
    }

    private void initView() {
        titleBarView = (TitleBarView) findViewById(R.id.lock_settings_titlebar);
        titleBarView.setTitle(R.string.titlebar_lock_setup);
        lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        title = (TextView) findViewById(R.id.text_lock_hint);
        step = STEP_1;
        updateView();
    }

    private void updateView() {
        switch (step) {
        case STEP_1:
            choosePattern = null;
            confirm = false;
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_2:
            title.setTextColor(getResources().getColor(R.color.right_text));
            title.setText(R.string.lockpattern_reenter_new_lockpattern);
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_3:
            title.setTextColor(getResources().getColor(R.color.right_text));
            title.setText(R.string.lockpattern_reenter_new_lockpattern);
            lockPatternView.clearPattern();
            lockPatternView.enableInput();
            break;
        case STEP_4:
            if (confirm) {
                title.setTextColor(getResources().getColor(R.color.right_text));
                title.setText(R.string.lockpattern_ok);
                lockPatternView.disableInput();

                PrefUtil.save(context, Constants.PREF_LOCK_KEY,
                        LockPatternUtils.patternToString(choosePattern));
                finish();
            } else {
                title.setTextColor(getResources().getColor(R.color.error_text));
                title.setText(R.string.lock_confirm_error);
                lockPatternView.setDisplayMode(DisplayMode.Wrong);
                lockPatternView.enableInput();
            }

            break;

        default:
            break;
        }
    }

    @Override
    public void onPatternStart() {
        LogUtil.d("onPatternStart");
    }

    @Override
    public void onPatternCleared() {
        LogUtil.d("onPatternCleared");
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
        LogUtil.d("onPatternCellAdded");
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        LogUtil.d("onPatternDetected");

        if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) {
            ToastUtil.showShort(this,
                    R.string.lockpattern_recording_incorrect_too_short);
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            return;
        }

        if (choosePattern == null) {
            choosePattern = new ArrayList<Cell>(pattern);
            // LogUtil.d( "choosePattern = "+choosePattern.toString());
            // LogUtil.d( "choosePattern.size() = "+choosePattern.size());
            LogUtil.d("choosePattern = "
                    + Arrays.toString(choosePattern.toArray()));

            step = STEP_2;
            updateView();
            return;
        }
        // [(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]
        // [(row=1,clmn=0), (row=2,clmn=0), (row=1,clmn=1), (row=0,clmn=2)]

        LogUtil.d("choosePattern = " + Arrays.toString(choosePattern.toArray()));
        LogUtil.d("pattern = " + Arrays.toString(pattern.toArray()));

        if (choosePattern.equals(pattern)) {
            // LogUtil.d( "pattern = "+pattern.toString());
            // LogUtil.d( "pattern.size() = "+pattern.size());
            LogUtil.d("pattern = " + Arrays.toString(pattern.toArray()));

            confirm = true;
        } else {
            confirm = false;
        }

        step = STEP_4;
        updateView();

    }

    /**
     * 
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LockSetupActivity.class);
        context.startActivity(intent);
    }

}
