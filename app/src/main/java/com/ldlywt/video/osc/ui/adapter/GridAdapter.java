package com.ldlywt.video.osc.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ldlywt.video.osc.R;
import com.ldlywt.video.osc.bean.Movie;
import com.ldlywt.video.osc.util.DefaultConfig;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/21
 * @description:
 */
public class GridAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {

    public GridAdapter() {
        super(R.layout.item_grid, new ArrayList<>());

    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {

        TextView tvYear = helper.getView(R.id.tvYear);
        if (item.year <= 0) {
            tvYear.setVisibility(View.GONE);
        } else {
            tvYear.setText(String.valueOf(item.year));
            tvYear.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(item.note)) {
            helper.setVisible(R.id.tvNote, false);
            helper.setVisible(R.id.sl_note, false);
        } else {
            if (item.note.contains("评分：")) {
                String value = item.note.split("评分：")[1];
                if (!value.equals("0")) {
                    helper.setVisible(R.id.tvNote, true);
                    helper.setText(R.id.tvNote, value);
                    helper.setVisible(R.id.sl_note, true);
                } else {
                    helper.setVisible(R.id.sl_note, false);
                }
            } else {
                helper.setVisible(R.id.sl_note, false);
            }
        }
        helper.setText(R.id.tvName, item.name);
        ImageView ivThumb = helper.getView(R.id.ivThumb);
        //由于部分电视机使用glide报错
        if (!TextUtils.isEmpty(item.pic)) {
            item.pic=item.pic.trim();
            Picasso.get()
                    .load(DefaultConfig.checkReplaceProxy(item.pic))
                    .placeholder(R.drawable.img_loading_placeholder)
                    .error(R.drawable.img_loading_placeholder)
                    .into(ivThumb);
        } else {
            ivThumb.setImageResource(R.drawable.img_loading_placeholder);
        }
    }
}