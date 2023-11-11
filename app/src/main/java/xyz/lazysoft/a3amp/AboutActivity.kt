package xyz.lazysoft.a3amp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.sufficientlysecure.donations.DonationsFragment


class AboutActivity : AppCompatActivity() {

    private fun getVersion(): String {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        return pInfo.versionName
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag("donationsFragment")
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
      //  setSupportActionBar(about_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
      //  title_about.text = "${getString(R.string.app_name)} ${getVersion()}"

        //donate
        val ft = supportFragmentManager.beginTransaction()
        val donationsFragment = DonationsFragment.newInstance(
                BuildConfig.DEBUG, false,
                GOOGLE_PUBKEY, GOOGLE_CATALOG,
                resources.getStringArray(R.array.donation_google_catalog_values),
                true, PAYPAL_USER, PAYPAL_CURRENCY_CODE,
                getString(R.string.donation_paypal_item),
                false, null, null, false,
                null)


        ft.replace(R.id.donations_activity_container, donationsFragment, "donationsFragment")
        ft.commit()

    }

    companion object {

        const val PAYPAL_USER = "sky.fion@gmail.com"
        const val PAYPAL_CURRENCY_CODE = "USD"

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
