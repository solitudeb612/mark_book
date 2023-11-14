package com.yyh.mark_book.process;

import com.yyh.mark_book.data.NoteData;

import javax.xml.transform.sax.SAXResult;
import java.util.List;

public class DefaultSourceNoteData implements SourceNoteData{
    private String filename;
    private String topic;
    private List<NoteData> noteDataList;

    public DefaultSourceNoteData(String filename, String topic, List<NoteData> noteDataList) {
        this.filename = filename;
        this.topic = topic;
        this.noteDataList = noteDataList;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public List<NoteData> getNoteList() {
        return noteDataList;
    }
}
