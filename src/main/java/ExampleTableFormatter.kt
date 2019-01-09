
import java.util.*
import java.util.regex.Pattern
import java.util.stream.Collectors

class ExampleTableFormatter {

    private val DEL = "|"
    private val DEL_REGEXP = "\\|"
    private var currentTableCellsCount = 0

    fun formatExampleTablesOfSourceLines(sourceLines: LinkedList<String>): List<String> {
        var tableLineCollecting = false
        var startIndex = 0
        for (lineIndex in sourceLines.indices) {
            val l = sourceLines[lineIndex]
            if (tableLineCollecting && !isCurrentTableLine(l)) {
                formatTable(sourceLines.subList(startIndex, lineIndex))
                tableLineCollecting = false
            }
            if (!tableLineCollecting && isTableLine(l)) {
                tableLineCollecting = true
                currentTableCellsCount = l.split(DEL_REGEXP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size
                startIndex = lineIndex
            }
            if (tableLineCollecting && lineIndex == sourceLines.size - 1) {
                formatTable(sourceLines.subList(startIndex, sourceLines.size))
                tableLineCollecting = false
            }
        }
        return sourceLines
    }

    private fun formatTable(exampleTable: MutableList<String>) {
        val columnLengthMap = getColumnLengthMap(exampleTable)
        for (lineIndex in exampleTable.indices) {
            val cells = getCellsFromTableLine(exampleTable[lineIndex])
            exampleTable.removeAt(lineIndex)
            exampleTable.add(lineIndex, "    $DEL " + cells.stream().map { c -> c.content + generateSpaces(columnLengthMap[cells.indexOf(c)]!! - c.contentLength) }
                    .collect(Collectors.joining(" $DEL ")) + " " + DEL)
        }
    }

    private fun getColumnLengthMap(exampleTable: List<String>): Map<Int, Int> {
        val columnLength = HashMap<Int, Int>()
        exampleTable.forEach { line ->
            val cells = getCellsFromTableLine(line)
            for (cellIndex in cells.indices) {
                val cell = cells[cellIndex]
                (columnLength as java.util.Map<Int, Int>).merge(cellIndex, cell.contentLength) { preMaxLength, maxLengthCandidate -> if (maxLengthCandidate > preMaxLength) maxLengthCandidate else preMaxLength }
            }
        }
        return columnLength
    }

    private fun isTableLine(str: String): Boolean {
        return isStringMatchesRegExp("^\\s*$DEL_REGEXP.*$DEL_REGEXP", str)
    }

    private fun isCurrentTableLine(str: String): Boolean {
        return isTableLine(str) && str.split(DEL_REGEXP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size == currentTableCellsCount
    }

    private fun getCellsFromTableLine(line: String): List<Cell> {
        val cellsArr = line.split(DEL_REGEXP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val cells = ArrayList<Cell>()
        for (cellIndex in cellsArr.indices) {
            cells.add(Cell(cellsArr[cellIndex].trim { it <= ' ' }))
        }
        if (!cells.isEmpty()) {
            cells.removeAt(0) // Removing first empty cell appearing after split
        }
        return cells
    }

    private fun generateSpaces(spaceCount: Int): String {
        return String(CharArray(spaceCount)).replace("\u0000", " ")
    }

    fun isStringMatchesRegExp(regExp: String, str: String): Boolean {
        val pattern = Pattern.compile(regExp)
        val matcher = pattern.matcher(str)
        return matcher.find()
    }
}
