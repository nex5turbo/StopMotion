package wonyong.by.ffmpeg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoRecyclerAdapter(val items:ArrayList<RecyclerData>) : RecyclerView.Adapter<VideoRecyclerAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.thumbnail.setImageBitmap(items[position].image)
        holder.order_text.text = items[position].order.toString()
        holder.time_text.text = items[position].time
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var time_text: TextView = itemView.findViewById((R.id.textView3))
        var order_text: TextView = itemView.findViewById((R.id.textView4))
        var thumbnail: ImageView = itemView.findViewById((R.id.imageView3))
    }



}