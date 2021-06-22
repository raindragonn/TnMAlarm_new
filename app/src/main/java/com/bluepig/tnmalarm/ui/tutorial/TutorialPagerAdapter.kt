package com.bluepig.tnmalarm.ui.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bluepig.tnmalarm.databinding.ItemTutorialBinding
import com.bumptech.glide.Glide

class TutorialPagerAdapter : PagerAdapter() {

    lateinit var picList : List<Int>

    fun setList(nList : List<Int>){
        picList = nList
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val inflater = LayoutInflater.from(container.context)
        val view = ItemTutorialBinding.inflate(inflater,container,false)
        Glide.with(container.context).load(picList[position]).into(view.ivTutorial)
        container.addView(view.root)
        return view.root
    }

    override fun getCount(): Int = picList.size
    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}