package com.gangaown.flipboardgame.ui

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gangaown.flipboardgame.R
import com.gangaown.flipboardgame.adapter.FlipViewAdapter
import com.gangaown.flipboardgame.databinding.ActivityFullscreenBinding
import com.gangaown.flipboardgame.rectangle.LargestRectangleSearch
import com.gangaown.flipboardgame.util.Constants
import com.gangaown.flipboardgame.util.Constants.COLUMN_SIZE
import com.gangaown.flipboardgame.util.Constants.RAW_SIZE


/**
 * Flip board game entry page
 */
class FlipBoardActivity : AppCompatActivity(), FlipViewAdapter.OnItemClickListener {

    private lateinit var binding: ActivityFullscreenBinding
    private lateinit var rvFlip: RecyclerView
    private lateinit var btnMode: Button
    private lateinit var btnExit: Button
    private lateinit var fullscreenContentControls: LinearLayout
    private lateinit var flipViewAdapter: FlipViewAdapter

    private lateinit var toggleArrayList: ArrayList<Int>

    private val hideHandler = Handler()

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

    override fun onItemClick(position: Int) {
        // set the background color to the clicked cell
        if (toggleArrayList[position] == 1) {
            toggleArrayList[position] = 0
        } else{
            toggleArrayList[position] = 1
        }

        flipViewAdapter.notifyItemChanged(position)
        biggestRectangleArea = LargestRectangleSearch.findLargeRectangle(toggleArrayList,COLUMN_SIZE, RAW_SIZE)
        binding.tvArea.text = biggestRectangleArea.toString()

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
        rvFlip = binding.rvFlip

        // setup the gridview using BaseAdapter
        setUpArrayList()

        flipViewAdapter = FlipViewAdapter(this, toggleArrayList, this)
        rvFlip.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.HORIZONTAL))
        rvFlip.addItemDecoration( DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))
        val gridLayout = GridLayoutManager(this, COLUMN_SIZE)
        rvFlip.layoutManager = gridLayout
        rvFlip.adapter = flipViewAdapter

        binding.exitBtn.setOnTouchListener(delayHideTouchListener)
    }

    private fun setUpArrayList() {
        toggleArrayList = ArrayList()
        for(i in 0 until COLUMN_SIZE){
            for(j in 0 until RAW_SIZE){
                toggleArrayList.add(0)
            }
        }
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
            rvFlip.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            rvFlip.systemUiVisibility =
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