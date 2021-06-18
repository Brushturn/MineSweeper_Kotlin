package model

class Game() {
    var rows: Int = 0
    var cols: Int = 0
    var mines: Int = 0
    lateinit var table: Table
    lateinit var name: String
    var time = 0
    var end = true
    var gameWon = false
    var paused = false

    fun startGame(rows: Int, cols: Int, mines: Int) {
        this.rows = rows
        this.cols = cols
        this.mines = mines
        end = false
        table = Table(rows, cols, mines)
        gameWon = false
    }

    fun increaseTime() {
        time++
    }

    fun pauseGame() {
        paused = !paused
    }

    fun endGame() {
        table.revealAll()
        end = true
    }

    fun checkWin(): Boolean
    {
        for(i in 0 until rows) {
            for(j in 0 until cols) {
                if(table.getCell(i, j)!!.mine == false) {
                    if(!table.getCell(i, j)!!.revealed) {
                        return false
                    }
                }
            }
        }
        if(end)
            return false
        return true
    }

    fun winGame() {
        endGame()
        gameWon = true
    }

    fun loseGame() {
        endGame()
        gameWon = false
    }

    fun reveal(r: Int, c: Int) {
        val kaboom = table.getCell(r, c)?.reveal()
        when {
            kaboom == true -> loseGame()
            table.checkEnd() == true -> winGame()
        }
    }
}