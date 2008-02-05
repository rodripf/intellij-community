package com.intellij.xdebugger.impl.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorComboBoxEditor;
import com.intellij.ui.EditorComboBoxRenderer;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.impl.XDebuggerHistoryManager;
import com.intellij.xdebugger.XSourcePosition;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author nik
 */
public class XDebuggerExpressionComboBox extends XDebuggerEditorBase {
  private final ComboBox myComboBox;
  private EditorComboBoxEditor myEditor;
  private final XDebuggerEditorsProvider myDebuggerEditorsProvider;
  private String myExpression;

  public XDebuggerExpressionComboBox(final @NotNull Project project, final @NotNull XDebuggerEditorsProvider debuggerEditorsProvider, final @Nullable @NonNls String historyId,
                                     final @Nullable XSourcePosition sourcePosition) {
    super(project, debuggerEditorsProvider, historyId, sourcePosition);
    myDebuggerEditorsProvider = debuggerEditorsProvider;
    myComboBox = new ComboBox();
    myComboBox.setEditable(true);
    myExpression = "";
    Dimension minimumSize = new Dimension(myComboBox.getMinimumSize());
    minimumSize.width = 100;
    myComboBox.setMinimumSize(minimumSize);
    initEditor();
    fillComboBox();
  }

  public ComboBox getComboBox() {
    return myComboBox;
  }

  public JComponent getComponent() {
    return myComboBox;
  }

  public void setEnabled(boolean enable) {
    if (enable == myComboBox.isEnabled()) return;

    myComboBox.setEnabled(enable);
    myComboBox.setEditable(enable);

    if (enable) {
      initEditor();
    }
    else {
      myExpression = getText();
    }
  }

  private void initEditor() {
    myEditor = new EditorComboBoxEditor(getProject(), myDebuggerEditorsProvider.getFileType()) {
      public void setItem(Object anObject) {
        if (anObject == null) {
          anObject = "";
        }
        super.setItem(createDocument((String)anObject));
      }

      public Object getItem() {
        return ((Document)super.getItem()).getText();
      }
    };
    myComboBox.setEditor(myEditor);
    myEditor.setItem(myExpression);
    myComboBox.setRenderer(new EditorComboBoxRenderer(myEditor));
    myComboBox.setMaximumRowCount(XDebuggerHistoryManager.MAX_RECENT_EXPRESSIONS);
  }

  protected void onHistoryChanged() {
    fillComboBox();
  }

  private void fillComboBox() {
    myComboBox.removeAllItems();
    for (String expression : getRecentExpressions()) {
      myComboBox.addItem(expression);
    }
    if (myComboBox.getItemCount() > 0) {
      myComboBox.setSelectedIndex(0);
    }
  }

  public void setText(final String text) {
    saveTextInHistory(text);
    if (myComboBox.getItemCount() > 0) {
      myComboBox.setSelectedIndex(0);
    }

    if (myComboBox.isEditable()) {
      myEditor.setItem(text);
    }
    else {
      myExpression = text;
    }
  }

  public String getText() {
    return (String)myEditor.getItem();
  }

  public JComponent getPreferredFocusedComponent() {
    return (JComponent)myComboBox.getEditor().getEditorComponent();
  }

  public void selectAll() {
    myComboBox.getEditor().selectAll();
  }
}
