package com.yyh.mark_book.window;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.yyh.mark_book.data.DataCenter;
import com.yyh.mark_book.notice.Notice;
import com.yyh.mark_book.process.DefaultSourceNoteData;
import com.yyh.mark_book.process.MDFreeMarkProcessor;
import com.yyh.mark_book.process.Processor;
import freemarker.template.TemplateException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class NoteListWindow {
    private JTextField tfTopic;
    private JButton btnCreate;
    private JButton btnClear;
    private JButton btnClose;
    private JTable tbContent;
    private JPanel contentPanel;



    public void init() {
        this.tbContent.setModel(DataCenter.TABLEMODEL);
        this.tbContent.setEnabled(true);
    }

    public JPanel getContentPanel() {
        return this.contentPanel;
    }


    public NoteListWindow(final Project project, final ToolWindow toolWindow) {

        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("文档标题");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tfTopic = new JTextField();
        tfTopic.setText("");
        panel1.add(tfTopic, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnCreate = new JButton();
        btnCreate.setText("生成文档");
        panel2.add(btnCreate, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnClear = new JButton();
        btnClear.setText("清空列表");
        panel2.add(btnClear, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnClose = new JButton();
        btnClose.setText("关闭");
        panel2.add(btnClose, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        contentPanel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tbContent = new JTable();
        scrollPane1.setViewportView(tbContent);

        this.init();
        this.btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String topic = NoteListWindow.this.tfTopic.getText();
                String fileName = topic + ".md";
                if (topic != null && !"".equals(topic)) {
                    VirtualFile virtualFile = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(), project, project.getBaseDir());
                    if (virtualFile != null) {
                        String path = virtualFile.getPath();
                        String fileFullPath = path + "/" + fileName;
                        Processor processor = new MDFreeMarkProcessor();

                        try {
                            processor.process(new DefaultSourceNoteData(fileFullPath, topic, DataCenter.NOTE_LIST));
                            Notice.success_file_notice();
                        } catch (TemplateException var9) {
                            throw new RuntimeException(var9);
                        } catch (IOException var10) {
                            throw new RuntimeException(var10);
                        }
                    }

                } else {
                    MessageDialogBuilder.yesNo("温馨提示：", "你还没有写mark文件的名字");
                }
            }
        });
        this.btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DataCenter.reset();
            }
        });
        this.btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolWindow.hide();
            }
        });
    }



}

