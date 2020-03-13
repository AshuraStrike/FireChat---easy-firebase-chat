package com.evenmatter.doesit.firechat.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.evenmatter.doesit.firechat.R
import com.evenmatter.doesit.firechat.model.ChatMsg

class MsgAdapter(context:Context,val name: String): RecyclerView.Adapter<MsgAdapter.ViewHolder>() {

    var data = mutableListOf<ChatMsg>()
    internal val layoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        if(data[position].name == name){
            return 0
        }else{
            return 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layout: Int
        when(viewType){
            0 -> layout = R.layout.item_chat
            else -> layout = R.layout.item_chat2
        }
        val view = layoutInflater.inflate(layout, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.textView_msg?.text = data[position].msg
        holder?.textView_name?.text = "${data[position].name}:"
        holder?.textView_time?.text = data[position].time
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView_msg = itemView.findViewById(R.id.text_view_content) as TextView
        val textView_name = itemView.findViewById(R.id.text_view_user) as TextView
        val textView_time = itemView.findViewById(R.id.text_view_time) as TextView
    }
}