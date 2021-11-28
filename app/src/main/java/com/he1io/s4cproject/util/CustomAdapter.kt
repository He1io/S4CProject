package com.he1io.s4cproject.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.he1io.s4cproject.R
import com.he1io.s4cproject.data.model.SocialAction

class CustomAdapter(private val dataSet: List<SocialAction>, private val onItemClicked: (SocialAction) -> Unit) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.social_action_name)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_social_action_list, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val socialAction = dataSet[position]
        viewHolder.textView.text = socialAction.name

        viewHolder.itemView.setOnClickListener{
            onItemClicked(socialAction)
        }
    }

    override fun getItemCount() = dataSet.size
}
