package com.elxreno.wallpaperscraft.viewer.adapter

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.elxreno.wallpaperscraft.viewer.R
import com.elxreno.wallpaperscraft.viewer.inflate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycleview_image.view.*
import org.apache.commons.io.FilenameUtils


class ImageAdapter(private val photos: ArrayList<String>, private val downloadService: DownloadManager) :
    RecyclerView.Adapter<ImageAdapter.PhotoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val inflatedView = parent.inflate(R.layout.recycleview_image, false)
        return PhotoHolder(inflatedView, downloadService)
    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val itemPhoto = photos[position]
        holder.bindPhoto(itemPhoto)
    }

    class PhotoHolder(private var view: View, private var downloadService: DownloadManager) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        private var photo: String? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val url = photo?.replace("1366x768", "3840x2160")
            val request = DownloadManager.Request(Uri.parse(url))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FilenameUtils.getName(url))

            downloadService.enqueue(request)
        }

        fun bindPhoto(photo: String) {
            this.photo = photo
            Picasso.get()
                .load(photo)
                .fit()
                .centerCrop()
                .into(view.image_imageview)
        }
    }

    fun addPhotos(photosList: ArrayList<String>) {
        photos.addAll(photosList)
        notifyDataSetChanged()
    }

}
