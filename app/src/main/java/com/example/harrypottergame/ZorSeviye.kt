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
import kotlinx.android.synthetic.main.orta_seviye.*
import kotlinx.android.synthetic.main.zor_seviye.*
import kotlinx.android.synthetic.main.zor_seviye.kalanSureSayaci
import kotlinx.android.synthetic.main.zor_seviye.kart1
import kotlinx.android.synthetic.main.zor_seviye.kart10
import kotlinx.android.synthetic.main.zor_seviye.kart11
import kotlinx.android.synthetic.main.zor_seviye.kart12
import kotlinx.android.synthetic.main.zor_seviye.kart13
import kotlinx.android.synthetic.main.zor_seviye.kart14
import kotlinx.android.synthetic.main.zor_seviye.kart15
import kotlinx.android.synthetic.main.zor_seviye.kart16
import kotlinx.android.synthetic.main.zor_seviye.kart2
import kotlinx.android.synthetic.main.zor_seviye.kart3
import kotlinx.android.synthetic.main.zor_seviye.kart4
import kotlinx.android.synthetic.main.zor_seviye.kart5
import kotlinx.android.synthetic.main.zor_seviye.kart6
import kotlinx.android.synthetic.main.zor_seviye.kart7
import kotlinx.android.synthetic.main.zor_seviye.kart8
import kotlinx.android.synthetic.main.zor_seviye.kart9
import kotlinx.android.synthetic.main.zor_seviye.toplamPuan

class ZorSeviye : AppCompatActivity() {
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
        setContentView(R.layout.zor_seviye)

        kalanSure() //ekran açıldığında sayacı başlatır
        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.anamuzik)
        mediaPlayer.start()

        //ana müziği kapatır
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
        val secilenKartSayisi1 = 5 //zor seviye için gryffindor ve slytherinden 5, hufflepuff ve ravenclawdan 4 tane kart seçilecek
        val secilenKartSayisi2 = 4
        val gryffindorResimler = gryffindorList.asSequence().shuffled().take(secilenKartSayisi1).toMutableList()
        val slytherinResimler = slytherinList.asSequence().shuffled().take(secilenKartSayisi1).toMutableList()
        val ravenclawResimler = ravenclawList.asSequence().shuffled().take(secilenKartSayisi2).toMutableList()
        val hufflepuffResimler = hufflepuffList.asSequence().shuffled().take(secilenKartSayisi2).toMutableList()


        //seçilen kartlar 6*6 formatına uymak için ikiye çıkarılmıştır
        gryffindorResimler.addAll(gryffindorResimler)
        slytherinResimler.addAll(slytherinResimler)
        ravenclawResimler.addAll(ravenclawResimler)
        hufflepuffResimler.addAll(hufflepuffResimler)

        //seçili kartlar karıştırılır
        val resimler = (gryffindorResimler + slytherinResimler + ravenclawResimler + hufflepuffResimler).toMutableList()
        resimler.shuffle()

        println(resimler.joinToString()) //hangi kartların seçildiğini konsolda görmek için eklenmiştir

        butonlar = listOf(kart1, kart2, kart3, kart4, kart5, kart6, kart7, kart8,
                    kart9, kart10, kart11, kart12, kart13, kart14, kart15, kart16,
                    kart17, kart18, kart19, kart20, kart21, kart22, kart23, kart24,
                    kart25, kart26, kart27, kart28, kart29, kart30, kart31, kart32,
                    kart33, kart34, kart35, kart36)

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

                    println("$kartAdi, $kartEvi, $kartPuani, $kartResmi") //verilerin alınıp alınmadığının kontrolü
                    kartListesi.add(
                        ZorSeviye.Kartlar(
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
        println("Kart bilgileri çekilmiştir")
    }

    //oyunda kalan sürenin hesaplanması
    private fun kalanSure() {
        val sayici = object : CountDownTimer(45000, 1000){
            override fun onTick(i: Long) {
                kalanSureSayaci.text = "Kalan Süre: ${i / 1000} sn"
            }

            override fun onFinish() {
                sureBitti()
            }
        }
        sayici.start()
    }

    //süre dolduğunda kullanıcıya uyarı verilir
    private fun sureBitti() {
        MaterialAlertDialogBuilder(this)
            .setTitle("UYARI")
            .setMessage("Süre doldu.")
            .setPositiveButton("Ana menüye dön") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this@ZorSeviye, MainOyunSayfasi::class.java)
                startActivity(intent)}
            .show()

        //Süre dolduğunda müzik çalması
        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.oyunsonu)
        mediaPlayer.start()
    }

    private fun oyunBitti(){
        MaterialAlertDialogBuilder(this)
            .setTitle("TEBRİKLER")
            .setMessage("Oyunu kazandınız.")
            .setPositiveButton("Ana menüye dön") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this@ZorSeviye, MainOyunSayfasi::class.java)
                startActivity(intent)}
            .show()

        val mediaPlayer : MediaPlayer = MediaPlayer.create(this,R.raw.tumkartlareslesti)
        mediaPlayer.start()
    }

    //kartların arka yüzlerinin gösterimi (hogwarts logosu)
    private fun gorunusleriGuncelle() {
        kartlar.forEachIndexed { index, kart ->
            val button = butonlar[index]
            button.setImageResource(if (kart.kartAcik) kart.tanimlayici else hogwarts)
        }
    }

    private fun modelleriGuncelle(pozisyon: Int) {
        val kart = kartlar[pozisyon]

        //kart açık olduğu halde kullanıcı karta basarsa hata mesajı verilir;
        if (kart.kartAcik){
            Toast.makeText(this, "Hatalı hamle!", Toast.LENGTH_SHORT).show()
            return
        }

        // kart durumları;
        // üç durum;
        // 1.durum: 0 kart daha önce çevrildi => kartları yenile, seçilen kartı döndür
        // 2.durum: 1 kart daha önce çevrildi => seçilen kartı döndür, resimler eşleşiyor mu diye kontrol et
        // 3.durum: 2 kart daha önce çevrildi => kartları yenile, seçilen kartı döndür
        if (tekSeciliKartinIndexi == null){
            //case 1 ya da 3 gerçekleşiyor;
            kartlariEskiHalineGetir()
            tekSeciliKartinIndexi = pozisyon
        } else{
            //case 2 gerçekleşiyor;
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

            //kartlar eşleşti, puan ekleme
            if (kartlar[pozisyon1].tanimlayici  == kartResmi && kartlar[pozisyon2].tanimlayici == kartResmi) {
                Toast.makeText(this, "Eşleşme bulundu!", Toast.LENGTH_SHORT).show()
                kartlar[pozisyon1].kartEslesti = true
                kartlar[pozisyon2].kartEslesti = true
                bulunanCiftSayisi++

                println("Kart Resmi:" + kartResmi)
                println("Kart Adı:" + kartAdi)
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
                if(bulunanCiftSayisi == 18){
                    oyunBitti()
                }
            }

            //kartlar eşleşmedi, puan kırma
            if (kartlar[pozisyon1].tanimlayici  !=  kartlar[pozisyon2].tanimlayici) {
                kartlar[pozisyon1].kartEslesti = false
                kartlar[pozisyon2].kartEslesti = false
                toplamPuan.text = "Eşleşmedi"
            }
        }
    }

}