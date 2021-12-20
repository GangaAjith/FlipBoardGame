package com.gangaown.flipboardgame

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.AdapterView
import android.widget.GridView
import android.widget.LinearLayout
import com.gangaown.flipboardgame.databinding.ActivityFullscreenBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var gvFlip: GridView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler()
    private val TAG = "FullscreenActivity"
    private var biggestRectangleArea = 0

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            gvFlip.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            gvFlip.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        gvFlip = binding.gvFlip
        val cellCount = 225

        val flipViewAdapter = FlipViewAdapter(this, cellCount)
        gvFlip.adapter = flipViewAdapter

        //fullscreenContent.setOnClickListener { toggle() }
        fullscreenContentControls = binding.fullscreenContentControls

        gvFlip.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->


            val toggleVal = view.tag.toString()


            if (toggleVal == "off") {
                view.setBackgroundColor(resources.getColor(R.color.grey))
                view.tag = "on"
                drawRectangle(position)

            } else {
                view.setBackgroundColor(resources.getColor(R.color.white))
                view.tag = "off"

            }


        }

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)
    }

    private fun drawRectangle(position: Int) {
        val row = position/15
        val column = (position - ( row * 15))


        // neighbour count
        val neighbourCells = checkNeighboursCount(row, column)
        val searchCount = getSearchCount(neighbourCells)
        val text = ""+position+ " , row = "+row + " , column ="+column+ " neighbours "+ neighbourCells
        Log.v(TAG, text)


        if( searchCount == 2){
            if(row == 0 && column == 0){

                //search right
                val listRight = searchRight(position)

                //search below
                val listBelow = searchBelow(position)

                Log.d(TAG, listRight.toString())
                Log.d(TAG, listBelow.toString())

                var area = 0
                if(listRight.size != 0 && listBelow.size != 0)
                    area = searchRightBelowQuadrant(listRight, listBelow)
                setArea(area)
            }

            else if(row == 0 && column == 14){

                //search left
                val listLeft = searchLeft(position)
                //search below
                val listBelow = searchBelow(position)


                Log.d(TAG, listLeft.toString())
                Log.d(TAG, listBelow.toString())

                var area = 0
                if(listLeft.size!= 0 && listBelow.size != 0)
                    area = searchLeftBelowQuadrant(listLeft, listBelow)
                setArea(area)

            }
            else if( row == 14 && column == 0){

                //search right
                val listRight = searchRight(position)
                //search above
                val listAbove = searchAbove(position)
                var area = 0

                Log.d(TAG, listRight.toString())
                Log.d(TAG, listAbove.toString())

                if(listRight.size != 0 && listAbove.size != 0)
                    area = searchRightAboveQuadrant(listRight, listAbove)
                setArea(area)

            }
            else{
                //search left
                val listLeft = searchLeft(position)
                //search above
                val listAbove = searchAbove(position)

                Log.d(TAG, listLeft.toString())
                Log.d(TAG, listAbove.toString())

                var area = 0
                if(listLeft.size != 0 && listAbove.size != 0)
                    area = searchLeftAboveQuadrant(listLeft, listAbove)
                setArea(area)
            }
        }
        else if(searchCount == 3){
            if( row == 0){
                var areaRightBelow = 0
                var areaLeftBelow = 0
                // search right
                val listRight = searchRight(position)
                // search left
                val listLeft = searchLeft(position)
                // search below
                val listBelow = searchBelow(position)

                if(listRight.size != 0 && listBelow.size != 0)
                    areaRightBelow = searchRightBelowQuadrant(listRight, listBelow)
                if(listLeft.size != 0 && listBelow.size != 0)
                    areaLeftBelow = searchLeftBelowQuadrant(listLeft, listBelow)

                var totalArea = 0
                if(areaRightBelow >= totalArea)
                    totalArea = areaRightBelow
                if(areaLeftBelow > totalArea)
                    totalArea = areaLeftBelow
                setArea(totalArea)

            }
            else if( column == 0){

                var areaRightAbove = 0
                var areaRightBelow = 0
                // search right
                val listRight = searchRight(position)
                // search above
                val listAbove = searchAbove(position)
                // search below
                val listBelow = searchBelow(position)

                if(listRight.size != 0 && listAbove.size != 0)
                    areaRightAbove = searchRightAboveQuadrant(listRight, listAbove)
                if(listRight.size != 0 && listBelow.size!= 0)
                    areaRightBelow = searchRightBelowQuadrant(listRight, listBelow)

                var totalArea = 0
                if (areaRightAbove >= totalArea)
                    totalArea = areaRightAbove
                if( areaRightBelow > totalArea)
                    totalArea = areaRightBelow
                setArea(totalArea)

            }
            else if(row == 14){
                var areaLeftAbove = 0
                var areaRightAbove = 0
                // search left
                val listLeft = searchLeft(position)
                // search right
                val listRight = searchRight(position)
                // search above
                val listAbove = searchAbove(position)


                if(listLeft.size != 0 && listAbove.size != 0)
                    areaLeftAbove = searchLeftAboveQuadrant(listLeft, listAbove)
                if(listRight.size != 0 && listAbove.size!= 0)
                    areaRightAbove = searchRightAboveQuadrant(listRight, listAbove)

                val totalArea = areaLeftAbove + areaRightAbove
                setArea(totalArea)
            }
            else{
                var areaLeftAbove = 0
                var areaLeftBelow = 0

                // search left
                val listLeft = searchLeft(position)
                // search above
                val listAbove = searchAbove(position)
                // search below
                val listBelow = searchBelow(position)

                if(listLeft.size != 0 && listAbove.size != 0)
                    areaLeftAbove = searchLeftAboveQuadrant(listLeft, listAbove)
                if(listLeft.size != 0 && listBelow.size!= 0)
                    areaLeftBelow= searchLeftBelowQuadrant(listLeft, listBelow)

                val totalArea = areaLeftAbove + areaLeftBelow
                setArea(totalArea)

            }
        }
        else{
            // search left
            val listLeft= searchLeft(position)
            // search right
            val listRight = searchRight(position)
            // search above
            val listAbove = searchAbove(position)
            // search below
            val listBelow = searchBelow(position)


            Log.v(TAG, listLeft.toString())
            Log.v(TAG, listRight.toString())
            Log.v(TAG, listAbove.toString())
            Log.v(TAG, listBelow.toString())

            var areaLeftAbove = 0
            var areaLeftBelow = 0
            var areaRightAbove = 0
            var areaRightBelow = 0


            if (listLeft.size != 0 && listAbove.size != 0){
                // search left-above quadrant
               areaLeftAbove = searchLeftAboveQuadrant(listLeft, listAbove)
            }
            if(listLeft.size != 0 && listBelow.size != 0){
                // search left-below quadrant
                areaLeftBelow = searchLeftBelowQuadrant(listLeft, listBelow)

            }
            if(listRight.size != 0 && listAbove.size != 0){
                // search right-above quadrant
                areaRightAbove = searchRightAboveQuadrant(listRight, listAbove)

            }
           if(listRight.size != 0 && listBelow.size != 0){
                //search right-below quadrant
                areaRightBelow = searchRightBelowQuadrant(listRight, listBelow)

            }

            var totalArea = 0
            if(areaLeftAbove >= totalArea)
                totalArea = areaLeftAbove
            if(areaLeftBelow>totalArea)
                totalArea = areaLeftBelow
            if(areaRightAbove > totalArea)
                totalArea =  areaRightAbove
            if(areaRightBelow > totalArea)
                totalArea = areaRightBelow
            setArea(totalArea)

        }

}

    private fun searchLeftAboveQuadrant(listLeft: ArrayList<Int>, listAbove: ArrayList<Int>): Int {
        // search left-above quadrant
        val rowSize = listLeft.size
        val columnSize = listAbove.size

        var height = 0
        var length = 0
        for (i in 0 until columnSize) {
            var curPos = listAbove[i]
            for (j in 0 .. rowSize) {
                val view = gvFlip.getChildAt(curPos)
                if (view?.tag == "on") {
                    view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos -= 1
                } else {
                    break
                }
                length = j
            }
            height = i
        }
        return (length + 2) * (height + 2)

    }

    private fun searchLeftBelowQuadrant(listLeft: ArrayList<Int>, listBelow:ArrayList<Int>):Int{
        // search left-above quadrant
        val rowSize = listLeft.size
        val columnSize = listBelow.size

        var height = 0
        var length = 0
        for( i in 0 until columnSize){
            var curPos = listBelow[i]
            for (j in 0 .. rowSize){
                val view = gvFlip.getChildAt(curPos)
                if(view?.tag == "on"){
                    view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos -= 1
                }
                else{
                    break
                }
                length = j
            }
            height = i
        }
        return (length + 2) * (height + 2)

    }

    private fun searchRightAboveQuadrant(listRight:ArrayList<Int>, listAbove: ArrayList<Int>) :Int{
        // search right-above quadrant
        val rowSize = listRight.size
        val columnSize = listAbove.size

        var height = 0
        var length = 0
        for (i in 0 until columnSize) {
            var curPos = listAbove[i]
            for (j in 0 .. rowSize) {
                val view = gvFlip.getChildAt(curPos)
                if (view?.tag == "on") {
                    view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos += 1
                } else {
                    break
                }
                length = j
            }
            height = i
        }
        return (length + 2) * (height + 2)

    }

    private fun searchRightBelowQuadrant(listRight: ArrayList<Int>, listBelow: ArrayList<Int>):Int{
        // search right-below quadrant
        val rowSize = listRight.size
        val columnSize = listBelow.size

        var height = 0
        var length = 0
        for( i in 0 until columnSize){
            var curPos = listBelow[i]
            for (j in 0 .. rowSize){
                val view = gvFlip.getChildAt(curPos)
                if(view?.tag == "on"){
                    Log.v(TAG, " view "+ curPos)
                    view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos += 1

                }
                else{
                    break
                }
                length = j
            }
            height  = i
        }
        return (length+2) * (height+2)

    }

    private fun searchLeft(position: Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search left
        var rw = position - 1

        while(true){
            val view = gvFlip.getChildAt(rw)
            if(view?.tag == "on"){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(rw)
                rw -= 1

            }
            else{
                break
            }
        }
        return arrayList
    }

    private fun searchRight(position: Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search right
        var rw = position + 1

        while(true){
            val view = gvFlip.getChildAt(rw)
            if(view?.tag == "on"){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(rw)
                rw += 1

            }
            else{
                break
            }
        }
        return arrayList
    }

    private fun searchAbove(position: Int):ArrayList<Int>{

        val arrayList = ArrayList<Int>()
        //search above
        var cl = position - 15
        while(true){
            val view = gvFlip.getChildAt(cl)
            if(view?.tag == "on"){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(cl)
                cl -= 15

            }
            else{
                break
            }
        }
        return arrayList
    }

    private fun searchBelow(position: Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search below
        var cl = position + 15
        while(true){
            val view = gvFlip.getChildAt(cl)
            if(view?.tag == "on"){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(cl)
                cl += 15

            }
            else{
                break
            }
        }
        return arrayList
    }
    private fun getSearchCount(neighbourCells: Int): Int {
        return when (neighbourCells) {
            8 -> 4
            5 -> 3
            else -> 2
        }
    }

    private fun setArea(area:Int){
        if(area > biggestRectangleArea){
            biggestRectangleArea = area
            binding.tvArea.text = biggestRectangleArea.toString()
        }
    }

    private fun checkNeighboursCount(row:Int, column:Int): Int {

        return if((row == 0 && column == 0) || ( row ==0 && column == 14) ||
            ( row == 14 && column == 0) || (row == 14 && column == 14)) 3
        else if( row==0 || column == 0 || row == 14 || column == 14) 5
        else 8
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            gvFlip.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            gvFlip.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}