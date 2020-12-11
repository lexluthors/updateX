package com.mycp.updatex

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.mycp.updatelib.DownloadUtil
import com.mycp.updatelib.UpdateManager
import com.mycp.updatelib.UpdateUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions()

    }

    var log = """
        12月17日晚更新说明：
        """.trimIndent()

    private fun permissions() {
        XXPermissions.with(this)
            .constantRequest() //可设置被拒绝后继续申请，直到用户授权或者永久拒绝
            .permission(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
            .request(object : OnPermission {
                override fun hasPermission(granted: List<String>, isAll: Boolean) {
                    if (granted.contains("android.permission.WRITE_EXTERNAL_STORAGE")) {

                        UpdateManager.newBuilder(this@MainActivity)
                            .setUpdateLog(log)
                            .setApkUrl("http://dl-tc.coolapkmarket.com/down/apk_upload/2020/1129/app-release_208_jiagu_sign-263117-o_1eo8mcg4fgub84l13gc8lpathr-uid-913919.apk?t=1607665269&sign=0513c0ac374226dc439ebcc144cf1992")
                            .setApkName("videocompress.apk")
                            .setApkPath(UpdateUtils.FILEPATH)
                            .setTitle("发现更新")
                            .setSubTitle("是否更新新版本？")
                            .setForceUpdate(false)
                            .setShowProgress(true)
                            .setShowNotification(true)
                            .build()
                            .showUpdate()
                    }
                }

                override fun noPermission(denied: List<String>, quick: Boolean) {}
            })
    }
}