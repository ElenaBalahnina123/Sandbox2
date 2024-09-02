package com.slobozhaninova.sandbox

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Random


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        runBlocking {
          val list = List(1000) {
                async {
                   doWork(it.toString())
                }
            }

            list.forEach {
                Log.d("Work", it.await())
            }

        }
    }
}
suspend fun doWork(name: String) : String {
    delay(Random().nextInt(5000).toLong())
    return "Done $name"
}