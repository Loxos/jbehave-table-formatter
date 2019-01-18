package tk.birneee.intellijplugins.jbehavetableformatter.formatter

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.RangeMarker
import tk.birneee.intellijplugins.jbehavetableformatter.common.*

private val TABLE_ROW_REGEX = """(?m)^\h*\|.*$""".toRegex()
private const val ENTRY_SEPARATOR = "|"
private val ENTRY_SEPARATOR_REGEXP = """\|""".toRegex()

fun List<String>.formatTable(): List<String> {
    val columnLengths = getColumnLengths(this)
    val offset = this.firstOrNull()?.takeWhile(Char::isWhitespace)?.length ?: 0
    return this.map { it.formatTableRow(columnLengths, offset) }
}

fun String.formatTableRow(columnLengthMap: Map<Int, Int>, offset: Int = 0): String {
    val entries = this.getTableRowEntries()
    val prefix = " ".repeat(offset) + ENTRY_SEPARATOR + " "
    return columnLengthMap.map { (index, length) ->
        val entry = entries.getOrNull(index) ?: ""
        entry.padEnd(length)
    }.joinToString(" $ENTRY_SEPARATOR ", prefix, " $ENTRY_SEPARATOR")
}

fun getColumnLengths(table: List<String>): Map<Int, Int> {
    val columnLengthMap = mutableMapOf<Int, Int>()
    table
            .map { it.getTableRowEntries() }
            .forEach { lineEntries ->
                lineEntries.forEachIndexed { index, entry ->
                    if (entry.length > columnLengthMap[index] ?: 0) {
                        columnLengthMap[index] = entry.length
                    }
                }
            }
    return columnLengthMap
}

fun String.getTableRowEntries(): List<String> {
    return this.split(ENTRY_SEPARATOR_REGEXP)
            .map { it.trim { it <= ' ' } }
            .dropWhile { it.isEmpty() }
            .dropLastWhile { it.isEmpty() }
}

fun Document.formatTables() {
    val tables = this.findTables()
    tables.forEach { table ->
        table.formatTable()
    }
}

fun List<RangeMarker>.formatTable() {
    this.map { it.text }.formatTable().forEachIndexed { i, rowText ->
        val rangeMarker = this[i]
        rangeMarker.text = rowText
    }
}

fun Document.findTables(): List<List<RangeMarker>> {
    return this.findAll(TABLE_ROW_REGEX)
            .groupAdjacentLines()
}

