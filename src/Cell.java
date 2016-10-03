/**
 * Created by Alex Rybkin on 28.09.2016.
 */
public class Cell {
    String content;

    public Cell(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
    public int getContentLength() {
        return content.length();
    }
}
