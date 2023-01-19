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
package fr.ensma.lias.mariusqlplus.common;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import fr.ensma.lias.mariusqlplus.common.util.WindowType;

/**
 * @author Stéphane JEAN
 */
public abstract class CustomInternalFrame extends JInternalFrame {

    private static final long serialVersionUID = -2339058871808570740L;

    public static int openFrameCount = 0;

    public static int xOffset = 30, yOffset = 30;

    public CustomInternalFrame(String title, boolean resizable,
	    boolean closable, boolean maximizable, boolean iconifiable) {
	super(title, resizable, closable, maximizable, iconifiable);
	++openFrameCount;
	setLocation(xOffset * openFrameCount, yOffset * openFrameCount);

	addInternalFrameListener(new InternalFrameAdapter() {
	    public void internalFrameClosed(InternalFrameEvent e) {
		--openFrameCount;
	    }
	});
    }

    public int getOpenFrameCount() {
	return openFrameCount;
    }

    public abstract WindowType getWindowType(); // prefered over instanceof

    public abstract void initialize(); // for operations that must be performed
				       // after internal frame is realized
}
