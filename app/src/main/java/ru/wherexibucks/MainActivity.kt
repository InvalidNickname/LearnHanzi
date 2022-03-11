package ru.wherexibucks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DatabaseConfigurationFactory
import com.couchbase.lite.create
import kotlinx.coroutines.DelicateCoroutinesApi
import ru.wherexibucks.database.Database
import ru.wherexibucks.fragment.AlternativeHomeFragment
import ru.wherexibucks.fragment.HomeFragment

@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    private lateinit var db: Database
    private lateinit var couchDb: com.couchbase.lite.Database
    private var alternativeHome = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.main_fragment, HomeFragment(), "home").commit()
        // подключаем Room
        db = Room.databaseBuilder(this, Database::class.java, "cards").createFromAsset("main.db").build()
        // подключаем CouchbaseLite
        CouchbaseLite.init(applicationContext)
        couchDb = com.couchbase.lite.Database("words", DatabaseConfigurationFactory.create())
    }

    fun getDatabase(): Database {
        return db
    }

    fun isHomeAlternative(): Boolean {
        return alternativeHome
    }

    fun setAlternativeHome(alternativeHome: Boolean) {
        this.alternativeHome = alternativeHome
    }

    override fun onBackPressed() {
        val learnFragment = supportFragmentManager.findFragmentByTag("learn")
        val reviewFragment = supportFragmentManager.findFragmentByTag("review")
        val statsFragment = supportFragmentManager.findFragmentByTag("stats")
        val listFragment = supportFragmentManager.findFragmentByTag("list")
        val managerFragment = supportFragmentManager.findFragmentByTag("manager")
        if (learnFragment != null || reviewFragment != null || statsFragment != null || listFragment != null || managerFragment != null) {
            if (alternativeHome) {
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment, AlternativeHomeFragment(), "alternative").commit()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.main_fragment, HomeFragment(), "home").commit()
            }
        } else {
            super.onBackPressed()
        }
    }
}