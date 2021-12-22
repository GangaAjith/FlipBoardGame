package com.gangaown.flipboardgame

import com.gangaown.flipboardgame.rectanglesearchutil.LargestRectangleSearch
import junit.framework.TestCase
import org.junit.Test

class FlipBoardActivityTest : TestCase(){
    @Test
    fun testFindLargeRectangle(){
        val toggleArrayList = ArrayList<Int>()
        for(i in 0 until 6){
            for(j in 0 until 6){
                toggleArrayList.add(1)
            }
        }
        val area = LargestRectangleSearch.findLargeRectangle(toggleArrayList, 6, 6)
        assertEquals(36, area)
    }

    @Test
    fun test_custom_value_rectangle_area(){
        val toggleArrayList = ArrayList<Int>()
        toggleArrayList.add(1)
        toggleArrayList.add(0)
        toggleArrayList.add(1)
        toggleArrayList.add(0)
        toggleArrayList.add(1)

        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(0)
        toggleArrayList.add(1)

        toggleArrayList.add(0)
        toggleArrayList.add(1)
        toggleArrayList.add(0)
        toggleArrayList.add(1)
        toggleArrayList.add(0)

        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(0)

        toggleArrayList.add(0)
        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(1)
        toggleArrayList.add(0)

        val area = LargestRectangleSearch.findLargeRectangle(toggleArrayList, 5, 5)
        assertEquals(6, area)


    }
}