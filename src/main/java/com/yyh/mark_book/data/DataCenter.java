package com.yyh.mark_book.data;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;

public class DataCenter {

    public static List<NoteData> NOTE_LIST = new LinkedList<>();

    public static String[] HEAD = {"标题", "备注", "文件名", "代码段"};

    //表头Head(一维数组） +  notedata(二维数组）  组成一张表（DefaultTableModel)
    public static DefaultTableModel TABLEMODEL = new DefaultTableModel(null, HEAD);

    public static void reset(){
        NOTE_LIST.clear();
        TABLEMODEL.setDataVector(null,HEAD);
    }
}
