package com.zwyl.course.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zwyl.course.R;
import com.zwyl.course.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        super.initView();
        setHeadView();
        String resourceUri = getIntent().getStringExtra("resourceUri");
        if(resourceUri != null) {
            videoView.setMediaController(new MediaController(PlayActivity.this));
            videoView.setVideoPath(resourceUri);
            videoView.start();
        }
    }
    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter("视频");
        setShowLeftHead(false);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(true);//logo
        setShowRefresh(false);//刷新
        setLogoClick(v -> {
            finish();
        });
    }

}
