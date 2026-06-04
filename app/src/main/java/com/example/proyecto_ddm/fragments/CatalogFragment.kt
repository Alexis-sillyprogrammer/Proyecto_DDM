package com.example.proyecto_ddm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_ddm.R
import com.example.proyecto_ddm.adapters.ProductAdapter
import com.example.proyecto_ddm.models.Product

class CatalogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño que armamos antes
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        // Configurar el RecyclerView
        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        rvProducts.layoutManager = LinearLayoutManager(requireContext())

        // Crear una lista de prueba para mostrar en el avance
        val mockProducts = listOf(
            Product(1, "Consola PlayStation 5", "Consola", 12999.00),
            Product(2, "Control DualSense", "Accesorio", 1599.00),
            Product(3, "Nintendo Switch OLED", "Consola", 6999.00),
            Product(4, "Resident Evil 4 Remake", "Videojuego", 1199.00),
            Product(5, "New Nintendo 3DS XL", "Consola Retro", 4500.00),
            Product(6, "The Legend of Zelda: Tears of the Kingdom", "Videojuego", 1399.00)
        )

        // Conectar los datos con el adaptador y la lista
        val adapter = ProductAdapter(mockProducts)
        rvProducts.adapter = adapter

        return view
    }
}