package com.example.harrypottergame

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.ContactsContract.Data
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harrypottergame.R.drawable.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.basit_seviye.*
import kotlinx.android.synthetic.main.orta_seviye.*
import kotlinx.android.synthetic.main.orta_seviye.kalanSureSayaci
import kotlinx.android.synthetic.main.orta_seviye.kart1
import kotlinx.android.synthetic.main.orta_seviye.kart10
import kotlinx.android.synthetic.main.orta_seviye.kart11
import kotlinx.android.synthetic.main.orta_seviye.kart12
import kotlinx.android.synthetic.main.orta_seviye.kart13
import kotlinx.android.synthetic.main.orta_seviye.kart14
import kotlinx.android.synthetic.main.orta_seviye.kart15
import kotlinx.android.synthetic.main.orta_seviye.kart16
import kotlinx.android.synthetic.main.orta_seviye.kart2
import kotlinx.android.synthetic.main.orta_seviye.kart3
import kotlinx.android.synthetic.main.orta_seviye.kart4
import kotlinx.android.synthetic.main.orta_seviye.kart5
import kotlinx.android.synthetic.main.orta_seviye.kart6
import kotlinx.android.synthetic.main.orta_seviye.kart7
import kotlinx.android.synthetic.main.orta_seviye.kart8
import kotlinx.android.synthetic.main.orta_seviye.kart9
import kotlinx.android.synthetic.main.orta_seviye.toplamPuan
import kotlinx.android.synthetic.main.zor_seviye.*

class OrtaSeviye : AppCompatActivity() {
    private lateinit var butonlar: List<ImageButton>
    private lateinit var kartlar: List<KartFonks>
    private var tekSeciliKartinIndexi: Int? = null

    val database = Firebase.database
    val referans = database.getReference("kartlar")

    private data class Kartlar(
        val kartAdi: String,
        var kartEvi: String,
        val kartPuani: String,
        val kartResmi: String
    )

    private val kartListesi: MutableList<Kartlar> = mutableListOf()
    var bulunanCiftSayisi = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orta_seviye)

        kalanSure() //ekran a????ld??????nda sayac?? ba??lat??r
        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.anamuzik)
        mediaPlayer.start()

        //ana m??zi??i kapat??r
        findViewById<Button>(R.id.muzikKapat).setOnClickListener {
            mediaPlayer.stop()
        }

        val gryffindorList = mutableListOf(albus_dumbledore, arthur_weasley, harry_potter, hermione_granger, lily_potter,
                                minerva_mcgonagall, peter_pettigrew, remus_lupin, ron_weasley, rubeus_hagrid, sirius_black)
        val slytherinList = mutableListOf(andromeda_tonks, bellatrix_lestrange, dolores_umbridge, draco_malfoy, evan_rosier,
                                horace_slughorn, leta_lestrange, lucius_malfoy, narcissa_malfoy, severus_snape, tom_riddle)
        val ravenclawList = mutableListOf(cho_chang, filius_flitwick, garrick_ollivander, gilderoy_lockhart, luna_lovegood, marcus_belby,
                                myrtle_warren, padma_patil, quirinus_quirrell, rowena_ravenclaw, sybill_trelawney)
        val hufflepuffList = mutableListOf(cedric_diggory, ernest_macmillan, fat_friar, hannah_abbott, helga_hufflepuff, leanne,
                                newt_scamander, nymphadora_tonks, pomona_sprout, silvanus_kettleburn, ted_lupin)
        val secilenKartSayisi = 2 //orta seviye = 4*4 (her evden iki kart se??ilecek)
        val gryffindorResimler = gryffindorList.asSequence().shuffled().take(secilenKartSayisi).toMutableList()
        val slytherinResimler = slytherinList.asSequence().shuffled().take(secilenKartSayisi).toMutableList()
        val ravenclawResimler = ravenclawList.asSequence().shuffled().take(secilenKartSayisi).toMutableList()
        val hufflepuffResimler = hufflepuffList.asSequence().shuffled().take(secilenKartSayisi).toMutableList()

        println(gryffindorList)


        //se??ilen kartlar 4*4 format??na uymak i??in ikiye ????kar??lm????t??r
        gryffindorResimler.addAll(gryffindorResimler)
        slytherinResimler.addAll(slytherinResimler)
        ravenclawResimler.addAll(ravenclawResimler)
        hufflepuffResimler.addAll(hufflepuffResimler)

        //se??ili kartlar kar????t??r??l??r
        val resimler = (gryffindorResimler + slytherinResimler + ravenclawResimler + hufflepuffResimler).toMutableList()
        resimler.shuffle()

        println(resimler.joinToString()) //hangi kartlar??n se??ildi??ini konsolda g??rmek i??in eklenmi??tir

        butonlar = listOf(kart1, kart2, kart3, kart4, kart5, kart6, kart7, kart8,
                    kart9, kart10, kart11, kart12, kart13, kart14, kart15, kart16)

        kartlar = butonlar.indices.map {index->
            KartFonks(resimler[index])
        }

        butonlar.forEachIndexed { index, button ->
            button.setOnClickListener{
                modelleriGuncelle(index)
                gorunusleriGuncelle()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val kartListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kartListesi.clear()
                for (user in dataSnapshot.children) {
                    val kartAdi = user.child("kartAdi").getValue(String::class.java)
                    val kartEvi = user.child("kartEvi").getValue(String::class.java)
                    val kartPuani = user.child("kartPuani").getValue(String::class.java)
                    val kartResmi = user.child("kartResmi").getValue(String::class.java)

                    println("$kartAdi, $kartEvi, $kartPuani, $kartResmi") //verilerin al??n??p al??nmad??????n??n kontrol??
                    kartListesi.add(
                        OrtaSeviye.Kartlar(
                            kartAdi.toString(),
                            kartEvi.toString(),
                            kartPuani.toString(),
                            kartResmi.toString()
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Hata olustu: $databaseError")
            }
        }
        referans.addListenerForSingleValueEvent(kartListener)
        println("Kart bilgileri ??ekilmi??tir")
    }

    //oyunda kalan s??renin hesaplanmas??
    private fun kalanSure() {
        val sayici = object : CountDownTimer(45000, 1000){
            override fun onTick(i: Long) {
                kalanSureSayaci.text = "Kalan S??re: ${i / 1000} sn"
            }

            override fun onFinish() {
                sureBitti()
            }
        }
        sayici.start()
    }

    //s??re doldu??unda kullan??c??ya uyar?? verilir
    private fun sureBitti() {
        MaterialAlertDialogBuilder(this)
            .setTitle("UYARI")
            .setMessage("S??re doldu.")
            .setPositiveButton("Ana men??ye d??n") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this@OrtaSeviye, MainOyunSayfasi::class.java)
                startActivity(intent)}
            .show()
        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.oyunsonu)
        mediaPlayer.start()
    }

    private fun oyunBitti(){
        MaterialAlertDialogBuilder(this)
            .setTitle("TEBR??KLER")
            .setMessage("Oyunu kazand??n??z.")
            .setPositiveButton("Ana men??ye d??n") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this@OrtaSeviye, MainOyunSayfasi::class.java)
                startActivity(intent)}
            .show()

        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.tumkartlareslesti)
        mediaPlayer.start()
    }

    //kartlar??n arka y??zlerinin g??sterimi (hogwarts logosu)
    private fun gorunusleriGuncelle() {
        kartlar.forEachIndexed { index, kart ->
            val button = butonlar[index]
            button.setImageResource(if (kart.kartAcik) kart.tanimlayici else hogwarts)
        }
    }

    private fun modelleriGuncelle(pozisyon: Int) {
        val kart = kartlar[pozisyon]

        //kart a????k oldu??u halde kullan??c?? karta basarsa hata mesaj?? verilir;
        if (kart.kartAcik){
            Toast.makeText(this, "Hatal?? hamle!", Toast.LENGTH_SHORT).show()
            return
        }

        // kart durumlar??;
        // ???? durum;
        // 1.durum: 0 kart daha ??nce ??evrildi => kartlar?? yenile, se??ilen kart?? d??nd??r
        // 2.durum: 1 kart daha ??nce ??evrildi => se??ilen kart?? d??nd??r, resimler e??le??iyor mu diye kontrol et
        // 3.durum: 2 kart daha ??nce ??evrildi => kartlar?? yenile, se??ilen kart?? d??nd??r
        if (tekSeciliKartinIndexi == null){
            //case 1 ya da 3 ger??ekle??iyor;
            kartlariEskiHalineGetir()
            tekSeciliKartinIndexi = pozisyon
        } else{
            //case 2 ger??ekle??iyor;
            esitlikKontrolu(tekSeciliKartinIndexi!!, pozisyon)
            tekSeciliKartinIndexi = null
        }
        kart.kartAcik = !kart.kartAcik
    }

    private fun kartlariEskiHalineGetir() {
        for (kart in kartlar){
            if (!kart.kartEslesti){
                kart.kartAcik = false
            }
        }
    }

    private fun esitlikKontrolu(pozisyon1: Int, pozisyon2: Int) {
        kartListesi.forEach {
            val kartResmi = it.kartResmi.toInt()
            val kartAdi = it.kartAdi
            val kartEvi = it.kartEvi
            val kartPuani = it.kartPuani.toInt()

            //kartlar e??le??ti, puan ekleme
            if (kartlar[pozisyon1].tanimlayici  == kartResmi && kartlar[pozisyon2].tanimlayici == kartResmi) {
                Toast.makeText(this, "E??le??me bulundu!", Toast.LENGTH_SHORT).show()
                kartlar[pozisyon1].kartEslesti = true
                kartlar[pozisyon2].kartEslesti = true
                bulunanCiftSayisi++

                println("Kart Resmi:" + kartResmi)
                println("Kart Ad??:" + kartAdi)
                println("Kart Evi:" + kartEvi)
                println("Kart Puani:" + kartPuani)

                var puan = 0
                if (kartEvi == "gryffindor" || kartEvi == "slytherin") {
                    puan = kartPuani * 2 * 2
                    toplamPuan.text = puan.toString()
                }
                if (kartEvi == "ravenclaw" || kartEvi == "hufflepuff"){
                    puan = kartPuani * 2
                    toplamPuan.text = puan.toString()
                }
                val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.kartlareslesti)
                mediaPlayer.start()
                if(bulunanCiftSayisi == 8){
                    oyunBitti()
                    }
                }

            //kartlar e??le??medi, puan k??rma
            if (kartlar[pozisyon1].tanimlayici  !=  kartlar[pozisyon2].tanimlayici) {
                kartlar[pozisyon1].kartEslesti = false
                kartlar[pozisyon2].kartEslesti = false
                toplamPuan.text = "Kartlar e??le??medi"
            }
            }
        }
    }