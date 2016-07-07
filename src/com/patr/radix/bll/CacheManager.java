package com.patr.radix.bll;

import java.io.File;

import android.os.Environment;

public class CacheManager {
    
    public static final File CACHE_DIR = new File(Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/radix/cache");;

}
