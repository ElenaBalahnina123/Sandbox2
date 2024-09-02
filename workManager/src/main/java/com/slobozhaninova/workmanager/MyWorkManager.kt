package com.slobozhaninova.workmanager

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.slobozhaninova.workmanager.databinding.MyWorkFragmentBinding
import java.util.concurrent.TimeUnit


class MyWorkManager(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    val TAG: String = "workmng"

    override fun doWork(): Result {
        Log.d(TAG, "doWork: start")
        try {
            TimeUnit.SECONDS.sleep(10)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d(TAG, "doWork: end")

        return Result.success()
    }

}


open class MyWorkFragment() : Fragment(R.layout.my_work_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            val myWorkRequest: OneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorkManager::class.java).build()
            WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)

    }
}

data object Oval : Shape()

sealed class Shape {
    sealed class Colors {
        data object Black : Shape()
    }
    open class Circle(val radius: Double) : Shape()
    abstract class Rectangle(val width: Double, val height: Double) : Shape()
    class Triangle(val base: Double, val height: Double) : Shape()
}

