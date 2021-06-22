package com.bluepig.tnmalarm.ui.alarm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.DialogDebugBinding
import com.bluepig.tnmalarm.ui.base.BaseDialogFragment

class DebugDialog : BaseDialogFragment<DialogDebugBinding>(R.layout.dialog_debug) {
    companion object {
        const val EXTRA_DEBUG_STATE = "EXTRA_DEBUG_STATE"
    }

    private val viewModel: DebugDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            vm = viewModel
        }

        viewModel.apply {
            arguments?.run {
                setDebugState(getString(EXTRA_DEBUG_STATE) ?: "데이터 없음")
            }
        }
    }


    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}