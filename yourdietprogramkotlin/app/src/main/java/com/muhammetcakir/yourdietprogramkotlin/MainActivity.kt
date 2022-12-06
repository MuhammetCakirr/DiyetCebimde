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
import android.os.Handler
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.muhammetcakir.yourdietprogramkotlin.Adapters.Views.*
import com.muhammetcakir.yourdietprogramkotlin.ApiServisleri.TimeApi
import com.muhammetcakir.yourdietprogramkotlin.ApiServisleri.TimeTurkey
import com.muhammetcakir.yourdietprogramkotlin.Models.*
import com.muhammetcakir.yourdietprogramkotlin.Views.EditProfile
import com.muhammetcakir.yourdietprogramkotlin.Views.KullanicilarArrayList
import com.muhammetcakir.yourdietprogramkotlin.Views.SplashScreen
import com.muhammetcakir.yourdietprogramkotlin.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


var retrofit:Retrofit?=null
var timeApi:TimeApi?=null
val baseurl:String="http://worldtimeapi.org/api/timezone/"
var timeTurkeycall: Call<TimeTurkey>?=null
var timeTurkey:TimeTurkey?=null
var timeTurkey2:TimeTurkey?=null
val diyetArrayList : ArrayList<Diyet> = ArrayList()
val bkidiyetArrayList : ArrayList<Diyet> = ArrayList()
val diyetbelirlemeList : ArrayList<DiyetBelirleme> = ArrayList()
var kisibki:Double?=null
var db : FirebaseFirestore=FirebaseFirestore.getInstance()
private  var auth : FirebaseAuth=FirebaseAuth.getInstance()
val suankikullanicilist : ArrayList<User> = ArrayList()
val yenikikullanicilist : ArrayList<User> = ArrayList()
var currentUser = auth.currentUser

class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        val view = binding.root
        setContentView(view)
        retrofitsettings()
        getkullaniciFromFirestore()
        registerLauncher()
        yemekgetir()
        diyetolustur()
        populateDiets()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("817128577458-9ft18ssml32b7i8a7583u7rth20dks6e.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        //Google ile giriş butonu
        binding.googlegiris.setOnClickListener(){
            signIn()
        }


       /* if (currentUser != null)
        {
           if (currentUser!!.email.toString()=="mami1@gmail.com")
            {
               val intent = Intent(applicationContext, SplashScreen::class.java)
               startActivity(intent)
           }
          //  intent = Intent(applicationContext, SplashScreen::class.java)
                //startActivity(intent)
        }*/

        //Kayıt ol tarafına geçince
        binding.singUp.setOnClickListener {
            binding.singUp.background = resources.getDrawable(R.drawable.switch_trcks, null)
            binding.singUp.setTextColor(resources.getColor(R.color.textColor, null))
            binding.logIn.background = null
            binding.singUpLayout.visibility = View.VISIBLE
            binding.logInLayout.visibility = View.GONE
            binding.logIn.setTextColor(resources.getColor(R.color.white, null))
            binding.yada.visibility = View.GONE
            binding.girisyapbutton.text = "Kayıt Ol"

            binding.googlegiris.visibility = View.GONE
            binding.girisyapbutton.visibility=View.GONE
            binding.kayitolbutton.visibility=View.VISIBLE

        }

        //Giriş Yap tarafına geçince
        binding.logIn.setOnClickListener {
            binding.logIn.setTextColor(resources.getColor(R.color.white, null))
            binding.singUp.background = null
            binding.singUp.setTextColor(resources.getColor(R.color.white, null))
            binding.logIn.background = resources.getDrawable(R.drawable.switch_trcks, null)
            binding.singUpLayout.visibility = View.GONE
            binding.logInLayout.visibility = View.VISIBLE
            binding.logIn.setTextColor(resources.getColor(R.color.textColor, null))
            binding.girisyapbutton.text = "Giriş Yap"
            binding.yada.visibility = View.VISIBLE

            binding.googlegiris.visibility = View.VISIBLE
            binding.girisyapbutton.visibility=View.VISIBLE
            binding.kayitolbutton.visibility=View.GONE
        }

        //Giriş yap butonu tıklanınca
        binding.girisyapbutton.setOnClickListener {
            signInClicked(view)
                startActivity(Intent(this@MainActivity, SplashScreen::class.java))
        }

        //Kayıt Ol butonu tıklanınca
        binding.kayitolbutton.setOnClickListener(){
                uploadClicked(view)
                signUpClicked(view)
            val intent = Intent(applicationContext, SplashScreen::class.java)
            startActivity(intent)
        }
        //Fotoğraf Seç butonu
        binding.fotosec.setOnClickListener(){
            imageViewClicked(view)
            fotografseciniz.visibility=View.GONE
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
            val intent = Intent(applicationContext, EditProfile::class.java)
            intent.putExtra(EXTRA_NAME, user.displayName)
            startActivity(intent)
        }
    }
    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA_NAME"
    }


    private fun retrofitsettings() {
        retrofit=Retrofit.Builder().baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

       timeApi= retrofit!!.create<TimeApi>()
        timeTurkeycall=timeApi!!.getTime()

        timeTurkeycall!!.enqueue(object : Callback<TimeTurkey?> {
            override fun onResponse(call: Call<TimeTurkey?>, response: Response<TimeTurkey?>) {
                if (response.isSuccessful) {
                    timeTurkey = response.body()
                    timeTurkey2=response.body()
                    if(timeTurkey!!.dayOfWeek=="1")
                    {
                        timeTurkey!!.dayOfWeek="Pazartesi Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="2")
                    {
                        timeTurkey!!.dayOfWeek="Salı Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="3")
                    {
                        timeTurkey!!.dayOfWeek="Çarşamba Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="4")
                    {
                        timeTurkey!!.dayOfWeek="Perşembe Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="5")
                    {
                        timeTurkey!!.dayOfWeek="Cuma Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="6")
                    {
                        timeTurkey!!.dayOfWeek="Cumartesi Günü İçin Öneri Yemekler"
                    }
                    else if (timeTurkey!!.dayOfWeek=="7")
                    {
                        timeTurkey!!.dayOfWeek="Pazar Günü İçin Öneri Yemekler"
                    }
                }
            }

            override fun onFailure(call: Call<TimeTurkey?>, t: Throwable) {
                println(t.toString())
            }
        })
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
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this@MainActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
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

                    val postMap = hashMapOf<String,Any>()
                    postMap.put("ImageUrl",downloadUrl)
                    postMap.put("email",auth.currentUser!!.email.toString())
                    postMap.put("isim",binding.kayitolisim.text.toString())
                    postMap.put("sifre",binding.kayitolsifre.text.toString())


                    db.collection( "Users").document(currentUser!!.uid.toString()).set(postMap).addOnCompleteListener{ task ->

                        if (task.isComplete && task.isSuccessful) {
                            //back
                        }

                    }.addOnFailureListener{exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }


                }

            }

        }


    }
    fun signInClicked(view : View) {
        val userEmail = binding.girisyapemail.text.toString()
        val password = binding.girisyapsifre.text.toString()

        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(userEmail,password).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Toast.makeText(applicationContext,"Hoşgeldin: ${auth.currentUser?.email.toString()}",Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
    fun signUpClicked(view : View) {
        val userEmail = binding.kayitolemail.text.toString()
        val password = binding.kayitolsifre.text.toString()
        val isim=binding.kayitolisim.text.toString()
        if (userEmail.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(userEmail,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    yenikikullanicilist.add(User("1",userEmail,password,isim,selectedPicture.toString(),"0","0"))

                }

            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun yemekgetir()
    {
        db.collection("Yemekler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    yemekList.add(
                       Yemek(
                           document.getId().toString(),document.getString("ImageUrl").toString(),
                           document.getString("yemekismi").toString(),document.getString("aciklamasi").toString(),
                           document.getString("icindekiler").toString(),document.getString("karbmiktari").toString(),
                           document.getString("proteinmiktari").toString(),document.getString("yagmiktari").toString(),
                           document.getString("toplamkalori").toString(),document.getString("yapimsuresi").toString(),
                           document.getString("kategoriid").toString(),
                       )
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "Error getting documents: ", exception)


            }

    }

    fun diyetolustur()
    {
        val diyet1=Diyet(
            "1",
            "1 el ayası büyüklüğünde ızgara Dana biftek",
            "Mevsim yağsız salata",
            "Elma sirkeli salata",
            "1 avuç beyaz leblebi + 1 bardak kefir",
            "5 adet kuru erik + 10 badem",
            "Bolca su iç!",
            "1 orta boy kepçe çorba",
            "Mevsim yeşillikleri",
            "4 yemek kaşığı light yoğurt",
            "1 ince dilim tam tahıllı ekmek",
            "2 yemek kaşığı lor",
            "1 fincan şekersiz yeşil çay",
            "Hergün",
        )
        diyetArrayList.add(diyet1)
        val diyet2=Diyet(
            "2",
            "½ porsiyon ızgara balık",
            "Elma sirkeli yağsız salata",
            "Mevsim yağsız salata",
            "1 kutu meyveli yoğurt + 10 fındık",
            "4-5 adet kuru erik + 2 adet tüm ceviz",
            "Bolca su iç!",
            "Haşlanmış sebze",
            "4 yemek kaşığı light yoğurt",
            "1 Bardak şekersiz çay",
            "1 adet kepekli galeta",
            "1 dilim yarım yağlı beyaz peynir",
            "Mevsim yeşillikleri",
            "Hergün",
        )
        diyetArrayList.add(diyet2)
        val diyet3=Diyet(
            "3",
            "1 el ayası büyüklüğünde ızgara Dana biftek",
            "Mevsim yağsız salata",
            "Elma sirkeli salata",
            "1 avuç beyaz leblebi + 1 bardak kefir",
            "5 adet kuru erik + 10 badem",
            "Bolca su iç!",
            "1 orta boy kepçe çorba",
            "Mevsim yeşillikleri",
            "4 yemek kaşığı light yoğurt",
            "1 ince dilim tam tahıllı ekmek",
            "1 adet haşlanmış yumurta",
            "1 fincan şekersiz yeşil çay",
            "Hergün",
        )
        diyetArrayList.add(diyet3)
        val diyet4=Diyet(
            "4",
            "30 gram kadar haşlama hindi",
            "Elma sirkeli yağsız salata",
            "5 yemek kaşığı pilav",
            "1 porsiyon meyve ve Light Fıstık Ezmesi (1 tatlı kaşığı)",
            "Tarçınlı yarım yağlı süt + 1 yemek kaşığı yulaf kepeği",
            "Bolca su iç!",
            "4 yemek kaşığı zeytinyağlı sebze yemeği",
            "Bol salata",
            "4 yk light yoğurt",
            "1 ince dilim tam tahıllı ekmek",
            "Söğüş sebze",
            "1 dilim light keçi peyniri",
            "Hergün",
        )
        diyetArrayList.add(diyet4)
        val diyet5=Diyet(
            "5",
            "Haşlama sebze",
            "4 yemek kaşığı light yoğurt + 1 tatlı kaşığı nane",
            "Elma sirkeli salata",
            "2 adet grisini + 1 bardak ayran",
            "1 su bardağı yoğurt + 3 tane kuru kayısı",
            "Bolca su iç!",
            "3 yemek kaşığı yağsız Menemen",
            "1 su bardağı sade mineralli maden suyu",
            "4 yemek kaşığı light yoğurt",
            "1 ince dilim tam tahıllı ekmek",
            "2 yemek kaşığı lor",
            "1 fincan şekersiz beyaz çay",
            "Hergün",
        )
        diyetArrayList.add(diyet5)
        val diyet6=Diyet(
            "6",
            "1 orta boy kepçe çorba",
            "Yağsız mevsim salata",
            "4 yemek kaşığı light yoğurt",
            "1 yemek kaşığı kuru üzüm + 6-7 tane badem",
            "1 kutu yarım yağlı süt + buğday ruşeymi",
            "Bolca su iç!",
            "1 el ayası büyüklüğünde ızgara köfte",
            "Bol yağsız salata",
            "Cacık",
            "1 ince dilim tam tahıllı ekmek",
            "1 dilim yağsız beyaz peynir",
            "1 fincan şekersiz biberiye çay",
            "Hergün",
        )
        diyetArrayList.add(diyet6)
        val diyet7=Diyet(
            "7",
            "1 porsiyon ızgara Hindi eti",
            "Elma sirkeli yağsız mevsim salata",
            "1 su bardağı light ayran",
            "5 adet kuru erik + 10 badem",
            "½ simit + 1 yemek kaşığı labne peynir veya karper",
            "Bolca su iç!",
            "2 ince dilim yarım yağlı beyaz peynir",
            "1 adet kepekli grissini",
            "Bol yeşillikli salata",
            "1 dilim sade wasa",
            "2 yemek kaşığı lor",
            "1 fincan şekersiz yeşil çay",
            "Hergün",
        )
        diyetArrayList.add(diyet7)
        val diyet8=Diyet(
            "8",
            "1 orta boy kepçe çorba",
            "Mevsim yağsız salata",
            "Elma sirkeli salata",
            "1 su bardağı yarım yağlı süt + ½ paket yağlı azaltılmış bisküvi",
            "1 kutu meyveli yoğurt + 10 fındık",
            "Bolca su iç!",
            "1 el ayası büyüklüğünde ızgara köfte",
            "Bol salata",
            "1 su bardağı sade mineralli maden suyu",
            "1 ince dilim tam tahıllı ekmek",
            "2 yemek kaşığı lor",
            "1 fincan şekersiz biberiye çay",
            "Hergün",
        )
        diyetArrayList.add(diyet8)
    }

    private fun populateDiets()
    {
        val Dukan = DietCesit(
            R.drawable.dietcesitleri1,
            "Dukan Diyeti Nedir?",
            "Dukan Diyeti",
            "Dukan Diyeti, 4 ana safhaya ayrılan yüksek proteinli ve çok düşük karbonhidratlı bir diyet listesidir. Fransız beslenme uzmanı Dr. Pierre Dukan, Dukan Diyeti’ni, et hariç olmak üzere her şeyi yemekten vazgeçerek kilo vermek isteyen obez bir insandan esinlenerek yazmıştır. Diyet, 4 aşamadan oluşmaktadır: Atak evresi, seyir evresi, güçlendirme evresi ve koruma evresi.\n" +
                    "Atak evresi (1 – 7 gün): Diyete, sınırsız yağsız protein ve güne 1.5 yemek kaşığı yulaf yiyerek başlanır. Kısa, hızlı ve hemen sonuç alınan aşamadır.\n" +
                    "Seyir evresi (1 – 12 ay): Bir gün yağsız proteinler, günde 2 yemek kaşığı yulaf yiyerek, nişasta bulunmayan sebzeler yiyerek devam edilir. Bu aşama gerçek ağırlık kaybının yaşandığı aşamadır.\n" +
                    "Güçlendirme evresi (Değişken): Sınırsız yağsız protein ve sebzeler, bazı karbonhidratlar ve yağlar, haftada sadece bir gün yağsız protein ve günde 2.5 yemek kaşığı yulaf kepeği yenilen, süresi değişkenlik gösterebilen bir aşamadır.\n" +
                    "Koruma evresi (Belirsiz): Temel kurallara uyulur, ancak ağırlık sabit kaldığı sürece kurallar gevşetilebilir. Yulaf kepeği günde 3 yemek kaşığına çıkartılabilir."
        )
        DietCesitList.add(Dukan)
        val HollywoodDiyeti = DietCesit(
            R.drawable.holywooddiet,
            "Hollywood Diyeti Nedir?",
            "Hollywood Diyeti",
            "Hollywood Diyeti, 3 ana ve 1 ara öğün önermektedir. Şekerli yiyecekler kesinlikle tüketilmemelidir. Saat 20:00’den itibaren kesinlikle bir şey yenilmemelidir. Yenilen besin sadece bir tabak tüketilmelidir. Hollywood Diyeti’nde patates, pirinç, havuç, muz, ananas, işlenmiş şeker, işlenmiş tüm beyaz un ürünleri, alkol, bal ve dondurma yer almamaktadır."
        )
        DietCesitList.add(HollywoodDiyeti)
        val KetojenikDiyet = DietCesit(
            R.drawable.ketojonikdiet,
            "Ketojenik Diyet Nedir",
            "Ketojenik Diyet",
            "Ketojenik diyet, tıpta öncelikle çocuklarda kontrol edilmesi zor yani refrakter epilepsiyi tedavi etmek için kullanılan yüksek yağlı besinler ile yeter miktarda protein içeren besinlerin öncelikli tüketildiği, düşük karbonhidratlı bir diyettir. Ketojenik diyetin halk arasındaki bir diğer adı da keto diyettir. \n" +
                    "Gerçekleştirilen sınırlı tıbbi çalışmalar sonucunda ketojenik diyet sürecinde aralarında öncelikle epilepsi olmak üzere Alzheimer, diyabet ve kanser gibi birtakım hastalıklara ve sağlık sorunlarına karşı faydalarının yanı sıra kilo verilmesinde yardımcı olduğu görülmüştür. \n" +
                    "\n" +
                    "Ancak uzun süre kesintisiz devam ettirilmesi sağlık açısından tehlikeli sonuçlara yol açabilir. Yapılan araştırmalar tıp uzmanları tarafından henüz yeterli görülecek çapta gerçekleştirilmediği için ketojenik diyete başlama ve diyeti sürdürme kararlarının dikkatli bir şekilde, ideal şartlar altında mutlaka bir diyetisyene başvurarak alınması tavsiye edilmektedir.\n" +
                    "\n" +
                    "Ketojenik diyetin epilepsi tedavisi için kullanıldığı araştırmalarda vakaların yarısından fazlasında epilepsi nöbetlerinde farklı oranlarda azalma görülmüştür. Hastaların belirli bir yüzdesinde ketojenik diyet, Atkins diyeti ve diğer düşük karbonhidrat diyetleri ile birçok benzerliğe sahiptir. \n" +
                    "\n" +
                    "Ketojenik diyetin temelinde vücudun dışarıdan karbonhidrat alımını büyük ölçüde azaltmak vardır. Karbonhidrat alımındaki bu azalma vücudu ketoz adı adı verilen metabolik bir duruma sokar. Ketoz durumunda vücut enerji için yağ yakmada çok daha verimli bir hale gelir ve vücut enerji ihtiyacını karbonhidratlar yerine yağlardan karşılamaya başlar. \n" +
                    "Bu süreç dahilinde yağ karaciğerde ketonlara dönüştürülür ve bunlar beyin için dahi enerji sağlamak üzere kullanılabilir. Ketojenik diyetler hem kan şekeri değerlerinde hem de insülin seviyelerinde büyük bir düşmeye neden olabilir. "
        )
        DietCesitList.add(KetojenikDiyet)
        val İsvecDiyeti = DietCesit(
            R.drawable.isvecdiet,
            "İsveç Diyeti Nedir?",
            "İsveç Diyeti",
            "İsveç Diyeti, tamamen protein tüketmeye odaklanmış bir diyet türüdür. İsveç Diyeti’nin hedefi özellikle yüksek protein tüketme ile metabolizmayı hızlandırma ve buna bağlı olarak ani kilo verdirme üzerinedir. Bu diyette kesinlikle tüketilmemesi gereken besinler vardır. Bunların başında gazlı içecekler gelmektedir.\n" +
                    "İsveç Diyeti’nin en önemli özelliği 6 günden az, 13 günden fazla uygulanamamasıdır. Bu diyetin önemli özelliği çok az kalori alınması ve buna bağlı olarak metabolizmayı hızlandırıcı besinler içermesidir. Örnek vermek gerekirse çay yasaktır, ama çayın yerine yağsız kahve içilebilir. Protein grubu dediğimiz et, tavuk ve balık grubuna ağırlık verilmiştir. Öğlen ya da akşam öğünlerinde yine güne göre değişmekle birlikte protein tüketimi olmaktadır."
        )
        DietCesitList.add(İsvecDiyeti)
        val KaratayDiyeti = DietCesit(
            R.drawable.karataydiyeti,
            "Karatay Diyeti Nedir?",
            "Karatay Diyeti",
            "Karatay Diyeti’nde tüm şeker ve şekerli gıdalar, tatlandırıcılar, diyabetik ürünler, tahıl unu ve bunlar ile hazırlanmış besinler, ekmek, kavrulmuş kuruyemişler, pişmiş havuç, patates, pirinç, üzüm, kavun, karpuz, incir, hazır alınan tavuk, salam, sosis, sucuk ve yumurta, diyet ve light içecekler dahil her türlü meşrubat, neskafe, alkollü içecekler, meyve içerikli yoğurt, öğütülmüş tahıl, ayçiçek yağı, hazır katı yağlar, mısırözü yağı yasaktır." +
                    "Glisemik indeksi düşük besinler tüketilmelidir. Doğal tavuk, işlenmemiş doğal ürünler, pastırma ve doğal yumurta, evde mayalanmış yoğurt, ev yoğurdu ile hazırlanmış ayran, soda, türk kahvesi ve filtre kahve tüketilmelidir. Fındık yağı, zeytinyağı ve tereyağı tüketilmelidir. Öğünlerden doymadan kalkmak ve ara öğün yapmak yasaktır. Salata, sebze, bakliyat, et ve balığın yer aldığı Karatay Diyeti’nde akşam 19:00’dan sonra atıştırmak yasaktır. Bu saatlerden sonra yalnızca bitki çayı içilebilir. Günde en az 2 litre su içilmeli, 30-45 dakika arası yürüyüş yapılmalıdır."
        )
        DietCesitList.add(KaratayDiyeti)
        val MontignacDiyeti = DietCesit(
            R.drawable.montignacdiet,
            "Montignac Diyeti Nedir?",
            "Montignac Diyeti",
            "Hollywood Diyeti, 3 ana ve 1 ara öğün önermektedir. Şekerli yiyecekler kesinlikle tüketilmemelidir. Saat 20:00’den itibaren kesinlikle bir şey yenilmemelidir. Yenilen besin sadece bir tabak tüketilmelidir. Hollywood Diyeti’nde patates, pirinç, havuç, muz, ananas, işlenmiş şeker, işlenmiş tüm beyaz un ürünleri, alkol, bal ve dondurma yer almamaktadır."
        )
        DietCesitList.add(MontignacDiyeti)
        val VejeteryanDiyet = DietCesit(
            R.drawable.vejetaryandiyet,
            "Vejeteryan Diyeti Nedir?",
            "Vejeteryan Diyeti",
            "Lakto vejetaryen diyetinde bitkisel besinlerle birlikte, hayvansal kaynaklı besinlerden süt ve süt ürünlerini tüketilir. Ova vejetaryen diyetinde bitkisel besinlerle birlikte yumurta da yer alır. Bunun yanında et ve süt tüketmezler. Lakto-ova vejetaryen diyetinde süt ve yumurta tüketilir.\n" +
                    "Bazı gruplar da etler arasında tercih yaparlar. Bitkisel besinler yanında hayvansal olarak yalnızca kümes hayvanlarını tüketenlere polo vejetaryen, yalnızca deniz ürünlerini tüketenlere peskovejetaryenler denilmektedir. Semi-vejetaryenler ise kırmızı eti tüketmeyen, sınırlı miktarda tavuk ve balık tüketenlerdir. Semi vejetaryenler yumurta, süt ve türevlerini serbestçe tüketirler.\n" +
                    "Vejetaryen diyetler kalp-damar hastalık riskini azaltmaktadır. Hayvansal kaynaklı besinlerin toplam yağ, doymuş yağ ve kolesterol içeriği yüksektir. Koroner kalp hastalığının, et yiyenlerde yemeyenlere göre %30 daha sık görüldüğü bildirilmektedir. Vejetaryen diyeti tüketen bireyler, et içeren diyetle beslenen bireylere oranla daha düşük sıklıkta kansere yakalanmaktadır. Vejetaryen diyeti, baklagiller, ceviz, fındık gibi sert kabuklu meyveler, taze sebze ve meyveler ile saflaştırılmamış tahıl ürünlerinden zengindir. Bu besinler de kansere karşı koruyucu olarak bilinen antioksidan öğelerin (E vitamini, C vitamini, karotenoidler, bioflavonoid ve diğer biyoaktif bileşikler) alımını artırır.\n" +
                    "Vejetaryen bireyler besin çeşitliliklerini iyi ayarlayamazlarsa demir mineralini yetersiz alabilirler. Bunun sonucunda ise kansızlık (anemi) görülmesi kaçınılmazdır. Vejetaryen diyetlerinde özellikle veganlarda B12 vitamini yetersizliği de anemiye neden olur ve sinir sisteminde geri dönüşü olmayan zararlar verir. Vejetaryen yetişkinler, büyüme çağındaki çocuk ve gençler kalsiyumun iyi kaynakları olan süt ve ürünlerini yetersiz tükettiklerinde kemik sağlıkları riske girecektir. Besin çeşitliliği sağlanamadığı ve B12 vitamini gereksinimini karşılayacak kadar yumurta ve süt gibi hayvansal kaynaklı besinler tüketilmediğinde homosistein yükselir. Homosistein seviyesinin yükselmesi ise kalp damar hastalıkları için bir risk faktörüdür. Dikkatli uygulanmazsa protein, demir, B12 vitamini, çinko, kalsiyum gibi çok önemli maddelerin eksiklik riski bulunur."
        )
        DietCesitList.add(VejeteryanDiyet)
        val AkdenizDiyeti = DietCesit(
            R.drawable.akdenizdiyeti,
            "Akdeniz Diyeti Nedir?",
            "Akdeniz Diyeti",
            "Akdeniz tipi beslenme, genelde sebze ve meyve ağırlıklı bir beslenme şeklidir. Akdeniz diyetinin temelinde ise zeytinyağı, peynir, sebze ve meyveler, balık, tahıl ve fındık bulunmaktadır. Ceviz, badem, yoğurt ve tam tahıllar da bolca tüketilir. Akdeniz diyeti damak tadı açısından da zengindir. Çünkü bu beslenme şekli kırmızı ete daha az yer verirken, sebze, meyve, tahıl ve balığa daha çok yer vermektedir. Akdeniz diyetinde zeytinyağı da önemli bir yere sahiptir. Yumurta haftada en fazla 4 kere verilmektedir. Bal temel tatlandırıcıdır."
        )
        DietCesitList.add(AkdenizDiyeti)
    }
}




