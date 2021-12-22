package com.gangaown.flipboardgame.rectanglesearchutil

import android.util.Log

object LargestRectangleSearch {

    private val TAG = "LargestRectangleSearch"

    fun findLargeRectangle(toggleArrayList: ArrayList<Int>, columnSize:Int, rowSize:Int):Int {
        Log.d(TAG, " Toggle array: "+toggleArrayList.toString())
        val tempArray = ArrayList<Int>()
        for(i in 0 until columnSize){
            tempArray.add(0)
        }
        var area = 0
        var limit = rowSize * columnSize


            var i = 0
            while(i< limit){
                for(j in 0 until columnSize){
                    if(toggleArrayList[i] == 1)
                        tempArray[j] += 1
                    else
                        tempArray[j] = 0
                    i++
                }
                Log.d(TAG, "tempArray: "+tempArray.toString())
                val curArea = findArea(tempArray)
                Log.d(TAG, "current Area "+curArea.toString())
                if(curArea > area){
                    area = curArea
                }
            }

        Log.d(TAG, "Largest area : "+area.toString())
        return area
    }

    private fun findArea(tempArray: ArrayList<Int>): Int {
        var maxArea = 0
        for(i in 0 until tempArray.size){
            var minHeight = tempArray[i]
            for(j in i until tempArray.size){
                if(minHeight > tempArray[j])
                    minHeight = tempArray[j]
                val currArea = (j-i+1)*minHeight
                if(currArea> maxArea)
                    maxArea = currArea
            }

        }
        //Log.d(TAG,maxArea.toString())
        return maxArea
    }

}