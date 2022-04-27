package io.github.guowenlong.camera.sample.fragment

import android.view.View
import io.github.guowenlong.camera.sample.R

/**
 * Description:
 * Author:      郭文龙
 * Date:        2022/4/27 18:21
 * Gmail:       guowenlong20000@sina.com
 */
class TwoFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_two

    companion object {
        fun instance(): TwoFragment {
            return TwoFragment()
        }
    }

    override fun init(view: View) {

    }

    fun open(){

    }
}