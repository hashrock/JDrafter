/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jobject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import jscreen.JEnvironment;
import jscreen.JRequest;

/**
 *
 * @author takashi
 */
public class JGuidLayer extends JLayer {

    private static final long serialVersionUID = 110l;
    private Color guidColor = JEnvironment.DEFAULT_GUID_COLOR;
    private Color guidPreviewColor = JEnvironment.DEFAULT_GUID_PREVIEW_COLOR;
    public static final Stroke LINE_STYLE_STROKE = new BasicStroke(0f);
    public static final Stroke DOT_STYLE_STROKE = new BasicStroke(0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{1f, 1f}, 0f);
    private boolean onTop = true;
    private boolean snapGuide = true;
    private boolean dotStyle = false;

    public JGuidLayer() {
        setLocked(true);
    }

    public Color getGuidColor() {
        return guidColor;
    }

    public void setGuidColor(Color c) {
        guidColor = c;
    }

    @Override
    public Color getPreviewColor() {
        return guidPreviewColor;
    }

    @Override
    public void setPreviewColor(Color c) {
        guidPreviewColor = c;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        JGuidLayer ret = new JGuidLayer();
        for (int i = 0; i < size(); i++) {
            ret.add((JLeaf) get(i).clone());
        }
        ret.guidColor = guidColor;
        ret.guidPreviewColor = guidPreviewColor;
        ret.onTop = onTop;
        ret.dotStyle = dotStyle;
        return ret;
    }

    @Override
    public int hitByPoint(JEnvironment env, JRequest req, Point2D point) {
        //Send to Children;
        if (!isVisible()) {
            return JRequest.HIT_NON;
        }
        int ret = JRequest.HIT_NON;
        for (int i = size() - 1; i >= 0; i--) {
            if ((ret = get(i).hitByPoint(env, req, point)) != JRequest.HIT_NON) {
                break;
            }
        }
        return ret;
    }

    public boolean isOnTop() {
        return onTop;
    }

    public void setOnTop(boolean b) {
        onTop = b;
    }

    public boolean isSnapGuide() {
        return snapGuide;
    }

    public void setSnapGuide(boolean b) {
        snapGuide = b;
    }

    public boolean isDotStyle() {
        return dotStyle;
    }

    public void setDotStyle(boolean b) {
        dotStyle = b;
    }
}
