package com.patr.radix.utils;

import com.patr.radix.R;
import com.patr.radix.R.drawable;
import com.patr.radix.R.string;
import com.patr.radix.ui.SettingsFragment;
import com.patr.radix.ui.UnlockFragment;
import com.patr.radix.ui.VisitorFragment;
import com.patr.radix.ui.message.MessageFragment;

public class TabDb {
    public static int[] getTabsTxt() {
        int[] tabs = { R.string.main_tab_unlock, R.string.main_tab_visitor,
                R.string.main_tab_message, R.string.main_tab_settings };
        return tabs;
    }

    public static int[] getTabsImg() {
        int[] ids = { R.drawable.foot_icon_01, R.drawable.foot_icon_02,
                R.drawable.foot_icon_03, R.drawable.foot_icon_04 };
        return ids;
    }

    public static int[] getTabsImgLight() {
        int[] ids = { R.drawable.foot_icon_hover_01,
                R.drawable.foot_icon_hover_02, R.drawable.foot_icon_hover_03,
                R.drawable.foot_icon_hover_04 };
        return ids;
    }

    public static Class[] getFragments() {
        Class[] clz = { UnlockFragment.class, VisitorFragment.class,
                MessageFragment.class, SettingsFragment.class };
        return clz;
    }
}
