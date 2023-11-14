package com.yyh.mark_book.data;

public class NoteData {
    //小标题
    private String title;
    //小标题下的记录
    private String mark;
    //代码内容
    private String content;
    //摘录自文件名称（从哪个文件右键添加为markdown文档）
    private String filename;
    //摘录自文件类型
    private String fileType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "NoteData{" +
                "title='" + title + '\'' +
                ", mark='" + mark + '\'' +
                ", content='" + content + '\'' +
                ", filename='" + filename + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public NoteData(String title, String mark, String content, String filename, String fileType) {
        this.title = title;
        this.mark = mark;
        this.content = content;
        this.filename = filename;
        this.fileType = fileType;
    }
}
