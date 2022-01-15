package com.moan.launcher.setting

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mohammedalaa.seekbar.OnRangeSeekBarChangeListener
import com.mohammedalaa.seekbar.RangeSeekBarView
import kotlinx.android.synthetic.main.activity_app_list_setting.*
import kotlinx.android.synthetic.main.activity_setting.back
import com.moan.launcher.R
import com.moan.launcher.utils.ActionKey
import com.moan.launcher.utils.SpUtil

class AppListSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list_setting)
        back.setOnClickListener { finish() }

        //自定义图标
        userIcon.isChecked = SpUtil.getBoolean(this, ActionKey.APP_ICON_SHOW, false)
        userIcon.setOnCheckedChangeListener { _, b ->
            SpUtil.setValue(this, ActionKey.APP_ICON_SHOW, b)
            val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
            i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
            sendBroadcast(i)
        }
        //应用名称
        appName.isChecked = SpUtil.getBoolean(this, ActionKey.APP_NAME_VISIBILITY, true)
        appName.setOnCheckedChangeListener { _, b ->
            SpUtil.setValue(this, ActionKey.APP_NAME_VISIBILITY, b)
            val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
            i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
            sendBroadcast(i)
        }
        //列表排列方式
        arrange.isChecked = SpUtil.getBoolean(this, ActionKey.APP_LIST_ARRANGE, true)
        arrange.setOnCheckedChangeListener { compoundButton, b ->
            SpUtil.setValue(this, ActionKey.APP_LIST_ARRANGE, b)
            val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
            i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
            sendBroadcast(i)
        }


        //列数
        column.setCurrentValue(SpUtil.getInt(this, ActionKey.APP_LIST_COLUMN, 5))
        column.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: RangeSeekBarView?,
                progress: Int,
                fromUser: Boolean
            ) {

            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?) {
                SpUtil.setValue(
                    this@AppListSettingActivity,
                    ActionKey.APP_LIST_COLUMN,
                    seekBar?.getCurrentValue() ?: 5
                )
                val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
                i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
                sendBroadcast(i)
            }
        })


        //蒙版透明度
        listAlpha.setCurrentValue(SpUtil.getInt(this, ActionKey.APP_LIST_ALPHA, 200))
        listAlpha.setOnRangeSeekBarViewChangeListener(object : OnRangeSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: RangeSeekBarView?,
                progress: Int,
                fromUser: Boolean
            ) {

            }

            override fun onStartTrackingTouch(seekBar: RangeSeekBarView?) {

            }

            override fun onStopTrackingTouch(seekBar: RangeSeekBarView?) {
                SpUtil.setValue(
                    this@AppListSettingActivity,
                    ActionKey.APP_LIST_ALPHA,
                    seekBar?.getCurrentValue() ?: 5
                )
                val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
                i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
                sendBroadcast(i)
            }
        })


        //Wifi图标
        wifiShow.isChecked = SpUtil.getBoolean(this, ActionKey.APP_LIST_WIFI_SHOW, true)
        wifiShow.setOnCheckedChangeListener { compoundButton, b ->
            SpUtil.setValue(this, ActionKey.APP_LIST_WIFI_SHOW, b)
            val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
            i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
            sendBroadcast(i)
        }
        //清理图标
        clearShow.isChecked = SpUtil.getBoolean(this, ActionKey.APP_LIST_CLEAR_SHOW, true)
        clearShow.setOnCheckedChangeListener { compoundButton, b ->
            SpUtil.setValue(this, ActionKey.APP_LIST_CLEAR_SHOW, b)
            val i = Intent(ActionKey.ACTION_APP_LIST_CHANGE)
            i.data = Uri.parse("package:${ActionKey.ACTION_APP_LIST_CHANGE}")
            sendBroadcast(i)
        }

    }
}
