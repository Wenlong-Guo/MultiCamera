package io.github.guowenlong.camera.mylibrary

import android.content.Context
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset


/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/23 22:40
 * Gmail:       guowenlong20000@sina.com
 */
object AssetsUtils {
    /**
     * 访问assets目录下的资源文件，获取文件中的字符串
     * @param assetsFilePath - 文件的相对路径，例如："listitemdata.txt或者"/why/listdata.txt"
     * @return 内容字符串
     */
    fun getStringFromAssert(mContext: Context, assetsFilePath: String?): String {
        var content = "" // 结果字符串
        try {
            val `is`: InputStream = mContext.resources.assets.open(assetsFilePath!!) // 打开文件
            var ch = 0
            val out = ByteArrayOutputStream() // 实现了一个输出流
            while (`is`.read().also { ch = it } != -1) {
                out.write(ch) // 将指定的字节写入此 byte 数组输出流
            }
            val buff: ByteArray = out.toByteArray() // 以 byte 数组的形式返回此输出流的当前内容
            out.close() // 关闭流
            `is`.close() // 关闭流
            content = String(buff, Charset.forName("UTF-8")) // 设置字符串编码
        } catch (e: Exception) {
            Toast.makeText(mContext, "对不起，没有找到指定文件！", Toast.LENGTH_SHORT)
                .show()
        }
        return content
    }

}