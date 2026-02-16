package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Главный класс приложения, точка входа.
 * Отвечает за установку внешнего вида (Nimbus) и запуск главного окна
 * в потоке обработки событий Swing.
 */
public class RobotsProgram {

    /**
     * Точка входа в приложение.
     * Устанавливает Look & Feel (Nimbus) и через SwingUtilities
     * создаёт и отображает главное окно MainApplicationFrame в развёрнутом состоянии.
     *
     */
    static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.pack();
            frame.setVisible(true);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
}
