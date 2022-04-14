package ca.hobin.mediaprojectionbugdemo

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val handler by lazy { getMediaProjectionHandler() }
    private val mediaProjectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private val startProjectionButton: View by lazy { findViewById(R.id.startProjection) }
    private val startProcessButton: View by lazy { findViewById(R.id.startProcess) }
    private val stopProcessButton: View by lazy { findViewById(R.id.stopProcess) }

    private val startActivityForResultHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onMediaProjectionStartResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startProjectionButton.setOnClickListener(this::requestMediaProjectionStart)
        startProcessButton.setOnClickListener(this::startProcess)
        stopProcessButton.setOnClickListener(this::stopProcess)
    }

    private fun getMediaProjectionHandler(): Handler {
        val taskThread = HandlerThread("mediaProjection")
        taskThread.isDaemon = true
        taskThread.start()
        return Handler(taskThread.looper)
    }

    private fun onMediaProjectionStartResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == RESULT_CANCELED) {
            Toast.makeText(this@MainActivity, "MediaProjection declined", Toast.LENGTH_LONG).show()
        }
        val mediaProjection: MediaProjection =
            mediaProjectionManager.getMediaProjection(activityResult.resultCode, activityResult.data!!)
        mediaProjection.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                Toast.makeText(this@MainActivity, "MediaProjection died! Oh no!", Toast.LENGTH_LONG).show()
            }
        }, handler)
        Toast.makeText(this@MainActivity, "MediaProjection started", Toast.LENGTH_LONG).show()
    }

    private fun startProcess(view: View?) {
        val namedProcessServiceIntent = Intent(this, NamedProcessService::class.java)
        ContextCompat.startForegroundService(this, namedProcessServiceIntent)
    }

    private fun stopProcess(view: View?) {
        val namedProcessServiceIntent = Intent(this, NamedProcessService::class.java)
        stopService(namedProcessServiceIntent)
    }

    private fun requestMediaProjectionStart(view: View?) {
        val foregroundServiceIntent = Intent(this, MediaProjectionForegroundService::class.java)
        ContextCompat.startForegroundService(this, foregroundServiceIntent)
        val startIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResultHandler.launch(startIntent)
    }
}
