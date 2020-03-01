package com.example.flickerimagegallery.data.repositories.file_upload

import android.content.Context
import android.util.Log
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.Regions
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.regions.Region
import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.Data
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import io.reactivex.Single
import java.io.File
import java.lang.Exception


class AwsUploadRepository(private val context: Context) {
    var awsCredentials = BasicAWSCredentials(BuildConfig.AWS_ACCESS_KEY, BuildConfig.AWS_SECRET_KEY)
    var s3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_1))
    var transferUtility = TransferUtility.builder().s3Client(s3Client).context(context).build()

    fun uploadFile(path: String): Single<FileUploadResponse> {
        val file = File(path)
        Log.e("File", "Files exists..")


        if (file.exists()) {
            TransferNetworkLossHandler.getInstance(context)
            var observer = transferUtility.upload(
                "tutorial-file-upload",
                file.name,
                file
            )

            observer.setTransferListener(object :TransferListener{
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Log.e("AWS","on Progress changed:: $bytesTotal")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    Log.e("AWS","on stateChanged changed:: $state")
                }

                override fun onError(id: Int, ex: Exception?) {
                    Log.e("AWS","on ex changed:: $ex")
                }
            })
        }


        return Single.just(FileUploadResponse(Data("", "","",""), "", false))
    }
}