package tk.birneee.intellijplugins.jbehavetableformatter.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import tk.birneee.intellijplugins.jbehavetableformatter.common.*
import tk.birneee.intellijplugins.jbehavetableformatter.formatter.formatTables

private const val COMMAND_TABLE_FORMATTER = "jBehaveTableFormatter"
private const val SUPPORTED_FILE_ENDING = "story"

class FormatjBehaveTablesOfOpenFile : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible =
                e.project != null
                && e.getEditor() != null
                && e.getFile()?.name?.endsWith(SUPPORTED_FILE_ENDING) ?: false
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = project.selectedTextEditor ?: return
        val document = editor.document
        document.file ?: return

        project.runWriteActionLater(COMMAND_TABLE_FORMATTER) {
            document.formatTables()
        }
    }
}