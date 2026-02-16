package gui;

import javax.swing.*;
import java.awt.event.KeyEvent;


/**
 * Класс для создания строки меню приложения.
 * Делегирует обработку выбранных пунктов слушателю.
 */
public class MenuBar {

    /**
     * Слушатель действий пунктов меню.
     * Через этот объект происходит делегирование обработки выбранных пользователем
     * команд из меню.
     * Устанавливается в конструкторе и не может быть изменён после создания меню.
     */
    private final MenuActionListener listener;

    /**
     * Создаёт фабрику меню с заданным слушателем.
     *
     * @param listener объект, реализующий действия при выборе пунктов меню
     */
    public MenuBar(MenuActionListener listener) {
        this.listener = listener;
    }

    /**
     * Строит и возвращает строку меню.
     *
     * @return готовая JMenuBar
     */
    public JMenuBar create() {
        JMenuBar menuBar = new JMenuBar();


        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Управление файлом");

        // Пункт "Выход"
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.addActionListener(e -> listener.onExit());
        fileMenu.add(exitItem);


        // Меню "Режим отображения"
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");

        // Пункт "Системная схема"
        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener(e -> listener.onSystemLookAndFeel());
        lookAndFeelMenu.add(systemLookAndFeel);

        // Пункт "Универсальная схема"
        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_C);
        crossplatformLookAndFeel.addActionListener(e -> listener.onCrossPlatformLookAndFeel());
        lookAndFeelMenu.add(crossplatformLookAndFeel);


        // Меню "Тесты"
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        // Пункт "Сообщение в лог"
        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener(e -> listener.onAddLogMessage());
        testMenu.add(addLogMessageItem);


        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);


        return menuBar;
    }

}
