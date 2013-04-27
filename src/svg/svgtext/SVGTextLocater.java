/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.svgtext;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 *
 * @author takashi
 */
public class SVGTextLocater {

    private Vector<Float> distances;
    private Vector<Point2D.Float> points;

    public SVGTextLocater(Shape s) {
        PathIterator pt = s.getPathIterator(null, 0.1);
        distances = new Vector<Float>();
        points = new Vector<Point2D.Float>();
        float[] coords = new float[6];
        float totaldst = 0;
        float dist = 0;
        Point2D.Float prevMoveto = null;
        Point2D.Float prevPoint = null;
        Point2D.Float currentPoint = null;
        while (!pt.isDone()) {
            int type = pt.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    currentPoint = new Point2D.Float(coords[0], coords[1]);
                    prevMoveto = currentPoint;
                    dist = 0;
                    break;
                case PathIterator.SEG_LINETO:
                    currentPoint = new Point2D.Float(coords[0], coords[1]);
                    dist = (float) currentPoint.distance(prevPoint);
                    break;
                case PathIterator.SEG_CLOSE:
                    currentPoint = prevMoveto;
                    dist = (float) currentPoint.distance(prevPoint);
                    break;
            }
            totaldst += dist;
            distances.add(totaldst);
            points.add(currentPoint);
            prevPoint = currentPoint;
            pt.next();
        }
        for (int i = 1; i < distances.size(); i++) {
            if (points.get(i).equals(distances.get(i - 1))) {
                points.remove(i);
                distances.remove(i--);
            }
        }
    }

    public AffineTransform getTransform(float dst) {
        int idx = 0;
        for (idx = 0; idx < points.size(); idx++) {
            if (dst < distances.get(idx)) {
                float d = (dst - distances.get(idx - 1)) / (distances.get(idx) - distances.get(idx - 1));
                Point2D.Float p1 = points.get(idx), p0 = points.get(idx - 1);
                float tx = p0.x + (p1.x - p0.x) * d;
                float ty = p0.y + (p1.y - p0.y) * d;
                double radian = Math.atan2(p1.y - p0.y, p1.x - p0.x);
                
                AffineTransform result = AffineTransform.getTranslateInstance(tx,ty);
                result.rotate(radian);
                return result;
            }
        }
        return null;
    }

    public float getLength() {
        return distances.lastElement();
    }
}
