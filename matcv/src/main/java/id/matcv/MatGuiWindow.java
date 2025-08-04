/*
 * Copyright 2022 matcv project
 * 
 * Website: https://github.com/lambdaprime/matcv
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.matcv;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.CountDownLatch;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

/**
 * Graphical User Interface Window to render OpenCV {@link Mat} objects
 *
 * @author lambdaprime intid@protonmail.com
 */
public class MatGuiWindow {

    private JLabel lbl;
    private volatile CountDownLatch keyPressed = new CountDownLatch(1);
    private String title;
    private int rows;
    private int cols;

    public MatGuiWindow(String title, int rows, int cols) {
        this.title = title;
        this.rows = rows;
        this.cols = cols;
    }

    public void update(Mat m) {
        init();
        var img = new ImageIcon(toImage(m));
        lbl.setIcon(img);
    }

    private void init() {
        if (lbl != null) return;
        var frame = new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(cols + 50, rows + 50);
        lbl = new JLabel();
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(
                new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {}

                    @Override
                    public void keyReleased(KeyEvent e) {}

                    @Override
                    public void keyPressed(KeyEvent e) {
                        keyPressed.countDown();
                    }
                });
    }

    public void waitKey() {
        keyPressed = new CountDownLatch(1);
        try {
            keyPressed.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Image toImage(Mat m) {
        return HighGui.toBufferedImage(m);
    }
}
