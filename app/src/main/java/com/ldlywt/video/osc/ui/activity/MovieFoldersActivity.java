package com.ldlywt.video.osc.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ldlywt.video.osc.base.BaseVbActivity;
import com.ldlywt.video.osc.bean.VideoFolder;
import com.ldlywt.video.osc.bean.VideoInfo;
import com.ldlywt.video.osc.databinding.ActivityMovieFoldersBinding;
import com.ldlywt.video.osc.ui.adapter.FolderAdapter;
import com.ldlywt.video.osc.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieFoldersActivity extends BaseVbActivity<ActivityMovieFoldersBinding> {

    private FolderAdapter mFolderAdapter;

    @Override
    protected void init() {

        mFolderAdapter = new FolderAdapter();
        mBinding.rv.setAdapter(mFolderAdapter);
        mFolderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VideoFolder videoFolder = (VideoFolder) adapter.getItem(position);
                if (videoFolder != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bucketDisplayName",videoFolder.getName());
                    jumpActivity(VideoListActivity.class,bundle);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupVideos();
    }

    /**
     * 按文件夹名字分组视频
     */
    private void groupVideos(){
        List<VideoInfo> videoList = Utils.getVideoList();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Map<String, List<VideoInfo>> videoMap = videoList.stream()
                    .collect(Collectors.groupingBy(VideoInfo::getBucketDisplayName));
            List<VideoFolder> videoFolders = new ArrayList<>();
            videoMap.forEach((key, value) -> {
                VideoFolder videoFolder = new VideoFolder(key,value);
                videoFolders.add(videoFolder);
            });
            mFolderAdapter.setNewData(videoFolders);
        }
    }
}