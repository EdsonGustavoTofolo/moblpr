package com.example.moblpr.models

data class Vehicle(
    val placa: String,
    val valor: String,
    val marca: String,
    val modelo: String,
    val anoModelo: String,
    val combustivel: String,
    val codigoFipe: String,
    val mesReferencia: String,
    val autenticacao: String,
    val tipoVeiculo: String,
    val siglaCombustivel: String,
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
) : java.io.Serializable {
    override fun toString(): String {
        return "placa: $placa, " +
                "valor: $valor, " +
                "marca: $marca, " +
                "modelo: $modelo, " +
                "anoModelo: $anoModelo, " +
                "combustivel: $combustivel, " +
                "codigoFipe: $codigoFipe, " +
                "mesReferencia: $mesReferencia, " +
                "autenticacao: $autenticacao, " +
                "tipoVeiculo: $tipoVeiculo, " +
                "siglaCombustivel: $siglaCombustivel, " +
                "cilindradas: $cilindradas, " +
                "potencia: $potencia, " +
                "chassi: $chassi, " +
                "cor: $cor, " +
                "uf: $uf, " +
                "municipio: $municipio, " +
                "renavam: $renavam, " +
                "extra: $extra, " +
                "ipva: $ipva, " +
                "dataConsulta: $dataConsulta"
    }
}