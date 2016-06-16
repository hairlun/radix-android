package com.patr.radix;

import com.patr.radix.fragment.FoundFragment;
import com.patr.radix.fragment.NewsFragment;
import com.patr.radix.fragment.ReadFragment;
import com.patr.radix.fragment.VideoFragment;

public class TabDb {
    public static String[] getTabsTxt(){
        String[] tabs={"手机开门","方可申请","消息推送","个人设置"};
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
        Class[] clz={NewsFragment.class, ReadFragment.class, VideoFragment.class, FoundFragment.class};
        return clz;
    }
}
