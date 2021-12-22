package com.gangaown.flipboardgame.rectangle



object LargestRectangleSearch {

    fun findLargeRectangle(toggleArrayList: List<Int>, columnSize:Int, rowSize:Int):Int {
        val tempArray = ArrayList<Int>()
        for(i in 0 until columnSize){
            tempArray.add(0)
        }
        var area = 0
        val limit = rowSize * columnSize

            var i = 0
            while(i< limit){
                for(j in 0 until columnSize){
                    if(toggleArrayList[i] == 1)
                        tempArray[j] += 1
                    else
                        tempArray[j] = 0
                    i++
                }
                val curArea = findArea(tempArray)
                if(curArea > area){
                    area = curArea
                }
            }

        return area
    }

   fun findArea(tempArray: List<Int>): Int {
        var maxArea = 0
        for(i in tempArray.indices){
            var minHeight = tempArray[i]
            for(j in i until tempArray.size){
                if(minHeight > tempArray[j])
                    minHeight = tempArray[j]
                val currArea = (j-i+1)*minHeight
                if(currArea> maxArea)
                    maxArea = currArea
            }

        }
        return maxArea
    }

}