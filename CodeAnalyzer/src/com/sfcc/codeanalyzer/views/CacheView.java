package com.sfcc.codeanalyzer.views;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

/**
 * The class to display the caching data and the iscache hierarchy
 * @author SFCC support
 */
public class CacheView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.sfcc.codeanalyzer.views.CacheView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	/**
	 * The ViewContentProvider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 * @implements IStructuredContentProvider
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		
		public void inputChanged(Viewer v,  Object oldInput, Object newInput) {}
		public void dispose() {}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	
	/**
	 *The ViewLabelProvider class is responsible for label text, label image for the given column of specified object and image used to label the object.  
	 *@parent LabelProvider
	 *@implements ITableLableProvider
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
	
	class NameSorter extends ViewerSorter {}

	/**
	 * The constructor of CacheView class
	 */
	public CacheView() {}

	/**
	 * Method createPartControl is a callback that will allow
	 * to create the viewer and initialize it.
	 * @param parent UI Composite object 
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		final Table table = viewer.getTable();
	    table.setHeaderVisible(true);
		String[] titles = { "Template", "Included by", "Type", "Status", "Hour", "Minutes", "Vary By" };
	    int[] bounds = { 100, 100, 100, 100, 100, 100, 100 };
	    TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
	    col = createTableViewerColumn(titles[1], bounds[1], 1);
	    col = createTableViewerColumn(titles[2], bounds[2], 2);
	    col = createTableViewerColumn(titles[3], bounds[3], 3);
	    col = createTableViewerColumn(titles[4], bounds[4], 4);
	    col = createTableViewerColumn(titles[5], bounds[5], 5);
	    col = createTableViewerColumn(titles[6], bounds[6], 6);
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.sfcc.codeanalyzer.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		//contributeToActionBars();
	}
	
	/**
	 * Method createTableViewerColumn gets the column of table viewer, sets text, sets width, enables resizing, enables reordering by dragging,  for the table column
	 * @param title
	 * @param bound
	 * @param colNumber
	 * @return TableViewerColumn object
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}
	
	/**
	 * Method hookContextMenu creates menu manager, 
	 * sets options on show, adds menu listener, 
	 * creates an SWT context menu control for the given menu, 
	 * gets the primary control to the viewer and sets menu, gets sites with context menu to the given viewer 
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CacheView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}
	
	/**
	 * Method contributeToActionBars gets the site for the view with associated action bars,
	 * runs two custom methods fillLocalPullDown and fillLocalToolBar
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	/**
	 * Method fillLocalPullDown adds actions with contribution item
	 * @param manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	/**
	 * Method fillContextMenu adds actions to the given protocol for managing contribution to a menu bar and submenu
	 * @param manager IMenuManager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Method fillLocalToolBar adds actions to the given protocol for managing contribution to a tool bar
	 * @param manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}
	
	/**
	 * Method test to test the customization for the action.
	 */
	public void test(){
		/* whatever */
		action1.setText("I like it");
		action1.setToolTipText("TEST");
	}
	
	/**
	 * Creates new customized actions with text, tool tip text, Image description, doubleClickAction 
	 */
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}
	
	/**
	 * Method hookDoubleClickAction adds new double click listener
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	/**
	 * Method showMessage displays the given message
	 * @param message
	 */
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Code Analyzer View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
