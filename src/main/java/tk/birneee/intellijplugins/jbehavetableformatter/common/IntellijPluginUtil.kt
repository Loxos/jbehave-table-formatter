package tk.birneee.intellijplugins.jbehavetableformatter.common

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile

val Document.file: VirtualFile?
    get() {
        return FileDocumentManager.getInstance().getFile(this)
    }

val Project.selectedTextEditor: Editor?
    get() {
        return FileEditorManager.getInstance(this).selectedTextEditor
    }

fun Project.runWriteActionLater(name: String, groupId: Any? = null, action: () -> Unit) {
    ApplicationManager.getApplication().invokeLater {
        CommandProcessor.getInstance().executeCommand(this, {
            ApplicationManager.getApplication().runWriteAction(action)
        }, name, groupId)
    }
}

fun Document.findAll(regex: Regex): List<RangeMarker> {
    return regex.findAll(this.charsSequence)
            .map { this.createRangeMarker(it, true) }
            .toList()
}

fun Document.createRangeMarker(matchResult: MatchResult, surviveOnExternalChange: Boolean = false) = this.createRangeMarker(matchResult.range.first, matchResult.range.last + 1, surviveOnExternalChange)

fun AnActionEvent.getEditor() = this.getData(CommonDataKeys.EDITOR)

fun AnActionEvent.getFile() = this.getData(CommonDataKeys.VIRTUAL_FILE)

var RangeMarker.text : String
    get() { return this.document.getText(this.toTextRange())}
    set(value) { this.document.replaceString(this.startOffset, this.endOffset, value) }

fun RangeMarker.toTextRange() = TextRange(startOffset, endOffset)

fun List<RangeMarker>.groupAdjacentLines(): List<List<RangeMarker>> {
    return this.groupAdjacentItems {
        it.document.getLineNumber(it.startOffset)
    }
}