package advanced;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import panel.converted.CodeGPTBundle;
import panel.converted.UIUtil;


import javax.swing.*;
import java.awt.event.ItemEvent;
import java.net.Proxy;

public class AdvancedSettingsComponent {

  private final JPanel mainPanel;
  private final ComboBox<Proxy.Type> proxyTypeComboBox;
  private final JBTextField proxyHostField;
  private final PortField proxyPortField;
  private final JBTextField proxyAuthUsername;
  private final JBPasswordField proxyAuthPassword;
  private final PortField connectionTimeoutField;
  private final PortField readTimeoutField;

  public AdvancedSettingsComponent(AdvancedSettingsState advancedSettings) {
    proxyTypeComboBox = new ComboBox<>(new Proxy.Type[]{
        Proxy.Type.SOCKS,
        Proxy.Type.HTTP,
        Proxy.Type.DIRECT,
    });
    proxyTypeComboBox.setSelectedItem(advancedSettings.getProxyType());
    proxyHostField = new JBTextField(advancedSettings.getProxyHost(), 20);
    proxyPortField = new PortField();

    proxyAuthUsername = new JBTextField(20);
    proxyAuthUsername.setEnabled(advancedSettings.isProxyAuthSelected());
    proxyAuthPassword = new JBPasswordField();
    proxyAuthPassword.setColumns(20);
    proxyAuthPassword.setEnabled(advancedSettings.isProxyAuthSelected());

    connectionTimeoutField = new PortField(advancedSettings.getConnectTimeout());
    readTimeoutField = new PortField(advancedSettings.getReadTimeout());

    mainPanel = FormBuilder.createFormBuilder()
        .addComponent(createProxySettingsForm())
        .addVerticalGap(4)
        .addComponent(createConnectionSettingsForm())
        .addComponentFillVertically(new JPanel(), 0)
        .getPanel();
  }

  private JPanel createConnectionSettingsForm() {
    var panel = FormBuilder.createFormBuilder()
        .getPanel();
    panel.setBorder(JBUI.Borders.emptyLeft(16));
    return panel;
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  private JComponent createProxySettingsForm() {
    var proxyPanel = new JPanel();
    proxyPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.setLayout(new BoxLayout(proxyPanel, BoxLayout.PAGE_AXIS));

    var proxyTypePanel = UIUtil.createPanel(
        proxyTypeComboBox,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.typeComboBoxField.label"),
        false);
    var proxyHostPanel = UIUtil.createPanel(
        proxyHostField,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.hostField.label"),
        false);
    var proxyPortPanel = UIUtil.createPanel(
        proxyPortField,
        CodeGPTBundle.get("shared.port"),
        false);
    UIUtil.setEqualLabelWidths(proxyTypePanel, proxyHostPanel);
    UIUtil.setEqualLabelWidths(proxyPortPanel, proxyHostPanel);

    proxyPanel.add(proxyTypePanel);
    proxyPanel.add(proxyHostPanel);
    proxyPanel.add(proxyPortPanel);


    var proxyUsernamePanel = UIUtil.createPanel(proxyAuthUsername,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.usernameField.label"),
        false);
    var proxyPasswordPanel = UIUtil.createPanel(proxyAuthPassword,
        CodeGPTBundle.get("advancedSettingsConfigurable.proxy.passwordField.label"),
        false);
    UIUtil.setEqualLabelWidths(proxyPasswordPanel, proxyUsernamePanel);

    var proxyAuthPanel = FormBuilder.createFormBuilder()
        .addVerticalGap(4)
        .addComponent(proxyUsernamePanel)
        .addComponent(proxyPasswordPanel)
        .getPanel();
    proxyAuthPanel.setBorder(JBUI.Borders.emptyLeft(16));
    proxyPanel.add(proxyAuthPanel);

    return proxyPanel;
  }

  public AdvancedSettingsState getCurrentFormState() {
    var state = new AdvancedSettingsState();
    state.setProxyType((Proxy.Type) proxyTypeComboBox.getSelectedItem());
    state.setProxyHost(proxyHostField.getText().trim());
    state.setProxyPort(proxyPortField.getNumber());
    state.setProxyUsername(proxyAuthUsername.getText().trim());
    state.setProxyPassword(new String(proxyAuthPassword.getPassword()));
    state.setConnectTimeout(connectionTimeoutField.getNumber());
    state.setReadTimeout(readTimeoutField.getNumber());
    return state;
  }

  public void resetForm() {
    var advancedSettings = AdvancedSettings.getCurrentState();
    proxyTypeComboBox.setSelectedItem(advancedSettings.getProxyType());
    proxyHostField.setText(advancedSettings.getProxyHost());
    proxyPortField.setNumber(advancedSettings.getProxyPort());
    proxyAuthUsername.setText(advancedSettings.getProxyUsername());
    proxyAuthPassword.setText(advancedSettings.getProxyPassword());
    connectionTimeoutField.setNumber(advancedSettings.getConnectTimeout());
    readTimeoutField.setNumber(advancedSettings.getReadTimeout());
  }
}
