package wonyong.by.ffmpeg

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.android.synthetic.main.activity_video_create.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class VideoCreateActivity : AppCompatActivity() {

    val CONST = Const()

    var resultFps = "4"
    var fps = ""
    var format = "mp4"
    var audio = ""
    var videoPath = ""
    lateinit var timeList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setContentView(R.layout.activity_video_create)
        timeList = intent.getStringArrayListExtra("timeList")
        videoPath = intent.getStringExtra("videoPath")
        initImage()
        buttonInit()
        spinnerInit()
    }

    private fun initImage() {
        make_file_button.isEnabled = false
        Thread(object :Runnable{
            override fun run() {
                createImage()
                runOnUiThread{
                    make_file_button.isEnabled = true
                }
            }
        }).start()

    }

    private fun buttonInit() {
        make_file_button.setOnClickListener {
            Toast.makeText(this, "파일을 제작중입니다.", Toast.LENGTH_SHORT).show()
            Thread(object :Runnable{
                override fun run() {
                    createVideo()
                }
            }).start()
        }
    }

    private fun createVideo() {
        var now = System.currentTimeMillis();
        var date = Date(now);
        var sdfNow = SimpleDateFormat("YYYYMMdd_HHmmss");
        var formatDate = sdfNow.format(date);
        Log.d("###", formatDate)
        FFmpeg.execute("-f image2 -r "+resultFps+" -i "+CONST.STORAGE+"/image-%d.jpg "+CONST.RESULT_STORAGE+"/StopMotion"+formatDate+"."+format)
    }

    private fun createImage() {

        var imageNumber = 1
        for(time: String in timeList) {
            FFmpeg.execute("-ss "+time+" -i "+videoPath+" -vframes 1 -f image2 "+CONST.STORAGE+"/image-"+imageNumber.toString()+".jpg")
            imageNumber++
        }
    }

    private fun spinnerInit() {
        fps_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fps = parent?.getItemAtPosition(position).toString()
                when(fps){
                    "4fps"->{
                        resultFps = "4"
                    }
                    "8fps"->{
                        resultFps = "8"
                    }
                    "16fps"->{
                        resultFps = "16"
                    }
                    "24fps"->{
                        resultFps = "24"
                    }
                    "30fps"->{
                        resultFps = "30"
                    }
                }
            }
        })

        format_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                format = parent?.getItemAtPosition(position).toString()
            }
        })

        format_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                audio = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })
    }
}