package com.mycp.updatex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.mycp.updatelib.UpdateManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions()
        download.setOnClickListener {
            UpdateManager.newBuilder(this@MainActivity)
                .setUpdateLog(log)
                .setApkUrl("https://image.flyfishoutlets.com/apk/215585161.apk")
                .setApkName("videocompress.apk")
                .setTitle("发现更新")
                .setSubTitle("是否更新新版本？")
                .setForceUpdate(false)
                .setShowProgress(true)
                .build()
                .showUpdate()
        }
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

                        }
                    }

                    override fun noPermission(denied: List<String>, quick: Boolean) {}
                })
    }
}