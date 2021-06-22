package com.bluepig.tnmalarm.ui.setting

import android.Manifest
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.activity.viewModels
import com.bluepig.tnmalarm.Const
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivitySettingBinding
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.ui.tutorial.TutorialActivity
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.logD
import com.bluepig.tnmalarm.utils.ext.mToast
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class SettingActivity : BaseActivity<ActivitySettingBinding>(R.layout.activity_setting) {
    companion object {
        const val RINGTONE_REQUEST_CODE = 777
    }

    private val viewModel: SettingViewModel by viewModels()

    private fun getDefaultRingtoneUrl(): String? {
        val preUrl = MyPreferences.readDefaultRingTone(this)
        val defaultRingtone =
            RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)

        return if (preUrl.isEmpty()) {
            defaultRingtone?.toString()?.apply {
                MyPreferences.writeDefaultRingTone(this@SettingActivity, this)
            }
        } else {
            preUrl
        }
    }

    override fun onCreate() {
        binding.apply {
            vm = viewModel

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                clPermission.visibility = View.VISIBLE
            }
        }

        init()

        viewModel.apply {
            eventVersionName.observe(this@SettingActivity, {
                versionCheck()
            })

            eventOpenSourceLibrary.observe(this@SettingActivity, {
                openSourceLicenses()
            })

            eventSelectRingTone.observe(this@SettingActivity, {
                showRingtonePickerDialog()
            })

            eventBack.observe(this@SettingActivity, {
                mfinish()
            })

            eventPermission.observe(this@SettingActivity, {
                settingPermission()
            })

            eventTutorial.observe(this@SettingActivity, {
                openAct(TutorialActivity::class.java, false)
            })
        }

    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }

    private fun init() {
        viewModel.setVersionName(packageManager.getPackageInfo(packageName, 0).versionName)

        val default = getDefaultRingtoneUrl()

        default?.let {
            viewModel.setRingtoneName(
                RingtoneManager.getRingtone(
                    this,
                    Uri.parse(it)
                ).getTitle(this)
            )
        } ?: run {
            binding.clRingtone.visibility = View.GONE
        }
    }

    private fun versionCheck() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Const.MARKET_URL + packageName)
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
        openPopupAnimation()
    }

    private fun showRingtonePickerDialog() {
        var uri = MyPreferences.readDefaultRingTone(this)

        if (uri.isBlank()) {
            uri = RingtoneManager.getActualDefaultRingtoneUri(
                this,
                RingtoneManager.TYPE_ALARM
            ).toString().apply {
                MyPreferences.writeDefaultRingTone(this@SettingActivity, this)
            }
        }

        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.defaultSong_guide))
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            putExtra(
                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(uri)
            )
        }
        startActivityForResult(intent, RINGTONE_REQUEST_CODE)
    }

    private fun openSourceLicenses() {
        OssLicensesMenuActivity.setActivityTitle("Ossl Title")
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        openPopupAnimation()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RINGTONE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) { // -- 알림음 재생하는 코드 -- //
                val ring = data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                MyPreferences.writeDefaultRingTone(this, ring.toString())
                RingtoneManager.getRingtone(this, Uri.parse(ring.toString())).getTitle(this).run {
                    viewModel.setRingtoneName(this)
                }
            }
        }
    }

    fun settingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 다른앱 위에 그리기 체크
                TedPermission.with(this)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            mToast("권한이 허용되었습니다.")
                        }

                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                            deniedPermissions?.let {
                                mToast("설정화면에서 권한을 허용할수 있습니다.")
                            }
                        }
                    })
                    .setRationaleTitle("권한 확인")
                    .setRationaleMessage(getString(R.string.alert_permission_guide))
                    .setRationaleConfirmText("확인")
                    .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .check()
            } else {
                mToast("권한이 허용되었습니다.")
            }
        }
    }
}