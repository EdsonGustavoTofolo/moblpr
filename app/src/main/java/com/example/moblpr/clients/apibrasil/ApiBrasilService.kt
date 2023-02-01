package com.example.moblpr.clients.apibrasil

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiBrasilService {

    @POST("/placa/consulta")
    fun consultarPlaca(@Body request: PlacaRequest): Call<VehicleResponse>
}