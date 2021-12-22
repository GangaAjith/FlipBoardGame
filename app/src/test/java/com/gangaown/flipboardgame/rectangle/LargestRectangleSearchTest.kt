package com.gangaown.flipboardgame.rectangle

import junit.framework.TestCase
import org.junit.Test

class LargestRectangleSearchTest : TestCase(){
    @Test
    fun test_all_1_rectangle_area_return_1(){
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
    fun test_all_0_rectangle_area_return_0(){
        val toggleArrayList = ArrayList<Int>()
        for(i in 0 until 6){
            for(j in 0 until 6){
                toggleArrayList.add(0)
            }
        }
        val area = LargestRectangleSearch.findLargeRectangle(toggleArrayList, 6, 6)
        assertEquals(0, area)
    }

    @Test
    fun test_custom_value_rectangle_area(){
        val toggleArrayList = mutableListOf<Int>()
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

    @Test
    fun test_find_area_1(){
        val toggleList = listOf(0,3,2,2,0)
        val area = LargestRectangleSearch.findArea(toggleList)
        assertEquals(6, area)
    }

    @Test
    fun test_find_area_2(){
        val toggleList = listOf(0,2,1,2,0,3)
        val area = LargestRectangleSearch.findArea(toggleList)
        assertEquals(3, area)
    }
}