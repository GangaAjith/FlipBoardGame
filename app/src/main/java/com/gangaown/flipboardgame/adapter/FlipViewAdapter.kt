package com.gangaown.flipboardgame.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gangaown.flipboardgame.R


class FlipViewAdapter(private val context: Context,
                      private val toggleStateArray: ArrayList<Int>,
                      private val listener: OnItemClickListener)
    : RecyclerView.Adapter<FlipViewAdapter.FlipViewHolderClass> (){


     inner class FlipViewHolderClass(itemView:View):RecyclerView.ViewHolder(itemView), View.OnClickListener  {

        private val tvPosition: TextView = itemView.findViewById(R.id.textView)

         fun setData(curItem:Int){
            if (curItem == 1){
                itemView.background = ContextCompat.getDrawable(context, R.drawable.red_round_box)
            } else {

                itemView.background = ContextCompat.getDrawable(context,
                    R.drawable.white_round_box)
            }
            tvPosition.text = adapterPosition.toString()
            itemView.setOnClickListener(this)
        }

         override fun onClick(v: View?) {
             listener.onItemClick(adapterPosition)
         }
     }

     interface OnItemClickListener{
         fun onItemClick(position: Int)
     }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlipViewHolderClass {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.cell_item, parent, false)
        return FlipViewHolderClass(view)
    }

    override fun onBindViewHolder(holder: FlipViewHolderClass, position: Int) {
        val curItem = toggleStateArray[position]
        holder.setData(curItem)
    }

    override fun getItemCount(): Int {
        return toggleStateArray.size
    }

}