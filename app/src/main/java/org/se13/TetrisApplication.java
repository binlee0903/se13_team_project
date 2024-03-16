package org.se13;

import org.se13.config.ProgramConfig;
import org.se13.ui.PanelManager;
import org.se13.ui.main.MainPanel;

public class TetrisApplication {
    public static void main(String[] args) {
        new TetrisApplication().start();
    }

    public void start() {
        ProgramConfig config = new ProgramConfig("SE13 Tetris", 1200, 720);
        PanelManager manager = new PanelManager(config);
        manager.push(MainPanel.class);
    }
}
