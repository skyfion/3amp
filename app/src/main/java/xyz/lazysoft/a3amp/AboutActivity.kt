package xyz.lazysoft.a3amp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    private fun getVersion(): String {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
      //  setSupportActionBar(about_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val titleAbout = findViewById<TextView>(R.id.title_about)

        titleAbout.text = "${getString(R.string.app_name)} ${getVersion()}"
    }

    companion object {

        /**
         * Google
         */
        const val GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxGOZ1xt4RKuFoY2UBFO9JhuSi1Is2/QB91IVxSp+Y5lG0HgLwcjt8hfn1CTPp5yj3RLduufYALtV+xcbjH87ETrWkPez1O3nt7+SF6I/PIXF4dWdbC1QiVSHls4PWFqAghZeDMEhPH4qVayJglz/oBgnRL5X4cDErir6tGtYrDZWZz7AWVxWsfYJDIzlUPklQtEyxpYDlNybCEJzVrOGam/TYTRr3Qs2gjeUi61Uehkh1Yy/wDzvwz7ZCr2zggw2TcqN4WV1Zh6AkzZfjtuHYv9sz4qQWO6hMDiJfxRluHBTw2e1oX9d02t8RCiIRMrrQiYwTYVLzO6EN1JdFUHm9QIDAQAB"
        val GOOGLE_CATALOG = arrayOf(
                "donation.1",
                "donation.2",
                "donation.3",
                "donation.5",
                "donation.8",
                "donation.13")
    }


}
