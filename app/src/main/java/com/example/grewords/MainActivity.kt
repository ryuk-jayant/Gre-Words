package com.example.grewords

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayoutStates
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import android.content.SharedPreferences;
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class SaveWord(context: Context){
    private val dataStore=context.createDataStore(name = "Words")

    companion object{
        val WORD_KEY= preferencesKey<String>("Word")
    }
    suspend fun store_data(word:String){
        dataStore.edit {
            it[WORD_KEY] = word
            // here it refers to the preferences we are editing
        }
    }

}
class MainActivity : AppCompatActivity(),TextToSpeech.OnInitListener{
    private lateinit var data:String
    //text to speech variable or object
    private var tts:TextToSpeech?=null
    //overriding method from text to speech class
    private var i=1
    lateinit var saveWord: SaveWord

    override fun onInit(status: Int) {
        val wordspeak=findViewById<ImageButton>(R.id.sayword)
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language not supported!")
            } else {
                wordspeak!!.isEnabled = true
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        saveWord= SaveWord(this)
        loaddata()
        val btn=findViewById<Button>(R.id.nextbutton)
        btn.setOnClickListener {
            loaddata()
            changeback()}
        tts= TextToSpeech(this,this)
        val wordspeak=findViewById<ImageButton>(R.id.sayword)
        val meaningspeak=findViewById<ImageButton>(R.id.saymeaning)
        val addfav=findViewById<ImageButton>(R.id.FavButton)
        wordspeak.setOnClickListener { speakword() }
        addfav.setOnClickListener { addtofavfile() }
        meaningspeak.setOnClickListener { speakmeaning() }
    }
    private fun addtofavfile(){
        //function adds word to fav file to access on another view

        GlobalScope.launch {
            saveWord.store_data(data)
        }
        Toast.makeText(this,data+" Added to Fav List",Toast.LENGTH_SHORT).show()
    }
    private fun speakword(){
        //speaks current word
        val word: TextView =findViewById<TextView>(R.id.word)
        tts!!.speak(word.text.toString(),TextToSpeech.QUEUE_FLUSH,null,"")
    }
    private fun speakmeaning(){
        //speaks current meaning
        val meaning=findViewById<TextView>(R.id.meaning)
        tts!!.speak(meaning.text.toString(),TextToSpeech.QUEUE_FLUSH,null,"")
    }
     fun loaddata(){
         //loads next word into view
        val word: TextView =findViewById<TextView>(R.id.word)
         val meaning=findViewById<TextView>(R.id.meaning)
         val diff=findViewById<TextView>(R.id.counter)
        val queue = Volley.newRequestQueue(this)
        val url = "https://ryuk-jayant.github.io/Gre-Words/baron-334.json"
        val req= JsonObjectRequest(
            Request.Method.GET,url,null,
            { response->word.text="ia m good"

                if(response!=null){

                    val ja:JSONArray=response.getJSONArray("words")
//                    for(i in 0 until ja.length()) {
//                        data.add(ja[i].());
//                    }
                    val jo:JSONObject = ja[Random.nextInt(0,ja.length()-1)] as JSONObject
                    word.text=jo.getString("word")
                    meaning.text=jo.getString("definition")
                    diff.text=jo.getString("difficulty")
                    data=jo.toString()
                    //Log.d("DATA",ja[0].toString())
                }

            },
            { word.text="iam broke"
                Log.e("Error",it.localizedMessage);
            })
        queue.add(req)

    }
    private fun changeback(){
        //changes background gradiant for each word
        val back=findViewById<ConstraintLayout>(R.id.contraintlayout)
        if(i==1)
        {
            back.setBackgroundResource(R.drawable.gradiant2)
            i=2}
        else if(i==2){
            back.setBackgroundResource(R.drawable.gradiant3)
            i=3
        }
        else if(i==3){
            back.setBackgroundResource(R.drawable.gradiant)
            i=1
        }
    }
    public override fun onDestroy() {
        // Shutdown TTS when
        // activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }
}