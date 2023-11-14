## markdown笔记生成器插件
[TOC]
### 配置plugin.xml
- plugin.xml中需要配置好开发插件的作者信息，插件表述（注意写英文描述，否则可能有警告），以及插件功能的配置信息
    - plugin.xml
```xml
<!-- Plugin Configuration File. Read more: https://plugins.jetbrains.com/docs/intellij/plugin-configuration-file.html -->
<idea-plugin>
    <!-- 插件id，不可重复，必须唯一。插件的升级后续也是依赖插件id来进行识别的 -->
    <id>com.yyh.mark_book</id>


    <!--  插件名称 -->
    <name>黎吧啦的markdown</name>

    <!-- 插件开发人员，这里写一下开发者的个人信息. -->
    <vendor email="2583194138@qq.com" url="https://www.yyh.com">yyh</vendor>

    <!--  插件描述，这里一般是写插件的功能介绍啥的 -->
    <description><![CDATA[
    This is a versatile plugin designed to assist programmers in adding annotations to their code while also facilitating the generation of comprehensive documentation. Its features include note-taking capabilities, code comprehension support, and automated document creation, enhancing code readability and collaboration within development teams.
    <em>it's called yyh_mark_book_plugin</em>
  ]]></description>

    <change-notes><![CDATA[
      This is a versatile plugin designed to assist programmers in adding annotations to their code while also facilitating the generation of comprehensive documentation. Its features include note-taking capabilities, code comprehension support, and automated document creation, enhancing code readability and collaboration within development teams.<br>
      <em>仅支持生成Markdown形式笔记。</em>
    ]]>
    </change-notes>


    <!--  插件依赖，这里我们默认引用idea自带的依赖即可  -->
    <depends>com.intellij.modules.platform</depends>

    <!-- 定义拓展点，比较少用到，一般是用于你去拓展其他人插件功能拓展点，或者是你的插件扩展了 IntelliJ 平台核心功能才会配置到这里 -->
    <extensions defaultExtensionNs="com.intellij">
        <toolWindow id="MarkBookWindown"
                    secondary="true"
                    anchor="right" factoryClass="com.yyh.mark_book.window.NoteListWindowFactory" icon="/img/markdown.svg">

        </toolWindow>
    </extensions>



    <actions>

        <action id=" PopupAction" class="com.yyh.mark_book.action.PopupAction" text="添加markdown笔记"
                description="添加markdown笔记">
            <add-to-group group-id="EditorPopupMenu" anchor="first"/>
        </action>
    </actions>
</idea-plugin>
```
### 配置build.gradle
- 这是给gradle配置本插件中要用到的依赖坐标，以及依赖源，以及插件的版本信息，idea的版本信息，gradle的版本信息，jdk的版本信息，这四者如果不兼容可能会build不成功
    - build.gradle
```gradle
plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.16.0"
}

group = "com.yyh"
version = "1.0-SNAPSHOT"

repositories {
  maven { url 'https://maven.aliyun.com/repository/central/'}
  maven { url 'https://maven.aliyun.com/repository/public/' }
  maven { url 'https://maven.aliyun.com/repository/google/' }
  maven { url 'https://maven.aliyun.com/repository/jcenter/'}
  maven { url 'https://maven.aliyun.com/repository/gradle-plugin'}
}

dependencies {
  testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.3'
  testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.3'
  testRuntimeOnly 'org.junit.vintage:junit-vintage-engine:5.9.3'
  testImplementation 'org.junit.jupiter:junit-jupiter-params:5.9.3'
  implementation fileTree(dir: 'lib', includes: ['forms_rt-142.1.jar'])
  implementation fileTree(dir: 'lib', includes: ['freemarker-2.3.28.jar'])

}

intellij {
  version = '2023.1.2'
}

tasks.withType(JavaCompile) {
  options.encoding = 'UTF-8'
  options.compilerArgs += ['-Xlint:unchecked', '-Xlint:deprecation', '-parameters']
}

//jar {
//  from configurations.compileClasspath
//  from ('lib/freemarker-2.3.28.jar')
//  manifest {
//    attributes 'Main-Class': 'com.yyh.mark_book.MainClass' // 替换为您的主类的包名和类名
//  }
//}

patchPluginXml {
  // 注意这个版本号不能高于上面intellij的version,否则runIde会报错
  sinceBuild = '231'
  untilBuild = '232.*'
}

```
### 配置gradle的版本
- 上面说错了，gradle的版本信息在这里指定，当时就是版本不对一直构建不好，最终在idea的构建日志的提示下，修改了插件的版本
    - gradle-wrapper.properties
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.1.1-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists

```
### 从编辑区右键获取文本
- 在编辑区选中代码后右键点击添加为markdown笔记，就会获取到编辑区的这段代码，以及这段代码的文件来源，之后会交给NoteData(小标题+描述+代码段）
    - PopupAction.java
```java
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

```
### NoteData（小标题+描述+代码段）
- NoteData（小标题+描述+代码段）
    - NoteData.java
```java
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

```
### DataCenter是整个markdown文件是数据中心
- 这里有两个东西，一个List<NoteData>，一个DefaultTableMode其中DefaultTableModel就是markbookwindow的数据中心（就是保存那个笔记表格窗口里的内容）
    - DataCenter.java
```java
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

```
### 转换器
- 将List<NoteData>里的内容存放到DefaultTableModel中需要转换
    - DataConvert.java
```java
package com.yyh.mark_book.data;

public class DataConvert {
    public static String[] convert(NoteData noteData){
        String[] raw = new String[4];
        raw[0] = noteData.getTitle();
        raw[1] = noteData.getMark();
        raw[2] = noteData.getFilename();
        raw[3] = noteData.getContent();
        return raw;
    }
}

```
### 点击添加markdown笔记后弹出笔记对话框
- 这里会填写好笔记的小标题和描述，点击添加笔记到列表后，同时将上一步从编辑去获取到的代码段一起存放到NoteData中
    - AddNoteDialog.java
```java
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

```
### 定义一个NoteListWindowFactory
- 工厂模式，由工厂创建ToolWindow（笔记表格视窗）
    - NoteListWindowFactory.java
```java
package com.yyh.mark_book.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class NoteListWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        //创建出NoteListWindow对象
        NoteListWindow noteListWindow = new NoteListWindow(project, toolWindow);
        //获取内容工厂的实例
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        //获取用于toolWindow显示的内容
        Content content = contentFactory.createContent(noteListWindow.getContentPanel(), "", false);
        //给toolWindow设置内容
        toolWindow.getContentManager().addContent(content);

    }
}

```
### 笔记表格视图及生成markdown文档
- 这里的NoteListWindow是用Swing UI Designer生成的，但是生成的代码并没有new对象，而是由一个.form文件联系和维护的，这样在idea中运行没有问题，但是打包部署后就包Not find的空指针异常（其实需要设置一下SwingDesinger的配置，然后在将整个重命名一下，又重命名回来就生成代码了）
    - NoteListWindow.java
```java
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


```
### 生成文档的过程
- 这里定义好一个接口，专门处理文件按数据的接口）
    - Processor.java
```java
package com.yyh.mark_book.process;

import freemarker.template.TemplateException;

import java.io.IOException;

public interface Processor {
    public void process(SourceNoteData sourceNoteData                                                             ) throws TemplateException, IOException;
}

```
### 笔记本数据抽象为一个接口
- 最终静态化的数据文件类型可能很多，但是都会包含这么三部分内容。注意这样的设计模式，通过结构约束规范的同时，方便编程
    - SourceNoteData.java
```java
package com.yyh.mark_book.process;

import com.yyh.mark_book.data.NoteData;

import java.util.List;

public interface SourceNoteData {
    public String getFilename();
    public String getTopic();
    public List<NoteData> getNoteList();
}

```
### 默认为markdown笔记类型
- 注意这样的设计模式，多态
    - DefaultSourceNoteData.java
```java
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

```
### 定义markdown文件生成的宏观三大步骤
- 1. 获取要处理的数据 2. 获取一个模板 3. 用一只笔将数据写入模板
    - AbstractFreeMarkProcessor.java
```java
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

```
### 模板内容
- 这里是markdown模板
    - md.ftl
```ftl
## ${topic}
[TOC]
<#list noteList as note>
### ${note.title}
- ${note.mark}
    - ${note.filename}
```${note.fileType}
${note.content}
</#list>
```



```
### markdown笔记生成的具体步骤
- markdown笔记生成的具体步骤
    - MDFreeMarkProcessor.java
```java
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

```

### 部署

切换到脱机模式否则构建不成功

![image-20231109140443355](C:\Users\14650\AppData\Roaming\Typora\typora-user-images\image-20231109140443355.png)

再点击buildPlugin （别点错了）

![image-20231109144007029](C:\Users\14650\AppData\Roaming\Typora\typora-user-images\image-20231109144007029.png)

最后找到插件的zip包

![image-20231109142504256](C:\Users\14650\AppData\Roaming\Typora\typora-user-images\image-20231109142504256.png)

ps:别选成了lib里面的

然后idea中磁盘安装插件就行



参考文章:

https://blog.csdn.net/y4x5M0nivSrJaY3X92c/article/details/105852046       (部署)

https://blog.csdn.net/stpice/article/details/72858913          (swing UI Designer没有new组件代码)



[Caused by: java.lang.NullPointerException: getHeaderField(“Location“) must not be null_Beer Home的博客-CSDN博客](https://blog.csdn.net/qq_30009669/article/details/131209866)       （离线模式运行）



[如何使用Gradle添加依赖_笔记大全_设计学院 (python100.com)](https://www.python100.com/html/84995.html)  （gradle添加依赖）

[Idea插件开发-开发自己的第一款idea插件 - 掘金 (juejin.cn)](https://juejin.cn/post/6844904127990857742)（markdown插件）
