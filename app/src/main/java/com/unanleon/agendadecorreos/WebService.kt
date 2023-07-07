package com.unanleon.agendadecorreos

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes {
    const val BASE_URL = "http://10.0.2.2:8001/api/"
}

interface WebService {

    // Obtener todos los usuarios
    @GET("usuarios")
    suspend fun obtenerUsuarios(): Response<UsuariosResponse>

    // Obtener un usuario específico por su ID
    @GET("usuario/{idUsuario}")
    suspend fun obtenerUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<Usuario>

    // Agregar un nuevo usuario
    @POST("usuario/add")
    suspend fun agregarUsuario(
        @Body usuario: Usuario
    ): Response<String>

    // Actualizar un usuario existente por su ID
    @PUT("usuario/update/{idUsuario}")
    suspend fun actualizarUsuario(
        @Path("idUsuario") idUsuario: Int,
        @Body usuario: Usuario
    ): Response<String>

    // Eliminar un usuario por su ID
    @DELETE("usuario/delete/{idUsuario}")
    suspend fun borrarUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<String>
}

object RetrofitClient {
    val webService: WebService by lazy {
        // Configuración de Gson para la deserialización de JSON
        val gson = GsonBuilder()
            .setLenient()
            .create()

        // Creación de la instancia de Retrofit
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(WebService::class.java)
    }
}
