package br.com.rubensrodrigues.controlefinanceiro.webservice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class RetrofitConfig() {

    private lateinit var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl("https://economia.awesomeapi.com.br/")
            .addConverterFactory(JacksonConverterFactory.create())
            .client(getClientHttp())
            .build()
    }

    private fun getClientHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient
            .Builder()
            .addInterceptor(logging)
            .build()
        return client
    }

    fun getCotacaoService(): CotacaoService {
        return this.retrofit.create(CotacaoService::class.java)
    }
}