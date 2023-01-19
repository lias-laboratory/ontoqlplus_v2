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
package fr.ensma.lias.mariusqlplus.datatable;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import fr.ensma.lias.mariusql.exception.MariusQLException;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusqlplus.common.util.GridBagUtil;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class ResultGridPanel extends JPanel {

    private static final long serialVersionUID = -642303465139415804L;

    private DataBoundTableModel resultsTableModel;

    private final JTable resultsTable = new JTable();

    public String sqlString;

    public ResultGridPanel(MariusQLSession s) {
	setLayout(new GridBagLayout());
	resultsTable.setAutoResizeMode(0);
	resultsTable.setModel(resultsTableModel = new DataBoundTableModel(s));
	resultsTable.setColumnModel(new ResultsColumnModel());
	resultsTable.setToolTipText(null);
	JScrollPane resultsScroller = new JScrollPane(resultsTable);
	resultsScroller
		.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	GridBagUtil.add(this, resultsScroller, 0, 0, 0, 0, 1.0d, 1.0d,
		GridBagConstraints.BOTH, GridBagConstraints.WEST, new Insets(0,
			0, 0, 0));
    }

    public float executeQuery(String q) throws MariusQLException {
	float res = (float) resultsTableModel.executeQuery(q);
	sqlString = resultsTableModel.sqlString;
	return res;
    }

    public float executeSPARQLQuery(String q){
	float res = (float) resultsTableModel.executeSPARQLQuery(q);
	sqlString = resultsTableModel.sqlString;
	return res;
    }

    public int executeUpdate(String u) throws SQLException {
	return resultsTableModel.executeUpdate(u);
    }

    public void bind() throws SQLException {
	resultsTableModel.bind();
    }

    public void close() throws SQLException {
	resultsTableModel.close();
    }

    public int recordsReturned() {
	return resultsTableModel.getRowCount();
    }
}
