package com.unanleon.agendadecorreos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import android.widget.SearchView
import com.unanleon.agendadecorreos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), UsuarioAdapter.OnItemClicked {

    // Declaración de variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UsuarioAdapter
    private var listaUsuarios = arrayListOf<Usuario>()
    private var usuario = Usuario(-1, "", "")
    private var isEditing = false

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del RecyclerView
        binding.rvUsuarios.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()

        // Inicialización de la vista de búsqueda
        searchView = findViewById(R.id.searchView)

        // Obtención de los usuarios
        obtenerUsuarios()

        // Configuración del botón de agregar/actualizar
        binding.btnAddUpdate.setOnClickListener {
            val isValid = validarCampos()
            if (isValid) {
                if (!isEditing) {
                    agregarUsuario()
                } else {
                    actualizarUsuario()
                }
            } else {
                Toast.makeText(this, "Se deben llenar los campos", Toast.LENGTH_LONG).show()
            }
        }

        // Configuración del listener para la búsqueda
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    // Configuración del RecyclerView
    private fun setupRecyclerView() {
        adapter = UsuarioAdapter(this, listaUsuarios)
        adapter.setOnClick(this@MainActivity)
        binding.rvUsuarios.adapter = adapter
    }

    // Validación de los campos de entrada
    private fun validarCampos(): Boolean {
        return !(binding.etNombre.text.isNullOrEmpty() || binding.etEmail.text.isNullOrEmpty())
    }

    // Obtención de la lista de usuarios
    private fun obtenerUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.obtenerUsuarios()
            runOnUiThread {
                if (call.isSuccessful) {
                    listaUsuarios = call.body()?.listaUsuarios ?: arrayListOf()
                    adapter.setUsuarios(listaUsuarios)
                } else {
                    Toast.makeText(this@MainActivity, "ERROR CONSULTAR TODOS", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Agregado de un nuevo usuario
    private fun agregarUsuario() {
        this.usuario.idUsuario = -1
        this.usuario.nombre = binding.etNombre.text.toString()
        this.usuario.email = binding.etEmail.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                    limpiarCampos()
                    limpiarObjeto()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR ADD", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Actualización de un usuario existente
    private fun actualizarUsuario() {
        val nombre = binding.etNombre.text.toString()
        val email = binding.etEmail.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usuarioActualizado = Usuario(usuario.idUsuario, nombre, email)
                val call = RetrofitClient.webService.actualizarUsuario(usuario.idUsuario, usuarioActualizado)
                runOnUiThread {
                    if (call.isSuccessful) {
                        Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                        obtenerUsuarios()
                        limpiarCampos()
                        limpiarObjeto()

                        binding.btnAddUpdate.text = "Agregar Usuario"
                        binding.btnAddUpdate.backgroundTintList = AppCompatResources.getColorStateList(this@MainActivity, R.color.verde_claro)
                        isEditing = false
                    } else {
                        Toast.makeText(this@MainActivity, "Error al actualizar el usuario", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Limpieza de los campos de entrada
    private fun limpiarCampos() {
        binding.etNombre.setText("")
        binding.etEmail.setText("")
    }

    // Limpieza del objeto usuario
    private fun limpiarObjeto() {
        this.usuario.idUsuario = -1
        this.usuario.nombre = ""
        this.usuario.email = ""
    }

    // Implementación de la interfaz OnItemClicked para la edición de un usuario
    override fun editarUsuario(usuario: Usuario) {
        binding.etNombre.setText(usuario.nombre)
        binding.etEmail.setText(usuario.email)
        binding.btnAddUpdate.text = "Actualizar Usuario"
        binding.btnAddUpdate.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.verde_claro)
        this.usuario = usuario
        isEditing = true
    }

    // Implementación de la interfaz OnItemClicked para la eliminación de un usuario
    override fun borrarUsuario(idUsuario: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitClient.webService.borrarUsuario(idUsuario)
            runOnUiThread {
                if (call.isSuccessful) {
                    Toast.makeText(this@MainActivity, call.body().toString(), Toast.LENGTH_LONG).show()
                    obtenerUsuarios()
                } else {
                    Toast.makeText(this@MainActivity, "ERROR al eliminar el usuario", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
