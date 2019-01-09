class Cell(content: String) {
    var content: String
        internal set
    val contentLength: Int
        get() = content.length

    init {
        this.content = content
    }
}
