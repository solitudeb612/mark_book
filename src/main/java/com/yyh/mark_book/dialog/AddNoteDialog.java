package com.yyh.mark_book.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.ui.EditorTextField;
import com.yyh.mark_book.action.PopupAction;
import com.yyh.mark_book.data.DataCenter;
import com.yyh.mark_book.data.DataConvert;
import com.yyh.mark_book.data.NoteData;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AddNoteDialog extends DialogWrapper {
    private EditorTextField tfTitle;
    private EditorTextField tfMark;

    public AddNoteDialog() {
        super(true);
        setTitle("添加笔记注释");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tfTitle = new EditorTextField("笔记标题");
        tfMark = new EditorTextField("笔记内容");
        tfMark.setPreferredSize(new Dimension(200, 100));
        panel.add(tfTitle, BorderLayout.NORTH);
        panel.add(tfMark, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel();
        JButton button = new JButton("添加笔记到列表");
        button.addActionListener(e -> {
            String title = tfTitle.getText();
            String mark = tfMark.getText();
            String selectedText = PopupAction.getSelectedText();
            String filename = PopupAction.getFilename();
            String filetype = PopupAction.getFileType();

            NoteData noteData = new NoteData(title, mark, selectedText, filename, filetype);
            DataCenter.NOTE_LIST.add(noteData);
            String[] convert = DataConvert.convert(noteData);
            DataCenter.TABLEMODEL.addRow(convert);
            MessageDialogBuilder.yesNo("操作结果", "添加成功");
            AddNoteDialog.this.dispose();



        });
        panel.add(button);
        return panel;
    }
}
