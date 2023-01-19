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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.JFileChooser;

import fr.ensma.lias.mariusql.exception.MariusQLException;
import fr.ensma.lias.mariusql.jdbc.MariusQLResultSet;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusql.jdbc.MariusQLStatement;
import fr.ensma.lias.mariusqlplus.common.AbstractExecuteAction;
import fr.ensma.lias.mariusqlplus.common.util.GuiUtils;
import fr.ensma.lias.mariusqlplus.workbench.MariusQLWorkBench;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class ExecuteAction extends AbstractExecuteAction {

    private static final long serialVersionUID = -8269057687157839591L;

    private Main main;

    private GuiUtils gUtils;

    private MariusQLWorkBench mwb;

    private String editorText = "";

    private final static int DML = 0;

    private final static int DDL = 1;

    private String mariusQLStatement;

    public ExecuteAction(String name, Main parent) {
	super(name);
	this.main = parent;
	this.gUtils = new GuiUtils(parent);
    }

    public void actionPerformed(ActionEvent e) {
	// reinit the editor
	editorText = "";

	// must not be hard coded but for the moment Laura needs it
	main.configUI.getSession().setDefaultNameSpace("INTERNATIONAL_ID");

	// choose the file to be executed
	File scriptFile = chooseScriptFile();

	try {
	    mwb = main.openMariusqlWorkBench("", scriptFile);
	    parseScript(scriptFile);
	} catch (Exception ex) {
	    gUtils.displayError("Script execution error : " + ex.getMessage());
	}
    }

    /**
     * Choose a .mariusql file with the SQL script
     **/
    private File chooseScriptFile() {

	File result = null;

	int isOpenDialog = fc.showOpenDialog(main);
	if (isOpenDialog == JFileChooser.APPROVE_OPTION) {
	    result = fc.getSelectedFile();
	}

	return result;
    }

    private void parseScript(File scriptFile) throws IOException {
	String scriptPath = scriptFile.getPath();
	FileReader freader = new FileReader(scriptPath);
	BufferedReader bufferedReader = new BufferedReader(freader);
	StringBuffer buffer = new StringBuffer();
	StreamTokenizer st = null;

	st = new StreamTokenizer(bufferedReader);
	st.eolIsSignificant(true); // EOL is considered in the reading
	st.slashStarComments(true); // C-like comments (/* */) are ignored
	st.slashSlashComments(true); // C-like comments (// ) are ignored
	st.ordinaryChar(' '); // white space will be considered a character to
	// be read
	st.ordinaryChar('/');
	st.ordinaryChar('\''); // the quotes will be considered a character to
	// be read (otherwise the token just after is
	// ignored)
	st.ordinaryChars(45, 57); // the characters in the range 45-57 have to
	// be read. They are the: "-", ".", "/", and
	// numbers from 0 to 9.
	st.wordChars(45, 57);

	do {
	    st.nextToken();

	    switch (st.ttype) {
	    case StreamTokenizer.TT_NUMBER:
		buffer.append(st.nval); // append a number to the SQL buffer
		break;

	    case StreamTokenizer.TT_WORD:
		buffer.append(st.sval); // append a word to the SQL buffer
		break;
	    case ';': // When the ";" character is found and when in the End of
		      // File,
	    case StreamTokenizer.TT_EOF: // it marks the execution of the MariusQL
		// statement that is in the buffer.
		String bufferString = buffer.toString();
		bufferString = bufferString.trim();
		if (bufferString.length() != 0) {
		    if (bufferString.startsWith("\n")) {
			bufferString = bufferString.replaceFirst("\n", "");
		    }
		    if (bufferString.startsWith("@")) {
			parseScript(new File(scriptFile.getParent() + "\\"
				+ bufferString.substring(1) + ".mariusql"));
		    } else {
			mariusQLStatement = bufferString;
			editorText += "\nMariusQL> " + mariusQLStatement + "\n\n";
			String result = performMariusQLStatement(mariusQLStatement);
			editorText += result;
		    }
		    // reinitialize buffer for next SQL statement
		    buffer.delete(0, buffer.length());
		    buffer.setLength(0);
		}
		break;

	    default: // a single character found in ttype: "#", '>', EOL,
		     // whitespace, etc.
		buffer.append((char) st.ttype); // append a character to the SQL
		// buffer
		break;
	    }
	} while (st.ttype != StreamTokenizer.TT_EOF);

	mwb.setEditorText("------------------------\n BEGINING OF THE SCRIPT\n------------------------\n "
		+ editorText
		+ "\n-------------------\n END OF THE SCRIPT\n-------------------\n ");

	bufferedReader.close();

    }

    /**
     * Discover whether the type of the SQL statement is a Query or manipulation
     * verifies if it starts with SELECT or with some other keyword.
     */
    private String performMariusQLStatement(String mariusqlStatement)
	    throws IOException {
	String result = "";
	Reader bufferedReader = new StringReader(mariusqlStatement);
	StreamTokenizer st = null;

	st = new StreamTokenizer(bufferedReader);
	st.eolIsSignificant(false); // EOL is not considered in the reading
	st.slashStarComments(true); // C-like comments (/* */) are ignored
	st.slashSlashComments(true); // C-like comments (// ) are ignored

	st.nextToken();
	if (st.ttype == StreamTokenizer.TT_WORD) {
	    if (st.sval.compareToIgnoreCase("SELECT") == 0) { // if the first
		// token is equal to
		// the word "SELECT"
		result = performMariusQLQuery(mariusqlStatement);
	    } else if ((st.sval.compareToIgnoreCase("INSERT") == 0)
		    || (st.sval.compareToIgnoreCase("UPDATE") == 0)
		    || (st.sval.compareToIgnoreCase("DELETE") == 0)) {
		// if it is not SELECT, it is not a query, so it needs to be
		// executed
		result = performMariusQLUpdate(mariusqlStatement, DML);
		// if the first token is not a word, then it is not to be
		// executed
		// ...
	    } else {
		result = performMariusQLUpdate(mariusqlStatement, DDL);
	    }
	}
	return result;
    }

    /**
     * perform the MariusQL query and return result.
     */
    private String performMariusQLQuery(String mariusqlQuery) {
	String result;

	try {
	    MariusQLSession session = main.configUI.getSession();
	    MariusQLStatement stmt = session.createMariusQLStatement();
	    MariusQLResultSet rset = (MariusQLResultSet) stmt
		    .executeQuery(mariusqlQuery);
	    result = resultSetToString(rset);
	} catch (MariusQLException e) {
	    result = "Error :" + e.getMessage();
	}

	return result;
    }

    /**
     * perform the MariusQL update and return the number of rows affected.
     */
    private String performMariusQLUpdate(String mariusqlQuery, int typeOfUpdate) {
	String result;

	try {
	    MariusQLSession session = main.configUI.getSession();
	    MariusQLStatement stmt = session.createMariusQLStatement();
	    int nbRows = stmt.executeUpdate(mariusqlQuery);
	    // if (MariusQLHelper.isNamespaceParameterCommand(ontoqlQuery)) {
	    // main.updateAppMenuBar();
	    // }
	    // if (MariusQLHelper.isLanguageParameterCommand(ontoqlQuery)) {
	    // main.updateLanguageMenu();
	    // }
	    if (typeOfUpdate == DML)
		result = nbRows + " rows affected\n";
	    else {
		if (nbRows == 0) {
		    result = "statement successfully executed\n";
		} else {
		    result = "an error has occured\n";
		}
	    }
	} catch (MariusQLException e) {
	    result = "Error: " + e.getMessage() + "\n";
	}

	return result;
    }

    public static String resultSetToString(ResultSet rs) {
	try {
	    int i;
	    String res = "";
	    // Get the ResultSetMetaData. This will be used for the column
	    // headings
	    ResultSetMetaData rsmd = rs.getMetaData();

	    // Get the number of columns in the result set
	    int numCols = rsmd.getColumnCount();

	    // Display column headings
	    for (i = 1; i <= numCols; i++) {
		if (i > 1)
		    res += (",");
		res += (rsmd.getColumnName(i));
	    }
	    res += ("\n-------------------------------------\n");

	    // Display data, fetching until end of the result set
	    while (rs.next()) {
		// Loop through each column, getting the
		// column data and displaying
		for (i = 1; i <= numCols; i++) {
		    if (i > 1)
			res += (",");
		    res += (rs.getString(i));
		}
		res += ("\n");
		// Fetch the next result set row
	    }
	    return res;
	} catch (Exception e) {

	}
	return null;
    }
}
