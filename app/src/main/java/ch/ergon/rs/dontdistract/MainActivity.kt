package ch.ergon.rs.dontdistract

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun open(view: View) {
        var intent = Intent(this@MainActivity, WorkSessionActivity::class.java)


        //intent.putExtra(WEIGHT, textWeight.text)
        //intent.putExtra(HEIGHT, textHight.text)
        startActivity(intent)
    }

    companion object {
        val WEIGHT = "weight"
        val HEIGHT = "height"
    }
}