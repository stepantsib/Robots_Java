package gui;

import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Главное окно приложения, содержащее панель рабочего стола
 * с внутренними окнами: лог, игровое поле, строку меню.
 *
 */
public class MainApplicationFrame extends JFrame implements MenuActionListener {
    /**
     * Панель рабочего стола, на которой размещаются все внутренние окна.
     */
    private final JDesktopPane desktopPane = new JDesktopPane();

    /**
     * Создаёт главное окно, устанавливает его размер на весь экран
     * с отступами от краёв, создаёт и добавляет окна лога и игры,
     * а также инициализирует строку меню.
     */
    public MainApplicationFrame() {
        //Делает так, чтобы большое окно отступало на 50 пикселей от каждого края экрана.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);


        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        setJMenuBar(new MenuBar(this).create());

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });
    }


    /**
     * Создаёт и настраивает окно протокола.
     *
     * @return созданное окно протокола
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Добавляет внутреннее окно на панель рабочего стола и делает его видимым.
     *
     * @param frame внутреннее окно для добавления
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    @Override
    public void onSystemLookAndFeel() {
        setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        invalidate();
    }

    @Override
    public void onCrossPlatformLookAndFeel() {
        setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        invalidate();
    }

    @Override
    public void onAddLogMessage() {
        Logger.debug("Новая строка");
    }

    @Override
    public void onExit() {
        int result = JOptionPane.showOptionDialog(this, "Вы действительно хотите выйти?", "Подтверждение выхода", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Да", "Нет"}, "Нет");
        if (result == JOptionPane.YES_OPTION) {
            dispose();
        }
    }


    /**
     * Пытается установить указанный Look & Feel.
     * В случае успеха обновляет дерево компонентов.
     *
     * @param className полное имя класса Look & Feel
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            // пока игнорируется
        }
    }

}
