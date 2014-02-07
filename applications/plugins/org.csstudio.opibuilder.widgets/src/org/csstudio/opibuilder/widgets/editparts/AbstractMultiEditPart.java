/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.AbstractMultiWidgetModel;

import org.csstudio.simplepv.VTypeHelper;
import org.csstudio.swt.widgets.figures.AbstractMultiFigure;
import org.csstudio.swt.widgets.figures.AbstractMultiFigure.LabelType;
import org.csstudio.swt.widgets.figures.AbstractMultiFigure.LabelPosition;
//import org.csstudio.swt.widgets.figures.AbstractMultiFigure.TotalBits;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.epics.vtype.VType;

/**
 * Base editPart controller for a widget based on {@link AbstractBoolWidgetModel}.
 *
 * @author Xihui Chen
 *
 */
public abstract class AbstractMultiEditPart extends AbstractPVWidgetEditPart {

	/**
	 * Sets those properties on the figure that are defined in the
	 * {@link AbstractBoolFigure} base class. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link AbstractBaseEditPart#doCreateFigure()}.
	 *
	 * @param figure
	 *            the figure.
	 * @param model
	 *            the model.
	 */
	protected void initializeCommonFigureProperties(
			final AbstractMultiFigure figure, final AbstractMultiWidgetModel model) {
		updatePropSheet(model.getLabelType());
		
		int nstates = model.getNStates();
		figure.setNStates(nstates);
		
		figure.setStateLabel(0, model.getStateLabel(0));
		figure.setStateColor(0, model.getStateColor(0));
		
		for(int state=1; state<=AbstractMultiFigure.MAX_NSTATES; state++) {
			figure.setStateLabel(state, model.getStateLabel(state));
			figure.setStateColor(state, model.getStateColor(state));
			figure.setStateValue(state, model.getStateValue(state));
			if(state > nstates) {
				model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, state), false);
				model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state), false);
				model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), false);
			}
		}
		figure.setShowLabel(model.isShowLabel());
		//figure.setOnLabel(model.getOnLabel());
		//figure.setOffLabel(model.getOffLabel());
		//figure.setOnColor(model.getOnColor());
		//figure.setOffColor(model.getOffColor());
		figure.setFont(CustomMediaFactory.getInstance().getFont(model.getFont().getFontData()));
		figure.setLabelPosition(model.getLabelPosition());

	}

	@Override
	public AbstractMultiWidgetModel getWidgetModel() {
		return (AbstractMultiWidgetModel)getModel();
	}

	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractBoolWidgetModel}. This method is provided for the convenience
	 * of subclasses, which can call this method in their implementation of
	 * {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerCommonPropertyChangeHandlers() {
		// value
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue, final IFigure refreshableFigure) {
				if(newValue == null || !(newValue instanceof VType)) {
					return false;
				}
				if(!(refreshableFigure instanceof AbstractMultiFigure)) {
					return false;
				}
				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
				
//				switch (VTypeHelper.getBasicDataType((VType) newValue)) {
//				case SHORT:
//					figure.setTotalBits(TotalBits.BITS_16);
//					break;
//				case INT:
//				case ENUM:
//					figure.setTotalBits(TotalBits.BITS_32);
//					break;
//				default:
//					break;
//				}
				updateFromValue((VType) newValue, figure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVVALUE, handler);

		//data type
	    handler = new IWidgetPropertyChangeHandler() {

	    	@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
				updateFromValue(getPVValue(AbstractPVWidgetModel.PROP_PVNAME), figure);
				updatePropSheet(LabelType.values()[(int)newValue]);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_LABEL_TYPE, handler);
		
//		getWidgetModel().getProperty(AbstractMultiWidgetModel.PROP_NAME_LABEL_TYPE).
//			addPropertyChangeListener(new PropertyChangeListener() {
//
//				public void propertyChange(PropertyChangeEvent evt) {
//					dataTypeHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
//				}
//			});
		
		setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, 0), new StateLabelChangeHandler(0)); 			
		setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, 0), new StateColorChangeHandler(0));
		
		for(int state=1; state<=AbstractMultiFigure.MAX_NSTATES; state++) {
		
	
			setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, state), new StateLabelChangeHandler(state)); 			
			setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state), new StateColorChangeHandler(state));
			setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), new StateValueChangeHandler(state));
		}
		
		
		handler = new IWidgetPropertyChangeHandler() {

			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				// TODO Auto-generated method stub
				
				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
				AbstractMultiWidgetModel model = getWidgetModel();
				
				
				int oldNStates = (int) oldValue;
				int newNStates = (int) newValue;
				if(newNStates > oldNStates) {
					System.out.println("More!!");
					for(int state = oldNStates+1; state<=newNStates; state++) {
						System.out.println("More!!");
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, state), true);
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state), true);
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), true);
						
					}
				} else {
					System.out.println("LEss");
					for(int state = oldNStates; state>newNStates; state--) {
						System.out.println("LEss");
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, state), false);
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state), false);
						model.setPropertyVisible(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), false);
					}
				}
				figure.setNStates(newNStates);
				
					//System.out.println("More!!");
//					for(int state = statesOld+1; state<=statesNew; state++) {
//						
//						getWidgetModel().addProperty(new StringProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, state),
//								String.format(AbstractMultiWidgetModel.DESC_STATE_LABEL, state), WidgetPropertyCategory.Display, String.valueOf(state)));
//						
//						setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), new StateLabelChangeHandler(state)); 
//						
//						getWidgetModel().addProperty(new ColorProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state),
//								String.format(AbstractMultiWidgetModel.DESC_STATE_COLOR, state), WidgetPropertyCategory.Display, String.valueOf(state)));
//						
//						setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, state), new StateColorChangeHandler(state));
//						
//						getWidgetModel().addProperty(new IntegerProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state),
//								String.format(AbstractMultiWidgetModel.DESC_STATE_VALUE, state), WidgetPropertyCategory.Display, 1));
//
//						setPropertyChangeHandler(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, state), new StateValueChangeHandler(state)); 
//					}
				//} else {
				//	System.out.println("Less!!");
//					for(int idx = statesOld; idx>statesNew; idx--) {
//						getWidgetModel().removeProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_LABEL, idx));
//						getWidgetModel().removeProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_COLOR, idx));
//						getWidgetModel().removeProperty(String.format(AbstractMultiWidgetModel.PROP_STATE_VALUE, idx));
//					}
				//}
				return true;
			}
			
		};
		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_NSTATES, handler);
		
		
	
		
		
		//on state
//		handler = new IWidgetPropertyChangeHandler() {
//
//			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
//				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
//				updateFromValue(getPVValue(AbstractPVWidgetModel.PROP_PVNAME), figure);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_ON_STATE, handler);


		// show label
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
				figure.setShowLabel((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_SHOW_LABEL, handler);

		//  label position
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
				figure.setLabelPosition(LabelPosition.values()[(Integer)newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_LABEL_POS, handler);
	
		// on label
//		handler = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue,
//					final Object newValue,
//					final IFigure refreshableFigure) {
//				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
//				figure.setOnLabel((String) newValue);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_ON_LABEL, handler);

		// off label
//		handler = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue,
//					final Object newValue,
//					final IFigure refreshableFigure) {
//				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
//				figure.setOffLabel((String) newValue);
//				return true;
//			}
//		};
//		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_OFF_LABEL, handler);

		// on color
//		handler = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue,
//					final Object newValue,
//					final IFigure refreshableFigure) {
//				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
//				figure.setOnColor(((OPIColor) newValue).getSWTColor());
//				return true;
//			}
//		};
//		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_ON_COLOR, handler);

		// off color
//		handler = new IWidgetPropertyChangeHandler() {
//			public boolean handleChange(final Object oldValue,
//					final Object newValue,
//					final IFigure refreshableFigure) {
//				AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
//				figure.setOffColor(((OPIColor) newValue).getSWTColor());
//				return true;
//			}
//		};
//		setPropertyChangeHandler(AbstractMultiWidgetModel.PROP_OFF_COLOR, handler);
	}


	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object value) {
		((AbstractMultiFigure)getFigure()).setValue(value);
		
		//if(value instanceof Number)
			//((AbstractMultiFigure)getFigure()).setValue(((Number)value).doubleValue());
		//else if (value instanceof Boolean)
			//((AbstractMultiFigure)getFigure()).setBooleanValue((Boolean)value);
		//else 
		//	super.setValue(value);
	}

	@Override
	public Boolean getValue() {
		return true; //((AbstractMultiFigure)getFigure()).getValue();
	}
	
	/**
	 * @param newValue
	 * @param figure
	 */
	private void updateFromValue(final VType newValue, AbstractMultiFigure figure) {
		if(newValue == null) {
			return;
		}
		figure.setValue(VTypeHelper.getDouble(newValue));
	}

	private void updatePropSheet(final LabelType dataType) {
//		getWidgetModel().setPropertyVisible(
//				AbstractMultiWidgetModel.PROP_BIT, dataType == 0);
//		getWidgetModel().setPropertyVisible(
//				AbstractMultiWidgetModel.PROP_ON_STATE, dataType == 1);
//		getWidgetModel().setPropertyVisible(
//				AbstractMultiWidgetModel.PROP_OFF_STATE, dataType == 1);
	}
	
	
	protected class StateLabelChangeHandler implements IWidgetPropertyChangeHandler {

		private int state;
		
		public StateLabelChangeHandler(int state) {
			this.state = state;
		}
		
		@Override
		public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
			AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
			figure.setStateLabel(state, (String)newValue);
			return true;
		}
	}
	
	
	protected class StateColorChangeHandler implements IWidgetPropertyChangeHandler {

		private int state;
		
		public StateColorChangeHandler(int state) {
			this.state = state;
		}
		
		@Override
		public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
			AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
			figure.setStateColor(state, ((OPIColor) newValue).getSWTColor());
			return true;
		}
	}
	
	protected class StateValueChangeHandler implements IWidgetPropertyChangeHandler {

		private int state;
		
		public StateValueChangeHandler(int state) {
			this.state = state;
		}
		
		@Override
		public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
			AbstractMultiFigure figure = (AbstractMultiFigure) refreshableFigure;
			figure.setStateValue(state, (int)newValue);
			return true;
		}
	}
}
