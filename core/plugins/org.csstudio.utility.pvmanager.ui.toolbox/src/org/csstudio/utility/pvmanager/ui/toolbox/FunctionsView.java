/**
 * 
 */
package org.csstudio.utility.pvmanager.ui.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.epics.pvmanager.formula.FormulaFunction;
import org.epics.pvmanager.formula.FormulaFunctionSet;
import org.epics.pvmanager.formula.FormulaFunctions;
import org.epics.pvmanager.formula.FormulaRegistry;
import org.epics.pvmanager.service.Service;
import org.epics.pvmanager.service.ServiceMethod;
import org.epics.pvmanager.service.ServiceRegistry;

import com.google.common.base.Joiner;

/**
 * @author shroffk
 * 
 */
public class FunctionsView extends ViewPart {

	public static final String ID = "org.csstudio.utility.pvmanager.ui.toolbox.FunctionsView"; //$NON-NLS-1$
	private TreeViewer treeViewer;

	/**
     * 
     */
	public FunctionsView() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		TreeColumnLayout tcl_composite = new TreeColumnLayout();
		composite.setLayout(tcl_composite);

		treeViewer = new TreeViewer(composite, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		treeViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				if (element instanceof FormulaFunctionSet) {
					return ((FormulaFunctionSet) element).getName();
				} else if (element instanceof FormulaFunction) {
					return FormulaFunctions.formatSignature((FormulaFunction) element);
				}
				return "";
			}
		});
		TreeColumn trclmnNewColumn = treeViewerColumn.getColumn();
		tcl_composite.setColumnData(trclmnNewColumn, new ColumnWeightData(10,
				ColumnWeightData.MINIMUM_WIDTH, true));
		trclmnNewColumn.setText("Name");

		TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		treeViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				if (element instanceof Service) {
					return ((FormulaFunctionSet) element).getDescription();
				} else if (element instanceof FormulaFunction) {
					return ((FormulaFunction) element).getDescription();
				}
				return "";
			}
		});
		TreeColumn trclmnNewColumn_1 = treeViewerColumn_1.getColumn();
		tcl_composite.setColumnData(trclmnNewColumn_1, new ColumnWeightData(7,
				ColumnWeightData.MINIMUM_WIDTH, true));
		trclmnNewColumn_1.setText("Description");
		treeViewer.setContentProvider(new FunctionTreeContentProvider());

		List<String> functionSetNames = new ArrayList<String>(FormulaRegistry.getDefault()
				.listFunctionSets());
		Collections.sort(functionSetNames);
		List<FormulaFunctionSet> functionSets = new ArrayList<FormulaFunctionSet>();
		for (String functionSetName : functionSetNames) {
			functionSets.add(FormulaRegistry.getDefault().findFunctionSet(functionSetName));
		}
		treeViewer.setInput(functionSets);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		treeViewer.getControl().setFocus();
	}
}
