package org.se13.ui;

import org.se13.config.ProgramConfig;
import org.se13.ui.base.BasePanel;

import java.util.Stack;

public class PanelManager {
    private final ProgramConfig mConfig;
    private final Stack<BasePanel> stack = new Stack<>();

    public PanelManager(ProgramConfig config) {
        mConfig = config;
    }

    public void push(Class<? extends BasePanel> clazz) {
        try {
            BasePanel panel = clazz
                    .getConstructor(PanelManager.class, ProgramConfig.class)
                    .newInstance(this, mConfig);

            if (!stack.isEmpty()) {
                stack.lastElement().setVisible(false);
            }
            stack.push(panel);
            panel.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pop() {
        try {
            stack.pop().setVisible(false);
            if (stack.isEmpty()) {
                System.exit(0);
            } else {
                stack.lastElement().setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
