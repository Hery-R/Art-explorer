package fr.alexis_hery.artexplorer

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import fr.alexis_hery.artexplorer.request.OeuvreRequest
import java.io.File

class OeuvrePopup(
    private val context: Context,
    private val oeuvre : OeuvreModel
) : Dialog(context){

    // la popup va s'afficher dans le contexte de notre adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_oeuvre)
        setupComponents()
    }

    // fonction qui met à jour les infos de la popup avec celles de l'oeuvre sélectionnée
    private fun setupComponents() {
        // image
        val image = findViewById<ImageView>(R.id.oeuvre_image)
        val imageFile = File(context.filesDir, oeuvre.image)

        if (imageFile.exists()) {
            // Charger l'image depuis le fichier
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            // Afficher l'image dans l'ImageView
            image.setImageBitmap(bitmap)
        }

        // titre
        val title = findViewById<TextView>(R.id.oeuvre_title)
        title.text = oeuvre.name

        // description
        val description = findViewById<TextView>(R.id.oeuvre_description)
        description.text = oeuvre.description

        // type
        val type = findViewById<TextView>(R.id.oeuvre_type)
        type.text = oeuvre.type

        // fermer la popup
        val closeBtn = findViewById<ImageView>(R.id.close_popup)
        closeBtn.setOnClickListener {
            dismiss()
        }

        // supprimer l'oeuvre d'art
        val delOeuvre = findViewById<ImageView>(R.id.delete_oeuvre)
        delOeuvre.setOnClickListener {
            OeuvreRequest(context).deleteOeuvre(oeuvre.id)
            dismiss()
            Toast.makeText(context, "Oeuvre supprimée avec succès", Toast.LENGTH_SHORT).show()
        }
    }

}