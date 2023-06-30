package com.github.bigmouth.cn.talkx.services;

import com.google.gson.JsonObject;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.TextRange;

/**
 * @author allen
 * @date 2023/6/2
 * @since 1.0.0
 */
public class EditorHelper {

    public static JsonObject getFileSelectionDetails(Editor editor) {
        return getFileSelectionDetails(editor, false);
    }

    public static JsonObject getFileSelectionDetails(Editor editor, boolean isCodeNote) {
        if (editor == null) {
            return getEmptyRange();
        } else {
            JsonObject range = new JsonObject();
            Document document = editor.getDocument();
            int endOffset = Integer.MAX_VALUE;
            boolean userSelectedText = true;
            String editorText = null;
            int startOffset;
            if (editor.getSelectionModel().hasSelection()) {
                startOffset = editor.getSelectionModel().getSelectionStart();
                endOffset = editor.getSelectionModel().getSelectionEnd();
                editorText = editor.getSelectionModel().getSelectedText();
            } else {
                userSelectedText = false;
                LogicalPosition logicalPos = editor.getCaretModel().getCurrentCaret().getLogicalPosition();
                int line = logicalPos.line;
                startOffset = editor.logicalPositionToOffset(new LogicalPosition(line, 0));
                endOffset = editor.logicalPositionToOffset(new LogicalPosition(line, Integer.MAX_VALUE));
                editorText = lspPosition(document, endOffset).get("text").toString();
                if (isCodeNote && editor.getDocument().getText().trim() != null) {
                    for(int linenumber = 0; linenumber < editor.getDocument().getLineCount(); ++linenumber) {
                        startOffset = editor.logicalPositionToOffset(new LogicalPosition(linenumber, 0));
                        endOffset = editor.logicalPositionToOffset(new LogicalPosition(linenumber, Integer.MAX_VALUE));
                        String tempText = lspPosition(document, endOffset).get("text").toString();
                        if (tempText != null && !tempText.trim().equals("\"\"") && !tempText.trim().isEmpty()) {
                            editorText = tempText;
                            break;
                        }
                    }
                }
            }

            range.add("start", lspPosition(document, startOffset));
            range.add("end", lspPosition(document, endOffset));
            JsonObject returnObj = new JsonObject();
            returnObj.add("range", range);
            returnObj.addProperty("selectedText", editorText);
            returnObj.addProperty("userSelectedText", userSelectedText);
            return returnObj;
        }
    }

    public static JsonObject lspPosition(Document document, int offset) {
        int line = document.getLineNumber(offset);
        int lineStart = document.getLineStartOffset(line);
        String lineTextBeforeOffset = document.getText(TextRange.create(lineStart, offset));
        int column = lineTextBeforeOffset.length();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("line", line);
        jsonObject.addProperty("column", column);
        jsonObject.addProperty("text", lineTextBeforeOffset);
        return jsonObject;
    }

    public static JsonObject getEmptyRange() {
        JsonObject jsonObject = new JsonObject();
        JsonObject range = new JsonObject();
        JsonObject lineCol = new JsonObject();
        lineCol.addProperty("line", "0");
        lineCol.addProperty("column", "0");
        lineCol.addProperty("text", "0");
        range.add("start", lineCol);
        range.add("end", lineCol);
        jsonObject.add("range", range);
        jsonObject.addProperty("selectedText", "");
        return jsonObject;
    }
}
