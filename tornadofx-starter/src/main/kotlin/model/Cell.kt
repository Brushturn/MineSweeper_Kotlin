package model

class Cell(val row: Int, val col: Int, val table: Table) {
    var mine: Boolean = false
    var revealed: Boolean = false
    var flagged: Boolean = false
    var mineNum: Int = 0

    fun flag() {
        flagged = !flagged
    }

    fun map() {
        mineNum = if(mine) { -1 }
        else {
            var count: Int = 0
            for(i in (row - 1).. (row + 1)) {
                for (j in (col - 1)..(col + 1)) {
                    table.getCell(i, j) ?: continue
                    if (table.getCell(i, j)!!.mine) {
                        count++
                    } else {
                        continue
                    }
                }
            }
            count
        }

    }

    fun reveal(): Boolean {
        if(revealed || flagged) return false
        this.revealed = true

        if(mine) return true

        if(mineNum == 0) {
            for (i in (row - 1)..(row + 1)) {
                for (j in (col - 1)..(col + 1)) {
                    table.getCell(i, j) ?: continue
                    table.getCell(i, j)!!.reveal()
                }
            }
        }
        return false
    }

}
