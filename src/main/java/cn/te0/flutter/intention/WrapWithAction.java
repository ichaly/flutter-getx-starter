package cn.te0.flutter.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Chaly
 */
public abstract class WrapWithAction extends PsiElementBaseIntentionAction implements IntentionAction {
    final SnippetType snippetType;
    PsiElement callExpressionElement;

    public WrapWithAction(SnippetType snippetType) {
        this.snippetType = snippetType;
    }

    @Override
    @NotNull
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        final PsiFile currentFile = getCurrentFile(project, editor);
        if (currentFile != null && !currentFile.getName().endsWith(".dart")) {
            return false;
        }
        if (!psiElement.toString().equals("PsiElement(IDENTIFIER)")) {
            return false;
        }
        callExpressionElement = WrapHelper.callExpressionFinder(psiElement);
        if (callExpressionElement == null) {
            return false;
        }
        return true;
    }


    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
        throws IncorrectOperationException {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            invokeSnippetAction(project, editor, snippetType);
        });
    }

    protected void invokeSnippetAction(@NotNull Project project, Editor editor, SnippetType snippetType) {
        final Document document = editor.getDocument();
        final PsiElement element = callExpressionElement;
        final TextRange elementSelectionRange = element.getTextRange();
        final int offsetStart = elementSelectionRange.getStartOffset();
        final int offsetEnd = elementSelectionRange.getEndOffset();
        if (!WrapHelper.isSelectionValid(offsetStart, offsetEnd)) {
            return;
        }
        final String selectedText = document.getText(TextRange.create(offsetStart, offsetEnd));
        final String replaceWith = Snippets.getSnippet(snippetType, selectedText);
        // wrap the widget:
        WriteCommandAction.runWriteCommandAction(project, () -> {
            document.replaceString(offsetStart, offsetEnd, replaceWith);
        });
        // place cursors to specify types:
        final String prefixSelection = Snippets.PREFIX_SELECTION;
        final String[] snippetArr = {Snippets.GETX_SNIPPET_KEY};
        final CaretModel caretModel = editor.getCaretModel();
        caretModel.removeSecondaryCarets();
        for (String snippet : snippetArr) {
            if (!replaceWith.contains(snippet)) {
                continue;
            }
            final int caretOffset = offsetStart + replaceWith.indexOf(snippet);
            final VisualPosition visualPos = editor.offsetToVisualPosition(caretOffset);
            caretModel.addCaret(visualPos);
            // select snippet prefix keys:
            final Caret currentCaret = caretModel.getCurrentCaret();
            currentCaret.setSelection(caretOffset, caretOffset + prefixSelection.length());
        }
        final Caret initialCaret = caretModel.getAllCarets().get(0);
        if (!initialCaret.hasSelection()) {
            // initial position from where was triggered the intention action
            caretModel.removeCaret(initialCaret);
        }
        // reformat file:
        ApplicationManager.getApplication().runWriteAction(() -> {
            PsiDocumentManager.getInstance(project).commitDocument(document);
            final PsiFile currentFile = getCurrentFile(project, editor);
            if (currentFile != null) {
                final String unformattedText = document.getText();
                final int unformattedLineCount = document.getLineCount();
                CodeStyleManager.getInstance(project).reformat(currentFile);
                final int formattedLineCount = document.getLineCount();
                // file was incorrectly formatted, revert formatting
                if (formattedLineCount > unformattedLineCount + 3) {
                    document.setText(unformattedText);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                }
            }
        });
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }

    private PsiFile getCurrentFile(Project project, Editor editor) {
        return PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
    }
}