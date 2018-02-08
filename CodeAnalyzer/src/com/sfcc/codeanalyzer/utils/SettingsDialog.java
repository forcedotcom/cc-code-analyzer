package com.sfcc.codeanalyzer.utils;
/**
 * Copyright (c) 2018, Salesforce.com, Inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license. 
 * For full license text, see LICENSE.txt file in the repo root
*/

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The SettingsDialog class controls the plug-in Dialog box interaction
 * @author SFCC support
 */
public class SettingsDialog extends Dialog {

    private String message = "";
    private String cartridgePath;
    private boolean checkCacheList;
    private Text cartridgePathText;
    private Button checkCacheListButton;

    /**
     * Constructor SettingsDialog, set the shell style and block it on open (rest of UI is blocked until closed)
     * @param parentShell Shell type to set in the super parent
     */
    public SettingsDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.OK | SWT.APPLICATION_MODAL);
        setBlockOnOpen(true);
    }

    /** 
     * Method to create the content of the dialog
     * @param parent Composite object
     * @return composite Control object is the abstract superclass of all windowed user interface classes.
     */
    protected Control createDialogArea(Composite parent) {
    	
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 15;
        layout.marginWidth = 100;
        composite.setLayout(layout);
        final Label content = new Label(composite, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        content.setText(message);
        Label lblPath = new Label(composite, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblNewLabel.horizontalIndent = 1;
        lblPath.setLayoutData(gd_lblNewLabel);
        lblPath.setText("Cartridge Path:");
        cartridgePathText = new Text(composite, SWT.BORDER);
        cartridgePathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        cartridgePathText.setText("");
        cartridgePathText.setSize(180, 15);
        checkCacheListButton = new Button(composite, SWT.CHECK);
        checkCacheListButton.setText("Show all iscache statements");
        checkCacheListButton.setSelection(false);
        return composite;
    }

    /**
     * Create the OK button for the given dialog box
     * @param parent of type Composite, the dialog box to happen the button
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "OK", true);
    }
    
    /**
     * Configure the dialog's shell, mostly the title
     * @param newShell Shell object to be passed to the Shell parent
     */    
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Code Analyzer Settings");
    }

    /**
     * OK button action trigger
     */
    public void okPressed() {
        cartridgePath = cartridgePathText.getText();
        checkCacheList = checkCacheListButton.getSelection();
        close();
    }

    /**
     * Setter of the cartridge path
     * @param path string representing the relative path of the cartridge
     */
    public void setCartridgePath(String path) {
        cartridgePathText.setText(path);
    }

    /**
     * Getter of the cartridge path
     * @return path string representing relative path of the cartridge
     */
    public String getCartridgePath() {
        return cartridgePath;
    }
    
    /**
     * Setter of the cache list
     * @param selection boolean
     */    
    public void setCheckCacheList(boolean selection) {
    		checkCacheListButton.setSelection(selection);
    	}
    
    /**
     * Getter of the cache list
     * @return selection boolean
     */  
    public boolean getCheckCacheList() {
    		return checkCacheList;
    }
}
