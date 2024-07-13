package com.ldlywt.video.osc.ui.fragment;

import com.ldlywt.video.osc.base.BaseVbFragment;
import com.ldlywt.video.osc.databinding.FragmentMyBinding;
import com.ldlywt.video.osc.ui.activity.CollectActivity;
import com.ldlywt.video.osc.ui.activity.HistoryActivity;
import com.ldlywt.video.osc.ui.activity.LiveActivity;
import com.ldlywt.video.osc.ui.activity.SettingActivity;
import com.ldlywt.video.osc.ui.activity.SubscriptionActivity;
import com.ldlywt.video.osc.ui.dialog.AboutDialog;
import com.lxj.xpopup.XPopup;

/**
 * @author pj567
 * @date :2021/3/9
 * @description:
 */
public class MyFragment extends BaseVbFragment<FragmentMyBinding> {


    @Override
    protected void init() {

        mBinding.tvLive.setOnClickListener(v -> jumpActivity(LiveActivity.class));

        mBinding.tvSetting.setOnClickListener(v -> jumpActivity(SettingActivity.class));

        mBinding.tvHistory.setOnClickListener(v -> jumpActivity(HistoryActivity.class));

        mBinding.tvFavorite.setOnClickListener(v -> jumpActivity(CollectActivity.class));

        mBinding.llSubscription.setOnClickListener(v -> jumpActivity(SubscriptionActivity.class));

        mBinding.llAbout.setOnClickListener(v -> {
            new XPopup.Builder(mActivity)
                    .asCustom(new AboutDialog(mActivity))
                    .show();
        });
    }

}