/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.swt.widgets.figures.AbstractMultiFigure;
import org.csstudio.swt.widgets.figures.AbstractMultiFigure.LabelType;
import org.csstudio.swt.widgets.figures.AbstractMultiFigure.LabelPosition;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * This class defines a common widget model for all the boolean widgets. 
 * @author Xihui Chen
 * @author Dylan Maxwell
 */
public abstract class AbstractMultiWidgetModel extends AbstractPVWidgetModel {
	
	/** Number of states for this multi state widget */
	public static final String PROP_NSTATES = "nstates"; //$NON-NLS-1$
	
	/** Description for the states property */
	public static final String DESC_NSTATES = "Number of States"; //$NON-NLS-1$

	/** Label text for multi state X */ 
	public static final String PROP_STATE_LABEL = "state_label_%s";
	
	/** Description for label text properties */ 
	public static final String DESC_STATE_LABEL = "State Label %s";
	
	/** Widget color for multi state X */
	public static final String PROP_STATE_COLOR = "state_color_%s";
	
	/** Description for widget color properties */
	public static final String DESC_STATE_COLOR = "State Color %s";
	
	/** State value for multi state X */
	public static final String PROP_STATE_VALUE = "state_value_%s";
	
	/** Description for state value properties */
	public static final String DESC_STATE_VALUE = "State Value %s";
	
	/** True if the boolean label should be visible. */
	public static final String PROP_SHOW_LABEL = "show_label"; //$NON-NLS-1$
	
	/** Description for show label property */
	public static final String DESC_SHOW_LABEL = "Show Label";
	
	/** Label position enumeration */
	public static final String PROP_LABEL_POS = "label_position"; //$NON-NLS-1$
	
	/** Description for label position property */
	public static final String DESC_LABEL_POS = "Label Position";
	
	/** Data type to be used by this widget. If "enum" is selected,
	 *  then use the enum text for the label. Otherwise the specified labels are used. */ 
	public static final String PROP_LABEL_TYPE = "label_type"; //$NON-NLS-1$
	
	/** Description for the data type property */
	public static final String DESC_LABEL_TYPE = "Label Type"; //$NON-NLS-1$
	
	
	/**If data type is Enum, it is the string value which will be written 
	 * to the PV when widget is on. */
	//public static final String PROP_ON_STATE = "on_state"; //$NON-NLS-1$
	
	/**If data type is Enum, it is the string value which will be written 
	 * to the PV when widget is off. */
	//public static final String PROP_OFF_STATE = "off_state"; //$NON-NLS-1$
	
	/** The default color of the on color property. */
	//private static final RGB DEFAULT_ON_COLOR = new RGB(0,255,0);
	
	/** The default color of the off color property. */
	//private static final RGB DEFAULT_OFF_COLOR = new RGB(0, 100 ,0);
	
	/** The default string of the on label property. */
	//private static final String DEFAULT_ON_LABEL = "ON";
	
	/** The default string of the off label property. */
	//private static final String DEFAULT_OFF_LABEL = "OFF";
	
	

	@Override
	protected void configureProperties() {				
		//addProperty(new IntegerProperty(PROP_BIT, "Bit",
		//		WidgetPropertyCategory.Behavior, -1, -1, 63));
		
		addProperty(new BooleanProperty(PROP_SHOW_LABEL,
				DESC_SHOW_LABEL,WidgetPropertyCategory.Display, false));
		addProperty(new IntegerProperty(PROP_NSTATES,
				DESC_NSTATES, WidgetPropertyCategory.Display, 2, 1, 10));
		
		addProperty(new StringProperty(String.format(PROP_STATE_LABEL, 0),
				String.format(DESC_STATE_LABEL, "Default"), WidgetPropertyCategory.Display, "0"));
		addProperty(new ColorProperty(String.format(PROP_STATE_COLOR, 0),
				String.format(DESC_STATE_COLOR, "Default"), WidgetPropertyCategory.Display, new RGB(0,255,0)));
		
		for(int state=1; state<=AbstractMultiFigure.MAX_NSTATES; state++) {
			addProperty(new StringProperty(String.format(PROP_STATE_LABEL, state),
					String.format(DESC_STATE_LABEL, state), WidgetPropertyCategory.Display, "1"));
			addProperty(new ColorProperty(String.format(PROP_STATE_COLOR, state),
					String.format(DESC_STATE_COLOR, state), WidgetPropertyCategory.Display, new RGB(0,255,0)));
			addProperty(new IntegerProperty(String.format(PROP_STATE_VALUE, state),
					String.format(DESC_STATE_VALUE, state), WidgetPropertyCategory.Display, 0));
			//if(state > 2) {
			//	setPropertyVisible(String.format(PROP_STATE_LABEL, state), false);
			//	setPropertyVisible(String.format(PROP_STATE_COLOR, state), false);
			//	setPropertyVisible(String.format(PROP_STATE_VALUE, state), false);
			//}
		}
		
		addProperty(new ComboProperty(PROP_LABEL_TYPE, DESC_LABEL_TYPE,
				WidgetPropertyCategory.Display, LabelType.toStrings(), LabelType.REGULAR.ordinal()));
		addProperty(new ComboProperty(PROP_LABEL_POS, DESC_LABEL_POS, 
				WidgetPropertyCategory.Display, LabelPosition.toStrings(), LabelPosition.DEFAULT.ordinal()));
	}


	public int getNStates() {
		return (int) getProperty(PROP_NSTATES).getPropertyValue();
	}
	
	/**
	 * @return the on label
	 */
	//public String getOnLabel() {
	//	return  ""; //(String) getProperty(PROP_ON_LABEL).getPropertyValue();
	//}

	/**
	 * @return the off label
	 */
	//public String getOffLabel() {
	//	return ""; //(String) getProperty(PROP_OFF_LABEL).getPropertyValue();
	//}
	
	public String getStateLabel(int state) {
		return (String) getProperty(String.format(PROP_STATE_LABEL, state)).getPropertyValue();
	}
	
	public int getStateValue(int state) {
		return (Integer) getProperty(String.format(PROP_STATE_VALUE, state)).getPropertyValue();
	}
	
	/**
	 * @return the on color
	 */
	//public Color getOnColor() {
	//	return getSWTColorFromColorProperty(String.format(PROP_COLOR, 1));
	//}	
	/**
	 * @return the off color
	 */
	//public Color getOffColor() {
	//	return getSWTColorFromColorProperty(String.format(PROP_COLOR, 1));
	//}
	
	public Color getStateColor(int state) {
		return getSWTColorFromColorProperty(String.format(PROP_STATE_COLOR, state));
	}
	
	/**
	 * @return true if the boolean label should be shown, false otherwise
	 */
	public boolean isShowLabel() {
		return (Boolean) getProperty(PROP_SHOW_LABEL).getPropertyValue();
	}
	
	public LabelType getLabelType(){
		return LabelType.values()[(int)getPropertyValue(PROP_LABEL_TYPE)];
	}
	
//	public String getOnState(){
//		return ""; //(String)getPropertyValue(PROP_ON_STATE);
//	}
//	
//	public String getOffState(){
//		return ""; //(String)getPropertyValue(PROP_OFF_STATE);
//	}
	
	public LabelPosition getLabelPosition(){
		return LabelPosition.values()[(Integer)getPropertyValue(PROP_LABEL_POS)];
	}
}
