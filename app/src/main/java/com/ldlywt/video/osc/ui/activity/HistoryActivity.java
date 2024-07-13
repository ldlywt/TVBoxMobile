package com.ldlywt.video.osc.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ldlywt.video.osc.base.BaseVbActivity;
import com.ldlywt.video.osc.bean.VodInfo;
import com.ldlywt.video.osc.cache.RoomDataManger;
import com.ldlywt.video.osc.databinding.ActivityHistoryBinding;
import com.ldlywt.video.osc.event.RefreshEvent;
import com.ldlywt.video.osc.ui.adapter.HistoryAdapter;
import com.ldlywt.video.osc.util.FastClickCheckUtil;
import com.ldlywt.video.osc.util.Utils;
import com.lxj.xpopup.XPopup;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2021/1/7
 * @description:
 */
public class HistoryActivity extends BaseVbActivity<ActivityHistoryBinding> {
    private HistoryAdapter historyAdapter;

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {

        mBinding.mGridView.setHasFixedSize(true);
        mBinding.mGridView.setLayoutManager(new V7GridLayoutManager(this.mContext, 3));
        historyAdapter = new HistoryAdapter();
        mBinding.mGridView.setAdapter(historyAdapter);
        historyAdapter.setOnItemLongClickListener((BaseQuickAdapter.OnItemLongClickListener) (adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            VodInfo vodInfo = historyAdapter.getData().get(position);
            if (vodInfo != null) {
                historyAdapter.remove(position);
                RoomDataManger.deleteVodRecord(vodInfo.sourceKey, vodInfo);
                if (historyAdapter.getData().isEmpty()){
                    mBinding.topTip.setVisibility(View.GONE);
                }
            } else {
                ToastUtils.showLong("未查询到该条记录,请重试或清空全部记录");
            }
            return true;
        });

        mBinding.titleBar.getRightView().setOnClickListener(view -> {
            new XPopup.Builder(this)
                    .isDarkTheme(Utils.isDarkTheme())
                    .asConfirm("提示", "确定清空?", () -> {
                        RoomDataManger.deleteVodRecordAll();
                        historyAdapter.setNewData(new ArrayList<>());
                        mBinding.topTip.setVisibility(View.GONE);
                    }).show();
        });

        historyAdapter.setOnItemClickListener((adapter, view, position) -> {
            FastClickCheckUtil.check(view);
            VodInfo vodInfo = historyAdapter.getData().get(position);
            if (vodInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", vodInfo.id);
                bundle.putString("sourceKey", vodInfo.sourceKey);
                jumpActivity(DetailActivity.class, bundle);
            } else {
                ToastUtils.showShort("记录失效,请重新点播");
            }
        });
    }

    private void initData() {
        List<VodInfo> allVodRecord = RoomDataManger.getAllVodRecord(100);
        List<VodInfo> vodInfoList = new ArrayList<>();
        for (VodInfo vodInfo : allVodRecord) {
            if (vodInfo.playNote != null && !vodInfo.playNote.isEmpty())
                vodInfo.note = vodInfo.playNote;
            vodInfoList.add(vodInfo);
        }
        historyAdapter.setNewData(vodInfoList);
        if (!vodInfoList.isEmpty()){
            mBinding.topTip.setVisibility(View.VISIBLE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_HISTORY_REFRESH) {
            initData();
        }
    }
}