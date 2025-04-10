package com.nanfeng.demo;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EventListenerService {

    private JFrame mainFrame;
    private JTextArea eventLog;
    private JWindow transientPopup;
    private Timer hideTimer;
    private boolean keyPressed = false;
    private boolean mouseClicked = false;

    public void startListening() {
        try {
            // 禁用 JNativeHook 的日志输出
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            ConsoleHandler consoleHandler = new ConsoleHandler();
            logger.addHandler(consoleHandler);
            logger.setLevel(Level.SEVERE);  // 禁用默认的日志输出

            // 注册全局屏幕监听器
            GlobalScreen.registerNativeHook();

            // 监听键盘和鼠标事件
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    if (!keyPressed) {
                        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
                        String combo = getCombinationKey(e);
                        String time = getCurrentTime();
                        String message = "时间: " + time + " 按下的键: " + (combo.isEmpty() ? keyText : combo + keyText);
                        eventLog.append(message + "\n");
                        eventLog.setCaretPosition(eventLog.getDocument().getLength());
                        showTransientMessage(message);
                        keyPressed = true;
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    keyPressed = false;
                }

                @Override
                public void nativeKeyTyped(NativeKeyEvent e) {}
            });

            GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
                @Override
                public void nativeMouseClicked(NativeMouseEvent e) {
                    if (!mouseClicked) {
                        String time = getCurrentTime();
                        String message = "时间: " + time + " 鼠标点击: (按钮 " + e.getButton() + ") @ (" + e.getX() + "," + e.getY() + ")";
                        eventLog.append(message + "\n");
                        eventLog.setCaretPosition(eventLog.getDocument().getLength());
                        showTransientMessage(message);
                        mouseClicked = true;
                    }
                }

                @Override
                public void nativeMousePressed(NativeMouseEvent e) {}

                @Override
                public void nativeMouseReleased(NativeMouseEvent e) {
                    mouseClicked = false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListening() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private String getCombinationKey(NativeKeyEvent e) {
        int key = e.getKeyCode();
        if (key == NativeKeyEvent.VC_SHIFT || key == NativeKeyEvent.VC_CONTROL || key == NativeKeyEvent.VC_ALT) {
            return "";
        }
        StringBuilder combo = new StringBuilder();
        if ((e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0) combo.append("Shift+");
        if ((e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0) combo.append("Ctrl+");
        if ((e.getModifiers() & NativeInputEvent.ALT_MASK) != 0) combo.append("Alt+");
        String result = combo.toString();
        if (result.endsWith("+")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private void showTransientMessage(String message) {
        if (transientPopup == null) {
            transientPopup = new JWindow();
            transientPopup.setLayout(new BorderLayout());
            transientPopup.setBackground(new Color(0, 0, 0, 150));
            transientPopup.setSize(500, 80);

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 22));
            label.setForeground(Color.WHITE);
            label.setBorder(new LineBorder(Color.BLACK, 2));
            transientPopup.getContentPane().add(label, BorderLayout.CENTER);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - transientPopup.getWidth()) / 2;
            int y = screenSize.height - transientPopup.getHeight() - 50;
            transientPopup.setLocation(x, y);
        }

        JLabel label = (JLabel) transientPopup.getContentPane().getComponent(0);
        label.setText(message);

        transientPopup.setVisible(true);

        if (hideTimer != null && hideTimer.isRunning()) {
            hideTimer.restart();
        } else {
            hideTimer = new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    transientPopup.setVisible(false);
                }
            });
            hideTimer.setRepeats(false);
            hideTimer.start();
        }
    }

    public void initializeGUI() {
        mainFrame = new JFrame("事件显示");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 400);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(true);

        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setFont(new Font("Arial", Font.PLAIN, 14));
        eventLog.setForeground(Color.DARK_GRAY);
        JScrollPane scrollPane = new JScrollPane(eventLog);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }
}
