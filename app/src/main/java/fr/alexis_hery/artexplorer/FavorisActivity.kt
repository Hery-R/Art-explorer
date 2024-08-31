package fr.alexis_hery.artexplorer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.alexis_hery.artexplorer.adapter.OeuvreAdapter
import fr.alexis_hery.artexplorer.request.OeuvreRequest

class FavorisActivity : AppCompatActivity() {

    // fonction qui va charger les données du Recycler View
    private fun loadRecyclerView(data: OeuvreRequest){
        // liste des oeuvres
        var lstOeuvres = data.loadJsonData()
        lstOeuvres = ArrayList(lstOeuvres.filter { it.liked })

        // récupérer le recycler view
        val oeuvreRecyclerView = findViewById<RecyclerView>(R.id.lst_oeuvres)

        // créer la vue pour chaque élément
        oeuvreRecyclerView.adapter = OeuvreAdapter(applicationContext, lstOeuvres)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoris)

        val data = OeuvreRequest(this)
        data.getOeuvres { loadRecyclerView(data) }

        // la page "Favoris" est sélectionnée
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.menu.getItem(1).isChecked = true

        // changer de page
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.home_page -> {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}