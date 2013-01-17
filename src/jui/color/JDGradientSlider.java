/*
 * JDGradientSlider.java
 *
 * Created on 2007/02/18, 21:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui.color;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.util.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author TK
 */
public class JDGradientSlider extends JComponent implements ChangeListener {

    MultipleGradientPaint paint;
    Vector<JDGradientHandle> handles;
    Vector<ChangeListener> listeners;
    ChangeEvent changeEvent;
    TexturePaint cTexture;

    /** Creates a new instance of JDGradientSlider */
    public JDGradientSlider() {
        float w = Math.max(1f, this.getWidth());
        paint = new LinearGradientPaint(0, 0, w, 0,
                new float[]{0f, 1f}, new Color[]{Color.WHITE, Color.BLACK}, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        this.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent e) {
                resized(e);
            }
        });
        if (JDPaintPreview.cTexture == null) {
            JDPaintPreview.createCImage();
        }
        cTexture = new TexturePaint(JDPaintPreview.cImage, new Rectangle(0, 0, 16, 16));
        listeners = null;
        changeEvent = null;
        JDGradientHandle js1, js2;
        js1 = new JDGradientHandle();
        js2 = new JDGradientHandle();
        js1.setColor(getColorAt(0f));
        js2.setColor(getColorAt(1f));
        this.add(js1);
        this.add(js2);
        js1.setValue(0f);
        js2.setValue(1f);
        this.addComponentListener(js1);
        this.addComponentListener(js2);
//        js1.setLocked(true);
//        js2.setLocked(true);
        handles = new Vector<JDGradientHandle>();
        handles.add(js1);
        handles.add(js2);
        js1.addChangeListener(this);
        js2.addChangeListener(this);
        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                clicked(e);
            }

            public void mouseReleased(MouseEvent e) {
                mReleased(e);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {

            public void mouseDragged(MouseEvent e) {
                mDragged(e);
            }
        });
    }

    public void setGradinet(MultipleGradientPaint p) {
        float w = Math.max(1f, this.getWidth());
        paint = new LinearGradientPaint(0, 0, w, 0,
                p.getFractions(), p.getColors(), p.getCycleMethod());
        setupHandles();
        repaint();
    }

    public LinearGradientPaint getGradient() {
        return (LinearGradientPaint) paint;
    }

    private void setupHandles() {
        for (int i = 0; i < handles.size(); i++) {
            handles.get(i).removeChangeListener(this);
            handles.get(i).setVisible(false);
        }
        handles.clear();
        /*        int i=0;
        while (i<handles.size()){
        this.remove(handles.get(i));
        handles.get(i).setVisible(false);
        handles.remove(i);
        }
         */
        float[] pts = paint.getFractions();
        Color[] cl = paint.getColors();
        if (pts != null) {
            for (int i = 0; i < pts.length; i++) {
                JDGradientHandle gs = new JDGradientHandle();
                this.add(gs);
                gs.setValue(pts[i]);
                gs.setColor(cl[i]);
                gs.addChangeListener(this);
                handles.add(gs);
            }
        }
        repaint();
    }

    private Color getColorAt(float f) {
        float[] flacs = paint.getFractions();
        Color[] colors = paint.getColors();
        int idx;
        for (idx = 0; idx < flacs.length; idx++) {
            if (flacs[idx] >= f) {
                break;
            }
        }
        if (idx == 0) {
            return colors[0];
        }
        if (idx >= flacs.length) {
            return colors[idx - 1];
        }
        float df = (f - flacs[idx - 1]) / (flacs[idx] - flacs[idx - 1]);
        float r = (colors[idx].getRed() - colors[idx - 1].getRed()) * df + colors[idx - 1].getRed();
        float g = (colors[idx].getGreen() - colors[idx - 1].getGreen()) * df + colors[idx - 1].getGreen();
        float b = (colors[idx].getBlue() - colors[idx - 1].getBlue()) * df + colors[idx - 1].getBlue();
        float a = (colors[idx].getAlpha() - colors[idx - 1].getAlpha()) * df + colors[idx - 1].getAlpha();
        return new Color(r / 255, g / 255, b / 255, a / 255);
    }

    private void resized(ComponentEvent e) {
        float w = Math.max(1f, this.getWidth());
        paint = new LinearGradientPaint(0, 0, w, 0, paint.getFractions(),
                paint.getColors(), paint.getCycleMethod());
        repaint();
    }
    private JDGradientHandle handle = null;

    private void clicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        Rectangle r = new Rectangle(5, 0, this.getWidth() - 9, this.getHeight() - 13);
        if (r.contains(e.getPoint())) {
            return;
        }
        int x = (int) e.getPoint().getX() - 5;
        if (x < 0 || x > this.getWidth() - 9) {
            return;
        }
        handle = new JDGradientHandle();
        this.add(handle, 0);
        float value = (float) x / (this.getWidth() - 9);
        handle.setValue(value);
        handle.setColor(getColorAt(value));
        handles.add(handle);
        handle.addChangeListener(this);
        fireChangeEvent();
    }

    private void mDragged(MouseEvent e) {
        if (handle != null) {
            if (SwingUtilities.isRightMouseButton(e)) {
                return;
            }
            Rectangle r = new Rectangle(5, 0, this.getWidth() - 9, this.getHeight() - 13);
            if (r.contains(e.getPoint())) {
                //return;
            }
            Point p = e.getPoint();
            if (p.y >this.getHeight() + 9) {
                handle.setVanished(true);
                handle.fireChangeEvent();
                return;
            }
            int x = (int) e.getPoint().getX() - 5;
            if (x < 0 || x > this.getWidth() - 9) {
                return;
            }
            float value = (float) x / (this.getWidth() - 9);
            handle.setValue(value);
            handle.fireChangeEvent();
        }
    }

    private void mReleased(MouseEvent e) {
        handle = null;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle r = new Rectangle(5, 0, this.getWidth() - 9, this.getHeight() - 13);
        g2.setPaint(cTexture);
        g2.fill(r);
        g2.setPaint(paint);
        g2.fill(r);
        g2.setColor(Color.BLACK);
        g2.draw(r);
    }

    public LinearGradientPaint createPaint() {
        Vector<JDGradientHandle> jdh = new Vector<JDGradientHandle>();
        while (!handles.isEmpty()) {
            float min = 9999999;
            JDGradientHandle mHandle = null;
            for (int i = 0; i < handles.size(); i++) {
                if (handles.get(i).getValue() <= min) {
                    mHandle = handles.get(i);
                    min = mHandle.getValue();

                }
            }
            handles.remove(mHandle);
            jdh.add(mHandle);
        }
        handles = jdh;
        float[] pts = new float[handles.size()];
        Color[] cs = new Color[handles.size()];
        for (int i = 0; i < handles.size(); i++) {
            pts[i] = handles.get(i).getValue();
            cs[i] = handles.get(i).getColor();
        }
        LinearGradientPaint ret = null;
        try {
            ret = new LinearGradientPaint(0, 0, this.getWidth(), 0,
                    pts, cs, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        } catch (Exception e) {
            ret = (LinearGradientPaint) paint;
        }
        return ret;

    }

    public void addChangeListener(ChangeListener l) {
        if (listeners == null) {
            listeners = new Vector<ChangeListener>();
        }
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeChangeListener(ChangeListener l) {
        if (listeners == null) {
            return;
        }
        listeners.remove(l);
    }

    public void fireChangeEvent() {
        if (listeners == null) {
            return;
        }
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).stateChanged(changeEvent);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JDGradientHandle h = (JDGradientHandle) e.getSource();
        if (h.isVanished()) {
            if (handles.size() > 2) {
                h.setVisible(false);
                this.remove(h);
                handles.remove(h);
            } else {
                h.setVanished(false);
                return;
            }
        }
        paint = createPaint();
        repaint();
        fireChangeEvent();
    }
}
