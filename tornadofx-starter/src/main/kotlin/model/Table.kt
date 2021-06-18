package model

import kotlin.random.Random

class Table(val row: Int, val col: Int, val mineNum: Int) {
    private val matrix: List<List<Cell>>

    init {
        matrix = List(row) { i -> List(col) { j -> Cell(i, j, this)} }
    }

    fun getCell(r: Int, c: Int): Cell? {
        if(r < 0 || r > row - 1) return null
        if(c < 0 || c > col - 1) return null
        return matrix[r][c]
    }

    fun fillTable(starter: Cell) {
        for(i in 1..mineNum) {
            var r: Int = Random.nextInt(0, row)
            var c: Int = Random.nextInt(0, col)
            if(getCell(r, c) == starter || (getCell(r, c)?.mine ?: true)) {
                r = Random.nextInt(0, row)
                c = Random.nextInt(0, col)
            }
            getCell(r, c)!!.mine = true
        }
        for (i in 0..(row - 1)) {
            for (j in 0..(col - 1)) {
                getCell(i, j)!!.map()
            }
        }
    }

    fun fillTable(row: Int, col: Int) {
        for (i in 1..mineNum) {
            var r: Int = Random.nextInt(0, this.row)
            var c: Int = Random.nextInt(0, this.col)
            if ((r == row && c == col) || (getCell(r, c)?.mine ?: true)) {
                r = Random.nextInt(0, this.row)
                c = Random.nextInt(0, this.col)
            }
            getCell(r, c)!!.mine = true
        }

        for (i in 0..(this.row - 1)) {
            for (j in 0..(this.col - 1)) {
                getCell(i, j)!!.map()
            }
        }
    }

    fun flag(r: Int, c: Int) {
        if(getCell(r, c)?.revealed == true) return
        getCell(r, c)?.flag()
    }

    fun reveal(r: Int, c: Int) {
        getCell(r, c)?.reveal()
    }

    fun revealAll() {
        for (i in 0 until row) {
            for (j in 0 until col) {
                if(getCell(i, j)!!.flagged) {
                    flag(i, j)
                }
                reveal(i, j)
            }
        }
    }

    fun checkEnd(): Boolean {
        for(i in 0 until row) {
            for(j in 0 until col) {
                if(getCell(i, j)!!.mine == false) {
                    if(!getCell(i, j)!!.revealed) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun write() {
        for(i in 0..(row - 1)) {
            for(j in 0..(col - 1)) {
                print(getCell(i, j)!!.mineNum)
                print(" ")
            }
            println()
        }
    }
}
