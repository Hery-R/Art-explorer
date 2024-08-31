package fr.alexis_hery.artexplorer.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import fr.alexis_hery.artexplorer.OeuvreModel
import fr.alexis_hery.artexplorer.OeuvrePopup
import fr.alexis_hery.artexplorer.R
import fr.alexis_hery.artexplorer.request.OeuvreRequest
import java.io.File

class OeuvreAdapter(
    val context: Context,
    private val lstOeuvres: List<OeuvreModel>
    ) : RecyclerView.Adapter<OeuvreAdapter.ViewHolder>() {

    // représentation d'un item "oeuvre d'art"
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val colorBubble = view.findViewById<LinearLayout>(R.id.item_container) // couleur de l'oeuvre
        val image = view.findViewById<ImageView>(R.id.oeuvre_image)            // image de l'oeuvre
        val name = view.findViewById<TextView>(R.id.oeuvre_title)              // titre de l'oeuvre
        val type = view.findViewById<TextView>(R.id.oeuvre_type)               // type d'oeuvre
        var liked = view.findViewById<ImageView>(R.id.star_icon)               // si l'oeuvre est likée
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_oeuvre, parent, false)

        return ViewHolder(view)
    }

    // fonction qui détermine la couleur de l'item
    private fun selectColor(oeuvreType: String) : Int{
        var color : Int = ContextCompat.getColor(context, R.color.white)

        when (oeuvreType) {
            "Peinture" -> {
                color = ContextCompat.getColor(context, R.color.lightblue)
            }
            "Sculpture" -> {
                color = ContextCompat.getColor(context, R.color.lightgreen)
            }
            "Cinéma" -> {
                color = ContextCompat.getColor(context, R.color.lightpurple)
            }
            "Architecture" -> {
                color = ContextCompat.getColor(context, R.color.grey)
            }
            "Jeu-vidéo" -> {
                color = ContextCompat.getColor(context, R.color.red)
            }
            "Musique" -> {
                color = ContextCompat.getColor(context, R.color.orange)
            }
        }

        return color
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oeuvre = lstOeuvres[position] // oeuvre courante


        // mettre à jour l'image
        val imageFile = File(context.filesDir, oeuvre.image)

        if (imageFile.exists()) {
            // Charger l'image depuis le fichier
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            // Afficher l'image dans l'ImageView
            holder.image.setImageBitmap(bitmap)
        }

        holder.name.text = oeuvre.name               // mettre à jour le nom
        holder.type.text = oeuvre.type               // mettre à jour le type

        // mettre à jour l'icône d'étoile si l'oeuvre est likée ou pas
        if(oeuvre.liked){
            holder.liked.setImageResource(R.drawable.ic_star)
        }
        else{
            holder.liked.setImageResource(R.drawable.ic_unstar)
        }

        // mettre à jour la couleur de la bulle
        holder.colorBubble.setBackgroundColor(selectColor(oeuvre.type))

        // afficher la popup
        holder.colorBubble.setOnClickListener {
            OeuvrePopup(holder.colorBubble.context, oeuvre).show()
        }

        // liker ou disliker l'oeuvre
        holder.liked.setOnClickListener {
            OeuvreRequest(context).likeOrDislike(oeuvre.id)
            oeuvre.liked = !oeuvre.liked
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = lstOeuvres.size
}