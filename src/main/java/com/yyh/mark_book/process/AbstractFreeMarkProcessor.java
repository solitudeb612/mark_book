package com.yyh.mark_book.process;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public abstract class AbstractFreeMarkProcessor implements Processor {

    protected abstract Template getTemplate() throws IOException;
    protected abstract Object getModel(SourceNoteData sourceNoteData);
    protected abstract Writer getWriter(SourceNoteData sourceNoteData) throws FileNotFoundException, UnsupportedEncodingException;

    @Override
    public void process(SourceNoteData sourceNoteData) throws TemplateException, IOException {
        Template template = getTemplate();
        Object model = getModel(sourceNoteData);
        Writer writer = getWriter(sourceNoteData);
        template.process(model,writer);
    }
}
