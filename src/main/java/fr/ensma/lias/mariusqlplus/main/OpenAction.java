/*********************************************************************************
 * This file is part of MariusQLPlus Project.
 * Copyright (C) 2014  LIAS - ENSMA
 *   Teleport 2 - 1 avenue Clement Ader
 *   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
 * 
 * MariusQLPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MariusQLPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with MariusQLPlus.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************************/
package fr.ensma.lias.mariusqlplus.main;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import fr.ensma.lias.mariusqlplus.common.AbstractOpenAction;
import fr.ensma.lias.mariusqlplus.common.util.GuiUtils;
import fr.ensma.lias.mariusqlplus.workbench.MariusQLWorkBench;
import fr.ensma.lias.mariusqlplus.workbench.WBOpenAction;

/**
 * @author Stéphane JEAN
 */
public class OpenAction extends AbstractOpenAction {

    private static final long serialVersionUID = 2798021910047191389L;
    
    private Main main;
    
    private GuiUtils gUtils;

    public OpenAction(String name, Main parent) {
	super(name);
	this.main = parent;
	this.gUtils = new GuiUtils(parent);
    }

    public void actionPerformed(ActionEvent e) {
	if (main.isWorkBenchInFocus()) {
	    MariusQLWorkBench aBench = main.getWorkBenchInFocus();
	    WBOpenAction openAction = aBench.getOpenAction();
	    openAction.actionPerformed(e);
	} else {
	    int result = fc.showOpenDialog(main);
	    if (result == JFileChooser.APPROVE_OPTION) {
		try {
		    File file = fc.getSelectedFile();
		    String fileText = readFile(file);
		    main.openMariusqlWorkBench(fileText, file);
		} catch (IOException ioe) {
		    gUtils.displayError("Could not read file");
		}
	    }
	}
    }
}
