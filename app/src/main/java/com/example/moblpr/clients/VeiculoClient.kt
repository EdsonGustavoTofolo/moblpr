package com.example.moblpr.clients

import com.example.moblpr.clients.apibrasil.ApiBrasilService
import com.example.moblpr.clients.apibrasil.PlacaRequest
import com.example.moblpr.clients.apibrasil.VehicleResponse
import com.example.moblpr.models.Vehicle
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VeiculoClient  {

    companion object {

        private const val BASE_URL = "https://placa-fipe.apibrasil.com.br"

        fun findByPlaca(placa: String,
                        onSuccess: (Vehicle) -> Unit,
                        onFail: (String) -> Unit
        ) {
            val retrofit = Retrofit.getInstance(BASE_URL)

            val apiBrasilService = retrofit.create(ApiBrasilService::class.java)

            val request = PlacaRequest(placa)

            val call = apiBrasilService.consultarPlaca(request)

            call.enqueue(object : Callback<VehicleResponse?> {
                override fun onResponse(
                    call: Call<VehicleResponse?>,
                    response: Response<VehicleResponse?>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val vehicle = Vehicle(
                                placa,
                                it.valor,
                                it.marca,
                                it.modelo,
                                it.anoModelo,
                                it.combustivel,
                                it.codigoFipe,
                                it.mesReferencia,
                                it.autenticacao,
                                it.tipoVeiculo,
                                it.siglaCombustivel,
                                it.dataConsulta,
                                it.cilindradas,
                                it.potencia,
                                it.chassi,
                                it.cor,
                                it.uf,
                                it.municipio,
                                it.renavam,
                                it.extra,
                                it.ipva
                            )
                            onSuccess(vehicle)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody.isNullOrEmpty()) {
                            onFail("Falha desconhecida ao buscar os dados.")
                        } else {
                            val jsonObject = JSONObject(errorBody)
                            val message = jsonObject.getString("message")
                            onFail(message)
                        }
                    }
                }
                override fun onFailure(call: Call<VehicleResponse?>, t: Throwable) {
                    onFail("Falha a buscar dados do veículo.")
                }
            })
        }
    }
}