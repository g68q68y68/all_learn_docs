package com.nanfeng.demo;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

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

public class Jnativehook {

    // 主窗口及日志显示
    private static JFrame mainFrame;
    private static JTextArea eventLog;

    // 用于显示短暂提示的弹出窗口以及对应计时器
    private static JWindow transientPopup;
    private static Timer hideTimer;

    // 控制按键和鼠标是否已经被触发
    private static boolean keyPressed = false;
    private static boolean mouseClicked = false;

    public static void main(String[] args) {
        // 设置统一的外观风格（美化）
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 初始化GUI
        SwingUtilities.invokeLater(Jnativehook::initializeGUI);

        // 初始化JNativeHook，捕获全局键盘和鼠标事件
        initializeJNativeHook();
    }

    /**
     * 初始化全局事件监听
     */
    private static void initializeJNativeHook() {
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
                        // 自动滚动到最新日志
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
                public void nativeKeyTyped(NativeKeyEvent e) {
                    // 不处理按键输入
                }
            });

            GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
                @Override
                public void nativeMouseClicked(NativeMouseEvent e) {
                    if (!mouseClicked) {
                        String time = getCurrentTime();
                        String message = "时间: " + time + " 鼠标点击: (按钮 " + e.getButton() + ") @ (" + e.getX() + "," + e.getY() + ")";
                        eventLog.append(message + "\n");
                        // 自动滚动到最新日志
                        eventLog.setCaretPosition(eventLog.getDocument().getLength());
                        showTransientMessage(message);
                        mouseClicked = true;
                    }
                }

                @Override
                public void nativeMousePressed(NativeMouseEvent e) {
                    // 不处理按下事件
                }

                @Override
                public void nativeMouseReleased(NativeMouseEvent e) {
                    mouseClicked = false;  // 释放鼠标后，允许再次触发
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前时间字符串（格式 yyyy-MM-dd HH:mm:ss）
     */
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * 获取组合键字符串，只在非修饰键事件且有其他修饰键按下时打印组合信息
     */
    private static String getCombinationKey(NativeKeyEvent e) {
        // 如果当前键本身就是修饰键，则不作为组合键打印
        int key = e.getKeyCode();
        if (key == NativeKeyEvent.VC_SHIFT || key == NativeKeyEvent.VC_CONTROL || key == NativeKeyEvent.VC_ALT) {
            return "";
        }
        StringBuilder combo = new StringBuilder();
        if ((e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0) combo.append("Shift+");
        if ((e.getModifiers() & NativeInputEvent.CTRL_MASK) != 0) combo.append("Ctrl+");
        if ((e.getModifiers() & NativeInputEvent.ALT_MASK) != 0) combo.append("Alt+");
        // 移除末尾可能多余的 " + "
        String result = combo.toString();
        if (result.endsWith("+")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 在屏幕下方弹出一个提示窗口，显示给定的消息，持续 2 秒，如果期间有新的消息则覆盖原先内容
     */
    private static void showTransientMessage(String message) {
        if (transientPopup == null) {
            transientPopup = new JWindow();
            transientPopup.setLayout(new BorderLayout());
            transientPopup.setBackground(new Color(0, 0, 0, 150)); // 半透明背景
            transientPopup.setSize(500, 80);

            // 使用 JLabel 显示消息，可根据需要设置字体和边框
            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 22));
            label.setForeground(Color.WHITE); // 白色字体
            label.setBorder(new LineBorder(Color.BLACK, 2));
            transientPopup.getContentPane().add(label, BorderLayout.CENTER);

            // 设置窗口出现在屏幕下方中央（距离屏幕下边缘 50 像素处）
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - transientPopup.getWidth()) / 2;
            int y = screenSize.height - transientPopup.getHeight() - 50;
            transientPopup.setLocation(x, y);
        }
        // 更新消息内容
        JLabel label = (JLabel) transientPopup.getContentPane().getComponent(0);
        label.setText(message);

        // 显示弹出窗口
        transientPopup.setVisible(true);

        // 如果之前存在计时器则重启，否则新建计时器
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

    /**
     * 初始化GUI窗口，显示日志
     */
    private static void initializeGUI() {
        // 创建主窗口（同时作为键盘和鼠标事件的捕获区域）
        mainFrame = new JFrame("事件显示");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 400);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setAlwaysOnTop(true);

        // 创建日志区域（用于持久记录所有事件）
        eventLog = new JTextArea();
        eventLog.setEditable(false);
        eventLog.setFont(new Font("Arial", Font.PLAIN, 14));
        eventLog.setForeground(Color.DARK_GRAY);
        JScrollPane scrollPane = new JScrollPane(eventLog);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }
}
