package com.gangaown.flipboardgame.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.gangaown.flipboardgame.R


class FlipViewAdapter(private val context: Context, private val toggleStateArray: ArrayList<Int>)
    :BaseAdapter() {

    private var layoutInflater:LayoutInflater? = null

    override fun getCount(): Int {
        return toggleStateArray.size
    }

    override fun getItem(position: Int): Int {
        return toggleStateArray[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {

        var convertView = view


        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        convertView = layoutInflater!!.inflate(R.layout.cell_item, null)
        val textView = convertView.findViewById<TextView>(R.id.textView)

        if (getItem(position)== 1){

            convertView!!.background = ContextCompat.getDrawable(context, R.drawable.grey_round_box)
        } else {

            convertView!!.background = ContextCompat.getDrawable(context,
                R.drawable.white_round_box)
        }
        textView.text = position.toString()

        return convertView
    }

}