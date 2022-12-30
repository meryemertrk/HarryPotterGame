package com.example.harrypottergame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main_seviye_secimi.*

class MainSeviyeSecimi : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_seviye_secimi)

        val kartSecenekler = findViewById<RadioGroup>(R.id.kartSecenekler)
        val oyunaBasla = findViewById<Button>(R.id.oyunaBasla)


        oyunaBasla.setOnClickListener {

            val secilenSecenek: Int = kartSecenekler!!.checkedRadioButtonId
            val secilenSeceneginIsmi = findViewById<RadioButton>(secilenSecenek).text


            Toast.makeText(this, "secilen secenek: $secilenSeceneginIsmi", Toast.LENGTH_SHORT).show()

            if(basitSeviye.isChecked){
                val intent = Intent(this@MainSeviyeSecimi, BasitSeviye::class.java)
                startActivity(intent)
            }

            if(ortaSeviye.isChecked){
                val intent = Intent(this@MainSeviyeSecimi, OrtaSeviye::class.java)
                startActivity(intent)
            }

            if(zorSeviye.isChecked){
                val intent = Intent(this@MainSeviyeSecimi, ZorSeviye::class.java)
                startActivity(intent)
            }
            
        }

    }


}