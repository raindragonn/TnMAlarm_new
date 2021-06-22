package com.bluepig.tnmalarm.utils

import android.app.Activity
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.model.SongResponse
import com.bluepig.tnmalarm.ui.main.MainAlarmAdapter
import com.bluepig.tnmalarm.ui.search.SearchAdapter
import com.bluepig.tnmalarm.ui.search.SearchViewModel
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@BindingAdapter("fabImageSet")
fun FloatingActionButton.imageSet(visible: Boolean) {
    if (visible)
        this.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_remove))
    else
        this.setImageDrawable(ContextCompat.getDrawable(this.context, R.drawable.ic_add))
}

@BindingAdapter("childVisible")
fun FloatingActionButton.childVisible(visible: Boolean) {
    val openAni = AnimationUtils.loadAnimation(this.context, R.anim.fab_open)
    val closeAni = AnimationUtils.loadAnimation(this.context, R.anim.fab_close)

    if (visible) {
        this.startAnimation(openAni)
        this.isClickable = true
        this.visibility = View.VISIBLE
    } else {
        if (this.visibility != View.INVISIBLE) {
            this.startAnimation(closeAni)
            this.isClickable = false
        }
    }
}

@BindingAdapter("setUrl")
fun ImageView.setUrl(url: String) {
    Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher).into(this)
}

@BindingAdapter("setSearchItems")
fun RecyclerView.setSearchItems(items: List<SongResponse>?) {
    with(adapter as SearchAdapter) {
        clear()
        items?.let {
            addItem(it)
        }
    }
}

@BindingAdapter("setAlarmItems")
fun RecyclerView.setAlarmItems(items: List<Alarm>?) {
    with(adapter as MainAlarmAdapter) {
        clear()

        items?.let {
            addItem(it.sortedBy { alarm ->
                alarm.date
            })
        }
    }
}

@BindingAdapter("loadMore")
fun RecyclerView.loadMore(vm: SearchViewModel) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            vm.response.value?.let {
                if (!canScrollVertically(1)) {
                    vm.onLoadMore()
                }
            }
        }
    })
}

@BindingAdapter("keyListener", "activity")
fun EditText.setKeyListener(action: () -> Unit, activity: Activity) {
    val inputMethodManager =
        this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    setOnKeyListener { _, keyCode, event ->
        if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            action()
            if (inputMethodManager != null && activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            return@setOnKeyListener true
        } else {
            return@setOnKeyListener false
        }
    }
}

@BindingAdapter("weakClick")
fun ConstraintLayout.weakClick(click: Boolean) {
    if (click)
        this.setBackgroundResource(R.drawable.bg_weak_select)
    else
        this.setBackgroundResource(R.drawable.bg_weak_unselect)
}

