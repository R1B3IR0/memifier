package com.example.memifier

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VideoAdapter(private val context: Context, private val videoList: List<Pair<String, Int>>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    inner class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoTitle: TextView = view.findViewById(R.id.video_title)
        val playButton: Button = view.findViewById(R.id.play_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val (videoName, videoResId) = videoList[position]
        holder.videoTitle.text = videoName

        holder.playButton.setOnClickListener {
            val intent = Intent(context, VideoEditActivity::class.java)
            intent.putExtra("videoResId", videoResId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}
