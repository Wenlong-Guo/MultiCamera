package io.github.guowenlong.multicamera.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*
import java.nio.charset.Charset


/**
 * Description: Assets工具类
 * Author:      郭文龙
 * Date:        2022/4/26 19:02
 * Gmail:       guowenlong20000@sina.com
 */
object AssetsUtils {

    private const val TAG = "AssetsUtils"

    fun readAssets2String(context: Context, assetsFilePath: String): String {
        var content = ""
        try {
            val inputStream = context.resources.assets.open(assetsFilePath)
            var ch: Int
            val out = ByteArrayOutputStream() // 实现了一个输出流
            while (inputStream.read().also { ch = it } != -1) {
                out.write(ch) // 将指定的字节写入此 byte 数组输出流
            }
            val buff: ByteArray = out.toByteArray() // 以 byte 数组的形式返回此输出流的当前内容
            out.close() // 关闭流
            inputStream.close() // 关闭流
            content = String(buff, Charset.forName("UTF-8")) // 设置字符串编码
        } catch (e: Exception) {
            Log.e(TAG, "readAssets2String", e)
        }
        return content
    }

    fun assetsToString(context: Context, rawId: Int): String {
        val `is`: InputStream = context.resources.openRawResource(rawId)
        val br = BufferedReader(InputStreamReader(`is`))
        var line: String?
        val sb = StringBuilder()

        try {
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        try {
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return sb.toString()
    }

    fun getImageFromAssetsFile(context: Context, fileName: String): Bitmap? {
        var image: Bitmap? = null
        val am = context.resources.assets
        try {
            val `is` = am.open(fileName)
            image = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }
}