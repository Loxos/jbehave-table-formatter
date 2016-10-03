import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by Alex Rybkin (alexamber91@gmail.com) on 27.09.2016.
 */
public class FormatExampleTablesOfOpenedFile extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor == null) {
            return;
        }
        final Document document = editor.getDocument();
        if (document == null) {
            return;
        }
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        if (virtualFile == null) {
            return;
        }
        List<String> newTextLinesList;
        try {
            newTextLinesList = new ExampleTableFormatter()
                    .formatExampleTablesOfSourceLines(new LinkedList<>(Arrays.asList(document.getText().split("\\r?\\n"))));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        String newText = String.join("\n", newTextLinesList);

        final Runnable readRunner = new Runnable() {
            @Override
            public void run() {
                document.setText(newText);
            }
        };
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                    @Override
                    public void run() {
                        ApplicationManager.getApplication().runWriteAction(readRunner);
                    }
                }, "ExampleTableFormatter", null);
            }
        });
    }
}
