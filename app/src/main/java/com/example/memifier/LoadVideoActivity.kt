package com.example.memifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LoadVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_video)

        // List of video resources in the raw folder
        val videoResources = listOf(
            Pair("Sample Video 1", R.raw.video1)
        )

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.video_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = VideoAdapter(this, videoResources)
    }
}
