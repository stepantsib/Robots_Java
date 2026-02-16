package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Панель для визуализации и управления движением робота.
 * Содержит логику перемещения робота к цели, задаваемой щелчком мыши,
 * и обеспечивает периодическую перерисовку.
 */
public class GameVisualizer extends JPanel {

    /**
     * Максимальная линейная скорость робота.
     */
    private static final double MAX_VELOCITY = 0.1;
    /**
     * Максимальная угловая скорость робота.
     */
    private static final double MAX_ANGULAR_VELOCITY = 0.001;
    /**
     * Таймер для периодического обновления модели и перерисовки.
     * Запускается в конструкторе и работает в фоновом потоке-демоне.
     */
    private final Timer timer = initTimer();
    /**
     * Текущая координата X робота в пикселях.
     */
    private volatile double robotPositionX = 100;
    /**
     * Текущая координата Y робота в пикселях.
     */
    private volatile double robotPositionY = 100;
    /**
     * Текущее направление робота в радианах.
     */
    private volatile double robotDirection = 0;
    /**
     * Координата X цели, задаваемой пользователем.
     */
    private volatile int targetPositionX = 150;
    /**
     * Координата Y цели, задаваемой пользователем.
     */
    private volatile int targetPositionY = 100;

    /**
     * Конструктор панели.
     * Настраивает таймер для обновления модели и перерисовки
     * и добавляет слушатель мыши для задания цели.
     */
    public GameVisualizer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);
    }

    /**
     * Создаёт и возвращает новый экземпляр таймера, работающего в режиме демона.
     *
     * @return настроенный таймер-демон
     */
    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    /**
     * Вычисляет расстояние между двумя точками.
     *
     * @param x1 координата X первой точки
     * @param y1 координата Y первой точки
     * @param x2 координата X второй точки
     * @param y2 координата Y второй точки
     * @return расстояние между точками
     */
    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     * Вычисляет угол (в радианах) от точки (fromX, fromY)
     * к точке (toX, toY) относительно оси X.
     *
     * @param fromX координата X исходной точки
     * @param fromY координата Y исходной точки
     * @param toX   координата X целевой точки
     * @param toY   координата Y целевой точки
     * @return угол в радианах в диапазоне [0, 2π)
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * Ограничивает значение заданным диапазоном.
     *
     * @param value исходное значение
     * @param min   минимально допустимое значение
     * @param max   максимально допустимое значение
     * @return значение, приведённое к диапазону [min, max]
     */
    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Нормализует угол в диапазон [0, 2π).
     *
     * @param angle исходный угол в радианах
     * @return нормализованный угол
     */
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Округляет вещественное значение до ближайшего целого.
     *
     * @param value округляемое число
     * @return результат округления
     */
    private static int round(double value) {
        return (int) (value + 0.5);
    }

    /**
     * Рисует закрашенный овал с центром в заданной точке.
     *
     * @param g       графический контекст
     * @param centerX координата X центра
     * @param centerY координата Y центра
     * @param diam1   ширина овала
     * @param diam2   высота овала
     */
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Рисует контур овала с центром в заданной точке.
     *
     * @param g       графический контекст
     * @param centerX координата X центра
     * @param centerY координата Y центра
     * @param diam1   ширина овала
     * @param diam2   высота овала
     */
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    /**
     * Устанавливает новую позицию цели по координатам точки.
     *
     * @param p точка, в которую перемещается цель
     */
    protected void setTargetPosition(Point p) {
        targetPositionX = p.x;
        targetPositionY = p.y;
    }

    /**
     * Запускает перерисовку панели в потоке обработки событий.
     * Вызывается таймером.
     */
    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    /**
     * Обновляет состояние модели робота: вычисляет новое положение и направление
     * на основе текущей цели. Вызывается таймером.
     */
    protected void onModelUpdateEvent() {
        double distance = distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY);
        if (distance < 0.5) {
            return;
        }
        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
        double angularVelocity = 0;
        if (angleToTarget > robotDirection) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        }
        if (angleToTarget < robotDirection) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    /**
     * Двигает робота с заданной линейной и угловой скоростью в течение указанного времени.
     *
     * @param velocity        линейная скорость
     * @param angularVelocity угловая скорость
     * @param duration        длительность перемещения
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);
        double newX = robotPositionX + velocity / angularVelocity * (Math.sin(robotDirection + angularVelocity * duration) - Math.sin(robotDirection));
        if (!Double.isFinite(newX)) {
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
        }
        double newY = robotPositionY - velocity / angularVelocity * (Math.cos(robotDirection + angularVelocity * duration) - Math.cos(robotDirection));
        if (!Double.isFinite(newY)) {
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        }
        robotPositionX = newX;
        robotPositionY = newY;
        double newDirection = asNormalizedRadians(robotDirection + angularVelocity * duration);
        robotDirection = newDirection;
    }

    /**
     * Метод отрисовки компонента.
     * Вызывает отрисовку робота и цели.
     *
     * @param g графический контекст
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(robotPositionX), round(robotPositionY), robotDirection);
        drawTarget(g2d, targetPositionX, targetPositionY);
    }

    /**
     * Рисует робота в виде вытянутого овала с "глазом" и поворотом.
     *
     * @param g         графический контекст
     * @param x         координата X центра робота (не используется)
     * @param y         координата Y центра робота (не используется)
     * @param direction направление робота в радианах
     */
    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        int robotCenterX = round(robotPositionX);
        int robotCenterY = round(robotPositionY);
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    /**
     * Рисует цель в виде зелёного кружка.
     *
     * @param g графический контекст
     * @param x координата X цели
     * @param y координата Y цели
     */
    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    /**
     * Переопределяет метод удаления компонента из иерархии.
     * Останавливает таймер при закрытии окна для корректного завершения приложения.
     */
    @Override
    public void removeNotify() {
        super.removeNotify(); //Вызов родительской реализации метода,
        stopTimer(); //Вызов собственного метода, который останавливает таймер
    }


    /**
     * Останавливает и очищает таймер, предотвращая дальнейшее выполнение задач.
     * Вызывается при удалении компонента.
     */
    public void stopTimer() {
        timer.cancel(); //метод отменяет таймер, предотвращая выполнение всех запланированных задач
        timer.purge(); //метод удаляет все отменённые задачи из очереди таймера, освобождая память
    }
}
