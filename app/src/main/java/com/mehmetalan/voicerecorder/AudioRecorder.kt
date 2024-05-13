package com.mehmetalan.voicerecorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudioFile: File? = null

    fun startRecording(fileName: String) {
        val audioDir = context.getExternalFilesDir(null)
        currentAudioFile = File(audioDir, "$fileName.mp3")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(currentAudioFile?.absolutePath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
            }
        }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            reset()
            release()
        }
        mediaRecorder = null
    }

    fun startPlaying(file: File) {
        stopPlaying()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(file.absolutePath)
            prepare()
            start()
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            stop()
            reset()
            release()
        }
        mediaPlayer = null
    }

    fun getRecordedFiles(): List<File> {
        val audioDir = context.getExternalFilesDir(null)
        return audioDir?.listFiles()?.toList() ?: emptyList()
    }
}