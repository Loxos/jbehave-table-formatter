
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import java.util.*

class FormatjBehaveTablesOfOpenedFile : AnAction() {

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible =
                e.project != null
                && e.getEditor() != null
                && e.getFile()?.name?.endsWith("story") ?: false
    }

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document) ?: return
        val newTextLinesList: List<String>
        newTextLinesList = jBehaveTablesOfSourceLines(LinkedList(Arrays.asList(*document.text.split("""\r?\n""".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())))

        val newText = newTextLinesList.joinToString("\n")

        val readRunner = { document.setText(newText) }
        ApplicationManager.getApplication().invokeLater {
            CommandProcessor.getInstance().executeCommand(project, {
                ApplicationManager.getApplication().runWriteAction(readRunner)
            }, "jBehaveTableFormatter", null)
        }
    }
}

private fun Document.jBehaveTableRowSequence() = this.charsSequence.lineSequence().filterIndexed{ index, line -> line.matches("""\s*\|.*""".toRegex())}

private fun AnActionEvent.getEditor() = this.getData(CommonDataKeys.EDITOR)

private fun AnActionEvent.requireEditor() = this.getRequiredData(CommonDataKeys.EDITOR)

private fun AnActionEvent.getFile() = this.getData(CommonDataKeys.VIRTUAL_FILE)