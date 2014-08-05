package ru.compscicenter.edide.ui;

import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import ru.compscicenter.edide.course.UserTest;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import java.awt.*;

public class TestContentPanel extends JPanel {
  public static final Dimension PREFERRED_SIZE = new Dimension(300, 200);
  private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
  private final JTextArea myInputArea = new JTextArea();
  private final JTextArea myOutputArea = new JTextArea();
  public TestContentPanel(UserTest userTest) {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    initContentLabel("input", myInputArea);
    myInputArea.getDocument().addDocumentListener(new BufferUpdater(userTest.getInputBuffer()));
    myInputArea.getDocument().addDocumentListener(new BufferUpdater(userTest.getOutputBuffer()));
    initContentLabel("output", myOutputArea);
  }

  private void initContentLabel(final String headerText, @NotNull final JTextArea contentArea) {
    JLabel headerLabel = new JLabel(headerText);
    headerLabel.setFont(HEADER_FONT);
    this.add(headerLabel);
    this.add(new JSeparator(SwingConstants.HORIZONTAL));
    JScrollPane scroll = new JBScrollPane(contentArea);
    scroll.setPreferredSize(PREFERRED_SIZE);
    this.add(scroll);
  }

  public void addInputContent(final String content) {
    myInputArea.setText(content);
  }

  public  void addOutputContent(final String content) {
    myOutputArea.setText(content);
  }

  private class BufferUpdater extends DocumentAdapter {
    private final StringBuffer myBuffer;

    private BufferUpdater(StringBuffer buffer) {
      myBuffer = buffer;
    }

    @Override
    protected void textChanged(DocumentEvent e) {
      myBuffer.delete(0, myBuffer.length());
      try {
        myBuffer.append(e.getDocument().getText(0, e.getDocument().getLength()));
      }
      catch (BadLocationException e1) {
        e1.printStackTrace();
      }
    }
  }
}
