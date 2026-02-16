package gui;

/**
 * Слушатель действий из меню.
 * Содержит методы, вызываемые при выборе соответствующих пунктов меню.
 */
public interface MenuActionListener {
    /**
     * Выбрана системная схема оформления.
     */
    void onSystemLookAndFeel();

    /**
     * Выбрана универсальная схема оформления.
     */
    void onCrossPlatformLookAndFeel();

    /**
     * Выбрана команда добавления сообщения в лог.
     */
    void onAddLogMessage();

    /**
     * Выбрана команда выхода из приложения.
     */
    void onExit();
}