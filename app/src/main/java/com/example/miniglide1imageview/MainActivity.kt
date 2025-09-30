package com.example.miniglide1imageview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlList = Datasource(this).getUrlList()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        recyclerView.adapter = ImageAdapter(urlList)
    }
}

