package uz.xsoft.todo

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_audio.*
import java.io.IOException

private const val LOG_TAG = "AudioRecord"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class AudioActivity : AppCompatActivity() {
    private var fileName: String = ""

    private var recorder: MediaRecorder? = null
    private var mStartRecording = true
    private var mStartPlaying = true

    private var player: MediaPlayer? = null

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        fileName = "${externalCacheDir?.absolutePath}/audiorecord.3gp"

        Log.d(LOG_TAG, "file name: $fileName")
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)



        record.setOnClickListener {
            onRecord(mStartRecording)
            mStartRecording = !mStartRecording
            record.setImageResource(
                when (mStartRecording) {
                    true -> R.drawable.ic_microphone_on
                    false -> R.drawable.ic_microphone
                }
            )

        }

        play.setOnClickListener {
            onPlay(mStartPlaying)
            mStartPlaying = !mStartPlaying
            play.setImageResource(
                when (mStartPlaying) {
                    true -> R.drawable.ic_play
                    false -> R.drawable.ic_pause
                }
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }


    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            setOnCompletionListener {
                Log.d("RRR","OnCompleted")
                play.setImageResource(R.drawable.ic_play)
                mStartPlaying = !mStartPlaying
            }
        }


    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }
}
