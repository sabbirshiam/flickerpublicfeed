package com.example.flickerimagegallery.utils

import android.view.View
import android.widget.PopupMenu
import com.example.flickerimagegallery.R


fun View.showDefaultPopupMenu(itemClickListener: PopupMenu.OnMenuItemClickListener) {
    PopupMenu(this.context, this).apply {
        inflate(R.menu.overflow_menu)
        this.setOnMenuItemClickListener(itemClickListener)
    }.show()
}