package com.example.practicekotlin5

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private val photoImageView: ImageView by lazy {
        findViewById(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById(R.id.backgroundPhotoImageView)
    }

    private var currentPosition = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        /* onCreate()에서 타이머 시작 시, 이후 onStop()에서 onStart()로 돌아올 때, 타이머가 실행되지 않기 때문에
            해당 주기에서는 타이머를 시작하지 않음
         */
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_frame)

        Log.d("PhotoFrame,", "onCreate()")

        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent() { // uri 데이터를 받아오는 메서드

        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it)) // String 형의 uri 데이터를 uri로 파싱하여 저장
            }
        }
    }

    private fun startTimer() {
        timer = timer(period = 5000) { // 타이머 설정
            runOnUiThread {

                Log.d("PhotoFrame,", "startTimer: 5초 경과")

                val current = currentPosition
                val next =
                    if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1 // currentPosition + 1이 더 커지게 되면 0

                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f // 투명도
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate() // 애니메이션 설정
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    override fun onStop() { // background로 전환될 경우, 타이머 중단
        super.onStop()

        Log.d("PhotoFrame,", "onStop: timer cancel()")

        timer?.cancel()
    }

    override fun onStart() { // 다시 foreground로 전환, 타이머 시작
        super.onStart()

        Log.d("PhotoFrame,", "onStart: timer start()")

        startTimer()
    }

    override fun onDestroy() { // 완전히 종료될 때, 타이머 중단
        super.onDestroy()

        Log.d("PhotoFrame,", "onDestroy: timer cancel()")

        timer?.cancel()
    }


}