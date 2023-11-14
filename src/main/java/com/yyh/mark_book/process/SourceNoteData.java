package com.yyh.mark_book.process;

import com.yyh.mark_book.data.NoteData;

import java.util.List;

public interface SourceNoteData {
    public String getFilename();
    public String getTopic();
    public List<NoteData> getNoteList();
}
