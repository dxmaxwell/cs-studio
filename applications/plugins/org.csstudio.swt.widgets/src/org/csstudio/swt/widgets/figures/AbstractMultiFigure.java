/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.figures;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.logging.Level;

import org.csstudio.swt.widgets.Activator;
import org.csstudio.swt.widgets.introspection.DefaultWidgetIntrospector;
import org.csstudio.swt.widgets.introspection.Introspectable;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
/**
 * Base figure for a widget based on {@link AbstractBoolWidgetModel}.
 *
 * @author Xihui Chen
 * @author Dylan Maxwell
 *
 */
public class AbstractMultiFigure extends Figure implements Introspectable {
	
	public static final int MAX_NSTATES = 10;
	
//	public enum TotalBits {
//		BITS_16,
//		BITS_32,
//		BITS_64
//	}
	
	public enum LabelType {
		REGULAR("Regular"),
		ENUMERATION("Enumeration");	
		
		public static String[] toStrings() {
			Object[] values = values();
			String[] strings = new String[values.length];
			for(int idx=0; idx<values.length; idx++) {
				strings[idx] = values[idx].toString();
			}
			return strings;
		}
		
		String descripion;
		LabelType(String description){
			this.descripion = description;
		}
		@Override
		public String toString() {
			return descripion;
		}
	}
	
	public static enum LabelPosition {
		DEFAULT("Default"),				
		TOP("Top"),	
		LEFT("Left"),
		CENTER("Center"),
		RIGHT("Right"),
		BOTTOM("Bottom"),
		TOP_LEFT("Top Left"),
		TOP_RIGHT("Top Right"),	
		BOTTOM_LEFT("Bottom Left"),
		BOTTOM_RIGHT("Bottom Right");
		
		public static String[] toStrings() {
			Object[] values = values();
			String[] strings = new String[values.length];
			for(int idx=0; idx<values.length; idx++) {
				strings[idx] = values[idx].toString();
			}
			return strings;
		}
		
		String descripion;
		LabelPosition(String description){
			this.descripion = description;
		}
		@Override
		public String toString() {
			return descripion;
		}
	}
	
	public static final String DEFAULT_STATE_LABEL = "";
			
	public static final Color DEFAULT_STATE_COLOR = CustomMediaFactory.getInstance().getColor(36,36, 36); // Grey
	
	public static final int DEFAULT_STATE_VALUE = 0;
	
	
	//private TotalBits totalBits = TotalBits.BITS_64;  
	

	
	protected int state = 0;
	
	protected int nstates = 0;

	protected Object value = DEFAULT_STATE_VALUE;
	
	protected String[] stateLabels = new String[MAX_NSTATES+1];
	
	protected Color[] stateColors = new Color[MAX_NSTATES+1];
	
	protected int[] stateValues = new int[MAX_NSTATES+1];
	
	
	//protected String defaultLabel;
	
	//protected Color defaultColor;
	
	//protected boolean booleanValue = false;

	//protected String onLabel = "ON";

	//protected String offLabel = "OFF";
	
	protected LabelPosition labelPosition = LabelPosition.DEFAULT;

	//protected Color onColor = CustomMediaFactory.getInstance().getColor(
	//		CustomMediaFactory.COLOR_GREEN);

	//protected Color offColor = CustomMediaFactory.getInstance().getColor(
	//		new RGB(0,128,0));

	private Point labelLocation = null;

	protected boolean showLabel = false;
	
	protected Label label = null;

	protected AbstractMultiFigure() {
		Arrays.fill(stateLabels, DEFAULT_STATE_LABEL);
		Arrays.fill(stateColors, DEFAULT_STATE_COLOR);
		Arrays.fill(stateValues, DEFAULT_STATE_VALUE); 
		label = new Label(stateLabels[state]) {
			@Override
			public boolean containsPoint(int x, int y) {
				return false;
			}
		};
		label.setVisible(showLabel);
	}

	protected void calculateLabelLocation(Point defaultLocation) {
		if(labelPosition == LabelPosition.DEFAULT) {
			labelLocation = defaultLocation;
			return;
		}
		Rectangle textArea = getClientArea();		
		Dimension textSize = TextUtilities.INSTANCE.getTextExtents(
			label.getText(), getFont());
			int x=0;
			if(textArea.width > textSize.width){				
				switch (labelPosition) {
				case CENTER:
				case TOP:
				case BOTTOM:
					x = (textArea.width - textSize.width)/2;
					break;
				case RIGHT:
				case TOP_RIGHT:
				case BOTTOM_RIGHT:
					x = textArea.width - textSize.width;
					break;
				default:					
					break;
				}
			}
			
			int y=0;
			if(textArea.height > textSize.height){
				switch (labelPosition) {
				case CENTER:
				case LEFT:
				case RIGHT:
					y = (textArea.height - textSize.height)/2;
					break;
				case BOTTOM:
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT:
					y =textArea.height - textSize.height;
					break;
				default:
					break;
				}
			}
			if(useLocalCoordinates()) {
				labelLocation = new Point(x, y);
			} else {
				labelLocation = new Point(x + textArea.x, y + textArea.y);
			}
	}

	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
		if(value instanceof Number) {
			int v = ((Number)value).intValue();
			for(int idx=1; idx<=nstates; idx++) {
				if(v == stateValues[idx]) {
					setState(idx);
					return;
				}
			}
		}
		else if(value instanceof Boolean) {
			if((Boolean)value) {
				setState(1);
			}
		}
		setState(0);
	}
	
	public int getState() {
		return state;
	}
	protected void setState(int state) {
		this.state = state;
		label.setText(this.stateLabels[state]);
	}
	
	public int getNStates() {
		return nstates;
	}
	public void setNStates(int nstates) {
		this.nstates = nstates;
		setValue(value);
	}
	
	public Color getStateColor(int state) {
		return stateColors[state];
	}
	public void setStateColor(int state, Color color) {
		stateColors[state] = color;
	}
	
	public int getStateValue(int state) {
		return stateValues[state];
	}
	public void setStateValue(int state, int value) {
		stateValues[state] = value;
	}
	
	public String getStateLabel(int state) {
		return stateLabels[state];
	}
	public void setStateLabel(int state, String label) {
		stateLabels[state] = label;
		if(this.state == state) {
			this.label.setText(label);
		}
	}
	/**
	 * @return the bit
	 */
	//public int getBit() {
	//	return bit;
	//}

	/**
	 * @return the boolValue
	 */
	//public boolean getBooleanValue() {
	//	return booleanValue;
	//}
	
	protected Point getLabelLocation(final int x, final int y){
		return getLabelLocation(new Point(x, y));
	}
	
	/**
	 * @param defaultLocation The default location.
	 * @return the location of the boolean label
	 */
	protected Point getLabelLocation(Point defaultLocation){
		if(labelLocation == null)
			calculateLabelLocation(defaultLocation);
		return labelLocation;
	}
	
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	/**
	 * @return the offColor
	 */
	//public Color getOffColor() {
	//	return offColor;
	//}

	
//	public String getLabel() {
//		return label.getText();
//	}
//	public void setLabel(String label) {
//		this.label.setText(label);
//	}
//	
//	public Color getColor() {
//		return color;
//	}
//	public void setColor(Color color) {
//		this.color = color;
//	}


	/**
	 * @return the offLabel
	 */
	//public String getOffLabel() {
	//	return offLabel;
	//}
	
	/**
	 * @return the onColor
	 */
	//public Color getOnColor() {
	//	return onColor;
	//}

	/**
	 * @return the onLabel
	 */
	//public String getOnLabel() {
	//	return onLabel;
	//}

	/**
	 * @return the value
	 */
	//public long getValue() {
	//	return value;
	//}

	@Override
	public void invalidate() {
		labelLocation = null;
		super.invalidate();
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}

	/**
	 * @return the showBooleanLabel
	 */
	public boolean isShowLabel() {
		return showLabel;
	}
	public void setShowLabel(boolean showLabel) {
		if(this.showLabel != showLabel) {
			this.showLabel = showLabel;
			this.label.setVisible(showLabel);
		}
	}
	
	
	/**
	 * @param bit the bit to set
	 */
	//public void setBit(int bit) {
	//	if(this.bit == bit)
	//		return;
	//	this.bit = bit;
	//	updateBoolValue();
	//}


	//public void setBooleanValue(boolean value){
	//	if(this.booleanValue == value)
	//		return;
	//	this.booleanValue = value;
	//	updateValue();
	//}

	@Override
	public void setEnabled(boolean value) {
		super.setEnabled(value);
		repaint();
	}
	
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		label.setFont(f);
		revalidate();
	}
	
	public void setLabelPosition(LabelPosition labelPosition) {
		this.labelPosition = labelPosition;
		//labelPosition = null;
		revalidate();
		repaint();
	}

	/**
	 * @param offColor the offColor to set
	 */
	//public void setOffColor(Color offColor) {
	//	if(this.offColor != null && this.offColor.equals(offColor))
	//		return;
	//	this.offColor = offColor;
	//	repaint();
	//}

	/**
	 * @param offLabel the offLabel to set
	 */
	//public void setOffLabel(String offLabel) {
	//	if(this.offLabel != null && this.offLabel.equals(offLabel))
	//		return;
	//	this.offLabel = offLabel;
	//	if(!booleanValue)
	//		boolLabel.setText(offLabel);
	//	
	//}

	/**
	 * @param onColor the onColor to set
	 */
	//public void setOnColor(Color onColor) {
	//	if(this.onColor != null && this.onColor.equals(onColor))
	//		return;
	//	this.onColor = onColor;
	//	repaint();
	//}

	/**
	 * @param onLabel the onLabel to set
	 */
	//public void setOnLabel(String onLabel) {
	//	if(this.onLabel != null && this.onLabel.equals(onLabel))
	//		return;
	//	this.onLabel = onLabel;
	//	if(booleanValue)
	//		boolLabel.setText(onLabel);
	//}

	/**
	 * @param showBooleanLabel the showBooleanLabel to set
	 */
//	public void setShowBooleanLabel(boolean showBooleanLabel) {
//		if(this.showBooleanLabel == showBooleanLabel)
//			return;
//		this.showBooleanLabel = showBooleanLabel;
//		boolLabel.setVisible(showBooleanLabel);
//	}

	/**
	 * @param value the value to set
	 */
	//public void setValue(double value) {
	//	setValue((long)value);
	//}

	/**
	 * @param value the value to set
	 */
	//public void setValue(long value) {
	//	if(this.value == value)
	//		return;
	//	this.value = value;
	//	updateBoolValue();
	//	revalidate();
	//	repaint();
	//}

	/**
	 * update the boolValue from value and bit.
	 * All the boolValue based behavior changes should be implemented here by inheritance.
	 */
//	protected void updateBoolValue() {
//		//get boolValue
//		if(bit < 0)
//			booleanValue = (this.value != 0);
//		else if(bit >=0) {
//			booleanValue = ((value>>bit)&1L) >0;
//		}
//		//change boolLabel text
//		if(booleanValue)
//			boolLabel.setText(onLabel);
//		else
//			boolLabel.setText(offLabel);
//	}

	/**
	 * update the value from boolValue
	 */
//	@SuppressWarnings("nls")
//    protected void updateValue(){
//		//get boolValue
//		if(bit < 0)
//			setValue(booleanValue ? 1 : 0);
//		else if(bit >=0) {
//			if(bit >= 64) {
//			    // Log with exception to obtain call stack
//                Activator.getLogger().log(Level.WARNING, "Bit " + bit + "can not exceed 63.", new Exception());
//			}
//			else {
//				switch (totalBits) {
//				case BITS_16:
//					setValue(booleanValue? value | ((short)1<<bit) : value & ~((short)1<<bit));
//				break;				
//				case BITS_32:
//					setValue(booleanValue? value | ((int)1<<bit) : value & ~((int)1<<bit));
//				break;
//				default:				
//					setValue(booleanValue? value | (1L<<bit) : value & ~(1L<<bit));
//					break;
//				}			
//			}
//		}
//	}
	
//	public TotalBits getTotalBits() {
//		return totalBits;
//	}

	/**
	 * @param totalBits number of total bits
	 */
//	public void setTotalBits(TotalBits totalBits) {
//		this.totalBits = totalBits;
//	}


	public BeanInfo getBeanInfo() throws IntrospectionException {
		return new DefaultWidgetIntrospector().getBeanInfo(this.getClass());
	}

}
