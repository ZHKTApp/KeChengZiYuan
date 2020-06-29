package com.zwyl.course.base;

/**
 * Created by zhubo on 2017/11/24.
 */

public class ComFlag {
    public class CoursePath {
        public static final int Path_ALL = 0;//全部
        public static final int Path_collect = 1;//收藏
        public static final int Path_local = 2;//本地
    }

    public class NumFlag {
        public static final int INTENT_WEL = 0;//app从后台到前台或者锁屏后回到app，跳转WelcomActivit时的参数
    }

    public class StrFlag {
        public static final String TAG = "TAG";//常用字符串
    }

    /*课本详情界面popwindow*/
    public class PopFlag {
        public static final String TITLE = "title";//顶部button按钮
        public static final String NAME = "name";//下面的条目
    }
}
