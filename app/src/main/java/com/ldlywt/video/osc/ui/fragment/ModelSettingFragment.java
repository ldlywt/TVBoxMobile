package com.ldlywt.video.osc.ui.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.ldlywt.video.osc.R;
import com.ldlywt.video.osc.api.ApiConfig;
import com.ldlywt.video.osc.base.BaseLazyFragment;
import com.ldlywt.video.osc.bean.IJKCode;

import com.ldlywt.video.osc.ui.activity.SettingActivity;
import com.ldlywt.video.osc.ui.adapter.SelectDialogAdapter;
import com.ldlywt.video.osc.ui.dialog.BackupDialog;
import com.ldlywt.video.osc.ui.dialog.LiveApiDialog;
import com.ldlywt.video.osc.ui.dialog.SearchRemoteTvDialog;
import com.ldlywt.video.osc.ui.dialog.SelectDialog;
import com.ldlywt.video.osc.ui.dialog.XWalkInitDialog;
import com.ldlywt.video.osc.util.FastClickCheckUtil;
import com.ldlywt.video.osc.util.FileUtils;
import com.ldlywt.video.osc.util.HawkConfig;
import com.ldlywt.video.osc.util.HistoryHelper;
import com.ldlywt.video.osc.util.OkGoHelper;
import com.ldlywt.video.osc.util.PlayerHelper;
import com.ldlywt.video.osc.util.Utils;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hjq.bar.TitleBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lxj.xpopup.XPopup;
import com.orhanobut.hawk.Hawk;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class ModelSettingFragment extends BaseLazyFragment {
    private TextView tvDebugOpen;
    private TextView tvMediaCodec;
    private TextView tvParseWebView;
    private TextView tvPlay;
    private TextView tvRender;
    private TextView tvScale;
    private TextView tvDns;
    private TextView tvHistoryNum;
    TextView tvLongPressSpeed;

    public static ModelSettingFragment newInstance() {
        return new ModelSettingFragment().setArguments();
    }

    public ModelSettingFragment setArguments() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_model;
    }

    @Override
    protected void init() {
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.getLeftView().setOnClickListener(view -> {
            SettingActivity activity = (SettingActivity) mActivity;
            activity.onBackPressed();
        });
        tvDebugOpen = findViewById(R.id.tvDebugOpen);
        tvParseWebView = findViewById(R.id.tvParseWebView);
        tvMediaCodec = findViewById(R.id.tvMediaCodec);
        tvPlay = findViewById(R.id.tvPlay);
        tvRender = findViewById(R.id.tvRenderType);
        tvScale = findViewById(R.id.tvScaleType);

        tvDns = findViewById(R.id.tvDns);
        tvHistoryNum = findViewById(R.id.tvHistoryNum);

        tvMediaCodec.setText(Hawk.get(HawkConfig.IJK_CODEC, ""));
        tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
        tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");

        tvDns.setText(OkGoHelper.dnsHttpsList.get(Hawk.get(HawkConfig.DOH_URL, 0)));
        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(Hawk.get(HawkConfig.HISTORY_NUM, 0)));
        tvScale.setText(PlayerHelper.getScaleName(Hawk.get(HawkConfig.PLAY_SCALE, 0)));
        tvPlay.setText(PlayerHelper.getPlayerName(Hawk.get(HawkConfig.PLAY_TYPE, 0)));
        tvRender.setText(PlayerHelper.getRenderName(Hawk.get(HawkConfig.PLAY_RENDER, 0)));

        //隐私浏览
        SwitchMaterial switchPrivate = findViewById(R.id.switchPrivateBrowsing);
        switchPrivate.setChecked(Hawk.get(HawkConfig.PRIVATE_BROWSING, false));
        findViewById(R.id.llPrivateBrowsing).setOnClickListener(view -> {
            boolean newConfig = !Hawk.get(HawkConfig.PRIVATE_BROWSING, false);
            switchPrivate.setChecked(newConfig);
            Hawk.put(HawkConfig.PRIVATE_BROWSING, newConfig);
        });

        findViewById(R.id.llLiveApi).setOnClickListener(view -> {
            new XPopup.Builder(mContext)
                    .autoFocusEditText(false)
                    .asCustom(new LiveApiDialog(mActivity))
                    .show();
        });

        //后台播放
        View backgroundPlay = findViewById(R.id.llBackgroundPlay);
        TextView tvBgPlayType = findViewById(R.id.tvBackgroundPlayType);
        Integer defaultBgPlayTypePos = Hawk.get(HawkConfig.BACKGROUND_PLAY_TYPE, 0);

        ArrayList<String> bgPlayTypes = new ArrayList<>();
        bgPlayTypes.add("关闭");
        bgPlayTypes.add("开启");
        bgPlayTypes.add("画中画");
        tvBgPlayType.setText(bgPlayTypes.get(defaultBgPlayTypePos));
        backgroundPlay.setOnClickListener(view -> {
            FastClickCheckUtil.check(view);
            SelectDialog<String> dialog = new SelectDialog<>(mActivity);
            dialog.setTip("请选择");
            dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                @Override
                public void click(String value, int pos) {
                    tvBgPlayType.setText(value);
                    Hawk.put(HawkConfig.BACKGROUND_PLAY_TYPE, pos);
                }

                @Override
                public String getDisplay(String val) {
                    return val;
                }
            }, new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                    return oldItem.equals(newItem);
                }
            }, bgPlayTypes, defaultBgPlayTypePos);
            dialog.show();
        });

        findViewById(R.id.llDebug).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                Hawk.put(HawkConfig.DEBUG_OPEN, !Hawk.get(HawkConfig.DEBUG_OPEN, false));
                tvDebugOpen.setText(Hawk.get(HawkConfig.DEBUG_OPEN, false) ? "已打开" : "已关闭");
            }
        });

        tvLongPressSpeed = findViewById(R.id.tvSpeed);
        tvLongPressSpeed.setText(String.valueOf(Hawk.get(HawkConfig.VIDEO_SPEED, 2.0f)));
        findViewById(R.id.llPressSpeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> types = new ArrayList<>();
                types.add("2.0");
                types.add("3.0");
                types.add("4.0");
                types.add("5.0");
                types.add("6.0");
                types.add("8.0");
                types.add("10.0");
                int defaultPos = types.indexOf(String.valueOf(Hawk.get(HawkConfig.VIDEO_SPEED, 2.0f)));
                SelectDialog<String> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override
                    public void click(String value, int pos) {
                        Hawk.put(HawkConfig.VIDEO_SPEED, Float.parseFloat(value));
                        tvLongPressSpeed.setText(value);
                    }

                    @Override
                    public String getDisplay(String val) {
                        return val;
                    }
                }, SelectDialogAdapter.stringDiff, types, defaultPos);
                dialog.show();
            }
        });

        findViewById(R.id.llParseWebVew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                boolean useSystem = !Hawk.get(HawkConfig.PARSE_WEBVIEW, true);
                Hawk.put(HawkConfig.PARSE_WEBVIEW, useSystem);
                tvParseWebView.setText(Hawk.get(HawkConfig.PARSE_WEBVIEW, true) ? "系统自带" : "XWalkView");
                if (!useSystem) {
                    Toast.makeText(mContext, "注意: XWalkView只适用于部分低Android版本，Android5.0以上推荐使用系统自带", Toast.LENGTH_LONG).show();
                    XWalkInitDialog dialog = new XWalkInitDialog(mContext);
                    dialog.setOnListener(new XWalkInitDialog.OnListener() {
                        @Override
                        public void onchange() {
                        }
                    });
                    dialog.show();
                }
            }
        });
        findViewById(R.id.llBackup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                if (XXPermissions.isGranted(mActivity, Permission.MANAGE_EXTERNAL_STORAGE)) {
                    BackupDialog dialog = new BackupDialog(mActivity);
                    dialog.show();
                } else {
                    XXPermissions.with(mActivity)
                            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(List<String> permissions, boolean all) {
                                    if (all) {
                                        BackupDialog dialog = new BackupDialog(mActivity);
                                        dialog.show();
                                    }
                                }

                                @Override
                                public void onDenied(List<String> permissions, boolean never) {
                                    if (never) {
                                        Toast.makeText(getContext(), "获取存储权限失败,请在系统设置中开启", Toast.LENGTH_SHORT).show();
                                        XXPermissions.startPermissionActivity((Activity) getContext(), permissions);
                                    } else {
                                        Toast.makeText(getContext(), "获取存储权限失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

        findViewById(R.id.llDns).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int dohUrl = Hawk.get(HawkConfig.DOH_URL, 0);

                SelectDialog<String> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择安全DNS");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<String>() {
                    @Override
                    public void click(String value, int pos) {
                        tvDns.setText(OkGoHelper.dnsHttpsList.get(pos));
                        Hawk.put(HawkConfig.DOH_URL, pos);
                        String url = OkGoHelper.getDohUrl(pos);
                        OkGoHelper.dnsOverHttps.setUrl(url.isEmpty() ? null : HttpUrl.get(url));
                        IjkMediaPlayer.toggleDotPort(pos > 0);
                    }

                    @Override
                    public String getDisplay(String val) {
                        return val;
                    }
                }, new DiffUtil.ItemCallback<String>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull String oldItem, @NonNull @NotNull String newItem) {
                        return oldItem.equals(newItem);
                    }
                }, OkGoHelper.dnsHttpsList, dohUrl);
                dialog.show();
            }
        });


        findViewById(R.id.llMediaCodec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<IJKCode> ijkCodes = ApiConfig.get().getIjkCodes();
                if (ijkCodes == null || ijkCodes.size() == 0)
                    return;
                FastClickCheckUtil.check(v);

                int defaultPos = 0;
                String ijkSel = Hawk.get(HawkConfig.IJK_CODEC, "");
                for (int j = 0; j < ijkCodes.size(); j++) {
                    if (ijkSel.equals(ijkCodes.get(j).getName())) {
                        defaultPos = j;
                        break;
                    }
                }

                SelectDialog<IJKCode> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择IJK解码");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<IJKCode>() {
                    @Override
                    public void click(IJKCode value, int pos) {
                        value.selected(true);
                        tvMediaCodec.setText(value.getName());
                    }

                    @Override
                    public String getDisplay(IJKCode val) {
                        return val.getName();
                    }
                }, new DiffUtil.ItemCallback<IJKCode>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem == newItem;
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull IJKCode oldItem, @NonNull @NotNull IJKCode newItem) {
                        return oldItem.getName().equals(newItem.getName());
                    }
                }, ijkCodes, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llScale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_SCALE, 0);
                ArrayList<Integer> players = new ArrayList<>();
                players.add(0);
                players.add(1);
                players.add(2);
                players.add(3);
                players.add(4);
                players.add(5);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择画面缩放");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_SCALE, value);
                        tvScale.setText(PlayerHelper.getScaleName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getScaleName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, players, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int playerType = Hawk.get(HawkConfig.PLAY_TYPE, 0);
                int defaultPos = 0;
                ArrayList<Integer> players = PlayerHelper.getExistPlayerTypes();
                ArrayList<Integer> renders = new ArrayList<>();
                for (int p = 0; p < players.size(); p++) {
                    renders.add(p);
                    if (players.get(p) == playerType) {
                        defaultPos = p;
                    }
                }
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认播放器");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Integer thisPlayerType = players.get(pos);
                        Hawk.put(HawkConfig.PLAY_TYPE, thisPlayerType);
                        tvPlay.setText(PlayerHelper.getPlayerName(thisPlayerType));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        Integer playerType = players.get(val);
                        return PlayerHelper.getPlayerName(playerType);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        findViewById(R.id.llRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.PLAY_RENDER, 0);
                ArrayList<Integer> renders = new ArrayList<>();
                renders.add(0);
                renders.add(1);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("请选择默认渲染方式");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.PLAY_RENDER, value);
                        tvRender.setText(PlayerHelper.getRenderName(value));
                        PlayerHelper.init();
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return PlayerHelper.getRenderName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, renders, defaultPos);
                dialog.show();
            }
        });
        SettingActivity.callback = new SettingActivity.DevModeCallback() {
            @Override
            public void onChange() {
                findViewById(R.id.llDebug).setVisibility(View.VISIBLE);
            }
        };

        findViewById(R.id.llHistoryNum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                int defaultPos = Hawk.get(HawkConfig.HISTORY_NUM, 0);
                ArrayList<Integer> types = new ArrayList<>();
                types.add(0);
                types.add(1);
                types.add(2);
                SelectDialog<Integer> dialog = new SelectDialog<>(mActivity);
                dialog.setTip("保留历史记录数量");
                dialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<Integer>() {
                    @Override
                    public void click(Integer value, int pos) {
                        Hawk.put(HawkConfig.HISTORY_NUM, value);
                        tvHistoryNum.setText(HistoryHelper.getHistoryNumName(value));
                    }

                    @Override
                    public String getDisplay(Integer val) {
                        return HistoryHelper.getHistoryNumName(val);
                    }
                }, new DiffUtil.ItemCallback<Integer>() {
                    @Override
                    public boolean areItemsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }

                    @Override
                    public boolean areContentsTheSame(@NonNull @NotNull Integer oldItem, @NonNull @NotNull Integer newItem) {
                        return oldItem.intValue() == newItem.intValue();
                    }
                }, types, defaultPos);
                dialog.show();
            }
        });

        findViewById(R.id.llClearCache).setOnClickListener((view -> {
            new XPopup.Builder(mActivity)
                    .isDarkTheme(Utils.isDarkTheme())
                    .asConfirm("提示", "确定清空吗？", () -> {
                        onClickClearCache(view);
                    }).show();
        }));

        SwitchMaterial switchVideoPurify = findViewById(R.id.switchVideoPurify);
        switchVideoPurify.setChecked(Hawk.get(HawkConfig.VIDEO_PURIFY, true));
        // toggle purify video -------------------------------------
        findViewById(R.id.llVideoPurify).setOnClickListener(v -> {
            FastClickCheckUtil.check(v);
            boolean newConfig = !Hawk.get(HawkConfig.VIDEO_PURIFY, true);
            switchVideoPurify.setChecked(newConfig);
            Hawk.put(HawkConfig.VIDEO_PURIFY, newConfig);
        });

        SwitchMaterial switchIjkCachePlay = findViewById(R.id.switchIjkCachePlay);
        switchIjkCachePlay.setChecked(Hawk.get(HawkConfig.IJK_CACHE_PLAY, false));
        findViewById(R.id.llIjkCachePlay).setOnClickListener((v -> {
            FastClickCheckUtil.check(v);
            boolean newConfig = !Hawk.get(HawkConfig.IJK_CACHE_PLAY, false);
            switchIjkCachePlay.setChecked(newConfig);
            Hawk.put(HawkConfig.IJK_CACHE_PLAY, newConfig);
        }));
    }

    private void onClickClearCache(View v) {
        FastClickCheckUtil.check(v);

        String cachePath = FileUtils.getCachePath();
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) return;
        new Thread(() -> {
            try {
                FileUtils.cleanDirectory(cacheDir);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Toast.makeText(getContext(), "缓存已清空", Toast.LENGTH_LONG).show();
    }


    public static SearchRemoteTvDialog loadingSearchRemoteTvDialog;
    public static List<String> remoteTvHostList;
    public static boolean foundRemoteTv;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SettingActivity.callback = null;
    }

    String getHomeRecName(int type) {
        switch (type) {
            case 0:
                return "豆瓣热播";
            case 1:
                return "站点推荐";
            default:
                return "关闭";
        }
    }

}