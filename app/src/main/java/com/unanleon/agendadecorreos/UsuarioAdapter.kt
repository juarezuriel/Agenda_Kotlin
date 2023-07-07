package com.unanleon.agendadecorreos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*
import kotlin.collections.ArrayList

// Adaptador para el RecyclerView que muestra la lista de usuarios
class UsuarioAdapter(
    private val context: Context,
    private var listaUsuarios: ArrayList<Usuario>
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>(), Filterable {

    // Interfaz para manejar los eventos de clic en los elementos del RecyclerView
    private var onClick: OnItemClicked? = null
    private var listaUsuariosCompleta: ArrayList<Usuario> = ArrayList(listaUsuarios)

    // Método que se llama cuando se crea un nuevo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        // Se infla el diseño de item_rv_usuario para cada elemento del RecyclerView
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_usuario, parent, false)
        return UsuarioViewHolder(vista)
    }

    // Método que se llama para asociar los datos de un usuario con un ViewHolder
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        // Se asignan los datos del usuario a los elementos de la vista del ViewHolder
        holder.tvIdUsuario.text = usuario.idUsuario.toString()
        holder.tvNombre.text = usuario.nombre
        holder.tvEmail.text = usuario.email

        // Manejadores de eventos para los botones de editar y borrar
        holder.btnEditar.setOnClickListener {
            onClick?.editarUsuario(usuario)
        }

        holder.btnBorrar.setOnClickListener {
            onClick?.borrarUsuario(usuario.idUsuario)
        }
    }

    // Método que devuelve el número de elementos en la lista de usuarios
    override fun getItemCount(): Int {
        return listaUsuarios.size
    }

    // ViewHolder para cada elemento del RecyclerView
    inner class UsuarioViewHolder(itemView: View) : ViewHolder(itemView) {
        val tvIdUsuario: TextView = itemView.findViewById(R.id.tvIdUsuario)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
        val btnBorrar: Button = itemView.findViewById(R.id.btnBorrar)
    }

    // Interfaz para manejar los eventos de clic en los elementos del RecyclerView
    interface OnItemClicked {
        fun editarUsuario(usuario: Usuario)
        fun borrarUsuario(idUsuario: Int)
    }

    // Método para asignar el manejador de eventos del adaptador
    fun setOnClick(onClick: OnItemClicked?) {
        this.onClick = onClick
    }

    // Implementación de la interfaz Filterable para realizar la filtración de la lista de usuarios
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchText = constraint?.toString()?.toLowerCase(Locale.getDefault())
                val filterResults = FilterResults()

                if (searchText.isNullOrEmpty()) {
                    // Si no hay texto de búsqueda, se muestra la lista completa de usuarios
                    filterResults.count = listaUsuariosCompleta.size
                    filterResults.values = listaUsuariosCompleta
                } else {
                    // Si hay texto de búsqueda, se filtran los usuarios según el nombre
                    val filteredList = ArrayList<Usuario>()
                    for (usuario in listaUsuariosCompleta) {
                        if (usuario.nombre.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            filteredList.add(usuario)
                        }
                    }
                    filterResults.count = filteredList.size
                    filterResults.values = filteredList
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Se actualiza la lista de usuarios filtrada y se notifica el cambio
                val filteredList = results?.values as? ArrayList<Usuario>
                if (filteredList != null) {
                    listaUsuarios = filteredList
                    notifyDataSetChanged()
                }
            }
        }
    }

    // Método para establecer la lista de usuarios y actualizar el adaptador
    fun setUsuarios(usuarios: ArrayList<Usuario>) {
        listaUsuarios = usuarios
        listaUsuariosCompleta = ArrayList(usuarios)
        notifyDataSetChanged()
    }
}
