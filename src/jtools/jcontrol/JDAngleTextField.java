/*
 * JDLengthTextField.java
 *
 * Created on 2007/02/02, 9:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools.jcontrol;
import java.text.ParseException;
import javax.swing.event.*;
import javax.swing.*;
import java.util.*;
import java.util.regex.*;
/**
 *角度入力のためのFormattedTextFieldのサブクラスです.
 *入力された数値及び単位のペアをRadianに変換しValueに格納します.
 *表示はunitの指定をもとに度、又はRadiannで行います.
 * @author i002060
 */
public class JDAngleTextField extends JFormattedTextField{
    public static final int DEGREE=0;
    public static final int RADIAN=1;
    private Vector<ChangeListener> changeListener;
    private int unit;
    /** Creates a new instance of JDLengthTextField */
    public JDAngleTextField() {
        unit=DEGREE;
        setFocusLostBehavior(JFormattedTextField.COMMIT);
        this.setFormatterFactory(new myFormatterFactory());
        changeListener=new Vector<ChangeListener>();
    }
    public void setUnit(int unit){
        this.unit=unit;
    }
    public int getUnit(){
        return unit;
    }
    public void addChangeListener(ChangeListener listener){
        if (changeListener.contains(listener))
            return;
        changeListener.add(listener);
    }
    public void removeChangeListener(ChangeListener listener){
        changeListener.remove(listener);
    }
    protected void fireChangeEvent(){
        ChangeEvent e=new ChangeEvent(this);
        for (int i=0;i<changeListener.size();i++)
            changeListener.get(i).stateChanged(e);
    }
    public void commitEdit() throws java.text.ParseException{
        try{
            super.commitEdit();
        }catch (java.text.ParseException e){
            throw e;
        }
        fireChangeEvent();
    }
    class myFormatterFactory extends JFormattedTextField.AbstractFormatterFactory{
        myFormatter formatter;
        public myFormatterFactory(){
            formatter=new myFormatter();
        }
        public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
            return formatter;
        }
        
    }
    class myFormatter extends JFormattedTextField.AbstractFormatter{
        public Object stringToValue(String text) throws ParseException {
            Pattern p;
            Matcher m;
            Double ret;
            String c=text.trim();
            Number returnValue=null;
            if (c.equals("")) c="0";
            if ((returnValue=getDouble(c,"°"))!=null){
                return new Double(returnValue.doubleValue()*Math.PI/180);
            }
            if ((returnValue=getDouble(c,"rad"))!=null){
                return returnValue;
            }
            if ((returnValue=getDouble(c,"radian"))!=null)
                return returnValue;
            if ((returnValue=getDouble(c,""))!=null){
                if (unit==DEGREE){
                    return new Double(returnValue.doubleValue()*Math.PI/180);
                }else{
                    return returnValue;
                }
            }
            throw new ParseException("NumericException",0);
        }
        private Double getDouble(String target,String unit){
            Pattern p=Pattern.compile("^\\d*\\.?\\d*\\s*"+unit+"$",Pattern.CASE_INSENSITIVE);
            Matcher m=p.matcher(target);
            if (!m.matches()) return null;
            p=Pattern.compile("\\s*"+unit,Pattern.CASE_INSENSITIVE);
            m=p.matcher(target);
            return new Double(Double.valueOf(m.replaceAll("")));
        }
        public String valueToString(Object value) throws ParseException {
            if (!(value instanceof Double))
                throw new ParseException("NumelicError",0);
            Double vl=(Double)value;
            if (unit==DEGREE){
                return String.format("%4.2f",(vl.doubleValue()/Math.PI*180))+"°";
            }
            return String.format("%g",vl.doubleValue())+"rad";
        }
        
    }
    
}
