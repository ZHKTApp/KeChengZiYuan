package com.zwyl.course.main;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.VideoView;

import com.mayigeek.frame.http.state.HttpSucess;
import com.zwyl.course.App;
import com.zwyl.course.R;
import com.zwyl.course.base.BaseActivity;
import com.zwyl.course.base.ComFlag;
import com.zwyl.course.base.adapter.CommonAdapter;
import com.zwyl.course.base.adapter.MultiItemTypeAdapter;
import com.zwyl.course.base.adapter.ViewHolder;
import com.zwyl.course.dialog.TitleDialog;
import com.zwyl.course.dialog.bean.PopClassBean;
import com.zwyl.course.dialog.bean.PopSubjectBean;
import com.zwyl.course.dialog.popwindow.PopupClass;
import com.zwyl.course.dialog.popwindow.PopupSubject;
import com.zwyl.course.http.ApiUtil;
import com.zwyl.course.service.UserService;
import com.zwyl.course.util.DensityUtil;
import com.zwyl.course.util.DeviceUtil;
import com.zwyl.course.util.DownloadAsyncTask;
import com.zwyl.course.util.FileUtils;
import com.zwyl.course.util.MyProgress;
import com.zwyl.course.viewstate.DownloadProgressButton;
import com.zwyl.course.viewstate.SpaceItemDecoration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.home_recyclerview)
    RecyclerView homeRecyclerview;
    @BindView(R.id.rt_internet)
    RadioButton rtInternet;
    @BindView(R.id.rt_e_book)
    RadioButton rtEBook;
    @BindView(R.id.rb_all)
    RadioButton rbAll;
    @BindView(R.id.rb_collect)
    RadioButton rbCollect;
    @BindView(R.id.rb_local)
    RadioButton rbLocal;
    @BindView(R.id.tv_subject)
    TextView tvSubject;
    @BindView(R.id.tv_subject_select)
    TextView tvSubjectSelect;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.tv_class_select)
    TextView tvClassSelect;
    @BindView(R.id.iv_serch)
    ImageView ivSerch;
    @BindView(R.id.internet_view)
    View internetView;
    @BindView(R.id.ebook_view)
    View ebookView;
    @BindView(R.id.et_search)
    EditText etSearch;


    private List<BeanHomeGrid> mlist = new ArrayList<BeanHomeGrid>();
    //    private List<PopSubjectBean> mlistPopsubject = new ArrayList<PopSubjectBean>();
    //    private List<PopClassBean> mlistPopClass = new ArrayList<PopClassBean>();
    private CommonAdapter<BeanHomeGrid> mAdapter;
    private GridLayoutManager layoutManager;
    private boolean isInternet = true;
    private UserService api;
    private int coursePath;
    private String schoolSubjectId = "";//科目id
    private String schoolEducationGradeId = "";//班级id
    private String resourceName;//搜索文字
    private String collectType = "0";//0全部,1收藏
    private String sourceType = "0";//0网络课程,1电子书
    private String TAG = MainActivity.class.getSimpleName();
    private String token;
    private String url;
    List<BeanHomeGrid> localData = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        //设置顶部按钮事件,日历筛选
        setHeadView();
        //Gridview内容
        token = getIntent().getStringExtra("token");
//        token = "380f984cd40d4754b4061ef4b4bdec6f";
        Log.e(TAG, "token : " + token);

        layoutManager = new GridLayoutManager(App.mContext, 4, OrientationHelper.VERTICAL, false);

        homeRecyclerview.setLayoutManager(layoutManager);
        int spacingInPixels = 5;
        homeRecyclerview.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        homeRecyclerview.setAdapter(mAdapter = new CommonAdapter<BeanHomeGrid>(App.mContext, R.layout.item_home_grid, mlist) {

            @Override
            protected void convert(ViewHolder holder, BeanHomeGrid beanHomeGrid, int position) {
                LinearLayout ll_item_parent = holder.getView(R.id.ll_item_parent);
                ll_item_parent.removeAllViews();
                String resourceId = beanHomeGrid.resourceId;
                String isDownload = beanHomeGrid.isDownload;
                url = beanHomeGrid.resourceUri;
                String videoName = beanHomeGrid.resourceName.trim();
                if (isInternet) {
                    //网络资源
                    View viewInternet = View.inflate(App.mContext, R.layout.item_internet, null);
                    ImageView iv_item_internet_img = (ImageView) viewInternet.findViewById(R.id.iv_item_internet_img);
                    TextView tv_item_internet_people = (TextView) viewInternet.findViewById(R.id.tv_item_internet_people);
                    FrameLayout fl_play = (FrameLayout) viewInternet.findViewById(R.id.fl_play);
                    ImageView iv_item_internet_cellect = (ImageView) viewInternet.findViewById(R.id.iv_item_internet_cellect);//收藏
                    TextView tv_item_internet_name = (TextView) viewInternet.findViewById(R.id.tv_item_internet_name);//标题
                    TextView tv_item_internet_down = (TextView) viewInternet.findViewById(R.id.tv_item_internet_down);//下载
                    TextView tv_item_internet_time = (TextView) viewInternet.findViewById(R.id.tv_item_internet_time);
                    MyProgress myProgress = (MyProgress) viewInternet.findViewById(R.id.progressBar);
                    //修改
                    tv_item_internet_time.setText(beanHomeGrid.createTime + "上传");
                    tv_item_internet_people.setText(beanHomeGrid.teacherName);
                    tv_item_internet_name.setText(beanHomeGrid.resourceName);
                    iv_item_internet_cellect.setSelected(beanHomeGrid.isCollection.equals("true"));//是否收藏
                    showIsDown(isDownload, tv_item_internet_down);//显示是否为已下载
                    ll_item_parent.addView(viewInternet);
                    iv_item_internet_cellect.setOnClickListener(v -> {//设置收藏/取消收藏
                        setcellect(resourceId, iv_item_internet_cellect);

                    });
                    iv_item_internet_img.setOnClickListener(v -> {

                        ApiUtil.doDefaultApi(api.addlog(mlist.get(position).resourceId, "1101", mlist.get(position).cmTeacherId, mlist.get(position).textBookId), new HttpSucess<String>() {
                            @Override
                            public void onSucess(String data) {
                                Log.e("http", "addlog : " + data);
                            }
                        });
                        Intent intent = createIntent(PlayActivity.class);
                        intent.putExtra("resourceUri", beanHomeGrid.resourceUri);
                        startActivity(intent);
                    });

                    iv_item_internet_img.setOnLongClickListener(view -> {
                        if (coursePath == ComFlag.CoursePath.Path_local) {
                            new TitleDialog(MainActivity.this, "确认删除该文件", new TitleDialog.OnclickListener() {
                                @Override
                                public void Onclick() {
                                    //修改
                                    BeanHomeGrid beanHomeGrid = localData.get(position);
                                    if (checkFileIsExist(beanHomeGrid.resourceName, beanHomeGrid.resourceUri, true)) {
                                        localData.remove(beanHomeGrid);
                                        showToast("已删除" + beanHomeGrid.resourceName);
                                    } else
                                        showToast("删除失败");
                                    mAdapter.setDatas(localData);
                                }
                            }).show();
                        }
                        return false;
                    });
                    //修改
                    tv_item_internet_down.setOnClickListener(v -> {//下载
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            return;
                        } else {
                            setDownload(tv_item_internet_down, beanHomeGrid, isDownload, myProgress);
                        }
                    });

                } else {
                    //电子书
                    View viewEbook = View.inflate(App.mContext, R.layout.item_ebook, null);
                    ImageView iv_item_ebook_img = (ImageView) viewEbook.findViewById(R.id.iv_item_ebook_img);
                    FrameLayout fl_play = (FrameLayout) viewEbook.findViewById(R.id.fl_play);
                    ImageView iv_item_ebook_cellect = (ImageView) viewEbook.findViewById(R.id.iv_item_ebook_cellect);//收藏
                    TextView tv_item_ebook_name = (TextView) viewEbook.findViewById(R.id.tv_item_ebook_name);//标题
                    TextView tv_item_ebook_down = (TextView) viewEbook.findViewById(R.id.tv_item_ebook_down);//下载
                    MyProgress myProgress = (MyProgress) viewEbook.findViewById(R.id.progressBar);
                    iv_item_ebook_img.setOnClickListener(v -> {
                        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                        intent.putExtra("fileUri", "http://ow365.cn/?i=18074&ssl=1&furl=" + beanHomeGrid.resourceUri);
                        startActivity(intent);

                    });
                    tv_item_ebook_name.setText(beanHomeGrid.resourceName);
                    iv_item_ebook_cellect.setSelected(beanHomeGrid.isCollection.equals("true"));//是否收藏
                    showIsDown(isDownload, tv_item_ebook_down);//显示是否为已下载
                    ll_item_parent.addView(viewEbook);
                    iv_item_ebook_cellect.setOnClickListener(v -> {//设置收藏/取消收藏
                        setcellect(resourceId, iv_item_ebook_cellect);
                    });
                    tv_item_ebook_down.setOnClickListener(v -> {//下载
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            return;
                        } else {
                            setDownload(tv_item_ebook_down, beanHomeGrid, isDownload, myProgress);
                        }
                    });

                }
            }
        });
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (coursePath == ComFlag.CoursePath.Path_local) {

                    new TitleDialog(MainActivity.this, "确认删除该文件", new TitleDialog.OnclickListener() {
                        @Override
                        public void Onclick() {
                            BeanHomeGrid beanHomeGrid = localData.get(position);
                            if (checkFileIsExist(beanHomeGrid.resourceName, beanHomeGrid.resourceUri, true)) {
                                localData.remove(beanHomeGrid);
                                showToast("已删除" + beanHomeGrid.resourceName);
                            } else
                                showToast("删除失败");
                            mAdapter.setDatas(localData);
                        }
                    }).show();
                }
                return false;
            }

        });
    }

    //显示是否为已下载
    private void showIsDown(String isDownload, TextView tv_item_internet_down) {
        if (isDownload.equals("true")) {
            tv_item_internet_down.setSelected(true);
            tv_item_internet_down.setText("已下载");
        } else {
            tv_item_internet_down.setSelected(false);
            tv_item_internet_down.setText("下载");
        }
    }

    //设置下载点击事件
    private void setDownload(TextView tv_item_down, BeanHomeGrid mBeanHomeGrid, String isDownload, MyProgress myProgress) {

        if (isDownload.equals("true")) {
            showToast("已下载");
        } else {
            DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(url, mBeanHomeGrid.resourceName, this, new DownloadAsyncTask.OnProgressListener() {
                @Override
                public void onProgress(double progress) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myProgress.setProgress((int) progress);
                        }
                    });

                }

                @Override
                public void onFinish() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ApiUtil.doDefaultApi(api.downloadCourseResource(mBeanHomeGrid.resourceId), data -> {
                                tv_item_down.setSelected(true);
                                tv_item_down.setText("已下载");
                                myProgress.setVisibility(View.GONE);
                                showToast("下载成功");
                            });
                        }
                    });
                }

                @Override
                public void onStart() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myProgress.setVisibility(View.VISIBLE);
                            myProgress.setProgress(0);
                        }
                    });

                }

                @Override
                public void onError() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myProgress.setVisibility(View.GONE);
                        }
                    });
                }
            });
            downloadAsyncTask.execute();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Log.d("TTTT", "啊偶，被拒绝了，少年不哭，站起来撸");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //设置收藏/取消收藏
    private void setcellect(String resourceId, ImageView iv_item_ebook_cellect) {
        if (iv_item_ebook_cellect.isSelected()) {
            ApiUtil.doDefaultApi(api.cancelCollect(resourceId), new HttpSucess<String>() {
                @Override
                public void onSucess(String data) {
                    iv_item_ebook_cellect.setSelected(false);
                    showToast("已取消收藏");
                }
            });
        } else {
            ApiUtil.doDefaultApi(api.collectCourseResource(resourceId), new HttpSucess<String>() {
                @Override
                public void onSucess(String data) {
                    iv_item_ebook_cellect.setSelected(true);
                    showToast("已收藏");
                }
            });
        }
    }

    @Override
    protected void initControl() {
        super.initControl();

    }

    @Override
    protected void initData() {
        //修改
        api = ApiUtil.createDefaultApi(UserService.class, token);
        //请求默认数据
        getSourceDate();
    }


    private void getSourceDate() {
        ApiUtil.doDefaultApi(api.selectCourseResource(schoolSubjectId, schoolEducationGradeId, resourceName, collectType, sourceType), new HttpSucess<List<BeanHomeGrid>>() {
            @Override
            public void onSucess(List<BeanHomeGrid> data) {
                if (!data.equals("")) {
                    //修改
                    if (coursePath == ComFlag.CoursePath.Path_local) {
                        localData.clear();
                        for (int i = 0; i < data.size(); i++) {
                            if (checkFileIsExist(data.get(i).resourceName, data.get(i).resourceUri, false))
                                localData.add(data.get(i));
                        }
                        mAdapter.setDatas(localData);
                    } else
                        mAdapter.setDatas(data);
                }
            }
        });


    }

    private boolean checkFileIsExist(String resourceName, String mUrl, boolean isDelete) {
        String fileName = resourceName + mUrl.substring(mUrl.lastIndexOf("."), mUrl.length());
        return FileUtils.getFilesbolen(fileName, isDelete, this);
    }

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter("课程资源");
        setShowLeftHead(false);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(true);//logo
        setShowRefresh(false);//刷新
        setLogoClick(v -> {
            finish();
        });
    }


    @OnClick({R.id.rt_internet, R.id.rt_e_book, R.id.rb_all, R.id.rb_collect, R.id.rb_local, R.id.tv_subject_select, R.id.tv_class_select, R.id.iv_serch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rt_internet://网络
                isInternet = true;
                layoutManager.setSpanCount(4);
                mAdapter.notifyDataSetChanged();
                internetView.setVisibility(View.VISIBLE);
                ebookView.setVisibility(View.GONE);
                sourceType = "0";
                getSourceDate();
                break;
            case R.id.rt_e_book://电子书
                isInternet = false;
                layoutManager.setSpanCount(6);
                mAdapter.notifyDataSetChanged();
                internetView.setVisibility(View.GONE);
                ebookView.setVisibility(View.VISIBLE);
                sourceType = "1";
                getSourceDate();
                break;
            case R.id.rb_all://全部
                coursePath = ComFlag.CoursePath.Path_ALL;
                collectType = "0";
                resourceName = "";
                // etSearch.setText(resourceName);
                getSourceDate();
                break;
            case R.id.rb_collect://收藏
                coursePath = ComFlag.CoursePath.Path_collect;
                collectType = "1";
                getSourceDate();
                break;
            case R.id.rb_local://本地
                coursePath = ComFlag.CoursePath.Path_local;
                collectType = "0";
                resourceName = "";
                getSourceDate();
                break;
            case R.id.tv_subject_select:
                //科目
                ApiUtil.doDefaultApi(api.selectAllSubjects(null), new HttpSucess<List<PopSubjectBean>>() {
                    @Override
                    public void onSucess(List<PopSubjectBean> data) {
                        if (!data.equals("") && data.size() > 0) {
                            PopupSubject<PopSubjectBean> PopupSubject = new PopupSubject<PopSubjectBean>(App.mContext, data, position -> {
                                schoolSubjectId = data.get(position).schoolSubjectId;
                                tvSubjectSelect.setText(data.get(position).schoolSubjectName);
                                getSourceDate();
                            });
                            PopupWindowCompat.showAsDropDown(PopupSubject, tvSubjectSelect, 0, DensityUtil.dip2px(0), Gravity.BOTTOM);
                        }
                    }
                });
                break;
            case R.id.tv_class_select:
                //班级
                ApiUtil.doDefaultApi(api.selectAllSchoolGrade(null), new HttpSucess<List<PopClassBean>>() {
                    @Override
                    public void onSucess(List<PopClassBean> data) {
                        if (!data.equals("") && data.size() > 0) {
                            PopupClass<PopClassBean> popupClass = new PopupClass<PopClassBean>(App.mContext, data, position -> {
                                schoolEducationGradeId = data.get(position).schoolEducationGradeId;
                                tvClassSelect.setText(data.get(position).schoolEducationGradeName);
                                getSourceDate();
                            });
                            PopupWindowCompat.showAsDropDown(popupClass, tvClassSelect, 0, DensityUtil.dip2px(0), Gravity.BOTTOM);
                        }
                    }
                });

                break;
            case R.id.iv_serch:
                resourceName = etSearch.getText().toString().trim();
                getSourceDate();
                break;
        }
    }

}

