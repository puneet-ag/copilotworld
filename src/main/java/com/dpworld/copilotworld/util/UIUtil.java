package com.dpworld.copilotworld.util;

import com.dpworld.copilotworld.avatar.AvatarBundle;
import com.dpworld.copilotworld.panel.SmartScroller;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.roots.ui.componentsList.components.ScrollablePanel;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.JBColor;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static javax.swing.event.HyperlinkEvent.EventType.ACTIVATED;

public class UIUtil {

  public static JTextPane createTextPane(String text) {
    return createTextPane(text, true);
  }

  public static JTextPane createTextPane(String text, boolean opaque) {
    return createTextPane(text, opaque, UIUtil::handleHyperlinkClicked);
  }

  public static JTextPane createTextPane(String text, boolean opaque, HyperlinkListener listener) {
    var textPane = new JTextPane();
    textPane.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, true);
    textPane.addHyperlinkListener(listener);
    textPane.setContentType("text/html");
    textPane.setEditable(false);
    textPane.setText(text);
    textPane.setOpaque(opaque);
    ((DefaultCaret) textPane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    return textPane;
  }

  public static JBTextArea createTextArea(String initialValue) {
    var textArea = new JBTextArea(initialValue);
    textArea.setRows(3);
    textArea.setBorder(JBUI.Borders.compound(
        JBUI.Borders.customLine(JBColor.border()),
        JBUI.Borders.empty(4)));
    textArea.setLineWrap(true);
    return textArea;
  }

  public static JButton createIconButton(Icon icon) {
    var button = new JButton(icon);
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    return button;
  }

  public static JScrollPane createScrollPaneWithSmartScroller(ScrollablePanel scrollablePanel) {
    var scrollPane = ScrollPaneFactory.createScrollPane(scrollablePanel, true);
    new SmartScroller(scrollPane);
    return scrollPane;
  }

  public static void setEqualLabelWidths(JPanel firstPanel, JPanel secondPanel) {
    var firstLabel = firstPanel.getComponents()[0];
    var secondLabel = secondPanel.getComponents()[0];
    if (firstLabel instanceof JLabel && secondLabel instanceof JLabel) {
      firstLabel.setPreferredSize(secondLabel.getPreferredSize());
    }
  }

  public static JPanel createPanel(JComponent component, String label, boolean resizeX) {
    return UI.PanelFactory.panel(component)
        .withLabel(label)
        .resizeX(resizeX)
        .createPanel();
  }

  public static void handleHyperlinkClicked(HyperlinkEvent event) {
    var url = event.getURL();
    if (ACTIVATED.equals(event.getEventType()) && url != null) {
      try {
        BrowserUtil.browse(url.toURI());
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void addShiftEnterInputMap(JTextArea textArea, AbstractAction onSubmit) {
    textArea.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
    textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "text-submit");
    textArea.getActionMap().put("text-submit", onSubmit);
  }


  public static JPanel createRadioButtonsPanel(List<JBRadioButton> radioButtons) {
    var buttonGroup = new ButtonGroup();
    var radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    for (int i = 0; i < radioButtons.size(); i++) {
      JBRadioButton radioButton = radioButtons.get(i);
      buttonGroup.add(radioButton);
      radioPanel.add(radioButton);
      radioPanel.add(Box.createVerticalStrut(i == radioButtons.size() - 1 ? 8 : 4));
    }
    return withEmptyLeftBorder(radioPanel);
  }

  public static <T extends JComponent> T withEmptyLeftBorder(T component) {
    component.setBorder(JBUI.Borders.emptyLeft(16));
    return component;
  }

  public static JLabel createComment(String messageKey) {
    var comment = ComponentPanelBuilder.createCommentComponent(
        AvatarBundle.get(messageKey), true);
    comment.setBorder(JBUI.Borders.empty(0, 4));
    return comment;
  }

  public static JPanel createForm(Map<String, RadioButtonWithLayout> layouts,
      String initialLayout) {
    JPanel finalPanel = new JPanel(new BorderLayout());
    finalPanel.add(createRadioButtonsPanel(layouts.values().stream().map(
            RadioButtonWithLayout::getRadioButton).toList()),
        BorderLayout.NORTH);
    finalPanel.add(createRadioButtonGroupLayouts(layouts, initialLayout), BorderLayout.CENTER);
    return finalPanel;
  }

  
  public static JPanel createRadioButtonGroupLayouts(
      Map<String, RadioButtonWithLayout> layouts,
      String initialLayout) {
    CardLayout cardlayout = new CardLayout() {
      @Override
      public void show(Container parent, String name) {
        super.show(parent, name);
        
        Arrays.stream(parent.getComponents())
            .filter(component -> name.equals(component.getName()))
            .findFirst()
            .map(component -> (int) component.getPreferredSize().getHeight())
            .map(height -> new Dimension(parent.getPreferredSize().width, height))
            .ifPresent(parent::setPreferredSize);
      }
    };

    var formPanelCards = new JPanel(cardlayout);
    for (Entry<String, RadioButtonWithLayout> layout : layouts.entrySet()) {
      RadioButtonWithLayout value = layout.getValue();
      Component component = value.getComponent();
      String key = layout.getKey();
      component.setName(key);
      formPanelCards.add(component, key);
      value.getRadioButton().addActionListener(e -> cardlayout.show(formPanelCards, key));
    }

    cardlayout.show(formPanelCards, initialLayout);
    return formPanelCards;
  }

  public static class RadioButtonWithLayout {

    private final JBRadioButton radioButton;
    private final Component layout;

    public RadioButtonWithLayout(JBRadioButton radioButton, Component layout) {
      this.radioButton = radioButton;
      this.layout = layout;
    }

    public JBRadioButton getRadioButton() {
      return radioButton;
    }

    public Component getComponent() {
      return layout;
    }
  }
}