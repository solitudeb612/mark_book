package com.yyh.mark_book.process;

import com.intellij.ide.fileTemplates.impl.UrlUtil;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.HashMap;

public class MDFreeMarkProcessor extends AbstractFreeMarkProcessor{
    @Override
    protected Template getTemplate() throws IOException {
        String templateContent = UrlUtil.loadText(MDFreeMarkProcessor.class.getResource("/template/md.ftl"));
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("MDTemplate", templateContent);
        configuration.setTemplateLoader(stringTemplateLoader);
        return configuration.getTemplate("MDTemplate");
    }

    @Override
    protected Object getModel(SourceNoteData sourceNoteData) {
        HashMap model = new HashMap();
        model.put ("topic", sourceNoteData.getTopic());
        model.put("noteList", sourceNoteData.getNoteList());
        return model;
    }

    @Override
    protected Writer getWriter(SourceNoteData sourceNoteData) throws FileNotFoundException, UnsupportedEncodingException {
        String fileName = sourceNoteData.getFilename();
        File file = new File(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
        return bufferedWriter;

    }
}
