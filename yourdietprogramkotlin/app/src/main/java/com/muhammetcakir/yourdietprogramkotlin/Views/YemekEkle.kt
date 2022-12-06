package com.muhammetcakir.yourdietprogramkotlin.Views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityYemekEkleBinding
import java.io.IOException
import java.util.*

class YemekEkle : AppCompatActivity() {
    private lateinit var binding:ActivityYemekEkleBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityYemekEkleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerLauncher()
        supportActionBar?.hide()
        binding.homebutton.setOnClickListener {
            startActivity(Intent(this, AdminAnaSayfaActivity::class.java))
        }

        binding.btnyemekekle.setOnClickListener {
            uploadClicked(binding.root)

        }
        binding.adminfotosec.setOnClickListener {
            imageViewClicked(binding.root)
        }
    }


    fun imageViewClicked(view : View)  {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeriye girme izni gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                    View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)

        }

    }
    fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(
                                this@YemekEkle.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.adminfotosec.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@YemekEkle.contentResolver,
                                selectedPicture
                            )
                            binding.adminfotosec.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@YemekEkle, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun uploadClicked (view: View) {

        //UUID -> image name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val storage = Firebase.storage
        val reference = storage.reference
        val imagesReference = reference.child("images").child(imageName)

        if (selectedPicture != null) {
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                val uploadedPictureReference = storage.reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    println(downloadUrl)

                    val yemekMap = hashMapOf<String,Any>()
                    yemekMap.put("ImageUrl",downloadUrl)
                    yemekMap.put("aciklamasi", binding.adminaciklamasi.text.toString())
                    yemekMap.put("icindekiler",binding.adminyemekicindekiler.text.toString())
                    yemekMap.put("karbmiktari",binding.adminyemekkarbonhidrat.text.toString())
                    yemekMap.put("kategoriid",binding.adminyemekkategoriid.text.toString())
                    yemekMap.put("proteinmiktari",binding.adminyemekprotein.text.toString())
                    yemekMap.put("toplamkalori",binding.adminyemektoplamkalori.text.toString())
                    yemekMap.put("yagmiktari",binding.adminyemekyag.text.toString())
                    yemekMap.put("yapimsuresi",binding.adminyemekhazirlamasuresi.text.toString())
                    yemekMap.put("yemekismi",binding.adminyemekadi.text.toString())
                    //  postMap.put("sifre",binding.uploadCommentText.text.toString())

                    com.muhammetcakir.yourdietprogramkotlin.db.collection( "Yemekler").add(yemekMap).addOnCompleteListener{ task ->

                        if (task.isComplete && task.isSuccessful) {
                            //back
                            println("oldu")
                            Toast.makeText(applicationContext,binding.adminyemekadi.text.toString()+ " Eklendi. Yemekler Kısmından Görebilirsin",Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, AdminAnaSayfaActivity::class.java))


                        }

                    }.addOnFailureListener{exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }


                }

            }

        }


    }
}