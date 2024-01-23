package com.barran.wendaobook.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TTSHelper(
    private val onInit: () -> Unit,
    private val onSpeakStart: (String) -> Unit,
    private val onSpeakFinish: (String) -> Unit
) {

    private val tag = "TTSHelper"

    private var tts: TextToSpeech? = null

    private var initSuc = false

    fun startTTS(context: Context) {
        tts = TextToSpeech(context, object : TextToSpeech.OnInitListener {
            override fun onInit(status: Int) {
                if (status == TextToSpeech.SUCCESS) {
                    initSuc = true
                    tts?.language = Locale.CHINA
                    onInit.invoke()
                } else {
                    Log.e(tag, "init tts failed $status")
                }
            }
        })
    }

    fun speak(text: String) {
        speak(text, tag)
    }

    fun speak(text: String, utteranceId: String) {

        if (initSuc) {

            val tts = this.tts ?: return
            val params = HashMap<String, String>()
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params)
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) {
                    Log.d(tag, "onStart: $utteranceId")
                    onSpeakStart.invoke(utteranceId)
                }

                override fun onDone(utteranceId: String) {
                    Log.d(tag, "onDone: $utteranceId")
                    onSpeakFinish.invoke(utteranceId)
                }

                override fun onError(utteranceId: String) {
                    Log.d(tag, "onError: $utteranceId")
                }
            })
        }
    }


    fun stopTTS() {
        tts?.apply {
            if (isSpeaking) {
                Log.i(tag, "stopTTS")
                stop()
            }
        }
    }

    fun shutdown() {
        tts?.apply {
            if (isSpeaking) {
                stop()
            }
            shutdown()
            Log.i(tag, "shutdown")
        }
    }
}