package ru.wherexibucks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.wherexibucks.database.Database

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, HomeFragment(), "home").commit()
        // подключаем базу
        db = Room.databaseBuilder(this, Database::class.java, "cards").createFromAsset("main.db").build()
    }

    fun getDatabase(): Database {
        return db
    }

    override fun onBackPressed() {
        val learnFragment = supportFragmentManager.findFragmentByTag("learn")
        val reviewFragment = supportFragmentManager.findFragmentByTag("review")
        if (learnFragment != null || reviewFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
        } else {
            super.onBackPressed()
        }
    }
}