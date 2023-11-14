package com.yyh.mark_book.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.yyh.mark_book.dialog.AddNoteDialog;

public class PopupAction extends AnAction {
    //用于保存选中的文本
    private static String selectedText;
    //用于保存选中文本的来源文件
    private static String filename;
    //来源文件的类型
    private static String fileType;

    public static String getFilename() {
        return filename;
    }

    public static void setFilename(String filename) {
        PopupAction.filename = filename;
    }

    public static String getFileType() {
        return fileType;
    }

    public static void setFileType(String fileType) {
        PopupAction.fileType = fileType;
    }



    public static String getSelectedText() {
        return selectedText;
    }

    public static void setSelectedText(String selectedText) {
        PopupAction.selectedText = selectedText;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {

        //获取选中的文本
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        SelectionModel selectionModel = editor.getSelectionModel();
        selectedText = selectionModel.getSelectedText();



        //获取文件名称
        filename = e.getRequiredData(CommonDataKeys.PSI_FILE).getViewProvider().getVirtualFile().getName();

        //获取文件类型
        fileType = filename.substring(filename.lastIndexOf(".") + 1);


        AddNoteDialog addNoteDialog = new AddNoteDialog();
        addNoteDialog.show();
    }
}
