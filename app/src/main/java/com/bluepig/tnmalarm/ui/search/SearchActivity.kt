package com.bluepig.tnmalarm.ui.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivitySearchBinding
import com.bluepig.tnmalarm.lib.TnMAdsImpl
import com.bluepig.tnmalarm.model.SongResponse
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.utils.MyEncoder
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.mToast
import com.bluepig.tnmalarm.utils.ext.hideKeyboard

class SearchActivity : BaseActivity<ActivitySearchBinding>(R.layout.activity_search) {
    private val viewModel: SearchViewModel by viewModels()

    private val adapter by lazy { SearchAdapter(itemCLickListener).apply { setHasStableIds(true) } }
    private val itemCLickListener: (SongResponse) -> Unit = { item ->
        viewModel.onSongDetail(item.url, item.title)
    }
    private val tnmAds: TnMAdsImpl by lazy { TnMAdsImpl(applicationContext) }

    override fun onCreate() {
        viewModel.setNetworkConnected(isNetworkConnected())

        binding.apply {
            vm = viewModel
            act = this@SearchActivity
            rv.adapter = this@SearchActivity.adapter
            tnmAds.bannerLoad(llRoot)
        }

        viewModel.apply {

            response.observe(this@SearchActivity, {
                if (it.isEmpty()) mToast(R.string.search_empty)
                viewModel.hideLoading()
                hideKeyboard()
            })

            songDetail.observe(this@SearchActivity, {
                SongDialog().apply {
                    arguments = Bundle().apply {
                        putString(SongDialog.EXTRA_URL, it)
                        putString(SongDialog.EXTRA_SONG_URL, viewModel.songUrl.value)
                        putString(SongDialog.EXTRA_TITLE, viewModel.songTitle.value)
                    }
                }.show(supportFragmentManager, "")
            })

            eventNetworkError.observe(this@SearchActivity,{
                mToast(R.string.toast_network_check)
            })

            eventBack.observe(this@SearchActivity,{
                mfinish()
            })
        }

    }

    override fun onBackPressed() {
        mfinish()
    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }
}