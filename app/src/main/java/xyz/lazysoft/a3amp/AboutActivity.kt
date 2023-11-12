package xyz.lazysoft.a3amp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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

        val buyMeCoffee = findViewById<Button>(R.id.button_buy_coffee)

        buyMeCoffee.setOnClickListener {
            val url = "https://www.buymeacoffee.com/skyfi"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

    }


}
