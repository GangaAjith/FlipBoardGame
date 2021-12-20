package com.gangaown.flipboardgame

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FlipViewAdapter(private val context: Context, private val cellCount:Int):BaseAdapter() {

    private var layoutInflater:LayoutInflater? = null
    override fun getCount(): Int {
        return cellCount
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var convertView = convertView
        if(layoutInflater == null )
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(convertView == null){
            convertView = layoutInflater!!.inflate(R.layout.cell_item, null)
            convertView.tag = "off"
            val textView = convertView.findViewById<TextView>(R.id.textView)
            textView.text = position.toString()
        }

        return convertView
    }
}