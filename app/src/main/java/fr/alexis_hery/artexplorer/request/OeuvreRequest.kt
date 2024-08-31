package fr.alexis_hery.artexplorer.request

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import fr.alexis_hery.artexplorer.OeuvreModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class OeuvreRequest(private val context: Context) {

    val lstOeuvres: ArrayList<OeuvreModel> = ArrayList()

    companion object {
        private const val URL = "http://51.68.91.213/gr-2-9/Data.json"
    }

    // fonction qui prend une image depuis internet, et l'enregistre en local
    fun getAndUploadImage(name: String){
        val fileWithoutExtension = name.substring(0, name.lastIndexOf('.'))

        // Enregistrer l'image localement
        val resourceId = context.resources.getIdentifier(fileWithoutExtension, "drawable", context.packageName)
        if (resourceId == 0) {
            Log.e("RESSOURCE NULLE", "J'ai pas trouvé l'image")
            return
        }

        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

        val outputFile = File(context.filesDir, name)

        try {
            val fileOutputStream = FileOutputStream(outputFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // fonction qui récupère toutes les images depuis internet
    private fun getImages(jsonArray: JSONArray){

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val image = jsonObject.getString("image")

            getAndUploadImage(image)
        }
    }

    // fonction qui récupère les oeuvres depuis internet (ou alors en local si le fichier existe déjà)
    fun getOeuvres(callback : () -> Unit){
        val file = File(context.filesDir, "Data.json")
        if (file.exists()) {
            // Charger les données à partir du fichier local
            callback()
        }
        else{
            val queue = Volley.newRequestQueue(context)
            val request = JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                {response ->
                    val oeuvreTab = response.getJSONArray("Data")
                    saveJsonData(oeuvreTab)
                    getImages(oeuvreTab)
                    callback()
                },
                {error ->
                    Toast.makeText(context, "Requête échouée", Toast.LENGTH_SHORT).show()

                }
            )
            queue.add(request)
            queue.start()
        }
    }

    // fonction qui like ou dislike une oeuvre
    fun likeOrDislike(oeuvreId: Int){
        // Lire le fichier JSON existant
        val fileInputStream = context.openFileInput("Data.json")
        val jsonString = fileInputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        // Chercher et mettre à jour l'élément avec l'id donné
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val currentId = jsonObject.getInt("id")
            if (currentId == oeuvreId) {
                // Inverser la valeur de l'attribut "liked"
                val currentLiked = jsonObject.getBoolean("liked")
                jsonObject.put("liked", !currentLiked)
                break
            }
        }

        // Réécrire le fichier JSON mis à jour
        saveJsonData(jsonArray)
    }

    // fonction qui sauvegarde les données modifiées dans le fichier json
    fun saveJsonData(jsonArray: JSONArray) {
        try {
            val fileOutputStream = context.openFileOutput("Data.json", Context.MODE_PRIVATE)
            fileOutputStream.write(jsonArray.toString().toByteArray())
            fileOutputStream.close()
        } catch (e: Exception) {
            Toast.makeText(context, "Erreur lors de la mise à jour des données", Toast.LENGTH_SHORT).show()
        }
    }

    // fonction qui récupère les données du json local
    fun loadJsonData(): ArrayList<OeuvreModel> {
        val res: ArrayList<OeuvreModel> = ArrayList()

        try {
            val fileInputStream = context.openFileInput("Data.json")
            val jsonString = fileInputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")
                val image = jsonObject.getString("image")
                val name = jsonObject.getString("name")
                val description = jsonObject.getString("description")
                val type = jsonObject.getString("type")
                val liked = jsonObject.getBoolean("liked")

                val oeuvre = OeuvreModel(id, image, name, description, type, liked)
                res.add(oeuvre)
            }
        }
        catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        return res
    }


    // fonction qui récupère l'id maximal du fichier local
    private fun maxId(jsonArray: JSONArray) : Int{
        var res = 0
        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getInt("id")
            if(id > res){ res = id }
        }
        return res+1
    }


    // fonction qui enregistre une nouvelle image en local
    private fun uploadImage(imageSrc: ImageView, name: String){
        // convertir l'ImageView en Bitmap
        val bitmap: Bitmap = Bitmap.createBitmap(imageSrc.width, imageSrc.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        imageSrc.draw(canvas)

        // chemin du fichier
        val file = File(context.filesDir, "$name.png")

        try {
            // Écrire le Bitmap dans un fichier PNG
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        }
        catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // fonction qui enregistre une nouvelle oeuvre d'art en local
    fun uploadOeuvre(imageSrc: ImageView, name: String, desc: String, type: String, callback : () -> Unit){
        // enregistrer l'image
        uploadImage(imageSrc, name)

        // récupérer le fichier JSON
        val fileInputStream = context.openFileInput("Data.json")
        val jsonString = fileInputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        // ajouter la nouvelle oeuvre d'art
        val nouvelElement = JSONObject()

        // récupérer l'id maximal
        val id = maxId(jsonArray)

        nouvelElement.put("id", id)
        nouvelElement.put("image", "$name.png")
        nouvelElement.put("name", name)
        nouvelElement.put("description", desc)
        nouvelElement.put("type", type)
        nouvelElement.put("liked", false)

        jsonArray.put(nouvelElement)

        // écrire le nouveau fichier
        saveJsonData(jsonArray)

        callback()
    }

    // fonction qui permet de supprimer localement une oeuvre d'art
    fun deleteOeuvre(oeuvreId: Int){
        val res = JSONArray()

        // récupérer le fichier JSON
        val fileInputStream = context.openFileInput("Data.json")
        val jsonString = fileInputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val jsonObject: JSONObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getInt("id")
            if(id != oeuvreId) res.put(jsonObject)
        }

        // Réécrire le fichier JSON mis à jour
        saveJsonData(res)
    }
}