package com.example.moblpr.clients.apibrasil

import com.google.gson.annotations.SerializedName

data class VehicleResponse(
    @SerializedName("Valor")
    val valor: String,
    @SerializedName("Marca")
    val marca: String,
    @SerializedName("Modelo")
    val modelo: String,
    @SerializedName("AnoModelo")
    val anoModelo: String,
    @SerializedName("Combustivel")
    val combustivel: String,
    @SerializedName("CodigoFipe")
    val codigoFipe: String,
    @SerializedName("MesReferencia")
    val mesReferencia: String,
    @SerializedName("Autenticacao")
    val autenticacao: String,
    @SerializedName("TipoVeiculo")
    val tipoVeiculo: String,
    @SerializedName("SiglaCombustivel")
    val siglaCombustivel: String,
    @SerializedName("DataConsulta")
    val dataConsulta: String,
    val cilindradas: String,
    val potencia: String,
    val chassi: String,
    val cor: String,
    val uf: String,
    val municipio: String,
    val renavam: String,
    val extra: Boolean,
    val ipva: String
)