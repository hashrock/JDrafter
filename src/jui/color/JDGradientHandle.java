/*
 * JDGradientHandle.java
 *
 * Created on 2007/02/18, 17:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package jui.color;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import jscreen.JEnvironment;

/**
 *
 * @author TK
 */
public class JDGradientHandle extends JComponent implements ComponentListener {

    private Color color;
    private Vector<ChangeListener> listeners;
    private ChangeEvent changeEvent;
    private float value;
    private JDColorPanel cPanel;
    private Color savedColor;
    private boolean locked;
    private boolean vanished;

    /** Creates a new instance of JDGradientHandle */
    public JDGradientHandle() {
        Dimension d = new Dimension(9, 13);
        this.setPreferredSize(d);
        this.setMaximumSize(d);
        this.setMinimumSize(d);
        this.setSize(d);
        locked = false;
        value = 0;
        color = null;
        savedColor = null;
        listeners = new Vector<ChangeListener>();
        changeEvent = null;
        cPanel = null;
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                doubleClicked(e);
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                dragged(e);
            }
        });
        vanished = false;

    }

    public void setColor(Color c) {
        color = c;
        repaint();
    }

    public Color getColor() {
        return color;
    }

    public void addChangeListener(ChangeListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }

    public void fireChangeEvent() {
        if (changeEvent == null) {
            changeEvent = new ChangeEvent(this);
        }
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).stateChanged(changeEvent);
        }
    }

    public void setValue(float f) {
        if (f < 0) {
            f = 0;
        }
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.value = f;
        setupValue();
    }

    public float getValue() {
        return value;
    }

    public void setLocked(boolean l) {
        locked = l;
    }

    public boolean getLocked() {
        return locked;
    }

    public  void setupValue() {
        Container parentControl = this.getParent();
        if (parentControl == null) {
            return;
        }
        int x = (int) (value * (parentControl.getWidth() - 9));
        int y = parentControl.getHeight() - 13;
        this.setLocation(x, y);
    }

    public boolean isVanished() {
        return vanished;
    }

    private void dragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        if (locked) {
            return;
        }
        Container parentControl = this.getParent();
        if (parentControl == null) {
            return;
        }
        Point p = SwingUtilities.convertPoint(this, e.getPoint(), parentControl);
        if (p.y > parentControl.getHeight() + 9) {
            vanished = true;
            fireChangeEvent();
            return;
        }
        if (p.x < 0) {
            p.x = 0;
        }
        if (p.x > parentControl.getWidth() - 9) {
            p.x = parentControl.getWidth() - 9;
        }
        this.value = (float) p.x / (parentControl.getWidth() - 9);
        setupValue();
        fireChangeEvent();
    }

    public void setVanished(boolean b) {
        this.vanished = b;
    }

    private void doubleClicked(MouseEvent e) {
        if (cPanel == null) {
            cPanel = new JDColorPanel(this);
            cPanel.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    colorChanged(e);
                }
            });
            cPanel.setAllowNull(false);
            Window w = SwingUtilities.windowForComponent(cPanel);
            w.addWindowListener(new WindowAdapter() {              
                @Override
                public void windowClosed(WindowEvent e){
                    focusLost(e);
                }
            });
        }
        JDGradientPanel gp = getGradientPanel(this);
        if (gp != null) {
            gp.setCloseWhenFocusLost(false);
        }
        savedColor = color;
        cPanel.setPrevColor(color);
        cPanel.showAsPopup(this);
    }

    private JDGradientPanel getGradientPanel(Component c) {
        if (c == null) {
            return null;
        }
        if (c instanceof JDGradientPanel) {
            return (JDGradientPanel) c;
        }
        return getGradientPanel(c.getParent());
    }

    private void focusLost(WindowEvent e) {
        JDGradientPanel gp = getGradientPanel(this);
        if (gp != null) {
            SwingUtilities.getWindowAncestor(this).requestFocus();
            gp.requestFocusInWindow();
            gp.setCloseWhenFocusLost(true);
        }

    }

    private void colorChanged(ChangeEvent e) {
        if (!cPanel.isDialogVisible()) {
            if (cPanel.isDesided() && cPanel.getDisidedColor() != null) {
                this.color = cPanel.getDisidedColor();
            } else {
                this.color = savedColor;
            }
        } else {
            if (cPanel.getDisidedColor() != null) {
                this.color = cPanel.getDisidedColor();
            }
        }
        fireChangeEvent();
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        ImageIcon ic = JEnvironment.ICONS.SLIDER_GRADIENT;
        ic.paintIcon(this, g, 0, 0);
        if (color != null) {
            g.setColor(color);
            g.fillRect(2, 6, 5, 5);
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        Component parentControl = (Component) e.getSource();
        int x = (int) (value * (parentControl.getWidth() - 9));
        int y = parentControl.getHeight() - 13;
        this.setLocation(x, y);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
