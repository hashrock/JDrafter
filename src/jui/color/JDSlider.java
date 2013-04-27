package jui.color;
/*
 * JDSlider.java
 *
 * Created on 2007/02/15, 8:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.geom.GeneralPath;
import jpaint.JDHSV1Paint;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import jscreen.JEnvironment;

/**
 *
 * @author i002060
 */
public class JDSlider extends JComponent {

    private Paint paint;
    private int maxValue;
    private int minValue;
    private int value;
    private ControlBar slider;
    private Vector<ChangeListener> changeListeners;
    private ChangeEvent evt;
    private Color baseColor;
    private float[] baseHSV;
    private int mode;

    /** Creates a new instance of JDSlider */
    public JDSlider() {
        maxValue = 255;
        minValue = 0;
        value = 0;
        baseColor = Color.WHITE;
        baseHSV = new float[3];
        baseHSV[0] = 0;
        baseHSV[1] = 0;
        baseHSV[2] = 0;
        paint = baseColor;
        mode = -1;
        slider = new ControlBar();
        this.add(slider);
        this.addComponentListener(new CpAdapter());
        adjustSlider();
        MAdapter adp = new MAdapter();
        slider.addMouseListener(adp);
        slider.addMouseMotionListener(adp);
        TAdapter tadapter=new TAdapter();
        this.addMouseListener(tadapter);
        this.addMouseMotionListener(tadapter);
        changeListeners = new Vector<ChangeListener>();
        evt = null;
    }

    public void setColor(float h, float s, float v) {
        this.baseColor = Color.getHSBColor(h, s, v);
        baseHSV[0] = h;
        baseHSV[1] = s;
        baseHSV[2] = v;
        setupPaint();
    }

    public void setColor(float[] cols) {
        setColor(cols[0], cols[1], cols[2]);
    }

    public Color getColor() {
        return this.baseColor;
    }

    public void setMode(int mode) {
        this.mode = mode;
        setupPaint();
    }

    private void setupPaint() {
        if (mode == JDHSV1Paint.H || mode == JDHSV1Paint.S || mode == JDHSV1Paint.V || baseColor != null) {
            paint = new JDHSV1Paint(JDHSV1Paint.HOLIZONTAL, mode, new Rectangle(5, 5, this.getWidth() - 10, 10), baseHSV);
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
        float[] hsb = baseHSV;
        if (this.mode == jpaint.JDHSV1Paint.H) {
            c = Color.getHSBColor((float) value / maxValue, hsb[1], hsb[2]);
        } else if (this.mode == jpaint.JDHSV1Paint.S) {
            c = Color.getHSBColor(hsb[0], (float) value / maxValue, hsb[2]);
        } else if (this.mode == jpaint.JDHSV1Paint.V) {
            c = Color.getHSBColor(hsb[0], hsb[1], (float) value / maxValue);
        }
        if (c != null) {
            this.baseColor = c;
            setupPaint();
        }
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
