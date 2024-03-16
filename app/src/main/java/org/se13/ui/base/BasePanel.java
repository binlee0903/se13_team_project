package org.se13.ui.base;

import org.se13.config.ProgramConfig;
import org.se13.ui.PanelManager;

import javax.swing.JFrame;

public abstract class BasePanel extends JFrame {

    protected final PanelManager manager;

    public BasePanel(PanelManager manager, ProgramConfig config) {
        this.manager = manager;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(config.width(), config.height());
    }
}
