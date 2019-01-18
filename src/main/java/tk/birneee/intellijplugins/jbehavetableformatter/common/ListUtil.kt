package tk.birneee.intellijplugins.jbehavetableformatter.common

fun <T> List<T>.groupAdjacentItems(project: (T) -> Int): List<List<T>> {
    if(this.isEmpty()){
       return emptyList()
    }

    val sortedItems = this.sortedBy(project)
    val adjacentItems = mutableListOf<List<T>>()
    var currentGroup = mutableListOf<T>()
    currentGroup.add(sortedItems[0])

    for (i in 0 until sortedItems.size - 1) {
        if (project(sortedItems[i + 1]) == project(sortedItems[i]) + 1) {
            currentGroup.add(sortedItems[i + 1])
        } else {
            adjacentItems.add(currentGroup)
            currentGroup = mutableListOf<T>()
            currentGroup.add(sortedItems[i + 1])
        }
    }

    adjacentItems.add(currentGroup)

    return adjacentItems
}