package com.moan.launcher.adapter

import androidx.recyclerview.widget.RecyclerView
import com.moan.launcher.R
import com.moan.launcher.adapterpro.adapter.BaseRecyclerViewAdapter
import com.moan.launcher.adapterpro.helper.ViewHolderHelper
import com.moan.launcher.bean.AppInfo

/**
 * 列表适配器
 */
class AppManagerAdapter(recyclerView: RecyclerView) :
    BaseRecyclerViewAdapter<AppInfo>(recyclerView, R.layout.item_app_manager) {

    override fun bindItemChildEvent(helper: ViewHolderHelper, viewType: Int) {
        helper.setItemChildClickListener(R.id.hide)
    }

    override fun bindData(helper: ViewHolderHelper, position: Int, model: AppInfo) {
        helper.setText(R.id.name, model.name + "  " + model.version)
        helper.setText(R.id.version, model.packageName)
        if (model.hide) {
            helper.setImageResource(R.id.hide, R.mipmap.ic_invisible)
        } else {
            helper.setImageResource(R.id.hide, R.mipmap.ic_visible)
        }
        helper.setImageDrawable(R.id.icon, model.icon)
    }
}