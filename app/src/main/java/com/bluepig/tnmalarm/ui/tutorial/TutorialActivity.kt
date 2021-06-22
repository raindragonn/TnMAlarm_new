package com.bluepig.tnmalarm.ui.tutorial

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivityTutorialBinding
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.ui.main.MainActivity
import com.bluepig.tnmalarm.utils.MyPreferences

class TutorialActivity : BaseActivity<ActivityTutorialBinding>(R.layout.activity_tutorial),
    ViewPager.OnPageChangeListener {
    private val list = listOf(
        R.drawable.tutorial_1,
        R.drawable.tutorial_2,
        R.drawable.tutorial_3
    )

    override fun onCreate() {
        binding.vpTutorial.apply {
            clipToPadding = false
            adapter = TutorialPagerAdapter().apply {
                setList(list)
            }
            addOnPageChangeListener(this@TutorialActivity)
        }

        binding.btnStart.setOnClickListener {
            MyPreferences.writeShowTutorial(this,true)
            openAct(MainActivity::class.java)
            mfinish()
        }
    }

    override fun onPageSelected(position: Int) {
        if (position == list.size - 1)
            binding.btnStart.visibility = View.VISIBLE
        else
            binding.btnStart.visibility = View.INVISIBLE
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

    override fun onNetworkChanged(connect: Boolean) {}

}