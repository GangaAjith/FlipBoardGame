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
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import com.gangaown.flipboardgame.adapter.FlipViewAdapter
import com.gangaown.flipboardgame.databinding.ActivityFullscreenBinding
import com.gangaown.flipboardgame.rectanglesearchutil.LargestRectangleSearch
import com.gangaown.flipboardgame.util.Constants
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.getSearchCount
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.initToggleArray
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchAbove
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchBelow
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchLeft
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchLeftAboveQuadrant
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchLeftBelowQuadrant
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchRight
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchRightAboveQuadrant
import com.gangaown.flipboardgame.rectanglesearchutil.RectangleSearch.searchRightBelowQuadrant


/**
 * Flip board game entry page
 */
class FlipBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var gvFlip: GridView
    private lateinit var btnMode: Button
    private lateinit var btnExit: Button
    private lateinit var fullscreenContentControls: LinearLayout

    private lateinit var toggleArrayList: ArrayList<Int>

    private val hideHandler = Handler()
    private val TAG = "FlipBoardActivity"
    private var biggestRectangleArea = 0

    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            btnMode.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            btnMode.systemUiVisibility =
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

        btnMode = binding.btnMode
        btnExit = binding.exitBtn
        // Set up the user interaction to manually show or hide the system UI.
        btnMode.setOnClickListener { toggle() }
        btnExit.setOnClickListener{
            this.finish()

        }
        fullscreenContentControls = binding.fullscreenContentControls


        // Gridview binding
        gvFlip = binding.gvFlip
       // Total cell for a 15*15 matrix

        // setup the gridview using BaseAdapter
        setUpArrayList()

        val flipViewAdapter = FlipViewAdapter(this, toggleArrayList)
        gvFlip.adapter = flipViewAdapter


        // Handling the click on the gridview cells
        gvFlip.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            Log.v(TAG,position.toString())
                // set the background color to the clicked cell
            if (toggleArrayList[position] == 1) {

                toggleArrayList[position] = 0
                flipViewAdapter.notifyDataSetChanged()
                biggestRectangleArea = LargestRectangleSearch.findLargeRectangle(toggleArrayList,Constants.COLUMN_SIZE, Constants.RAW_SIZE)
                binding.tvArea.text = biggestRectangleArea.toString()

            } else{
                toggleArrayList[position] = 1
                flipViewAdapter.notifyDataSetChanged()
                biggestRectangleArea = LargestRectangleSearch.findLargeRectangle(toggleArrayList,Constants.COLUMN_SIZE, Constants.RAW_SIZE)
                binding.tvArea.text = biggestRectangleArea.toString()
                //drawRectangle(position)
            }

        }

        binding.exitBtn.setOnTouchListener(delayHideTouchListener)
    }



    private fun setUpArrayList() {
        toggleArrayList = ArrayList()
        for(i in 0 until Constants.COLUMN_SIZE){
            for(j in 0 until Constants.RAW_SIZE){
                toggleArrayList.add(0)
            }
        }
       // initToggleArray(toggleArrayList)

    }

    /*private fun drawRectangle(position: Int) {
        val row = position/15
        val column = (position - ( row * 15))


        // get the count of neighbour cells
        val neighbourCells = checkNeighboursCount(row, column)
        val searchCount = getSearchCount(neighbourCells)


        if( searchCount == 2){
            if(row == 0 && column == 0){

                //search right
                val listRight = searchRight(position, column)

                //search below
                val listBelow = searchBelow(position, row)


                Log.v(TAG, listRight.toString())
                Log.v(TAG, listBelow.toString())

                Log.d(TAG, listRight.toString())
                Log.d(TAG, listBelow.toString())

                var area = 0
                if(listRight.size != 0 && listBelow.size != 0)
                    area = searchRightBelowQuadrant(listRight, listBelow)

                setArea(area)
            }

            else if(row == 0 && column == 14){

                //search left
                val listLeft = searchLeft(position, column)
                //search below
                val listBelow = searchBelow(position, row)

                Log.v(TAG, listLeft.toString())
                Log.v(TAG, listBelow.toString())

                var area = 0
                if(listLeft.size!= 0 && listBelow.size != 0)
                    area = searchLeftBelowQuadrant(listLeft, listBelow)
                Log.d(TAG, "area = "+area)
                //setArea(area)

            }
            else if( row == 14 && column == 0){

                //search right
                val listRight = searchRight(position, column)
                //search above
                val listAbove = searchAbove(position, row)
                var area = 0



                Log.d(TAG, listRight.toString())
                Log.d(TAG, listAbove.toString())

                if(listRight.size != 0 && listAbove.size != 0)
                    area = searchRightAboveQuadrant(listRight, listAbove)
                //setArea(area)

            }
            else{
                //search left
                val listLeft = searchLeft(position, column)
                //search above
                val listAbove = searchAbove(position, row)

                Log.v(TAG, listLeft.toString())
                Log.v(TAG, listAbove.toString())


                Log.d(TAG, listLeft.toString())
                Log.d(TAG, listAbove.toString())

                var area = 0
                if(listLeft.size != 0 && listAbove.size != 0)
                    area = searchLeftAboveQuadrant(listLeft, listAbove)
                //setArea(area)
            }
        }
        else if(searchCount == 3){
            when {
                row == 0 -> {
                    var areaRightBelow = 0
                    var areaLeftBelow = 0
                    // search right
                    val listRight = searchRight(position, column)
                    // search left
                    val listLeft = searchLeft(position, column)
                    // search below
                    val listBelow = searchBelow(position, row)

                    Log.v(TAG, listLeft.toString())
                    Log.v(TAG, listRight.toString())
                    Log.v(TAG, listBelow.toString())

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
                column == 0 -> {

                    var areaRightAbove = 0
                    var areaRightBelow = 0
                    // search right
                    val listRight = searchRight(position, column)
                    // search above
                    val listAbove = searchAbove(position, row)
                    // search below
                    val listBelow = searchBelow(position, row)


                    Log.v(TAG, listRight.toString())
                    Log.v(TAG, listAbove.toString())
                    Log.v(TAG, listBelow.toString())

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
                row == 14 -> {
                    var areaLeftAbove = 0
                    var areaRightAbove = 0
                    // search left
                    val listLeft = searchLeft(position, column)
                    // search right
                    val listRight = searchRight(position, column)
                    // search above
                    val listAbove = searchAbove(position, row)

                    Log.v(TAG, listLeft.toString())
                    Log.v(TAG, listRight.toString())
                    Log.v(TAG, listAbove.toString())


                    if(listLeft.size != 0 && listAbove.size != 0)
                        areaLeftAbove = searchLeftAboveQuadrant(listLeft, listAbove)
                    if(listRight.size != 0 && listAbove.size!= 0)
                        areaRightAbove = searchRightAboveQuadrant(listRight, listAbove)

                    val totalArea = areaLeftAbove + areaRightAbove
                    setArea(totalArea)
                }
                else -> {
                    var areaLeftAbove = 0
                    var areaLeftBelow = 0

                    // search left
                    val listLeft = searchLeft(position, column)
                    // search above
                    val listAbove = searchAbove(position, row)
                    // search below
                    val listBelow = searchBelow(position, row)

                    Log.v(TAG, listLeft.toString())
                    Log.v(TAG, listBelow.toString())
                    Log.v(TAG, listAbove.toString())

                    if(listLeft.size != 0 && listAbove.size != 0)
                        areaLeftAbove = searchLeftAboveQuadrant(listLeft, listAbove)
                    if(listLeft.size != 0 && listBelow.size!= 0)
                        areaLeftBelow= searchLeftBelowQuadrant(listLeft, listBelow)

                    val totalArea = areaLeftAbove + areaLeftBelow
                    setArea(totalArea)

                }
            }
        }
        else{
            // search left
            val listLeft= searchLeft(position, column)
            // search right
            val listRight = searchRight(position, column)
            // search above
            val listAbove = searchAbove(position, row)
            // search below
            val listBelow = searchBelow(position, row)


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
    }*/


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
        binding.btnMode.text = resources.getString(R.string.exit_fullscreen_mode)
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        binding.btnMode.text = resources.getString(R.string.enter_fullscreen_mode)
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