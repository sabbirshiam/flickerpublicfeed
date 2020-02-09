package com.example.flickerimagegallery.data.repositories.file_upload

import android.content.Context
import android.util.Log
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class FileUploadRepository(private val context: Context) {
    private val application: GalleryApplication = context as GalleryApplication

    suspend fun uploadFile(path: String): Response<FileUploadResponse> {
        val file = File(path)
        Log.e("File", "Files exists..")
        if (file.exists()) {
            val requestFile: RequestBody = RequestBody.create(
                MediaType.parse("image/*"), // any image can be uploaded.
                file
            )

            //context.contentResolver.getType(file.toUri())

            // if we want to create a builder
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(
                    "avatar",
                    file.name,
                    RequestBody.create(MediaType.parse("image/*"), file)
                )

            // MultipartBody.Part is used to send also the actual file name
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            return application.getRetrofitIntance().fileUploadService.uploadFile(body)
        }

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("avatar", file.name, null)
        return application.getRetrofitIntance().fileUploadService.uploadFile(body)

        //return Response.error(404, ResponseBody.create(MediaType.get(""), ""))
    }
}