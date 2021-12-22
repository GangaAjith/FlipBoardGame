package com.gangaown.flipboardgame.rectanglesearchutil

object RectangleSearch {

    private lateinit var toggleArrayList:ArrayList<Boolean>

    fun initToggleArray(toggleList:ArrayList<Boolean>){
        toggleArrayList = toggleList
    }
     fun searchLeft(position: Int, column: Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search left
        var curPos = position - 1
        var colmn = column - 1
        while(true){
            //val view = gvFlip.getChildAt(curPos)
            if(colmn > -1 && toggleArrayList[curPos]){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(curPos)
                curPos -= 1
                colmn -= 1

            }
            else{
                break
            }
        }
        return arrayList
    }

    fun searchRight(position: Int, column: Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search right
        var curPos = position + 1
        var colmn = column + 1

        while(true){
            //val view = gvFlip.getChildAt(curPos)
            if(colmn < 15 && toggleArrayList[curPos]){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(curPos)
                curPos += 1
                colmn += 1
            }
            else{
                break
            }
        }
        return arrayList
    }

    fun searchAbove(position: Int, row: Int):ArrayList<Int>{

        val arrayList = ArrayList<Int>()
        //search above
        var curPos = position - 15
        var rw = row - 1
        while(true){
            //val view = gvFlip.getChildAt(curPos)
            if(rw >-1 && toggleArrayList[curPos]){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(curPos)
                curPos -= 15
                rw -= 1

            }
            else{
                break
            }
        }
        return arrayList
    }

    fun searchBelow(position: Int, row:Int):ArrayList<Int>{
        val arrayList = ArrayList<Int>()
        //search below
        var curPos = position + 15
        var rw = row + 1
        while(true){
            //val view = gvFlip.getChildAt(curPos)
            if(rw<15 && toggleArrayList[curPos]){
                //view.setBackgroundColor(resources.getColor(R.color.red_300))
                arrayList.add(curPos)
                curPos += 15
                rw += 1

            }
            else{
                break
            }
        }
        return arrayList
    }

    fun getSearchCount(neighbourCells: Int): Int {
        return when (neighbourCells) {
            8 -> 4
            5 -> 3
            else -> 2
        }
    }

    fun searchLeftAboveQuadrant(listLeft: ArrayList<Int>, listAbove: ArrayList<Int>): Int {
        // search left-above quadrant
        val rowCount = listAbove.size
        val columnCount = listLeft.size
        var area = 0

        var height = 0
        var length = 0
        for (i in 0 until rowCount) {
            var curPos = listAbove[i]
            for (j in 0 .. columnCount) {
                length = j
                //val view = gvFlip.getChildAt(curPos)
                if (toggleArrayList[curPos]) {
                    height = i
                    //view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos -= 1
                    val tempArea = (length+1) * (height+2)
                    if(area < tempArea)
                        area = tempArea
                } else {
                    height -= i+2
                    break
                }

            }

        }
        return area
    }

    fun searchLeftBelowQuadrant(listLeft: ArrayList<Int>, listBelow:ArrayList<Int>):Int{
        // search left-above quadrant
        val rowCount = listBelow.size
        val columnCount = listLeft.size

        var area = 0
        var height = 0
        var length:Int

        for( i in 0 until rowCount){
            var curPos = listBelow[i]
            for (j in 0 .. columnCount){
                length = j
                //val view = gvFlip.getChildAt(curPos)
                if(toggleArrayList[curPos]){
                    height = i
                    //view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos -= 1
                    val tempArea = (length+1) * (height+2)
                    if(area < tempArea)
                        area = tempArea
                }
                else{
                    height -= i+2
                    break
                }
            }
        }
        return area

    }

    fun searchRightAboveQuadrant(listRight:ArrayList<Int>, listAbove: ArrayList<Int>) :Int{
        // search right-above quadrant
        val rowCount = listAbove.size
        val columnCount = listRight.size

        var area = 0
        var height = 0
        var length = 0
        for (i in 0 until rowCount) {

            var curPos = listAbove[i]
            for (j in 0 .. columnCount) {
                //val view = gvFlip.getChildAt(curPos)
                length = j
                if (toggleArrayList[curPos]) {
                    height = i
                    //view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos += 1
                    val tempArea = (length+1) * (height+2)
                    if(area < tempArea)
                        area = tempArea
                } else {
                    height -= i+2
                    break
                }

            }
        }

        return area

    }

    fun searchRightBelowQuadrant(listRight: ArrayList<Int>, listBelow: ArrayList<Int>):Int{
        // search right-below quadrant
        val rowCount = listBelow.size
        val columnCount = listRight.size

        var height = 0
        var length = 0
        var area = 0
        for( i in 0 until rowCount){
            var curPos = listBelow[i]
            for (j in 0 .. columnCount){
                length = j
                //val view = gvFlip.getChildAt(curPos)
                if(toggleArrayList[curPos]){
                    height  = i
                    //view.setBackgroundColor(resources.getColor(R.color.red_300))
                    curPos += 1
                    val tempArea = (length+1) * (height+2)
                    if(area < tempArea)
                        area = tempArea
                }
                else{
                    height -= i+2
                    break
                }
            }
        }
        return area
    }
}