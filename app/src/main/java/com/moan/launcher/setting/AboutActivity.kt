package com.moan.launcher.setting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.*
import com.moan.launcher.R


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        back.setOnClickListener { finish() }

        versionName.text = "版本：${getAppVersionName(this)}"

        var count = 7
        versionName.setOnClickListener {
            val developerEnabled = Settings.Global.getInt(contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED) == 1
            count--
            if (count <= 0) {
                count = 7
                if (developerEnabled) {
                    //开发者选项
                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                } else {
                    //关于本机
                    startActivity(Intent(Settings.ACTION_DEVICE_INFO_SETTINGS))
                }
            } else if (count < 5) {
                if (developerEnabled) {
                    Toast.makeText(this, "再执行 %s 步操作即可进入开发者选项".replace("%s", count.toString()), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "再执行 %s 步操作即可开启关于本机".replace("%s", count.toString()), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    /**
     * 获取当前app version name
     */
    fun getAppVersionName(context: Context): String? {
        var appVersionName = ""
        try {
            val packageInfo: PackageInfo = context.getApplicationContext()
                .getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
            appVersionName = packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("", e.message)
        }
        return appVersionName
    }
}
