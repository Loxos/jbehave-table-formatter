
import java.util.*
import java.util.regex.Pattern


private val DEL = "|"
private val DEL_REGEXP = """\|""".toRegex()


fun jBehaveTablesOfSourceLines(sourceLines: LinkedList<String>): List<String> {
    var tableLineCollecting = false
    var startIndex = 0
    for (lineIndex in sourceLines.indices) {
        val l = sourceLines[lineIndex]
        if (tableLineCollecting && !isTableLine(l)) {
            formatTable(sourceLines.subList(startIndex, lineIndex))
            tableLineCollecting = false
        }
        if (!tableLineCollecting && isTableLine(l)) {
            tableLineCollecting = true
            startIndex = lineIndex
        }
        if (tableLineCollecting && lineIndex == sourceLines.size - 1) {
            formatTable(sourceLines.subList(startIndex, sourceLines.size))
            tableLineCollecting = false
        }
    }
    return sourceLines
}

private fun getTables(sourceLines: LinkedList<String>): List<MutableList<String>>{
    val tables = mutableListOf<MutableList<String>>()

    var tableLineCollecting = false
    var startIndex = 0
    for (lineIndex in sourceLines.indices) {
        val l = sourceLines[lineIndex]
        if (tableLineCollecting && !isTableLine(l)) {
            tables.add(sourceLines.subList(startIndex, lineIndex))
            tableLineCollecting = false
        }
        if (!tableLineCollecting && isTableLine(l)) {
            tableLineCollecting = true
            startIndex = lineIndex
        }
        if (tableLineCollecting && lineIndex == sourceLines.size - 1) {
            tables.add(sourceLines.subList(startIndex, sourceLines.size))
            tableLineCollecting = false
        }
    }
    return tables
}

private fun formatTable(table: MutableList<String>) {
    val offset = table.firstOrNull()?.takeWhile(Char::isWhitespace)?.length ?: 0
    val columnLengthMap = getColumnLengthMap(table)
    table.map { line -> formatTableLine(line, columnLengthMap, offset) }.toList(table)
}

private fun formatTableLine(line: String, columnLengthMap: Map<Int, Int>, offset: Int) : String{
    val cells = getCellsFromTableLine(line)
    val prefix = " ".repeat(offset) + DEL + " "
    return columnLengthMap.map { (index, length) ->
        val cell = cells.getOrNull(index) ?: Cell("")
        cell.content.padEnd(length)
    }.joinToString(" $DEL ", prefix, " $DEL")
}

private fun getColumnLengthMap(table: List<String>): Map<Int, Int> {
    val columnLengthMap = mutableMapOf<Int, Int>()
    table
        .map { tableLine -> getCellsFromTableLine(tableLine) }
            .forEach{ lineCells ->
                lineCells.forEachIndexed{ index, cell ->
                    if(cell.contentLength > columnLengthMap[index] ?: 0){
                        columnLengthMap[index] = cell.contentLength
                    }
                }
            }
    return columnLengthMap
}

private fun isTableLine(str: String): Boolean {
    return isStringMatchesRegExp("""^\s*$DEL_REGEXP.*$DEL_REGEXP""", str)
}

private fun getCellsFromTableLine(line: String): List<Cell> {
    return line.split(DEL_REGEXP)
            .map { it.trim{ it <= ' '} }
            .dropWhile { it.isEmpty() }
            .dropLastWhile { it.isEmpty() }
            .map { Cell(it) }
}

fun isStringMatchesRegExp(regExp: String, str: String): Boolean {
    val pattern = Pattern.compile(regExp)
    val matcher = pattern.matcher(str)
    return matcher.find()
}

/**
 * overwrite list by this list content
 */
fun <T> List<T>.toList(list: MutableList<T>){
    list.clear()
    this.forEach { list.add(it) }
}

