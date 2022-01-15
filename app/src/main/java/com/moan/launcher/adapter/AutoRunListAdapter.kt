package com.moan.launcher.adapter

import androidx.recyclerview.widget.RecyclerView
import com.moan.launcher.R
import com.moan.launcher.adapterpro.adapter.BaseRecyclerViewAdapter
import com.moan.launcher.adapterpro.helper.ViewHolderHelper
import com.moan.launcher.bean.AppInfo

/**
 * 列表适配器
 */
class AutoRunListAdapter(recyclerView: RecyclerView) :
    BaseRecyclerViewAdapter<AppInfo>(recyclerView, R.layout.item_auto_run_list) {


    override fun bindData(helper: ViewHolderHelper, position: Int, model: AppInfo) {
        helper.setText(R.id.name, model.name)
        helper.setText(R.id.version, model.version)
        helper.setImageDrawable(R.id.icon, model.icon)
    }
}