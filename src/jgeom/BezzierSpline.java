/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jgeom;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 *
 * @author takashi
 */
public class BezzierSpline {
    //近似を実行する最大回数
    private int maxIterations = 3;
    //最大誤差
    private double tolerance = 1;
    //誤差が許容範囲を超える点
    private int splitPoint = 0;
    //
    public BezzierSpline() {
    }

    public GeneralPath getGeneralPath(Point2D[] points, double tolerance) {
        Vector<Point2D[]> beziers = new Vector<Point2D[]>();
        this.tolerance=tolerance;
        spline(points, new Point2D.Double(), beziers);
        GeneralPath result=new GeneralPath();
        if (!beziers.isEmpty()){
            result.moveTo(beziers.get(0)[0].getX(),beziers.get(0)[0].getY());
            for(Point2D[] p:beziers){
                result.curveTo(p[1].getX(), p[1].getY(),p[2].getX(), p[2].getY(), p[3].getX(), p[3].getY());
            }
        }
        return result;
    }

    private Point2D spline(Point2D[] ps, Point2D tangent, Vector<Point2D[]> vec) {
        Point2D[] bezier = createBezier(ps, tangent, new Point2D.Double());
        if (bezier != null) {
            vec.add(bezier);
            Point2D tg = ptMinus(bezier[3], bezier[2]);
            if (!ptIsZero(tg)) {
                tg = ptUnit(tg);
            }
            return tg;
        } else {
            Point2D tg = spline(rowerArray(ps), tangent, vec);
            return spline(upperArray(ps), tg, vec);
        }
    }

    private Point2D[] rowerArray(Point2D[] source) {
        int div1 = Math.round(source.length / 2f);
        Point2D[] result = new Point2D[div1];
        for (int i = 0; i < div1; i++) {
            result[i] = source[i];
        }
        return result;
    }

    private Point2D[] upperArray(Point2D[] source) {
        int div1 = Math.round(source.length / 2f);
        int div2 = source.length - div1 + 1;
        Point2D[] result = new Point2D[div2];
        for (int i = 0; i < div2; i++) {
            result[i] = source[i + div1 - 1];
        }
        return result;
    }

    public Point2D[] createBezier(Point2D[] points, Point2D startTangent, Point2D endTangent) {
        Point2D[] result = null;
        if (points.length == 2) {
            result = fromTwoPoints(points, startTangent, endTangent);
        } else {
            result = generateBezier(points, startTangent, endTangent);
        }
        return result;
    }
    //パラメータポイントが２個の場合
    public Point2D[] fromTwoPoints(Point2D[] points, Point2D startTangent, Point2D endTangent) {
        Point2D[] bezier = new Point2D[4];
        bezier[0] = points[0];
        bezier[3] = points[1];
        double distance = bezier[0].distance(bezier[3]) / 3;//ptDistance(bezier[0], bezier[3]) / 3;
        if (ptIsZero(startTangent)) {
            bezier[1] = new Point2D.Double(
                    (bezier[0].getX() * 2 + bezier[3].getX()) / 3,
                    (bezier[0].getY() * 2 + bezier[3].getY()) / 3);
        } else {
            bezier[1] = new Point2D.Double(
                    bezier[0].getX() + startTangent.getX() * distance,
                    bezier[0].getY() + startTangent.getY() * distance);
        }
        if (ptIsZero(endTangent)) {
            bezier[2] = new Point2D.Double(
                    (bezier[3].getX() * 2 + bezier[0].getX()) / 3,
                    (bezier[3].getY() * 2 + bezier[0].getY()) / 3);
        } else {
            bezier[2] = new Point2D.Double(
                    bezier[3].getX() + endTangent.getX() * distance,
                    bezier[3].getY() + endTangent.getY() * distance);
        }
        return bezier;
    }
    //ベジェパスの作成;
    private Point2D[] generateBezier(Point2D[] points, Point2D startTangent, Point2D endTangent) {
        double[] params = setParamsByLength(points);
        Point2D[] bezier = setAssumptBezier(points, startTangent, endTangent, params);
        //splitPoint = 0;
        double error = maxError(points, bezier, params);
        if (Math.abs(error) <= 1) {
            return bezier;
        }
        if (error >= 0 && error <= 3) {
            for (int i = 0; i < maxIterations; i++) {
                bezier = setAssumptBezier(points, startTangent, endTangent, params);
                error = maxError(points, bezier, params);
                if (Math.abs(error) <= 1) {
                    return bezier;
                }
            }
        }

        if (error < 0) {
            if (splitPoint == 0 && !ptIsZero(startTangent)) {
                return createBezier(points, new Point2D.Double(0, 0), endTangent);
            }
            if (splitPoint == points.length - 1 && !ptIsZero(endTangent)) {
                return createBezier(points, startTangent, new Point2D.Double(0, 0));
            }
        }
        return null;
    }
    //仮パスの作成
    private Point2D[] setAssumptBezier(Point2D[] points, Point2D startTangent, Point2D endTangent, double[] params) {
        Point2D start = ptIsZero(startTangent) ? estimateStartTangent(points) : startTangent;
        Point2D end = ptIsZero(endTangent) ? estimateEndTangent(points) : endTangent;
        Point2D[] bezier = setAssumptBezierWithTangent(points, start, end, params);
        if (ptIsZero(startTangent)) {
            setControllPoint(points, 1, bezier, params);
            if (!bezier[0].equals(bezier[1])) {
                start = ptUnit(ptMinus(bezier[1], bezier[0]));
            }
            bezier = setAssumptBezierWithTangent(points, start, end, params);
        }
        params=reparameterize(points, bezier, params);
        return bezier;
    }

    private Point2D estimateStartTangent(Point2D[] points) {
        Point2D tangent = null;
        for (int i = 1; i < points.length; i++) {
            tangent = new Point2D.Double(points[i].getX() - points[0].getX(), points[i].getY() - points[0].getY());
            if (ptSquare(tangent) > tolerance * tolerance) {
                return ptUnit(tangent);
            }
        }
        return ptIsZero(tangent) ? ptUnit(ptMinus(points[1], points[0])) : ptUnit(tangent);
    }

    private Point2D estimateEndTangent(Point2D[] points) {
        Point2D tangent = null;
        for (int i = points.length - 2; i >= 0; i--) {
            tangent = ptMinus(points[i], points[points.length - 1]);
            if (ptSquare(tangent) > tolerance * tolerance) {
                return ptUnit(tangent);
            }
        }
        return ptIsZero(tangent) ? ptUnit(ptMinus(points[points.length - 2], points[points.length - 1])) : ptUnit(tangent);
    }

    private Point2D[] setAssumptBezierWithTangent(Point2D[] points, Point2D start, Point2D end, double[] params) {
        double[][] C = {{0, 0}, {0, 0}};
        double[] X = {0, 0};
        Point2D[] bezier = new Point2D[4];
        bezier[0] = points[0];
        bezier[3] = points[points.length - 1];

        for (int i = 0; i < points.length; i++) {
            double[] b = B(params[i]);
            Point2D[] a = new Point2D[]{
                ptTime(start, b[1]), ptTime(end, b[2])
            };

            C[0][0] += ptDot(a[0], a[0]);
            C[0][1] += ptDot(a[0], a[1]);
            C[1][0] = C[0][1];
            C[1][1] += ptDot(a[1], a[1]);
            Point2D offset = new Point2D.Double(
                    points[i].getX() - (b[0] + b[1]) * bezier[0].getX() - (b[2] + b[3]) * bezier[3].getX(),
                    points[i].getY() - (b[0] + b[1]) * bezier[0].getY() - (b[2] + b[3]) * bezier[3].getY());
            X[0] += ptDot(a[0], offset);
            X[1] += ptDot(a[1], offset);
        }

        double alphaL,  alphaR;
        double detC = C[0][0] * C[1][1] - C[1][0] * C[0][1];
        if (detC != 0) {
            double detC0X = C[0][0] * X[1] - C[0][1] * X[0];
            double detXC1 = X[0] * C[1][1] - X[1] * C[0][1];
            alphaL = detXC1 / detC;
            alphaR = detC0X / detC;
        } else {
            double c0 = C[0][0] + C[0][1];
            if (c0 != 0) {
                alphaL = X[0] / c0;
            } else {
                double c1 = C[1][0] + C[1][1];
                alphaL = (c1 != 0) ? X[1] / c1 : 0;
            }
            alphaR = alphaL;
        }

        if (alphaL < 1e-6 || alphaR < 1e-6) {
            alphaL = alphaR = points[0].distance(points[points.length - 1]) / 3;
        }
        bezier[1] = ptPlus(ptTime(start, alphaL), bezier[0]);
        bezier[2] = ptPlus(ptTime(end, alphaR), bezier[3]);
        return bezier;
    }

    private void setControllPoint(Point2D[] points, int ei, Point2D[] bezier, double[] params) {
        int oi = 3 - ei;
        Point2D.Double result = new Point2D.Double(0, 0);
        double den = 0;
        for (int i = 0; i < params.length; i++) {
            double[] b = B(params[i]);
            result.x = result.x + b[ei] * (b[0] * bezier[0].getX() + b[3] * bezier[3].getX() + b[oi] * bezier[0].getX() - points[i].getX());
            result.y = result.y + b[ei] * (b[0] * bezier[0].getY() + b[3] * bezier[3].getY() + b[oi] * bezier[0].getY() - points[i].getY());
            //result = ptOperate(result, bezier[0], bezier[3], bezier[oi], data[i], function(r, b0, b3, bo, p) 
            //{ return r + b[ei] * (b[0] * b0 + b[3] * b3 + b[oi] * bo - p);
            //});

            den -= b[ei] * b[ei];
        }
        if (den != 0) {
            result.setLocation(ptTime(result, 1 / den));
        } else {
            result.x = (bezier[0].getX() * oi + bezier[3].getX() * ei) / 3;
            result.y = (bezier[0].getY() * oi + bezier[3].getY() * ei) / 3;
        }
        bezier[ei] = result;
    }

    //直線補完からパラメタの設定;
    private double[] setParamsByLength(Point2D[] points) {
        double[] result = new double[points.length];
        result[0] = 0;

        for (int i = 1; i < points.length; i++) {
            result[i] = result[i - 1] + points[i].distance(points[i - 1]);//ptDistance(data[i], data[i - 1]);
        }
        double total = result[points.length - 1];
        for (int i = 0; i < points.length; i++) {
            result[i] /= total;
        }
        return result;
    }
    //曲線からのパラメタ設定
    private double[] reparameterize(Point2D[] points, Point2D[] bezier, double[] params) {
        for (int i = 0; i < points.length - 1; i++) {
            params[i] = newtonRaphsonRootFind(bezier, points[i], params[i]);
        }
        return params;
    }

    //ニュートンラプソン法による解
    private double newtonRaphsonRootFind(Point2D[] bezier, Point2D point, double param) {
        Point2D[] dbezier = pointDifference(bezier);
        Point2D[] ddbezier = pointDifference(dbezier);

        Point2D p = bezierPt(bezier, param);
        Point2D dp = bezierPt(dbezier, param);
        Point2D ddp = bezierPt(ddbezier, param);

        Point2D diff = new Point2D.Double(p.getX() - point.getX(), p.getY() - point.getY());
        double numerator = ptDot(diff, dp);
        double denominator = ptSquare(dp) + ptDot(diff, ddp);

        double improvedParam;
        if (denominator > 0) {
            improvedParam = param - (numerator / denominator);
        } else {
            if (numerator > 0) {
                improvedParam = param * 0.98 - 0.01;
            } else if (numerator < 0) {
                improvedParam = param * 0.98 + 0.031;
            } else {
                improvedParam = param;
            }
        }
        if (improvedParam < 0) {
            improvedParam = 0;
        } else if (improvedParam > 1) {
            improvedParam = 1;
        }

        double diffSquare = ptSquare(diff);
        for (double proportion = 0.125;; proportion += 0.125) {
            Point2D pp = bezierPt(bezier, improvedParam);
            pp.setLocation(pp.getX() - point.getX(), pp.getY() - point.getY());
            if (ptSquare(pp) > diffSquare) {
                if (proportion > 1) {
                    improvedParam = param;
                    break;
                }
                improvedParam = (1 - proportion) * improvedParam + proportion * param;
            } else {
                break;
            }
        }
        return improvedParam;
    }
    //最大遠点と曲線との相対距離とその位置(splitPointに格納)を求める。
    private double maxError(Point2D[] data, Point2D[] bezier, double[] fractions) {
        double toleranceMore = tolerance;
        double maxDistanceSquare = 0,  maxHook = 0;
        int snapEnd = 0;
        Point2D prevPoint = bezier[0];
        for (int i = 1; i <data.length; i++) {
            Point2D currentPoint = bezierPt(bezier, fractions[i]);
            Double distanceSquare = currentPoint.distanceSq(data[i]);
            if (distanceSquare > maxDistanceSquare) {
                maxDistanceSquare = distanceSquare;
                splitPoint = i;
            }

            double hook = computeHook(bezier,prevPoint,currentPoint, (fractions[i] + fractions[i - 1]) / 2);
            if (hook > maxHook) {
                maxHook = hook;
                snapEnd = i;
            }

            prevPoint = currentPoint;
        }

        double maxDistanceRatio = Math.sqrt(maxDistanceSquare) / toleranceMore;

        if (maxHook <= maxDistanceRatio) {
            return maxDistanceRatio;
        } else {
            splitPoint = snapEnd - 1;
            return -maxHook;
        }

    }
    //t(0<=t<=1)におけるベジェ曲線上の点
    private Point2D bezierPt(Point2D[] b, double t) {
        double[][] pascal = new double[][]{{1, 0, 0, 0}, {1, 1, 0, 0}, {1, 2, 1, 0}, {1, 3, 3, 1}};
        double s = 1 - t;
        int degree = b.length - 1;

        double[] spow = new double[]{1, 0, 0, 0};
        double[] tpow = new double[]{1, 0, 0, 0};
        for (int d = 0; d <
                degree; d++) {
            spow[d + 1] = spow[d] * s;
            tpow[d + 1] = tpow[d] * t;
        }

        Point2D.Double result = new Point2D.Double();
        result.setLocation(b[0]);
        result.x *= spow[degree];
        result.y *= spow[degree];
        for (int d = 1; d <=
                degree; d++) {
            result.x += b[d].getX() * pascal[degree][d] * spow[degree - d] * tpow[d];
            result.y += b[d].getY() * pascal[degree][d] * spow[degree - d] * tpow[d];
        }

        return result;
    }

    private Point2D[] pointDifference(Point2D[] points) {
        Point2D[] diff = new Point2D[points.length - 1];
        for (int i = 0,   len = points.length; i <
                len - 1; i++) {
            diff[i] = new Point2D.Double(
                    (points[i + 1].getX() - points[i].getX()) * (len - 1),
                    (points[i + 1].getY() - points[i].getY()) * (len - 1));
        }

        return diff;
    }

    private double computeHook(Point2D[] bezier,Point2D point1, Point2D point2, double param) {
        double toleranceMore = tolerance;
        Point2D dp=bezierPt(bezier,param);
        Point2D.Double p = new Point2D.Double(
                (point1.getX() + point2.getX()) / 2 - dp.getX(),
                (point1.getY() + point2.getY()) / 2 - dp.getY());
        double distance = p.distance(0, 0);
        return (distance < toleranceMore) ? 0 : distance / (point1.distance(point2) + toleranceMore);
    }

    private double ptDot(Point2D p1, Point2D p2) {
        return p1.getX() * p2.getX() + p1.getY() * p2.getY();
    }

    private double ptSquare(Point2D p) {
        return p.getX() * p.getX() + p.getY() * p.getY();
    }

    private boolean ptIsZero(Point2D p) {
        return p.getX() == p.getY() && p.getX() == 0;
    }

    private Point2D ptUnit(Point2D p) {
        double len = p.distance(0, 0);
        return new Point2D.Double(p.getX() / len, p.getY() / len);
    }

    private Point2D ptMinus(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() - p2.getX(), p1.getY() - p2.getY());
    }

    private Point2D ptPlus(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    private Point2D ptTime(Point2D p, double value) {
        return new Point2D.Double(p.getX() * value, p.getY() * value);
    }

    private double[] B(double u) {
        return new double[]{
                    (1 - u) * (1 - u) * (1 - u),
                    3 * u * (1 - u) * (1 - u),
                    3 * u * u * (1 - u),
                    u * u * u
                };
    }
}
