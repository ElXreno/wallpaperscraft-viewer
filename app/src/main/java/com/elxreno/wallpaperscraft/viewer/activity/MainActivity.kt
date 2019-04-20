package com.elxreno.wallpaperscraft.viewer.activity

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.elxreno.wallpaperscraft.viewer.R
import com.elxreno.wallpaperscraft.viewer.adapter.ImageAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private val lastVisibleItemPosition: Int
        get() = linearLayoutManager.findLastVisibleItemPosition()

    private var isLoading = false
    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageAdapter = ImageAdapter(requestPhotos(), getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
        linearLayoutManager = LinearLayoutManager(this@MainActivity)

        images_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = imageAdapter
        }

        setRecyclerViewScrollListener()
    }

    private fun setRecyclerViewScrollListener() {
        images_recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val totalItemCount = recyclerView.layoutManager!!.itemCount

                if (totalItemCount - lastVisibleItemPosition < 10 && !isLoading) {
                    doAsync {
                        val photosList = requestPhotos()
                        runOnUiThread { imageAdapter.addPhotos(photosList) }
                    }
                }
            }
        })
    }

    private fun requestPhotos(): ArrayList<String> {
        page++

        isLoading = true

        val photosList = ArrayList<String>()

        doAsync {
            val pageUrl = "https://wallpaperscraft.com/all/3840x2160/page$page"

            val document = Jsoup.connect(pageUrl).get()

            document.select(".wallpapers__image")
                .forEach {
                    photosList.add(
                        it
                            .attr("src")
                            .replace("300x168", "1366x768")
                    )
                }

            Log.e("DEBUG::::", page.toString())

        }.get()

        isLoading = false

        return photosList
    }

}
