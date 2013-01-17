/*
 * JDSlider.java
 *
 * Created on 2007/02/15, 8:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui.color;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import jpaint.JDHSV1Paint;
import jpaint.JDRGB1Paint;
import jscreen.*;

/**
 *
 * @author i002060
 */
public class JDRGBSlider extends JComponent {

    private Paint paint;
    private int maxValue;
    private int minValue;
    private int value;
    private ControlBar slider;
    private Vector<ChangeListener> changeListeners;
    private ChangeEvent evt;
    private Color baseColor;
    private int mode;

    /** Creates a new instance of JDSlider */
    public JDRGBSlider() {
        maxValue = 255;
        minValue = 0;
        value = 0;
        baseColor = Color.WHITE;
        paint = baseColor;
        mode = -1;
        slider = new ControlBar();
        this.add(slider);
        this.addComponentListener(new CpAdapter());
        adjustSlider();
        MAdapter adp = new MAdapter();
        slider.addMouseListener(adp);
        slider.addMouseMotionListener(adp);
        TAdapter tadapter = new TAdapter();
        this.addMouseListener(tadapter);
        this.addMouseMotionListener(tadapter);
        changeListeners = new Vector<ChangeListener>();
        evt = null;
    }

    public void setColor(Color c) {
        this.baseColor = c;
        setupPaint();
    }

    public Color getColor() {
        return this.baseColor;
    }

    public void setMode(int mode) {
        this.mode = mode;
        setupPaint();
    }

    private void setupPaint() {
        if (mode == JDRGB1Paint.R || mode == JDRGB1Paint.G || mode == JDRGB1Paint.B || baseColor != null) {
            paint = new JDRGB1Paint(JDHSV1Paint.HOLIZONTAL, mode, new Rectangle(5, 5, this.getWidth() - 10, 10), baseColor);
        } else {
            if (baseColor != null) {
                paint = baseColor;
            } else {
                paint = Color.WHITE;
            }
        }
        repaint();
    }

    public void addChangeListener(ChangeListener listener) {
        if (!changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    public void revmoveChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    public void fireChangeEvent() {
        if (evt == null) {
            evt = new ChangeEvent(this);
        }
        Color c = null;
        int r = baseColor.getRed(), g = baseColor.getGreen(), b = baseColor.getBlue();
        if (this.mode == JDRGB1Paint.R) {
            r = (int) (255f * value / maxValue);
        }
        if (this.mode == JDRGB1Paint.G) {
            g = (int) (255f * value / maxValue);
        }
        if (this.mode == JDRGB1Paint.B) {
            b = (int) (255f * value / maxValue);
        }
        baseColor = new Color(r, g, b);
        for (int i = 0; i < changeListeners.size(); i++) {
            changeListeners.get(i).stateChanged(evt);
        }
    }

    public void setValue(int v) {
        if (v < minValue) {
            v = minValue;
        }
        if (v > maxValue) {
            v = maxValue;
        }
        this.value = v;
        adjustSlider();
    }

    public int getValue() {
        return this.value;
    }

    public void setMaxValue(int v) {
        if (minValue > v) {
            v = minValue;
        }
        if (value > v) {
            value = v;
        }
        maxValue = v;
        adjustSlider();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int v) {
        if (v > maxValue) {
            v = maxValue;
        }
        if (value < v) {
            value = v;
        }
        minValue = v;
        adjustSlider();
    }

    public int getMinValue() {
        return minValue;
    }

    @Override
    public void paintComponent(Graphics g) {
        int w = this.getWidth() - 10;
        int h = this.getHeight() - 14;
        Graphics2D g2 = (Graphics2D) g;
        w = w < 0 ? 0 : w;
        h = h < 0 ? 0 : h;
        Rectangle rc = new Rectangle(5, 4, w, h);
        GeneralPath shade = new GeneralPath(), blight = new GeneralPath();
        shade.moveTo(4, 5 + h);
        shade.lineTo(4, 3);
        shade.lineTo(5 + w, 3);
        blight.moveTo(6 + w, 3);
        blight.lineTo(6 + w, 5 + h);
        blight.lineTo(4, 5 + h);
        if (!rc.isEmpty()) {
            g2.setPaint(paint);
            g2.fill(rc);
            g2.setPaint(Color.BLACK);
            g2.draw(rc);
            g2.setColor(new Color(0f, 0f, 0f, 0.3f));
            g2.draw(shade);
            g2.setColor(Color.WHITE);
            g2.draw(blight);
        }
    }

    private void adjustSlider() {
        int x;
        if (maxValue - minValue != 0) {
            x = (int) (((float) value / (maxValue - minValue)) * (this.getWidth() - 10));
        } else {
            x = 0;
        }
        int y = this.getHeight() - 9;
        slider.setBounds(x, y, 10, 10);
    }

    private JComponent returnThis() {
        return this;
    }

    class ControlBar extends JComponent {

        public void paintComponent(Graphics g) {
            JEnvironment.ICONS.SLIDER_HORIZONTAL.paintIcon(this, g, 0, 0);
        }
    }

    class CpAdapter extends ComponentAdapter {

        public void componentResized(ComponentEvent e) {
            setupPaint();
            adjustSlider();
        }
    }

    class MAdapter extends MouseAdapter {

        public void mouseDragged(MouseEvent e) {
            Point p = SwingUtilities.convertPoint(slider, e.getPoint(), returnThis());
            setValue((int) ((maxValue - minValue) * ((float) p.x - 5) / (returnThis().getWidth() - 10)) + minValue);
            fireChangeEvent();
        }
    }

    class TAdapter extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            setValue((int) ((maxValue - minValue) * ((float) p.x - 5) / (returnThis().getWidth() - 10)) + minValue);
            fireChangeEvent();
        }

        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            setValue((int) ((maxValue - minValue) * ((float) p.x - 5) / (returnThis().getWidth() - 10)) + minValue);
            fireChangeEvent();
        }
    }
}
