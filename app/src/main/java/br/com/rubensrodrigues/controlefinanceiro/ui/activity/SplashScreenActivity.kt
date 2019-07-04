package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import br.com.rubensrodrigues.controlefinanceiro.R

class SplashScreenActivity : AppCompatActivity() {

    private var tempo: Long = 500

    private val CHAVE_SPLASH = "splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        verificaSePrimeiroAcesso()
        executeThread()
    }

    private fun verificaSePrimeiroAcesso() {
        val preferences = getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        if (!preferences.contains(CHAVE_SPLASH)) {
            tempo = 2000

            val editor = preferences.edit()
            editor.putString(CHAVE_SPLASH, "primeiro acesso realizado")
            editor.apply()
        }
    }

    private fun executeThread() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                finish()
            }
        }, tempo)
    }
}
