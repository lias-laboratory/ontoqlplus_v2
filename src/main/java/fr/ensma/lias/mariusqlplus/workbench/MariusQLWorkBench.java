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
package fr.ensma.lias.mariusqlplus.workbench;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import fr.ensma.lias.mariusql.exception.MariusQLException;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusqlplus.common.CustomInternalFrame;
import fr.ensma.lias.mariusqlplus.common.Environment;
import fr.ensma.lias.mariusqlplus.common.StatusBar;
import fr.ensma.lias.mariusqlplus.common.util.GuiUtils;
import fr.ensma.lias.mariusqlplus.common.util.SwingWorker;
import fr.ensma.lias.mariusqlplus.common.util.WindowType;
import fr.ensma.lias.mariusqlplus.datatable.ResultGridPanel;
import fr.ensma.lias.mariusqlplus.main.Main;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class MariusQLWorkBench extends CustomInternalFrame implements
	DocumentListener {

    private static final long serialVersionUID = 1228173321648312640L;

    private final static String DEFAULT_TITLE = "MariusQL WorkBench";

    private boolean modifiedShown = false;

    // private Main main;

    private final RunAction runAction = new RunAction(null);
    private final SPARQLAction sparqlAction = new SPARQLAction(null);
    private final StatusBar statusBar = new StatusBar();

    private final CommandEditorPanel cep = new CommandEditorPanel();
    private final GuiUtils gUtils = new GuiUtils(this);
    private final ResultGridPanel rgp;
    private final JSplitPane splitPane;

    private WBGeneratedSQLAction generatedSQLAction;
    private WBOpenAction openAction;
    private WBSaveAction saveAction;
    private WBSaveAsAction saveAsAction;

    public MariusQLWorkBench(Main main, MariusQLSession s) {
	super(DEFAULT_TITLE, true, true, true, true);

	// this.main = main;
	this.setFrameIcon(Environment.WORK_ICON);
	generatedSQLAction = new WBGeneratedSQLAction(null, this);
	saveAction = new WBSaveAction(null, this);
	saveAsAction = new WBSaveAsAction(null, this, saveAction);
	openAction = new WBOpenAction(null, this, saveAction);

	cep.addEditorDocumentListener(this);
	rgp = new ResultGridPanel(s);
	splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cep, rgp);
	JPanel contentPanel = new JPanel();
	contentPanel.setLayout(new BorderLayout());
	contentPanel.add(createEditorToolBar(), BorderLayout.NORTH);
	splitPane.setOneTouchExpandable(true);
	contentPanel.add(splitPane, BorderLayout.CENTER);
	contentPanel.add(statusBar, BorderLayout.SOUTH);
	setContentPane(contentPanel);
	pack();
    }

    public MariusQLWorkBench(Main main, MariusQLSession s, String text,
	    File file) {
	this(main, s);
	cep.setEditorText(text);
	saveAction.setFile(file);
    }

    public WindowType getWindowType() {
	return WindowType.MARIUSQL_WORKBENCH;
    }

    public void initialize() {
	splitPane.setDividerLocation(0.525d);
    }

    public void setEditorText(String text) {
	cep.setEditorText(text);
    }

    public String getEditorText() {
	return cep.getEditorText();
    }

    public WBSaveAction getSaveAction() {
	return saveAction;
    }

    public WBSaveAsAction getSaveAsAction() {
	return saveAsAction;
    }

    public WBOpenAction getOpenAction() {
	return openAction;
    }

    public void setTitleFileString(String titleFileString) {
	setTitle(DEFAULT_TITLE + " - " + titleFileString);
    }

    public void setModifiedShown(boolean b) {
	modifiedShown = b;
    }

    public void handleError(String message) {
	final String m = message;
	if (!SwingUtilities.isEventDispatchThread()) {
	    Runnable doError = new Runnable() {
		public void run() {
		    statusBar.setText(m);
		    gUtils.displayError(m);
		    return;
		}
	    };
	    SwingUtilities.invokeLater(doError);
	} else {
	    statusBar.setText(m);
	    gUtils.displayError(m);
	}
    }

    // ----------------------------------------------------
    // DocumentListener Impl
    // ----------------------------------------------------

    public void insertUpdate(DocumentEvent e) {
	if (!modifiedShown) {
	    setTitle(getTitle() + " [Modified]");
	    setModifiedShown(true);
	}
    }

    public void removeUpdate(DocumentEvent e) {
	if (!modifiedShown) {
	    setTitle(getTitle() + " [Modified]");
	    setModifiedShown(true);
	}
    }

    public void changedUpdate(DocumentEvent e) {
    }

    private JToolBar createEditorToolBar() {
	Insets buttonInsets = new Insets(0, 0, 0, 0);
	JToolBar editorToolBar = new JToolBar();
	editorToolBar.setFloatable(false);

	JButton runButton = new JButton(runAction);
	runButton.setMargin(buttonInsets);
	runButton.setToolTipText("Run MariusQL statement in window");
	editorToolBar.add(runButton);

	JButton sparqlButton = new JButton(sparqlAction);
	sparqlButton.setMargin(buttonInsets);
	sparqlButton.setToolTipText("Run SPARQL statement in window");
	editorToolBar.add(sparqlButton);

	JButton generatedSQLButton = new JButton(generatedSQLAction);
	generatedSQLButton.setText(null);
	generatedSQLButton.setMargin(buttonInsets);
	generatedSQLButton.setToolTipText("Get the SQL generated");
	editorToolBar.add(generatedSQLButton);

	editorToolBar.addSeparator();

	JButton openButton = new JButton(openAction);
	openButton.setText(null);
	openButton.setMargin(buttonInsets);
	openButton.setToolTipText("Open MariusQL file");
	editorToolBar.add(openButton);

	JButton saveButton = new JButton(saveAction);
	saveButton.setMargin(buttonInsets);
	saveButton.setToolTipText("Save contents of window");
	editorToolBar.add(saveButton);

	JButton saveAsButton = new JButton(saveAsAction);
	saveAsButton.setMargin(buttonInsets);
	saveAsButton.setToolTipText("Save contents of window as...");
	editorToolBar.add(saveAsButton);

	return editorToolBar;
    }

    // RUN ACTION
    protected class RunAction extends AbstractAction {

	private static final long serialVersionUID = -2817010968242963652L;

	public RunAction(String name) {
	    super(name, Environment.RUN_ICON);
	}

	public void actionPerformed(ActionEvent ae) {
	    if (isQuery())
		executeQuery();
	    else
		executeUpdate();
	}

	private void executeQuery() {
	    
	    SwingWorker worker = new SwingWorker() {
		public Object construct() {
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		    try {
			final float executeTime = rgp.executeQuery(cep
				.getCommand().replaceAll("[\r\n]+", ""));
			cep.setEditorText(cep.getEditorText());
			Runnable doUpdateUI = new Runnable() {
			    public void run() {
				try {
				    rgp.bind();
				    statusBar.setText(rgp.recordsReturned()
					    + " rows returned in "
					    + Float.toString(executeTime)
					    + " milliseconds");
				    cep.addHistoryItem(cep.getCommand());
				} catch (SQLException sqle) {
				    handleError(sqle.getMessage());
				}
			    }
			};
			SwingUtilities.invokeLater(doUpdateUI);
		    } catch (MariusQLException sqle) {
			sqle.printStackTrace();
			handleError(sqle.getMessage());
		    } catch (Exception e) {
			e.printStackTrace();
			handleError(e.getMessage());
		    }
		    return null;
		}

		public void finished() {
		    handleFinished();
		}
	    };
	    worker.start();
	}

	private void executeUpdate() {
	    SwingWorker worker = new SwingWorker() {

		public Object construct() {
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		    try {
			String text = cep.getCommand()
				.replaceAll("[\r\n]+", "");
			String[] requestList = text.split(";");
			int rowsAffected = 0;
			for (final String request : requestList) {
			    rowsAffected += rgp.executeUpdate(request);
			    final int rows = rowsAffected;
			    // if
			    // (MariusQLHelper.isNamespaceParameterCommand(cep
			    // .getCommand())) {
			    // main.updateAppMenuBar();
			    // }
			    // if (MariusQLHelper.isLanguageParameterCommand(cep
			    // .getCommand())) {
			    // main.updateLanguageMenu();
			    // }
			    Runnable doUpdateUI = new Runnable() {
				public void run() {
				    
				    statusBar.setText(rows
					    + " rows updated");
				    cep.addHistoryItem(request);
				    return;
				}
			    };
			    SwingUtilities.invokeLater(doUpdateUI);
			}
			gUtils.displayInfo(rowsAffected
				    + " rows updated");
		    } catch (SQLException sqle) {
			handleError(sqle.getMessage());
		    } catch (MariusQLException sqle) {
			handleError(sqle.getMessage());
		    } catch (Exception e) {
			handleError(e.getMessage());
		    }
		    return null;
		}

		public void finished() {
		    handleFinished();
		}
	    };
	    worker.start();
	}

	private boolean isQuery() {
	    String trim = cep.getCommand().trim();
	    String firstToken = trim.split("\\s+")[0];
	    String other = firstToken.toLowerCase();
	    return "select".equals(other);
	}

	private void handleFinished() {
	    try {
		rgp.close();
	    } catch (SQLException sqle) {
		handleError(sqle.getMessage());
	    } catch (MariusQLException sqle) {
		handleError(sqle.getMessage());
	    } finally {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
    }

    // SPARQL ACTION
    protected class SPARQLAction extends AbstractAction {
	private static final long serialVersionUID = 4365579972303893099L;

	public SPARQLAction(String name) {
	    super(name, Environment.SPARQL_ICON);
	}

	public void actionPerformed(ActionEvent ae) {
	    executeQuery();
	}

	private void executeQuery() {
	    SwingWorker worker = new SwingWorker() {
		public Object construct() {
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		    try {
			 final float executeTime = rgp.executeSPARQLQuery(cep.getCommand());
			cep.setEditorText(cep.getEditorText());
			Runnable doUpdateUI = new Runnable() {
			    public void run() {
				try {
				    rgp.bind();
				    statusBar.setText(rgp.recordsReturned()
					    + " rows returned in ???"
					     + Float.toString(executeTime)
					    + " milliseconds");
				    cep.addHistoryItem(cep.getCommand());
				} catch (SQLException sqle) {
				    handleError(sqle.getMessage());
				}
			    }
			};
			SwingUtilities.invokeLater(doUpdateUI);
		    } catch (MariusQLException sqle) {
			handleError(sqle.getMessage());
		    }
		    return null;
		}

		public void finished() {
		    handleFinished();
		}
	    };
	    worker.start();
	}

	private void handleFinished() {
	    try {
		rgp.close();
	    } catch (SQLException sqle) {
		handleError(sqle.getMessage());
	    } finally {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    }
	}
    }

    public ResultGridPanel getRgp() {
	return rgp;
    }

    public CommandEditorPanel getCep() {
	return cep;
    }
}
