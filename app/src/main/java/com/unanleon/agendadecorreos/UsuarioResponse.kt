package com.unanleon.agendadecorreos

import com.google.gson.annotations.SerializedName

// Clase de datos que representa la respuesta de la API que contiene una lista de usuarios
data class UsuariosResponse(
    @SerializedName("listaUsuarios") var listaUsuarios: ArrayList<Usuario>
)
