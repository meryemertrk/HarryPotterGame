package com.example.harrypottergame

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainOyunSayfasi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_oyun_sayfasi)


        //Tek oyuncu seçimi yapıldığında oyun zorluk seviyesi sayfasına yönlendirme
        findViewById<Button>(R.id.tekOyuncu).setOnClickListener {

            val intent = Intent(this@MainOyunSayfasi, MainSeviyeSecimi::class.java)
            startActivity(intent)


        }

        findViewById<Button>(R.id.cokOyuncu).setOnClickListener {

            val intent = Intent(this@MainOyunSayfasi, CokluOyun::class.java)
            startActivity(intent)


        }


    }
}