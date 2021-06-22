package com.bluepig.tnmalarm.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.DialogSongBinding
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.ui.base.BaseDialogFragment
import com.bluepig.tnmalarm.ui.edit.EditActivity
import com.google.android.exoplayer2.ExoPlayer

class SongDialog : BaseDialogFragment<DialogSongBinding>(R.layout.dialog_song) {
    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_SONG_URL = "EXTRA_SONG_URL"
        const val EXTRA_TITLE = "EXTRA_TITLE"
    }

    private lateinit var exoPlayer: ExoPlayer
    private val viewModel: SongDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            vm = viewModel
        }

        viewModel.apply {
            arguments?.run {
                setTitle(getString(EXTRA_TITLE) ?: "제목없음")
                setUrl(getString(EXTRA_URL) ?: run {
                    onDismiss()
                    ""
                })
                setSongUrl(getString(EXTRA_SONG_URL) ?: "")

            }

            activity?.let { act ->
                exoPlayer = viewModel.getSongPlayer().createPlayer(act.applicationContext)
                binding.exo.player = exoPlayer
                loadUrl()
            }

            eventCancel.observe(this@SongDialog, {
                onDismiss()
            })

            eventSelect.observe(this@SongDialog, {
                openEdit()
            })
        }
    }

    private fun openEdit() {
        try {
            if (viewModel.url.value != null && viewModel.title.value != null) {
                startActivity(
                    Intent(
                        requireActivity(),
                        EditActivity::class.java
                    ).apply {
                        putExtra(EditActivity.EXTRA_URL, viewModel.url.value)
                        putExtra(EditActivity.EXTRA_SONG_URL, viewModel.songUrl.value)
                        putExtra(EditActivity.EXTRA_TITLE, viewModel.title.value)
                    })

                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        } catch (e: Exception) {

        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun onDismiss() {
        viewModel.release()
        dismiss()
    }
}