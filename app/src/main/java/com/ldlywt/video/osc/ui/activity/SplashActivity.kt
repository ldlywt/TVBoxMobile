package com.ldlywt.video.osc.ui.activity

import android.content.Intent
import com.ldlywt.video.osc.base.BaseVbActivity
import com.ldlywt.video.osc.databinding.ActivitySplashBinding

class SplashActivity : BaseVbActivity<ActivitySplashBinding>() {
    override fun init() {
        mBinding.root.postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        },400)
    }
}