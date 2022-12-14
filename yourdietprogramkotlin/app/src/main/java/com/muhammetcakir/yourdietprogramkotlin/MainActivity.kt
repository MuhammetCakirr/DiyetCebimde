package com.muhammetcakir.yourdietprogramkotlin


import android.Manifest
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import com.muhammetcakir.yourdietprogramkotlin.ApiServisleri.TimeApi
import com.muhammetcakir.yourdietprogramkotlin.ApiServisleri.TimeTurkey
import com.muhammetcakir.yourdietprogramkotlin.Models.*
import com.muhammetcakir.yourdietprogramkotlin.Views.*
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


var retrofit: Retrofit? = null
var timeApi: TimeApi? = null
val baseurl: String = "http://worldtimeapi.org/api/timezone/"
var timeTurkeycall: Call<TimeTurkey>? = null
var timeTurkey: TimeTurkey? = null
var timeTurkey2: TimeTurkey? = null
val diyetArrayList: ArrayList<Diyet> = ArrayList()
val bkidiyetArrayList: ArrayList<Diyet> = ArrayList()
val diyetbelirlemeList: ArrayList<DiyetBelirleme> = ArrayList()
var kisibki: Double? = null
var db: FirebaseFirestore = FirebaseFirestore.getInstance()
private var auth: FirebaseAuth = FirebaseAuth.getInstance()
val suankikullanicilist: ArrayList<User> = ArrayList()
val yenikikullanicilist: ArrayList<User> = ArrayList()
var currentUser = auth.currentUser

class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    var selectedBitmap: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        val view = binding.root
        setContentView(view)
        retrofitsettings()
        getkullaniciFromFirestore()
        getKategoriFromFirestore()
        diyetgetir()
        registerLauncher()
        yemekgetir()
        diyetolustur()
        populateDiets()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("817128577458-9ft18ssml32b7i8a7583u7rth20dks6e.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //Google ile giri?? butonu
        binding.googlegiris.setOnClickListener() {
            signIn()
        }
        //Kay??t ol taraf??na ge??ince
        binding.singUp.setOnClickListener {
            binding.singUp.background = resources.getDrawable(R.drawable.switch_trcks, null)
            binding.singUp.setTextColor(resources.getColor(R.color.textColor, null))
            binding.logIn.background = null
            binding.singUpLayout.visibility = View.VISIBLE
            binding.logInLayout.visibility = View.GONE
            binding.logIn.setTextColor(resources.getColor(R.color.white, null))
            binding.yada.visibility = View.GONE
            binding.girisyapbutton.text = "Kay??t Ol"
            binding.googlegiris.visibility = View.GONE
            binding.girisyapbutton.visibility = View.GONE
            binding.kayitolbutton.visibility = View.VISIBLE
        }
        //Giri?? Yap taraf??na ge??ince
        binding.logIn.setOnClickListener {
            binding.logIn.setTextColor(resources.getColor(R.color.white, null))
            binding.singUp.background = null
            binding.singUp.setTextColor(resources.getColor(R.color.white, null))
            binding.logIn.background = resources.getDrawable(R.drawable.switch_trcks, null)
            binding.singUpLayout.visibility = View.GONE
            binding.logInLayout.visibility = View.VISIBLE
            binding.logIn.setTextColor(resources.getColor(R.color.textColor, null))
            binding.girisyapbutton.text = "Giri?? Yap"
            binding.yada.visibility = View.VISIBLE
            binding.googlegiris.visibility = View.VISIBLE
            binding.girisyapbutton.visibility = View.VISIBLE
            binding.kayitolbutton.visibility = View.GONE
        }

        //Giri?? yap butonu t??klan??nca
        binding.girisyapbutton.setOnClickListener {
            signInClicked(view)
            startActivity(Intent(this@MainActivity, SplashScreen::class.java))
        }
        //Kay??t Ol butonu t??klan??nca
        binding.kayitolbutton.setOnClickListener() {

            signUpClicked(view)

                val intent = Intent(applicationContext, SplashScreen::class.java)
                startActivity(intent)


        }
        //Foto??raf Se?? butonu
        binding.fotosec.setOnClickListener() {
            imageViewClicked(view)
            fotografseciniz.visibility = View.GONE
        }
    }

    private fun getkullaniciFromFirestore() {
        db.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    KullanicilarArrayList.add(
                        User(
                            document.getId().toString(),
                            document.getString("email").toString(),
                            document.getString("sifre").toString(),
                            document.getString("isim").toString(),
                            document.getString("ImageUrl").toString(),
                            document.getString("kilo").toString(),
                            document.getString("boy").toString()
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, NewActivity::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }

    private fun retrofitsettings() {
        retrofit = Retrofit.Builder().baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        timeApi = retrofit!!.create<TimeApi>()
        timeTurkeycall = timeApi!!.getTime()

        timeTurkeycall!!.enqueue(object : Callback<TimeTurkey?> {
            override fun onResponse(call: Call<TimeTurkey?>, response: Response<TimeTurkey?>) {
                if (response.isSuccessful) {
                    timeTurkey = response.body()
                    timeTurkey2 = response.body()
                    if (timeTurkey!!.dayOfWeek == "1") {
                        timeTurkey!!.dayOfWeek = "Pazartesi G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "2") {
                        timeTurkey!!.dayOfWeek = "Sal?? G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "3") {
                        timeTurkey!!.dayOfWeek = "??ar??amba G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "4") {
                        timeTurkey!!.dayOfWeek = "Per??embe G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "5") {
                        timeTurkey!!.dayOfWeek = "Cuma G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "6") {
                        timeTurkey!!.dayOfWeek = "Cumartesi G??n?? ????in ??neri Yemekler"
                    } else if (timeTurkey!!.dayOfWeek == "7") {
                        timeTurkey!!.dayOfWeek = "Pazar G??n?? ????in ??neri Yemekler"
                    }
                }
            }

            override fun onFailure(call: Call<TimeTurkey?>, t: Throwable) {
                println(t.toString())
            }
        })
    }

    fun imageViewClicked(view: View) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, "Galeriye girme izni gerekli", Snackbar.LENGTH_INDEFINITE)
                    .setAction("??zin ver",
                        View.OnClickListener {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                                this@MainActivity.contentResolver,
                                selectedPicture!!
                            )
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.fotosec.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                this@MainActivity.contentResolver,
                                selectedPicture
                            )
                            binding.fotosec.setImageBitmap(selectedBitmap)
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
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@MainActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun uploadClicked(view: View) {
        //UUID -> image name
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val storage = Firebase.storage
        val reference = storage.reference
        val abc = "0"
        val imagesReference = reference.child("images").child(imageName)
        if (selectedPicture != null) {
            imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->
                val uploadedPictureReference = storage.reference.child("images").child(imageName)
                uploadedPictureReference.downloadUrl.addOnSuccessListener{ uri ->
                    val downloadUrl = uri.toString()
                    val userMap = hashMapOf<String, Any>()
                    userMap.put("ImageUrl", downloadUrl)
                    userMap.put("email", auth.currentUser!!.email.toString())
                    userMap.put("isim", yenikikullanicilist[0].isim.toString())
                    userMap.put("sifre", yenikikullanicilist[0].sifre.toString())
                    userMap.put("kilo", abc.toString())
                    userMap.put("boy", abc.toString())
                    db.collection("Users").document(currentUser!!.uid).set(userMap).addOnCompleteListener { task ->
                        if (task.isComplete && task.isSuccessful) {
                            //back
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            applicationContext,
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    fun signInClicked(view: View) {
        val userEmail = binding.girisyapemail.text.toString()
        val password = binding.girisyapsifre.text.toString()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Toast.makeText(
                        applicationContext,
                        "Ho??geldin: ${auth.currentUser?.email.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    fun signUpClicked(view: View) {
        val userEmail = binding.kayitolemail.text.toString()
        val password = binding.kayitolsifre.text.toString()
        val isim = binding.kayitolisim.text.toString()
        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadClicked(view)
                    yenikikullanicilist.add(
                        User(
                            "1",
                            userEmail,
                            password,
                            isim,
                            selectedPicture.toString(),
                            "0",
                            "0"
                        )
                    )
                    Toast.makeText(
                        applicationContext,
                        "Aram??za Ho??geldin: ${auth.currentUser?.email.toString()}",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun yemekgetir() {
        yemekList.clear()
        db.collection("Yemekler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    yemekList.add(
                        Yemek(
                            document.getId().toString(),
                            document.getString("ImageUrl").toString(),
                            document.getString("yemekismi").toString(),
                            document.getString("aciklamasi").toString(),
                            document.getString("icindekiler").toString(),
                            document.getString("karbmiktari").toString(),
                            document.getString("proteinmiktari").toString(),
                            document.getString("yagmiktari").toString(),
                            document.getString("toplamkalori").toString(),
                            document.getString("yapimsuresi").toString(),
                            document.getString("kategoriid").toString(),
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }

    }
    fun diyetolustur() {
        val diyet1 = Diyet(
            "1",
            "1 el ayas?? b??y??kl??????nde ??zgara Dana biftek",
            "Mevsim ya??s??z salata",
            "Elma sirkeli salata",
            "1 avu?? beyaz leblebi + 1 bardak kefir",
            "5 adet kuru erik + 10 badem",
            "Bolca su i??!",
            "1 orta boy kep??e ??orba",
            "Mevsim ye??illikleri",
            "4 yemek ka???????? light yo??urt",
            "1 ince dilim tam tah??ll?? ekmek",
            "2 yemek ka???????? lor",
            "1 fincan ??ekersiz ye??il ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet1)
        val diyet2 = Diyet(
            "2",
            "?? porsiyon ??zgara bal??k",
            "Elma sirkeli ya??s??z salata",
            "Mevsim ya??s??z salata",
            "1 kutu meyveli yo??urt + 10 f??nd??k",
            "4-5 adet kuru erik + 2 adet t??m ceviz",
            "Bolca su i??!",
            "Ha??lanm???? sebze",
            "4 yemek ka???????? light yo??urt",
            "1 Bardak ??ekersiz ??ay",
            "1 adet kepekli galeta",
            "1 dilim yar??m ya??l?? beyaz peynir",
            "Mevsim ye??illikleri",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet2)
        val diyet3 = Diyet(
            "3",
            "1 el ayas?? b??y??kl??????nde ??zgara Dana biftek",
            "Mevsim ya??s??z salata",
            "Elma sirkeli salata",
            "1 avu?? beyaz leblebi + 1 bardak kefir",
            "5 adet kuru erik + 10 badem",
            "Bolca su i??!",
            "1 orta boy kep??e ??orba",
            "Mevsim ye??illikleri",
            "4 yemek ka???????? light yo??urt",
            "1 ince dilim tam tah??ll?? ekmek",
            "1 adet ha??lanm???? yumurta",
            "1 fincan ??ekersiz ye??il ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet3)
        val diyet4 = Diyet(
            "4",
            "30 gram kadar ha??lama hindi",
            "Elma sirkeli ya??s??z salata",
            "5 yemek ka???????? pilav",
            "1 porsiyon meyve ve Light F??st??k Ezmesi (1 tatl?? ka????????)",
            "Tar????nl?? yar??m ya??l?? s??t + 1 yemek ka???????? yulaf kepe??i",
            "Bolca su i??!",
            "4 yemek ka???????? zeytinya??l?? sebze yeme??i",
            "Bol salata",
            "4 yk light yo??urt",
            "1 ince dilim tam tah??ll?? ekmek",
            "S???????? sebze",
            "1 dilim light ke??i peyniri",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet4)
        val diyet5 = Diyet(
            "5",
            "Ha??lama sebze",
            "4 yemek ka???????? light yo??urt + 1 tatl?? ka???????? nane",
            "Elma sirkeli salata",
            "2 adet grisini + 1 bardak ayran",
            "1 su barda???? yo??urt + 3 tane kuru kay??s??",
            "Bolca su i??!",
            "3 yemek ka???????? ya??s??z Menemen",
            "1 su barda???? sade mineralli maden suyu",
            "4 yemek ka???????? light yo??urt",
            "1 ince dilim tam tah??ll?? ekmek",
            "2 yemek ka???????? lor",
            "1 fincan ??ekersiz beyaz ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet5)
        val diyet6 = Diyet(
            "6",
            "1 orta boy kep??e ??orba",
            "Ya??s??z mevsim salata",
            "4 yemek ka???????? light yo??urt",
            "1 yemek ka???????? kuru ??z??m + 6-7 tane badem",
            "1 kutu yar??m ya??l?? s??t + bu??day ru??eymi",
            "Bolca su i??!",
            "1 el ayas?? b??y??kl??????nde ??zgara k??fte",
            "Bol ya??s??z salata",
            "Cac??k",
            "1 ince dilim tam tah??ll?? ekmek",
            "1 dilim ya??s??z beyaz peynir",
            "1 fincan ??ekersiz biberiye ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet6)
        val diyet7 = Diyet(
            "7",
            "1 porsiyon ??zgara Hindi eti",
            "Elma sirkeli ya??s??z mevsim salata",
            "1 su barda???? light ayran",
            "5 adet kuru erik + 10 badem",
            "?? simit + 1 yemek ka???????? labne peynir veya karper",
            "Bolca su i??!",
            "2 ince dilim yar??m ya??l?? beyaz peynir",
            "1 adet kepekli grissini",
            "Bol ye??illikli salata",
            "1 dilim sade wasa",
            "2 yemek ka???????? lor",
            "1 fincan ??ekersiz ye??il ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet7)
        val diyet8 = Diyet(
            "8",
            "1 orta boy kep??e ??orba",
            "Mevsim ya??s??z salata",
            "Elma sirkeli salata",
            "1 su barda???? yar??m ya??l?? s??t + ?? paket ya??l?? azalt??lm???? bisk??vi",
            "1 kutu meyveli yo??urt + 10 f??nd??k",
            "Bolca su i??!",
            "1 el ayas?? b??y??kl??????nde ??zgara k??fte",
            "Bol salata",
            "1 su barda???? sade mineralli maden suyu",
            "1 ince dilim tam tah??ll?? ekmek",
            "2 yemek ka???????? lor",
            "1 fincan ??ekersiz biberiye ??ay",
            "Herg??n",
            "x"
        )
        diyetArrayList.add(diyet8)
    }
    private fun populateDiets() {
        val Dukan = DietCesit(
            R.drawable.dietcesitleri1,
            "Dukan Diyeti Nedir?",
            "Dukan Diyeti",
            "Dukan Diyeti, 4 ana safhaya ayr??lan y??ksek proteinli ve ??ok d??????k karbonhidratl?? bir diyet listesidir. Frans??z beslenme uzman?? Dr. Pierre Dukan, Dukan Diyeti???ni, et hari?? olmak ??zere her ??eyi yemekten vazge??erek kilo vermek isteyen obez bir insandan esinlenerek yazm????t??r. Diyet, 4 a??amadan olu??maktad??r: Atak evresi, seyir evresi, g????lendirme evresi ve koruma evresi.\n" +
                    "Atak evresi (1 ??? 7 g??n): Diyete, s??n??rs??z ya??s??z protein ve g??ne 1.5 yemek ka???????? yulaf yiyerek ba??lan??r. K??sa, h??zl?? ve hemen sonu?? al??nan a??amad??r.\n" +
                    "Seyir evresi (1 ??? 12 ay): Bir g??n ya??s??z proteinler, g??nde 2 yemek ka???????? yulaf yiyerek, ni??asta bulunmayan sebzeler yiyerek devam edilir. Bu a??ama ger??ek a????rl??k kayb??n??n ya??and?????? a??amad??r.\n" +
                    "G????lendirme evresi (De??i??ken): S??n??rs??z ya??s??z protein ve sebzeler, baz?? karbonhidratlar ve ya??lar, haftada sadece bir g??n ya??s??z protein ve g??nde 2.5 yemek ka???????? yulaf kepe??i yenilen, s??resi de??i??kenlik g??sterebilen bir a??amad??r.\n" +
                    "Koruma evresi (Belirsiz): Temel kurallara uyulur, ancak a????rl??k sabit kald?????? s??rece kurallar gev??etilebilir. Yulaf kepe??i g??nde 3 yemek ka????????na ????kart??labilir."
        )
        DietCesitList.add(Dukan)
        val HollywoodDiyeti = DietCesit(
            R.drawable.holywooddiet,
            "Hollywood Diyeti Nedir?",
            "Hollywood Diyeti",
            "Hollywood Diyeti, 3 ana ve 1 ara ??????n ??nermektedir. ??ekerli yiyecekler kesinlikle t??ketilmemelidir. Saat 20:00???den itibaren kesinlikle bir ??ey yenilmemelidir. Yenilen besin sadece bir tabak t??ketilmelidir. Hollywood Diyeti???nde patates, pirin??, havu??, muz, ananas, i??lenmi?? ??eker, i??lenmi?? t??m beyaz un ??r??nleri, alkol, bal ve dondurma yer almamaktad??r."
        )
        DietCesitList.add(HollywoodDiyeti)
        val KetojenikDiyet = DietCesit(
            R.drawable.ketojonikdiet,
            "Ketojenik Diyet Nedir",
            "Ketojenik Diyet",
            "Ketojenik diyet, t??pta ??ncelikle ??ocuklarda kontrol edilmesi zor yani refrakter epilepsiyi tedavi etmek i??in kullan??lan y??ksek ya??l?? besinler ile yeter miktarda protein i??eren besinlerin ??ncelikli t??ketildi??i, d??????k karbonhidratl?? bir diyettir. Ketojenik diyetin halk aras??ndaki bir di??er ad?? da keto diyettir. \n" +
                    "Ger??ekle??tirilen s??n??rl?? t??bbi ??al????malar sonucunda ketojenik diyet s??recinde aralar??nda ??ncelikle epilepsi olmak ??zere Alzheimer, diyabet ve kanser gibi birtak??m hastal??klara ve sa??l??k sorunlar??na kar???? faydalar??n??n yan?? s??ra kilo verilmesinde yard??mc?? oldu??u g??r??lm????t??r. \n" +
                    "\n" +
                    "Ancak uzun s??re kesintisiz devam ettirilmesi sa??l??k a????s??ndan tehlikeli sonu??lara yol a??abilir. Yap??lan ara??t??rmalar t??p uzmanlar?? taraf??ndan hen??z yeterli g??r??lecek ??apta ger??ekle??tirilmedi??i i??in ketojenik diyete ba??lama ve diyeti s??rd??rme kararlar??n??n dikkatli bir ??ekilde, ideal ??artlar alt??nda mutlaka bir diyetisyene ba??vurarak al??nmas?? tavsiye edilmektedir.\n" +
                    "\n" +
                    "Ketojenik diyetin epilepsi tedavisi i??in kullan??ld?????? ara??t??rmalarda vakalar??n yar??s??ndan fazlas??nda epilepsi n??betlerinde farkl?? oranlarda azalma g??r??lm????t??r. Hastalar??n belirli bir y??zdesinde ketojenik diyet, Atkins diyeti ve di??er d??????k karbonhidrat diyetleri ile bir??ok benzerli??e sahiptir. \n" +
                    "\n" +
                    "Ketojenik diyetin temelinde v??cudun d????ar??dan karbonhidrat al??m??n?? b??y??k ??l????de azaltmak vard??r. Karbonhidrat al??m??ndaki bu azalma v??cudu ketoz ad?? ad?? verilen metabolik bir duruma sokar. Ketoz durumunda v??cut enerji i??in ya?? yakmada ??ok daha verimli bir hale gelir ve v??cut enerji ihtiyac??n?? karbonhidratlar yerine ya??lardan kar????lamaya ba??lar. \n" +
                    "Bu s??re?? dahilinde ya?? karaci??erde ketonlara d??n????t??r??l??r ve bunlar beyin i??in dahi enerji sa??lamak ??zere kullan??labilir. Ketojenik diyetler hem kan ??ekeri de??erlerinde hem de ins??lin seviyelerinde b??y??k bir d????meye neden olabilir. "
        )
        DietCesitList.add(KetojenikDiyet)
        val ??svecDiyeti = DietCesit(
            R.drawable.isvecdiet,
            "??sve?? Diyeti Nedir?",
            "??sve?? Diyeti",
            "??sve?? Diyeti, tamamen protein t??ketmeye odaklanm???? bir diyet t??r??d??r. ??sve?? Diyeti???nin hedefi ??zellikle y??ksek protein t??ketme ile metabolizmay?? h??zland??rma ve buna ba??l?? olarak ani kilo verdirme ??zerinedir. Bu diyette kesinlikle t??ketilmemesi gereken besinler vard??r. Bunlar??n ba????nda gazl?? i??ecekler gelmektedir.\n" +
                    "??sve?? Diyeti???nin en ??nemli ??zelli??i 6 g??nden az, 13 g??nden fazla uygulanamamas??d??r. Bu diyetin ??nemli ??zelli??i ??ok az kalori al??nmas?? ve buna ba??l?? olarak metabolizmay?? h??zland??r??c?? besinler i??ermesidir. ??rnek vermek gerekirse ??ay yasakt??r, ama ??ay??n yerine ya??s??z kahve i??ilebilir. Protein grubu dedi??imiz et, tavuk ve bal??k grubuna a????rl??k verilmi??tir. ????len ya da ak??am ??????nlerinde yine g??ne g??re de??i??mekle birlikte protein t??ketimi olmaktad??r."
        )
        DietCesitList.add(??svecDiyeti)
        val KaratayDiyeti = DietCesit(
            R.drawable.karataydiyeti,
            "Karatay Diyeti Nedir?",
            "Karatay Diyeti",
            "Karatay Diyeti???nde t??m ??eker ve ??ekerli g??dalar, tatland??r??c??lar, diyabetik ??r??nler, tah??l unu ve bunlar ile haz??rlanm???? besinler, ekmek, kavrulmu?? kuruyemi??ler, pi??mi?? havu??, patates, pirin??, ??z??m, kavun, karpuz, incir, haz??r al??nan tavuk, salam, sosis, sucuk ve yumurta, diyet ve light i??ecekler dahil her t??rl?? me??rubat, neskafe, alkoll?? i??ecekler, meyve i??erikli yo??urt, ??????t??lm???? tah??l, ay??i??ek ya????, haz??r kat?? ya??lar, m??s??r??z?? ya???? yasakt??r." +
                    "Glisemik indeksi d??????k besinler t??ketilmelidir. Do??al tavuk, i??lenmemi?? do??al ??r??nler, past??rma ve do??al yumurta, evde mayalanm???? yo??urt, ev yo??urdu ile haz??rlanm???? ayran, soda, t??rk kahvesi ve filtre kahve t??ketilmelidir. F??nd??k ya????, zeytinya???? ve tereya???? t??ketilmelidir. ??????nlerden doymadan kalkmak ve ara ??????n yapmak yasakt??r. Salata, sebze, bakliyat, et ve bal??????n yer ald?????? Karatay Diyeti???nde ak??am 19:00???dan sonra at????t??rmak yasakt??r. Bu saatlerden sonra yaln??zca bitki ??ay?? i??ilebilir. G??nde en az 2 litre su i??ilmeli, 30-45 dakika aras?? y??r??y???? yap??lmal??d??r."
        )
        DietCesitList.add(KaratayDiyeti)
        val MontignacDiyeti = DietCesit(
            R.drawable.montignacdiet,
            "Montignac Diyeti Nedir?",
            "Montignac Diyeti",
            "Hollywood Diyeti, 3 ana ve 1 ara ??????n ??nermektedir. ??ekerli yiyecekler kesinlikle t??ketilmemelidir. Saat 20:00???den itibaren kesinlikle bir ??ey yenilmemelidir. Yenilen besin sadece bir tabak t??ketilmelidir. Hollywood Diyeti???nde patates, pirin??, havu??, muz, ananas, i??lenmi?? ??eker, i??lenmi?? t??m beyaz un ??r??nleri, alkol, bal ve dondurma yer almamaktad??r."
        )
        DietCesitList.add(MontignacDiyeti)
        val VejeteryanDiyet = DietCesit(
            R.drawable.vejetaryandiyet,
            "Vejeteryan Diyeti Nedir?",
            "Vejeteryan Diyeti",
            "Lakto vejetaryen diyetinde bitkisel besinlerle birlikte, hayvansal kaynakl?? besinlerden s??t ve s??t ??r??nlerini t??ketilir. Ova vejetaryen diyetinde bitkisel besinlerle birlikte yumurta da yer al??r. Bunun yan??nda et ve s??t t??ketmezler. Lakto-ova vejetaryen diyetinde s??t ve yumurta t??ketilir.\n" +
                    "Baz?? gruplar da etler aras??nda tercih yaparlar. Bitkisel besinler yan??nda hayvansal olarak yaln??zca k??mes hayvanlar??n?? t??ketenlere polo vejetaryen, yaln??zca deniz ??r??nlerini t??ketenlere peskovejetaryenler denilmektedir. Semi-vejetaryenler ise k??rm??z?? eti t??ketmeyen, s??n??rl?? miktarda tavuk ve bal??k t??ketenlerdir. Semi vejetaryenler yumurta, s??t ve t??revlerini serbest??e t??ketirler.\n" +
                    "Vejetaryen diyetler kalp-damar hastal??k riskini azaltmaktad??r. Hayvansal kaynakl?? besinlerin toplam ya??, doymu?? ya?? ve kolesterol i??eri??i y??ksektir. Koroner kalp hastal??????n??n, et yiyenlerde yemeyenlere g??re %30 daha s??k g??r??ld?????? bildirilmektedir. Vejetaryen diyeti t??keten bireyler, et i??eren diyetle beslenen bireylere oranla daha d??????k s??kl??kta kansere yakalanmaktad??r. Vejetaryen diyeti, baklagiller, ceviz, f??nd??k gibi sert kabuklu meyveler, taze sebze ve meyveler ile safla??t??r??lmam???? tah??l ??r??nlerinden zengindir. Bu besinler de kansere kar???? koruyucu olarak bilinen antioksidan ????elerin (E vitamini, C vitamini, karotenoidler, bioflavonoid ve di??er biyoaktif bile??ikler) al??m??n?? art??r??r.\n" +
                    "Vejetaryen bireyler besin ??e??itliliklerini iyi ayarlayamazlarsa demir mineralini yetersiz alabilirler. Bunun sonucunda ise kans??zl??k (anemi) g??r??lmesi ka????n??lmazd??r. Vejetaryen diyetlerinde ??zellikle veganlarda B12 vitamini yetersizli??i de anemiye neden olur ve sinir sisteminde geri d??n?????? olmayan zararlar verir. Vejetaryen yeti??kinler, b??y??me ??a????ndaki ??ocuk ve gen??ler kalsiyumun iyi kaynaklar?? olan s??t ve ??r??nlerini yetersiz t??kettiklerinde kemik sa??l??klar?? riske girecektir. Besin ??e??itlili??i sa??lanamad?????? ve B12 vitamini gereksinimini kar????layacak kadar yumurta ve s??t gibi hayvansal kaynakl?? besinler t??ketilmedi??inde homosistein y??kselir. Homosistein seviyesinin y??kselmesi ise kalp damar hastal??klar?? i??in bir risk fakt??r??d??r. Dikkatli uygulanmazsa protein, demir, B12 vitamini, ??inko, kalsiyum gibi ??ok ??nemli maddelerin eksiklik riski bulunur."
        )
        DietCesitList.add(VejeteryanDiyet)
        val AkdenizDiyeti = DietCesit(
            R.drawable.akdenizdiyeti,
            "Akdeniz Diyeti Nedir?",
            "Akdeniz Diyeti",
            "Akdeniz tipi beslenme, genelde sebze ve meyve a????rl??kl?? bir beslenme ??eklidir. Akdeniz diyetinin temelinde ise zeytinya????, peynir, sebze ve meyveler, bal??k, tah??l ve f??nd??k bulunmaktad??r. Ceviz, badem, yo??urt ve tam tah??llar da bolca t??ketilir. Akdeniz diyeti damak tad?? a????s??ndan da zengindir. ????nk?? bu beslenme ??ekli k??rm??z?? ete daha az yer verirken, sebze, meyve, tah??l ve bal????a daha ??ok yer vermektedir. Akdeniz diyetinde zeytinya???? da ??nemli bir yere sahiptir. Yumurta haftada en fazla 4 kere verilmektedir. Bal temel tatland??r??c??d??r."
        )
        DietCesitList.add(AkdenizDiyeti)
    }
    fun getKategoriFromFirestore() {
        kategoriArrayList.clear()
        db.collection("Kategoriler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    kategoriArrayList.add(
                        YemekKategori(
                            document.getId(),
                            document.getString("Kategoriisim").toString(),
                            document.getString("kategoriresim").toString()
                        )
                    )
                    println(kategoriArrayList.size)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)
            }


    }
    fun diyetgetir() {
        db.collection("Diyetler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    diyetlerArrayList.add(
                        Diyet(
                            document.getId().toString(),
                            document.getString("aksamogun1").toString(),
                            document.getString("aksamogun2").toString(),
                            document.getString("aksamogun3").toString(),
                            document.getString("araogun1").toString(),
                            document.getString("araogun2").toString(),
                            document.getString("not").toString(),
                            document.getString("ogleogun1").toString(),
                            document.getString("ogleogun2").toString(),
                            document.getString("ogleogun3").toString(),
                            document.getString("sabahogun1").toString(),
                            document.getString("sabahogun2").toString(),
                            document.getString("sabahogun3").toString(),
                            document.getString("hangigun").toString(),
                            document.getString("kimeait").toString()
                        )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }
    }
}




