package com.example.moblpr.clients

class Retrofit {

    companion object {

        fun getInstance(path: String): retrofit2.Retrofit {
            return retrofit2.Retrofit.Builder()
                .baseUrl(path)
                .addConverterFactory(ApiWorker.gsonConverter)
                .client(ApiWorker.client)
                .build()
        }
    }
}