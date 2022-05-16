package io.github.guowenlong.camera.sample.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Description: fragment的基类
 * Author:      郭文龙
 * Date:        2022/4/27 18:16
 * Gmail:       guowenlong20000@sina.com
 */
abstract class BaseFragment : Fragment() {

    abstract val layoutId: Int

    abstract fun init(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container,false)
        init(view)
        return view
    }
 }