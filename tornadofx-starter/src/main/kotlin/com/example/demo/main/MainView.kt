package com.example.demo.main

import model.Game
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.text.FontWeight
import model.Colors
import tornadofx.*

class MainView : View("Minesweeper") {

    //private val controller: MainController by inject()
    var actualGame = Game()

    lateinit var buttons: Array<Array<Button>>
    init{
        actualGame.startGame(10, 10, 20)
        buttons = Array(actualGame.rows) { Array(actualGame.cols) { button() } }
    }

    val btnSize = 35.0
    var firstClick = true

    val timerLabel = label {
        text = "Time: " + actualGame.time.toString() + "s"
    }
    var pauseBtn = button("\u23F8") {
        style {
            textFill = Colors.pauseColor
        }
        action {
            actualGame.pauseGame()
            text = when (actualGame.paused) {
                true -> {
                    gridPane.isVisible = false
                    timer.pause()
                    "\u25B6" //|>
                }
                else -> {
                    gridPane.isVisible = true
                    timer.play()
                    "\u23F8"
                } // ||
            }
        }
        prefHeight = btnSize
        prefWidth = btnSize
    }
    val bombsLabel = label {
        text = "Bombs: " + actualGame.mines
    }

    val timer = Timeline(KeyFrame(1.seconds, EventHandler {
        if (actualGame.end == false) {
            actualGame.increaseTime()
        }
        timerLabel.text = "Time: " + actualGame.time.toString() + "s"

    }))

    var menu = menubar {
        menu("Game") {
            menu("New") {
                item("Easy").action {
                    actualGame = Game()
                    actualGame.startGame(5, 5, 10)
                    reset()
                    timer.stop()
                }
                item("Medium").action {
                    actualGame = Game()
                    actualGame.startGame(10, 10, 20)
                    reset()
                    timer.stop()
                }
                item("Hard").action {
                    actualGame = Game()
                    actualGame.startGame(20, 20, 40)
                    reset()
                    timer.stop()
                }
                item("Custom").action {
                    dialog("New Game") {
                        val rows = textfield()
                        val cols = textfield()
                        val mines = textfield()

                        fieldset {
                            field("Rows") {
                                this += rows
                            }
                            field("Coloums") {
                                this += cols
                            }
                            field("Mines") {
                                this += mines
                            }
                        }

                        button("Start!").action {
                            val r = rows.text.toInt()
                            val c = cols.text.toInt()
                            val m = mines.text.toInt()
                            if(r > 0 && c > 0 && m > 0) {
                                actualGame = Game()
                                actualGame.startGame(r, c, m)
                                reset()
                            }
                            timer.stop()
                            close()
                        }
                    }

                }
            }
        }
        menu("Help") {
            item("Rules").action {
                dialog("Rules") {
                    alignment = Pos.CENTER
                    val rules = textarea {
                        isEditable = false
                        prefColumnCount = 50
                        prefRowCount = 15
                        text =
                            "A játékmezőt egy kétdimenziós téglalap alakú négyzetrács alkotja. Minden négyzetrács (továbbiakban:\n" +
                                    "mező) vagy aknát rejt, vagy pedig semmit. Azon mezők, amelyek nem tartalmaznak aknát, azt jelzik,\n" +
                                    "hogy közvetlen szomszédságukban (fent, lent, jobbra, balra és átlósan) összesen hány akna (0-8 db)\n" +
                                    "található. Az, hogy egy adott mező mit rejt, természetesen a játék kezdetekor nem látszik.\n" +
                                    "A játékos a játék kezdetén választhat, hogy mekkora legyen a játékmező (kicsi, közepes, nagy), és ezzel\n" +
                                    "eldönti, hogy milyen nehéz legyen a játék.\n" +
                                    "A játékos egérrel kattinthat az egyes mezőkre. Aknára való kattintás a játék elvesztését eredményezi.\n" +
                                    "Ha a játékos egy olyan mezőre kattint, ami nem akna, akkor a mezőn megjelenik, hogy összesen hány\n" +
                                    "aknával szomszédos. A legelső kattintott mező biztosan nem rejt aknát.\n" +
                                    "A játékos a szerinte aknát tartalmazó mezőket zászlóval láthatja el, ekkor a mező kattintásra nem fog\n" +
                                    "reagálni.\n" +
                                    "A játék akkor végződik győzelemmel, ha a játékos megtalálta az összes nem-akna mezőt. Amennyiben\n" +
                                    "a játékos gyorsan tisztította le a pályát, akkor felkerül a toplistára."
                    }
                    val closeBtn = button("Close").action {
                        alignment = Pos.CENTER
                        close()
                    }

                }
            }
        }
    }

    var statusBar = hbox {
        alignment = Pos.CENTER
        style {
            padding = box(5.px)

        }
        spacing = 10.0

        this += bombsLabel
        this += pauseBtn
        this += timerLabel
    }

    var gridPane = initGrid()

    override val root = borderpane {
        style {
            baseColor = Colors.paneBase
        }
        top = vbox {
            this += menu
            this += statusBar
        }
        center = pane {
            this += gridPane
        }
    }

    fun initGrid(): Pane {
        firstClick = true
        buttons = Array(actualGame.rows) { Array(actualGame.cols) { button() } }
        return gridpane {
            alignment = Pos.CENTER
            style {
                minHeight = (actualGame.rows * btnSize).px
                minWidth = (actualGame.cols * btnSize).px
                padding = box(5.px)
            }
            for (i in 0..(actualGame.rows - 1)) {
                row {
                    for (j in 0..(actualGame.cols - 1)) {
                        buttons[i][j] = button {
                            val cell = actualGame.table.getCell(i, j)!!
                            text = ""
                            prefHeight = btnSize
                            prefWidth = btnSize
                            style {
                                baseColor = Colors.btnBase
                                fontWeight = FontWeight.BOLD
                            }
                            onRightClick {
                                cell.flag()
                                if(!cell.revealed) {
                                    text = when (cell.flagged) {
                                        true -> "\u2691"
                                        else -> ""
                                    }
                                }
                            }
                            action {
                                when {
                                    firstClick -> {
                                        timer.cycleCount = Animation.INDEFINITE
                                        timer.play()
                                        actualGame.table.fillTable(i, j)
                                        cell.reveal()
                                        text = cell.mineNum.toString()
                                        firstClick = false
                                    }
                                    else -> {
                                        if(!cell.revealed && !cell.flagged) {
                                            cell.reveal()
                                        }
                                        when {
                                            cell.revealed && !cell.mine -> text = cell.mineNum.toString()
                                            cell.revealed && cell.mine -> {
                                                actualGame.loseGame()
                                                text = "*"
                                                style {
                                                    textFill = Colors.mineColor
                                                    fontWeight = FontWeight.EXTRA_BOLD
                                                    baseColor = Colors.mineBase
                                                }
                                                dialog ("You Lost!") {
                                                    label("Your Time: " + actualGame.time.toString() + "s")
                                                    button("Restart!").action {
                                                        val r = actualGame.rows
                                                        val c = actualGame.cols
                                                        val m = actualGame.mines
                                                        actualGame = Game()
                                                        actualGame.startGame(r, c, m)
                                                        reset()
                                                        close()
                                                    }
                                                }
                                                timer.stop()
                                            }
                                        }
                                    }
                                }
                                if(actualGame.checkWin()) {
                                    actualGame.winGame()
                                    dialog ("You Won!") {
                                        label("Your Time: " + actualGame.time.toString() + "s")
                                        button("Restart!").action {
                                            val r = actualGame.rows
                                            val c = actualGame.cols
                                            val m = actualGame.mines
                                            actualGame = Game()
                                            actualGame.startGame(r, c, m)
                                            reset()
                                            close()
                                        }
                                    }
                                    timer.stop()
                                }
                                update()
                            }
                        }
                    }
                }
            }
        }
    }

    fun reset() {
        bombsLabel.text = "Bombs: " + actualGame.mines.toString()
        gridPane.removeFromParent()
        val grid = initGrid()
        gridPane = grid
        root.center = grid
        this.setWindowMaxSize((btnSize) * actualGame.cols + 35, (btnSize) * actualGame.rows + statusBar.height + menu.height + 50)
        this.setWindowMinSize((btnSize) * actualGame.cols + 35, (btnSize) * actualGame.rows + statusBar.height + menu.height + 50)
    }

    fun update() {
        for (i in 0..(actualGame.rows - 1)) {
            for (j in 0..(actualGame.cols - 1)) {
                val cell = actualGame.table.getCell(i, j)!!
                buttons[i][j].text = when {
                    cell.revealed && !cell.mine -> {
                        buttons[i][j].isDisable = true
                        if(cell.mineNum == 0) {
                            ""
                        }
                        else {
                            buttons[i][j].textFill = Colors.numberColors[cell.mineNum - 1]
                            cell.mineNum.toString()
                        }
                    }
                    cell.revealed && cell.mine -> "*"
                    cell.flagged -> "\u2691"
                    else -> ""
                }
            }
        }
    }
}
