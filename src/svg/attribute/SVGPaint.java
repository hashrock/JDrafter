/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package svg.attribute;

import java.awt.Paint;
import svg.SVGElement;
import svg.oject.SVGObject;

/**
 *
 * @author takashi
 */
public interface SVGPaint extends SVGElement{
    public Paint getPaint(SVGObject obj);
    public void setPaint(Paint paint);
}
