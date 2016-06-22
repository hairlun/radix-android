package com.patr.radix;

import com.patr.radix.fragment.SettingsFragment;
import com.patr.radix.fragment.UnlockFragment;
import com.patr.radix.fragment.VisitorFragment;
import com.patr.radix.fragment.MessageFragment;

public class TabDb {
    public static String[] getTabsTxt(){
        String[] tabs={"手机开门","访客申请","消息推送","个人设置"};
        return tabs;
    }
    public static int[] getTabsImg(){
        int[] ids={R.drawable.foot_icon_01, R.drawable.foot_icon_02, R.drawable.foot_icon_03, R.drawable.foot_icon_04};
        return ids;
    }
    public static int[] getTabsImgLight(){
        int[] ids={R.drawable.foot_icon_hover_01, R.drawable.foot_icon_hover_02, R.drawable.foot_icon_hover_03, R.drawable.foot_icon_hover_04};
        return ids;
    }
    public static Class[] getFragments(){
        Class[] clz={UnlockFragment.class, VisitorFragment.class, MessageFragment.class, SettingsFragment.class};
        return clz;
    }
}
