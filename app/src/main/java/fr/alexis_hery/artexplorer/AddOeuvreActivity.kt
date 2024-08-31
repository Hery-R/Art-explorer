package fr.alexis_hery.artexplorer

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.alexis_hery.artexplorer.request.OeuvreRequest

class AddOeuvreActivity : ComponentActivity() {

    private var previewImage : ImageView? = null

    // lanceur de l'activité pour choisir une photo
    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                previewImage?.setImageBitmap(bitmap)
            }
        }
    }

    // fonction qui permet de vérifier si l'utilisateur a bien mis la permission
    private fun checkPermission(permission: String): Boolean {
        var res = true
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            }
            res = false
        }
        return res
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_oeuvre)

        // image par défaut
        previewImage = findViewById(R.id.preview_image)

        // récupérer la liste déroulante
        val spinner = findViewById<Spinner>(R.id.type_spinner)

        // liste des items de la liste déroulante
        val itemList = arrayOf<String?>("Architecture", "Sculpture", "Peinture", "Musique", "Cinéma", "Jeu-vidéo")

        // adapter la vue de chaque item
        val mArrayAdapter = ArrayAdapter<Any?>(this, R.layout.spinner_list, itemList)
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = mArrayAdapter

        // retourner à la page d'accueil
        val returnBtn = findViewById<TextView>(R.id.return_button)
        returnBtn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        // charger une image
        val addImage = findViewById<Button>(R.id.add_image_button)
        addImage.setOnClickListener {
            if(checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val pickPhotoIntent = Intent(Intent.ACTION_PICK)
                pickPhotoIntent.type = "image/*"
                pickPhotoLauncher.launch(pickPhotoIntent)
            }
        }

        // envoyer l'oeuvre d'art en local
        val registerBtn = findViewById<Button>(R.id.register_button)
        registerBtn.setOnClickListener {
            // récupérer les données saisies
            val name = findViewById<EditText>(R.id.name_input).text.toString()
            val desc = findViewById<EditText>(R.id.description_input).text.toString()
            val type = findViewById<Spinner>(R.id.type_spinner).selectedItem.toString()

            OeuvreRequest(this).uploadOeuvre(previewImage!!, name, desc, type){
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}