package com.example.harrypottergame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_kayit_ol_sayfasi.*


class MainKayitOlSayfasi : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kayit_ol_sayfasi)
        supportActionBar?.hide()

        val database = Firebase.database
        val referans = database.getReference("users")

        data class User(val kayitKullaniciAdi: String, val kayitSifre: String,val eposta: String)
        val kullaniciListesi: MutableList<User> = mutableListOf()

        //Veritabanından veri alma
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kullaniciListesi.clear()
                for (user in dataSnapshot.children) {
                    val kayitKullaniciAdi = user.child("kayitKullaniciAdi").getValue(String::class.java)
                    val kayitSifre = user.child("kayitSifre").getValue(String::class.java)
                    val eposta = user.child("eposta").getValue(String::class.java)

                    println("$kayitKullaniciAdi, $kayitSifre, $eposta")
                    kullaniciListesi.add(User(kayitKullaniciAdi.toString(), kayitSifre.toString(),eposta.toString()))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("hata olustu: $databaseError")
            }
        }
        referans.addListenerForSingleValueEvent(valueEventListener)

        val kayitKullaniciAdi = findViewById<EditText>(R.id.kayitKullaniciAdi)
        val kayitSifre = findViewById<EditText>(R.id.kayitSifre)
        val eposta = findViewById<EditText>(R.id.eposta)

        //Kaydet butonuna basıldığında bilgileri veritabanına kaydetme
        findViewById<Button>(R.id.btnKaydet).setOnClickListener {
            val kullanici = object {
                val kayitKullaniciAdi = kayitKullaniciAdi.text.toString()
                val kayitSifre = kayitSifre.text.toString()
                val eposta = eposta.text.toString()
            }


            var isimKullanilmakta = false

            kullaniciListesi.forEach {
                if (it.kayitKullaniciAdi == kullanici.kayitKullaniciAdi) {
                    isimKullanilmakta = true
                }
            }

            if (!isimKullanilmakta and (kullanici.kayitSifre != "")) {
                val idReferans = referans.push()
                idReferans.setValue(kullanici)



                Toast.makeText(this, "kullanici basariyla kayit oldu!!!", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "hata: kullanici ismi kullanilmakta!!!", Toast.LENGTH_SHORT)
                .show()


                val intent = Intent(this@MainKayitOlSayfasi, MainActivity::class.java)
                startActivity(intent)

        }
    }
}