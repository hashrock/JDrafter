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
import javax.swing.*;
import javax.swing.event.*;
import java.util.regex.*;
import java.util.*;
import jscreen.JEnvironment;
/**
 *距離入力のためのFormattedTextFieldのサブクラスです.
 *入力された数値及び単位のペアをポイント単位に変換し、Valueに
 *格納します。表示は、指定された単位(mm又はpoint)に変換し表示
 *します.
 * @author i002060
 */
public class JDLengthTextField extends JFormattedTextField{
    private int unit;
    private Vector<ChangeListener> changeListeners;
    /** Creates a new instance of JDLengthTextField */
    public JDLengthTextField() {
        changeListeners=new Vector<ChangeListener>();
        unit=JEnvironment.guageUnit;
        setFocusLostBehavior(JFormattedTextField.COMMIT);
        this.setFormatterFactory(new myFormatterFactory());
    }
    public void addChangeListener(ChangeListener listener){
        if (changeListeners.contains(listener))
            return;
        changeListeners.add(listener);
    }
    public void removeChangeListener(ChangeListener listener){
        changeListeners.remove(listener);
    }
    private void fireChangeEvent(){
        ChangeEvent e=new ChangeEvent(this);
        for (int i=0;i<changeListeners.size();i++)
            changeListeners.get(i).stateChanged(e);
    }
    public void commitEdit() throws java.text.ParseException{
        try{
            super.commitEdit();
        }catch (java.text.ParseException e){
            throw e;
        }
        fireChangeEvent();
    }
    public void setUnit(int unit){
        this.unit=unit;
    }
    public int getUnit(){
        return unit;
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
            //ミリ単位入力
            if ((ret=getDouble(c,"mm"))!=null){
                return new Double(ret.doubleValue()/JEnvironment.MIL_PER_INCH*72);
            }
            if ((ret=getDouble(c,"cm"))!=null){
                return new Double (ret.doubleValue()/JEnvironment.MIL_PER_INCH*720);
            }
            if ((ret=getDouble(c,"m"))!=null){
                return new Double (ret.doubleValue()/JEnvironment.MIL_PER_INCH*72000);
            }
            if ((ret=getDouble(c,"inch"))!=null){
                return new Double (ret.doubleValue()*72);
            }
            if ((ret=getDouble(c,"in"))!=null){
                return new Double (ret.doubleValue()*72);
            }
            if ((ret=getDouble(c,"Point"))!=null)
                return ret;
            if ((ret=getDouble(c,"pt"))!=null)
                return ret;
            if ((ret=getDouble(c,""))!=null){
                if (unit ==JEnvironment.METRIC_GAUGE){
                    return new Double(ret.doubleValue()/JEnvironment.MIL_PER_INCH*72);
                }else
                    return ret;
            }
            throw new ParseException("NumelicError",0);
        }
        private Double getDouble(String target,String unit){
            Pattern p=Pattern.compile("^-?\\d*\\.?\\d*\\s*"+unit+"$",Pattern.CASE_INSENSITIVE);
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
            if (unit==JEnvironment.METRIC_GAUGE){
                vl=new Double( vl.doubleValue()/72*JEnvironment.MIL_PER_INCH);
                return String.format("%5.4g",vl.doubleValue())+" mm";
            }
            return String.format("%5.4g",vl.doubleValue())+" point";
        }
        
    }
    
}
