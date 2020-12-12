package com.mycp.updatex

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.thl.filechooser.FileChooser
import com.thl.filechooser.FileInfo
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.logInit(true)
        permissions()
        download.setOnClickListener {
//            UpdateManager.newBuilder(this@MainActivity)
//                .setUpdateLog(log)
//                .setApkUrl("https://image.flyfishoutlets.com/apk/215585161.apk")
//                .setApkName("videocompress.apk")
//                .setTitle("发现更新")
//                .setSubTitle("是否更新新版本？")
//                .setForceUpdate(false)
//                .setShowProgress(true)
//                .build()
//                .showUpdate()
           var  scaleCmd = "-vf scale=1080:1920 "
            val cmds = listOf("ffmpeg","-help").toTypedArray()
            RxFFmpegInvoke.getInstance()
                .runCommandRxJava(cmds)
                .subscribe(object : RxFFmpegSubscriber() {
                    override fun onError(message: String?) {
                        LogUtils.loge("完成》》》》》》》》》》》$message")
                    }

                    override fun onFinish() {
                        LogUtils.loge("完成》》》》》》》》》》》")
                    }

                    override fun onProgress(progress: Int, progressTime: Long) {
                        LogUtils.loge("处理中onProgress》》》》》》》》》》》progress"+progress+" <<<<<<<<progressTime>>>>>>>>"+progressTime)
                    }

                    override fun onCancel() {
                    }
                })

//            getFileSelect(this, "选择文件", 1) {
//                if(it.size>0){
//                    val path = it[0].filePath
//                    val outPath = FileUtil.PATH + "_my" +  ".mp4"
//                    val cmds = getCompressCmd(
//                        path,
//                        outPath,
//                        "30",
//                        scaleCmd,
//                        "2048",
//                        "60"
//                    )
//                    RxFFmpegInvoke.getInstance()
//                        .runCommandRxJava(cmds)
//                        .subscribe(object : RxFFmpegSubscriber() {
//                            override fun onError(message: String?) {
//                            }
//
//                            override fun onFinish() {
//                                LogUtils.loge("完成》》》》》》》》》》》")
//                            }
//
//                            override fun onProgress(progress: Int, progressTime: Long) {
//                                LogUtils.loge("处理中onProgress》》》》》》》》》》》progress"+progress+" <<<<<<<<progressTime>>>>>>>>"+progressTime)
//                            }
//
//                            override fun onCancel() {
//                            }
//                        })
//                }
//            }

        }
    }


    fun getFileSelect(context: Activity, title: String?, chooseCount: Int, listener: FileSelectCall) {
        val fileChooser = FileChooser(context,
            FileChooser.FileChoosenListener { filePath -> listener.onFileSelectList(filePath) })
        fileChooser.setBackIconRes(R.drawable.back_white)
        fileChooser.setTitle(title)
        fileChooser.setDoneText("确定")
        fileChooser.setThemeColor(R.color.themeColor)
        fileChooser.isShowHideFile = false
        fileChooser.chooseType = FileInfo.FILE_TYPE_FILE
        fileChooser.showFile(true) //是否显示文件
        fileChooser.chooseCount = chooseCount
        fileChooser.open()
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
                            FileUtil.createDir()
                        }
                    }

                    override fun noPermission(denied: List<String>, quick: Boolean) {}
                })
    }

    /**
     * 视频压缩命令
     * @param inputFile String
     * @param targetFile String
     * @param crf String
     * @param scaleCmd String
     * @return Array<String>
     */
    fun getCompressCmd(
        inputFile: String,
        targetFile: String,
        crf: String,
        scaleCmd: String,
        bitrate: String,
        frame: String
    ): Array<String> {
        val flagTime = System.currentTimeMillis().toString()
//        var cmd = "-threads 4 -y -i $flagTime -c:v libx264 -preset superfast " +
//                "-crf $crf -acodec copy $scaleCmd$targetFile"

        val frameCmd = if (frame.isNotEmpty()) "-r $frame " else ""

        val cmd = if (bitrate.isNotEmpty()) {
            "ffmpeg -threads 4 -y -i $flagTime -c:v libx264 -preset superfast " +
                    "-b:v ${bitrate}k $frameCmd-acodec copy $scaleCmd$targetFile"
        } else {
            "ffmpeg -threads 4 -y -i $flagTime -c:v libx264 -preset superfast " +
                    "-crf $crf $frameCmd-acodec copy $scaleCmd$targetFile"
        }
        val cmds = cmd.split(" ").toTypedArray()
        val position = cmds.indexOf(flagTime)
        cmds[position] = inputFile
        return cmds
    }
}