package com.ldlywt.video.osc.ui.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.ldlywt.video.osc.R;
import com.ldlywt.video.osc.bean.TmdbVodInfo;
import com.ldlywt.video.osc.databinding.DialogTmdbVodInfoBinding;
import com.lxj.xpopup.core.CenterPopupView;

import org.jetbrains.annotations.NotNull;

public class TmdbVodInfoDialog extends CenterPopupView {

    private final TmdbVodInfo.ResultsDTO mVod;

    public TmdbVodInfoDialog(@NonNull @NotNull Context context, TmdbVodInfo.ResultsDTO vod) {
        super(context);
        mVod = vod;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_tmdb_vod_info;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        DialogTmdbVodInfoBinding binding = DialogTmdbVodInfoBinding.bind(getPopupImplView());
        binding.tvTitle.setText(mVod.getTitle());
        binding.tvRating.setText("评分: "+mVod.getVote_average());
        binding.tvDes.setText(mVod.getOverview());
    }
}