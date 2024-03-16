package org.se13.ui.setting;

import org.se13.config.ProgramConfig;
import org.se13.ui.PanelManager;
import org.se13.ui.base.BasePanel;

import javax.swing.JButton;

public class SettingPanel extends BasePanel {

    public SettingPanel(PanelManager manager, ProgramConfig config) {
        super(manager, config);

        JButton button = new JButton("Back");
        button.addActionListener(e -> manager.pop());
        add(button);
    }
}
