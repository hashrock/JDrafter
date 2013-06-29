/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.oject.svgshape;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import svg.oject.*;

/**
 *
 * @author takashi
 */
public class SVGPath extends SVGShape {
    
    public static final String D = "d";
    
    private SVGPath() {
    }
    
    public SVGPath(SVGObject parent, Attributes attr) {
        super(parent);
        setAttributes(attr);
        setObjectAttributes(attr);
    }
    
    @Override
    public Shape createShape() {
        String f = "(\\s*(\\-)?((\\d+(\\.\\d+)?)|(\\.\\d+)))";
        String units = "(\\s*((pt)|(px)|(cm)|(mm)|(pc)|(em)|(ex)|(%))?)";
        String sep = "((\\s*\\,)|(\\s+))";
        String mltMatch = "(\\s*[mMlLtT](" + f + units + sep + f + units + ")+)";
        String hvMatch = "(\\s*[hHvV](" + f + units + ")+)";
        String qsMatch = "(\\s*[QqSs](" + f + units + "(" + sep + f + units + "){3})+)";
        String cMatch = "(\\s*[cC](\\s*" + f + units + "(" + sep + f + units + "){5})+)";
        String aMatch="(\\s*[aA](\\s*" + f + units+sep+f+units+sep+f+sep+f+sep+f+sep+f+units+sep+f+units+")+)";
        String zMatch = "(\\s*[zZ])";
        String match = mltMatch + "|" + hvMatch + "|" + qsMatch + "|" + cMatch + "|" +aMatch+"|"+ zMatch;
        String s = objectAttr.get(D);
        if (s == null) {
            return null;
        }
        s=s.replaceAll("\\-", " -");
        Pattern p = Pattern.compile(match);
        Matcher matcher = p.matcher(s);
        GeneralPath gp = new GeneralPath();
        int i = 0;
        float cx = 0, cy = 0, x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        boolean prevCurve = false;
        boolean prevQuad = false;
        while (matcher.find()) {
            String sc = matcher.group().trim();
            char cm = sc.charAt(0);
            sc = (sc.substring(1)).trim();
            String[] params = sc.split("(\\s*\\,\\s*)|(\\s+)");
            switch (cm) {
                case 'm':
                    if (i != 0) {
                        for (int j = 0; j < params.length - 1; j += 2) {
                            cx += toPixel(this, params[j], HORIZONTAL, LENGTH);
                            cy += toPixel(this, params[j + 1], VERTICAL, LENGTH);
                            gp.moveTo(cx, cy);
                        }
                        prevCurve = false;
                        prevQuad = false;
                        break;
                    }
                case 'M':
                    for (int j = 0; j < params.length - 1; j += 2) {
                        cx = toPixel(this, params[j], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        gp.moveTo(cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = false;
                    break;
                case 'l':
                    for (int j = 0; j < params.length - 1; j += 2) {
                        cx += toPixel(this, params[j], HORIZONTAL, LENGTH);
                        cy += toPixel(this, params[j + 1], VERTICAL, LENGTH);
                        gp.lineTo(cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = false;
                    break;
                case 'L':
                    for (int j = 0; j < params.length - 1; j += 2) {
                        cx = toPixel(this, params[j], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        gp.lineTo(cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = false;
                    break;
                case 'h':
                    for (int j=0;j<params.length;j++){
                        cx+=toPixel(this,params[0],HORIZONTAL,LENGTH);
                        gp.lineTo(cx,cy);
                    }
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 'H':
                    for (int j=0;j<params.length;j++){
                        cx=toPixel(this,params[0],HORIZONTAL,LENGTH);
                        gp.lineTo(cx,cy);
                    }
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 'v':
                    for (int j=0;j<params.length;j++){
                        cy+=toPixel(this,params[0],VERTICAL,LENGTH);
                        gp.lineTo(cx,cy);
                    }
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 'V':
                    for (int j=0;j<params.length;j++){
                        cy=toPixel(this,params[0],VERTICAL,LENGTH);
                        gp.lineTo(cx,cy);
                    }
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 's':
                    for (int j = 0; j < params.length - 3; j += 4) {
                        if (prevCurve) {
                            x1 = 2 * cx - x2;
                            y1 = 2 * cy - y2;
                        } else {
                            x1 = cx;
                            y1 = cy;
                        }
                        x2 = cx + toPixel(this, params[j], HORIZONTAL, LENGTH);
                        y2 = cy + toPixel(this, params[j + 1], VERTICAL, LENGTH);
                        cx += toPixel(this, params[j + 2], HORIZONTAL, LENGTH);
                        cy += toPixel(this, params[j + 3], VERTICAL, LENGTH);
                        gp.curveTo(x1, y1, x2, y2, cx, cy);
                        prevCurve = true;
                        prevQuad = false;
                    }
                    
                    break;
                case 'S':
                    for (int j = 0; j < params.length - 3; j += 4) {
                        if (prevCurve) {
                            x1 = 2 * cx - x2;
                            y1 = 2 * cy - y2;
                        } else {
                            x1 = cx;
                            y1 = cy;
                        }
                        x2 = toPixel(this, params[j], HORIZONTAL, COORDS);
                        y2 = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        cx = toPixel(this, params[j + 2], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 3], VERTICAL, COORDS);
                        gp.curveTo(x1, y1, x2, y2, cx, cy);
                        prevCurve = true;
                        prevQuad = false;
                    }
                    
                    break;
                case 'c':
                    for (int j = 0; j < params.length - 5; j += 6) {
                        x1 = cx + toPixel(this, params[j], HORIZONTAL, LENGTH);
                        y1 = cy + toPixel(this, params[j + 1], VERTICAL, LENGTH);
                        x2 = cx + toPixel(this, params[j + 2], HORIZONTAL, LENGTH);
                        y2 = cy + toPixel(this, params[j + 3], VERTICAL, LENGTH);
                        cx += toPixel(this, params[j + 4], HORIZONTAL, LENGTH);
                        cy += toPixel(this, params[j + 5], VERTICAL, LENGTH);
                        gp.curveTo(x1, y1, x2, y2, cx, cy);
                        prevCurve = true;
                        prevQuad = false;
                    }
                    
                    break;
                case 'C':
                    for (int j = 0; j < params.length - 5; j += 6) {
                        x1 = toPixel(this, params[j], HORIZONTAL, COORDS);
                        y1 = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        x2 = toPixel(this, params[j + 2], HORIZONTAL, COORDS);
                        y2 = toPixel(this, params[j + 3], VERTICAL, COORDS);
                        cx = toPixel(this, params[j + 4], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 5], VERTICAL, COORDS);
                        gp.curveTo(x1, y1, x2, y2, cx, cy);
                    }
                    prevCurve = true;
                    prevQuad = false;
                    break;
                case 't':
                    for (int j = 0; j < params.length - 1; j += 2) {
                        if (prevQuad) {
                            x1 = 2 * cx - x1;
                            y1 = 2 * cy - y1;
                        } else {
                            x1 = cx;
                            y1 = cy;
                        }
                        cx += toPixel(this, params[j], HORIZONTAL, LENGTH);
                        cy += toPixel(this, params[j + 1], VERTICAL, LENGTH);
                        gp.quadTo(x1, y1, cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = true;
                    break;
                case 'T':
                    for (int j = 0; j < params.length - 1; j += 2) {
                        if (prevQuad) {
                            x1 = 2 * cx - x1;
                            y1 = 2 * cy - y1;
                        } else {
                            x1 = cx;
                            y1 = cy;
                        }
                        cx = toPixel(this, params[j], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        gp.quadTo(x1, y1, cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = true;
                    break;
                case 'q':
                    for (int j = 0; j < params.length - 3; j += 4) {
                        x1 = cx + toPixel(this, params[j], HORIZONTAL, LENGTH);
                        y1 = cy + toPixel(this, params[j + 1], VERTICAL, LENGTH);
                        cx += toPixel(this, params[j + 2], HORIZONTAL, LENGTH);
                        cy += toPixel(this, params[j + 3], VERTICAL, LENGTH);
                        gp.quadTo(x1, y1, cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = true;
                    break;
                case 'Q':
                    for (int j = 0; j < params.length - 3; j += 4) {
                        x1 = toPixel(this, params[j], HORIZONTAL, COORDS);
                        y1 = toPixel(this, params[j + 1], VERTICAL, COORDS);
                        cx = toPixel(this, params[j + 2], HORIZONTAL, COORDS);
                        cy = toPixel(this, params[j + 3], VERTICAL, COORDS);
                        gp.quadTo(x1, y1, cx, cy);
                    }
                    prevCurve = false;
                    prevQuad = true;
                    break;
                case 'A':
                    for (int j=0;j < params.length-6;j+=7){
                        x1=cx;
                        y1=cy;
                        float rx=toPixel(this,params[j],HORIZONTAL,LENGTH);
                        float ry=toPixel(this,params[j+1],VERTICAL,LENGTH);
                        cx=toPixel(this,params[j+5],HORIZONTAL,COORDS);
                        cy=toPixel(this,params[j+6],VERTICAL,COORDS);
                        float rotate=Float.valueOf(params[j+2].trim());
                        int lFlag=Integer.valueOf(params[j+3].trim());
                        int sFlag=Integer.valueOf(params[j+4].trim());
                        Arc2D arc=computeArc(x1,y1,rx,ry,rotate,lFlag==1,sFlag==1,cx,cy);
                        AffineTransform t = AffineTransform.getRotateInstance
                                (Math.toRadians(rotate), arc.getCenterX(), arc.getCenterY());
                        Shape sp = t.createTransformedShape(arc);
                        gp.append(sp, true);
                    }
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 'a':
                    for (int j=0;j < params.length-6;j+=7){
                        x1=cx;
                        y1=cy;
                        float rx=toPixel(this,params[j],HORIZONTAL,LENGTH);
                        float ry=toPixel(this,params[j+1],VERTICAL,LENGTH);
                        cx+=toPixel(this,params[j+5],HORIZONTAL,LENGTH);
                        cy+=toPixel(this,params[j+6],VERTICAL,LENGTH);
                        float rotate=Float.valueOf(params[j+2].trim());
                        int lFlag=Integer.valueOf(params[j+3].trim());
                        int sFlag=Integer.valueOf(params[j+4].trim());
                        Arc2D arc=computeArc(x1,y1,rx,ry,rotate,lFlag==1,sFlag==1,cx,cy);
                        AffineTransform t = AffineTransform.getRotateInstance
                                (Math.toRadians(rotate), arc.getCenterX(), arc.getCenterY());
                        Shape sp = t.createTransformedShape(arc);
                        gp.append(sp, true);
                    }
                    gp.lineTo(cx,cy);
                    prevCurve=false;
                    prevQuad=false;
                    break;
                case 'z':
                case 'Z':
                    gp.closePath();
                    prevCurve = prevQuad = false;
            }
        }
        return gp;
    }
    
    @Override
    protected void setObjectAttributes(Attributes attr) {
        String s=attr.getValue(D);
        if (s !=null)
            objectAttr.put(D, s);
    }
    
    @Override
    public SVGPath getWritableInstance(SVGObject newParent) {
        SVGPath result = new SVGPath();
        result.setAttributes(getAttributes().clone());
        result.objectAttr = (HashMap<String,String>)objectAttr.clone();
        result.viewAttribute = (HashMap<String,String>)viewAttribute.clone();
        result.parent = newParent;
        return result;
    }
    
    @Override
    public String getName() {
        return "path";
    }
    
    @Override
    public String getXML() {
        return "";
    }
    @Override
    public void paintMaker(Graphics2D g,Shape s){
        SVGMarker marker=null;
        marker=getAttributes().getStartMarker(this);
        if (marker !=null){
            marker.paintMarker(g,1,s);
        }
        marker=getAttributes().getMiddleMarker(this);
        if (marker !=null){
            marker.paintMarker(g,2,s);
        }
        marker=getAttributes().getEndMarker(this);
        if (marker!=null){
            marker.paintMarker(g,4,s);
        }
        System.out.println("Marker Painted");
    }
    public static Arc2D computeArc(double x0, double y0,
            double rx, double ry,
            double angle,
            boolean largeArcFlag,
            boolean sweepFlag,
            double x, double y) {
        //
        // Elliptical arc implementation based on the SVG specification notes
        //
        
        // Compute the half distance between the current and the final point
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        // Convert angle from degrees to radians
        angle = Math.toRadians(angle % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        
        //
        // Step 1 : Compute (x1, y1)
        //
        double x1 = (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        // check that radii are large enough
        double radiiCheck = Px1/Prx + Py1/Pry;
        if (radiiCheck > 1) {
            rx = Math.sqrt(radiiCheck) * rx;
            ry = Math.sqrt(radiiCheck) * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }
        
        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
        double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);
        
        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);
        
        //
        // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
        //
        double ux = (x1 - cx1) / rx;
        double uy = (y1 - cy1) / ry;
        double vx = (-x1 - cx1) / rx;
        double vy = (-y1 - cy1) / ry;
        double p, n;
        // Compute the angle start
        n = Math.sqrt((ux * ux) + (uy * uy));
        p = ux; // (1 * ux) + (0 * uy)
        sign = (uy < 0) ? -1.0 : 1.0;
        double angleStart = Math.toDegrees(sign * Math.acos(p / n));
        
        // Compute the angle extent
        n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1.0 : 1.0;
        double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
        if(!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;
        
        //
        // We can now build the resulting Arc2D in double precision
        //
        Arc2D.Double arc = new Arc2D.Double();
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0;
        arc.height = ry * 2.0;
        arc.start = -angleStart;
        arc.extent = -angleExtent;
        
        return arc;
    }
    
    
}
