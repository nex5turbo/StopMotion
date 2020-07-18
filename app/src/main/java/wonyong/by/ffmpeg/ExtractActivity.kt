package wonyong.by.ffmpeg

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.android.synthetic.main.activity_extract.*
import java.io.File

class ExtractActivity : AppCompatActivity() {

    val SELECT_VIDEO_CODE = 2
    val FILE_NOT_SELECTED = true
    val FILE_SELECTED = false
    val CONST = Const()


    var mHandler = Handler()
    var retreiver = MediaMetadataRetriever()
    var fileSelected = FILE_NOT_SELECTED
    var resultPath: String? = ""
    var imageNumber = 0
    var recyclerData: ArrayList<RecyclerData> = ArrayList<RecyclerData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        setContentView(R.layout.activity_extract)
        button_init()
        dirCheck()
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = VideoRecyclerAdapter(recyclerData)
    }

    private fun dirCheck() {
        var path = File(CONST.STORAGE)
        if(!path.exists()){
            path.mkdir()
        }
        var resultP = File(CONST.RESULT_STORAGE)
        if(!resultP.exists()){
            resultP.mkdir()
        }
    }

    private fun button_init() {
        select_file_button.setOnClickListener {
            var i = Intent(Intent.ACTION_GET_CONTENT)
            i.setType("video/*")
            startActivityForResult(i, SELECT_VIDEO_CODE)
        }
        extract_button.setOnClickListener {
            if(fileSelected){
                return@setOnClickListener
            }
            extract()
        }
        create_button.setOnClickListener {
            var timeList: ArrayList<String> = ArrayList()

            for(tmp: RecyclerData in recyclerData){
                timeList.add(tmp.time)
            }
            val i = Intent(this, VideoCreateActivity::class.java)
            i.putExtra("timeList", timeList)
            i.putExtra("videoPath", resultPath)
            removeDirectory(CONST.STORAGE)
            startActivity(i)
        }
        videoView.setOnClickListener {
            if(play_button.visibility == View.VISIBLE){
                play_button.visibility = View.GONE
            }else{
                play_button.visibility = View.VISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            if(requestCode == SELECT_VIDEO_CODE){
                var realPath = RealPath()
                var uri = data?.data
                resultPath = realPath.getRealPath(this, uri!!)
                Toast.makeText(this, resultPath, Toast.LENGTH_SHORT).show()
                videoView.setVideoPath(resultPath)
                playerButtonInit()
                seekButtonInit()
                imageNumber = 0
                retreiver.setDataSource(resultPath)
                fileSelected = FILE_SELECTED
                removeDirectory(CONST.STORAGE)
                videoView.setOnPreparedListener {
                    val time = videoView.getDuration()
                    seekBar.setProgress(0)
                    seekBar.setMax(time)
                    Log.d("###", time.toString())
                    updateProgressBar()
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {

                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            videoView.pause()
                        }

                        override fun onStopTrackingTouch(p0: SeekBar?) {
                            var progress = seekBar?.progress
                            var position = progress!!
                            videoView.seekTo(position)
                            videoView.start()
                        }
                    })
                }
            }
        }
    }

    private fun updateProgressBar() {
        var updateTimeTask = object : Runnable{
            override fun run() {
                var position = videoView.currentPosition
                seekBar.setProgress(position)
                mHandler.postDelayed(this, 100)
            }
        }

        mHandler.postDelayed(updateTimeTask, 100)
    }

    private fun seekButtonInit() {
        backward_button.setOnClickListener {
            var position = videoView.currentPosition
            videoView.seekTo(position-1000)
        }
        forward_button.setOnClickListener {
            var position = videoView.currentPosition
            videoView.seekTo(position+1000)
        }
    }

    private fun playerButtonInit() {
        play_button.setOnClickListener {
            if(videoView.isPlaying){
                videoView.pause()
            }else{
                videoView.start()
            }
        }
    }

    fun removeDirectory(path:String){
        var file = File(path)
        var child_file_list = file.listFiles()
        for(child_file:File in child_file_list){
            child_file.delete()
        }
        recyclerData.clear()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun extract(){
        imageNumber++
        var inum = imageNumber
        var position = videoView.currentPosition
        var hour = 0
        var minute = 0
        var second = position/1000
        var milsecond = if((position%1000)/10 < 10) ((position%1000)/10)*10
        else (position%1000)/10

        if(second > 59){
            minute = second/60
            hour = minute/60
            second = second%60
        }
        var hourString = hour.toString()
        var minuteString = minute.toString()
        var secondString = second.toString()
        var milsecondString = milsecond.toString()
        var time = hourString+":"+minuteString+":"+secondString+"."+milsecondString
        Thread(object : Runnable {
            override fun run() {
                FFmpeg.execute("-ss "+time+" -i "+resultPath+" -vframes 1 -f image2 "+CONST.STORAGE+"/image-"+inum.toString()+".jpg")
                var bm: Bitmap = BitmapFactory.decodeFile(CONST.STORAGE+"/image-"+inum.toString()+".jpg")
                recyclerData.add(RecyclerData(bm, inum, time))
                runOnUiThread {
                    recyclerView.adapter?.notifyDataSetChanged()
                    recyclerView.scrollToPosition(inum-1)
                }
            }
        }).start()
    }

}