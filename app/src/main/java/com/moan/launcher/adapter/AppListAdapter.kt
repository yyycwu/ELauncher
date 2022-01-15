package com.moan.launcher.adapter

import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moan.launcher.R
import com.moan.launcher.adapterpro.adapter.BaseRecyclerViewAdapter
import com.moan.launcher.adapterpro.helper.ViewHolderHelper
import com.moan.launcher.bean.AppInfo
import com.moan.launcher.utils.ActionKey
import com.moan.launcher.utils.FileUtils
import com.moan.launcher.utils.SpUtil
import com.moan.launcher.utils.dip2px

/**
 * 列表适配器
 */
class AppListAdapter(recyclerView: RecyclerView) :
    BaseRecyclerViewAdapter<AppInfo>(recyclerView, R.layout.item_app_list) {
    override fun bindData(helper: ViewHolderHelper, position: Int, model: AppInfo) {
        //是否显示名字
        val nameVisibility = SpUtil.getBoolean(mContext, ActionKey.APP_NAME_VISIBILITY, true)

        val name = helper.getTextView(R.id.name)

        name.text = model.name

        if (!nameVisibility) {
            name.visibility = View.GONE
        } else {
            name.visibility = View.VISIBLE
        }


        val lm = mRecyclerView.layoutManager as GridLayoutManager
        val img = helper.getImageView(R.id.icon)
        img.setPadding(2, 2, 2, 2)
        val lp = img.layoutParams as LinearLayout.LayoutParams

        val pMargin = 24f - (lm.spanCount - 5) * 2.5f
        val hMargin = 24f - (lm.spanCount - 5) * 3f

        //设置图片边距
        lp.setMargins(
            dip2px(
                mContext, if (hMargin > 0) {
                    hMargin
                } else 0f
            ),
            dip2px(mContext, if (pMargin > 0) pMargin else 0f),
            dip2px(mContext, if (hMargin > 0) hMargin else 0f),
            dip2px(mContext, if (nameVisibility) 0f else if (pMargin > 0) pMargin else 0f)
        );


        if (model.isApp) {
            img.setImageDrawable(model.icon)
        } else {

            val showIcon = SpUtil.getBoolean(mContext, ActionKey.APP_ICON_SHOW, false)

            val userIcon = if (showIcon) FileUtils.getIcon(model.packageName) else null
            if (userIcon == null) {
                when (model.packageName) {
                    "setting" -> {
                        img.setImageResource(R.mipmap.ic_setting)
                    }
                    "clear" -> {
                        img.setImageResource(R.mipmap.ic_clear)
                    }
                    "wifi" -> {
                        img.setImageResource(R.mipmap.ic_wifi)
                    }
                    else -> img.setImageResource(R.mipmap.ic_launcher)
                }
            } else {
                img.setImageBitmap(userIcon)
            }


        }

    }
}