package com.pkk.android.attendance.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.pkk.android.attendance.R
import com.pkk.android.attendance.misc.CentralVariables

class AvatarAdapter(
    context: Context,
    private val images: ArrayList<Int>,
    private val listener: View.OnClickListener
) : ArrayAdapter<Int>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.layout_image_view, parent, false)
        val image = view!!.findViewById(R.id.imageView) as ImageView
        image.setTag(CentralVariables.KEY_POSITION, position)
        image.setImageResource(images[position])
        image.setOnClickListener(listener)
        return view
    }
}