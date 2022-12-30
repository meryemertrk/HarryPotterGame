package com.example.harrypottergame

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.harrypottergame.databinding.ActivityMainBinding
import com.example.harrypottergame.databinding.ActivityMainKayitOlSayfasiBinding
import com.example.harrypottergame.databinding.ActivityMainSifreDegistirmeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_sifre_degistirme.*



class MainSifreDegistirme : AppCompatActivity() {
    private val database = Firebase.database
    private val referans = database.getReference("users")

    private data class Kullanici(
        val id: String,
        val kayitKullaniciAdi: String,
        val kayitSifre: String,
        val eposta: String
    )

    private val kullaniciListesi: MutableList<Kullanici> = mutableListOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_sifre_degistirme)
        supportActionBar?.hide()

        val eposta = findViewById<EditText>(R.id.eposta)
        val yeniSifre = findViewById<EditText>(R.id.yeniSifre)

        data class GuncellenmisKullanici(
            val kayitKullaniciAdi: String,
            val kayitSifre: String,
            val eposta: String
        )

        findViewById<Button>(R.id.yeniSifreKaydet).setOnClickListener {
            val user = object {
                val eposta = eposta.text.toString()
                val yeniSifre = yeniSifre.text.toString()
            }

            var sifreDegisti = false

            kullaniciListesi.forEach {
                if ((it.eposta == user.eposta)) {
                    referans.child(it.id).setValue(
                        GuncellenmisKullanici(
                            it.kayitKullaniciAdi,
                            user.yeniSifre,
                            it.eposta
                        )
                    )
                    sifreDegisti = true
                }
            }

            if (sifreDegisti) {
                Toast.makeText(this, "sifre basariyla degistirildi!!!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@MainSifreDegistirme, MainActivity::class.java)
                startActivity(intent)
            } else Toast.makeText(this, "sifre degistirilemedi!!!", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onResume() {
        super.onResume()
        val kullaniciListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kullaniciListesi.clear()
                for (user in dataSnapshot.children) {
                    val id = user.key;
                    val kayitKullaniciAdi = user.child("kayitKullaniciAdi").getValue(String::class.java)
                    val kayitSifre = user.child("kayitSifre").getValue(String::class.java)
                    val eposta = user.child("eposta").getValue(String::class.java)

                    println("$id, $kayitKullaniciAdi, $kayitSifre, $eposta")
                    kullaniciListesi.add(
                        Kullanici(
                            id.toString(),
                            kayitKullaniciAdi.toString(),
                            kayitSifre.toString(),
                            eposta.toString()
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("hata olustu: $databaseError")
            }
        }
        referans.addListenerForSingleValueEvent(kullaniciListener)
    }
}


