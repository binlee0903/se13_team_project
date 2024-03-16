package org.se13.ui.main;

import org.se13.config.ProgramConfig;
import org.se13.ui.PanelManager;
import org.se13.ui.base.BasePanel;
import org.se13.ui.setting.SettingPanel;

import javax.swing.JButton;

public class MainPanel extends BasePanel {

    public MainPanel(PanelManager manager, ProgramConfig config) {
        super(manager, config);

        JButton button = new JButton("Setting");
        button.addActionListener(e -> manager.push(SettingPanel.class));
        add(button);
    }
}
