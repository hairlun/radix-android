package com.patr.radix.utils;

import com.patr.radix.R;
import com.patr.radix.R.drawable;
import com.patr.radix.R.string;
import com.patr.radix.ui.message.MessageFragment;
import com.patr.radix.ui.settings.SettingsFragment;
import com.patr.radix.ui.unlock.UnlockFragment;
import com.patr.radix.ui.visitor.VisitorFragment;

public class TabDb {
    public static int[] getTabsTxt() {
        int[] tabs = { R.string.main_tab_unlock, R.string.main_tab_visitor,
                R.string.main_tab_message, R.string.main_tab_settings };
        return tabs;
    }

    public static int[] getTabsImg() {
        int[] ids = { R.drawable.bar_icon_open_the_door, R.drawable.bar_icon_visitor,
                R.drawable.bar_icon_news, R.drawable.bar_icon_set_up };
        return ids;
    }

    public static int[] getTabsImgLight() {
        int[] ids = { R.drawable.bar_icon_open_the_door_n,
                R.drawable.bar_icon_visitor_n, R.drawable.bar_icon_news_n,
                R.drawable.bar_icon_set_up_n };
        return ids;
    }

    public static Class[] getFragments() {
        Class[] clz = { UnlockFragment.class, VisitorFragment.class,
                MessageFragment.class, SettingsFragment.class };
        return clz;
    }
}
