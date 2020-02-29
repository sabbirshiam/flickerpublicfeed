package com.example.flickerimagegallery.presentation

interface BasePresenter<T : BaseView>  {
    fun takeView(view: T)
    fun dropView()
}