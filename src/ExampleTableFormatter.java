import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExampleTableFormatter {

    private final String DEL = "|";
    private final String DEL_REGEXP = "\\|";
    private int currentTableCellsCount = 0;

    public List<String> formatExampleTablesOfSourceLines(final LinkedList<String> sourceLines) throws IOException {
        boolean tableLineCollecting = false;
        int startIndex = 0;
        for (int lineIndex = 0; lineIndex < sourceLines.size(); lineIndex++) {
            String l = sourceLines.get(lineIndex);
            if (tableLineCollecting && !isCurrentTableLine(l)) {
                formatTable(sourceLines.subList(startIndex, lineIndex));
                tableLineCollecting = false;
            }
            if (!tableLineCollecting && isTableLine(l)) {
                tableLineCollecting = true;
                currentTableCellsCount = l.split(DEL_REGEXP).length;
                startIndex = lineIndex;
            }
            if (tableLineCollecting && lineIndex == sourceLines.size() - 1) {
                formatTable(sourceLines.subList(startIndex, sourceLines.size()));
                tableLineCollecting = false;
            }
        }
        return sourceLines;
    }

    private void formatTable(List<String> exampleTable) {
        Map<Integer, Integer> columnLengthMap = getColumnLengthMap(exampleTable);
        for (int lineIndex = 0; lineIndex < exampleTable.size(); lineIndex++) {
            List<Cell> cells = getCellsFromTableLine(exampleTable.get(lineIndex));
            exampleTable.remove(lineIndex);
            exampleTable.add(lineIndex, DEL + cells.stream().map(
                    c -> c.getContent() + generateSpaces(columnLengthMap.get(cells.indexOf(c)) - c.getContentLength()))
                    .collect(Collectors.joining(DEL)) + DEL);
        }
    }

    private Map<Integer, Integer> getColumnLengthMap(List<String> exampleTable) {
        Map<Integer, Integer> columnLength = new HashMap<>();
        exampleTable.forEach(line -> {
            List<Cell> cells = getCellsFromTableLine(line);
            for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
                Cell cell = cells.get(cellIndex);
                columnLength.merge(cellIndex, cell.getContentLength(), (preMaxLength,
                        maxLengthCandidate) -> maxLengthCandidate > preMaxLength ? maxLengthCandidate : preMaxLength);
            }
        });
        return columnLength;
    }

    private boolean isTableLine(final String str) {
        return isStringMatchesRegExp("^" + DEL_REGEXP + ".*" + DEL_REGEXP, str);
    }

    private boolean isCurrentTableLine(final String str) {
        return isTableLine(str) && str.split(DEL_REGEXP).length == currentTableCellsCount;
    }

    private List<Cell> getCellsFromTableLine(final String line) {
        String[] cellsArr = line.split(DEL_REGEXP);
        List<Cell> cells = new ArrayList<>();
        for (int cellIndex = 0; cellIndex < cellsArr.length; cellIndex++) {
            cells.add(new Cell(cellsArr[cellIndex].trim()));
        }
        if (!cells.isEmpty()) {
            cells.remove(0); // Removing first empty cell appearing after split
        }
        return cells;
    }

    private String generateSpaces(final int spaceCount) {
        return new String(new char[spaceCount]).replace("\0", " ");
    }

    public boolean isStringMatchesRegExp(final String regExp, final String str) {
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
