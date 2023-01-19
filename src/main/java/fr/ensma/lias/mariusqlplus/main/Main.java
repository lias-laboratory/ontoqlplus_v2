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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fr.ensma.lias.mariusql.MariusQLConstants;
import fr.ensma.lias.mariusql.jdbc.MariusQLSession;
import fr.ensma.lias.mariusql.util.MariusQLHelper;
import fr.ensma.lias.mariusqlplus.common.ActionDialog;
import fr.ensma.lias.mariusqlplus.common.CustomInternalFrame;
import fr.ensma.lias.mariusqlplus.common.Environment;
import fr.ensma.lias.mariusqlplus.common.StatusBar;
import fr.ensma.lias.mariusqlplus.common.util.GuiUtils;
import fr.ensma.lias.mariusqlplus.common.util.SwingWorker;
import fr.ensma.lias.mariusqlplus.common.util.WindowType;
import fr.ensma.lias.mariusqlplus.config.ConnectionConfigDialog;
import fr.ensma.lias.mariusqlplus.workbench.MariusQLWorkBench;

/**
 * @author Stéphane JEAN
 * @author Valentin CASSAIR
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 1554119310077614846L;

    public final Image ICON = Toolkit.getDefaultToolkit().getImage(
	    getClass().getClassLoader().getResource("images/icon.png"));

    public final ConnectionConfigDialog configUI = new ConnectionConfigDialog(
	    this);

    private final StatusBar statusBar = new StatusBar();

    private final JDesktopPane desktop = new JDesktopPane();

    private final GuiUtils gUtils = new GuiUtils(this);

    private EnglishLanguageAction englishLanguageAction = null;

    private FrenchLanguageAction frenchLanguageAction = null;

    private NoLanguageAction noLanguageAction = null;

    private final OpenAction openAction = new OpenAction("Open File...", this);

    private final SaveAction saveAction = new SaveAction("Save File", this);

    private final SaveAsAction saveAsAction = new SaveAsAction("Save As...",
	    this);

    private final ExecuteAction executeAction = new ExecuteAction(
	    "Execute File", this);

    public Main() {
	super("OntoQL+ : Query editor");
	getContentPane().setLayout(new BorderLayout());
	setIconImage(ICON);
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    SwingUtilities.updateComponentTreeUI(this);
	    // force chaque composant de la fenêtre à appeler sa méthode
	    // updateUI
	} catch (InstantiationException e) {
	} catch (ClassNotFoundException e) {
	} catch (UnsupportedLookAndFeelException e) {
	} catch (IllegalAccessException e) {
	}
	//
	// menubar
	setJMenuBar(createAppMenuBar());
	//
	// desktop
	initializePositionOnScreen();
	desktop.setBackground(Color.LIGHT_GRAY);
	getContentPane().add(desktop, BorderLayout.CENTER);
	getContentPane().add(statusBar, BorderLayout.SOUTH);
	//
	// shutdownHook
	installShutdownHook();
	//
	// configuration directory
	if (!createConfigDir())
	    gUtils.displayWarning("Warning: Couldn't create configuration directory.\n"
		    + "You will not be able to save configuration data.");
	// configUI
	Runnable doOpen = new Runnable() {
	    public void run() {
		waitCursor(true);
		configUI.setVisible(true);
		waitCursor(false);
	    }
	};
	SwingUtilities.invokeLater(doOpen);
    }

    private JMenuBar createAppMenuBar() {
	JMenuBar menuBar = new JMenuBar();

	//
	// File Menu
	JMenu fileMenu = new JMenu("File");
	fileMenu.setMnemonic(KeyEvent.VK_F);
	fileMenu.addMouseListener(new MouseAdapter() {
	    public void mouseEntered(MouseEvent e) {
		if (!isWorkBenchInFocus()) {
		    saveAction.setEnabled(false);
		    saveAsAction.setEnabled(false);
		} else {
		    saveAction.setEnabled(true);
		    saveAsAction.setEnabled(true);
		}
	    }
	});

	// open
	JMenuItem open = new JMenuItem(openAction);
	open.setMnemonic(KeyEvent.VK_O);
	fileMenu.add(open);

	// save
	JMenuItem save = new JMenuItem(saveAction);
	save.setMnemonic(KeyEvent.VK_S);
	fileMenu.add(save);

	// save as
	JMenuItem saveAs = new JMenuItem(saveAsAction);
	saveAs.setMnemonic(KeyEvent.VK_A);
	fileMenu.add(saveAs);

	JMenuItem execute = new JMenuItem(executeAction);
	fileMenu.add(execute);

	fileMenu.addSeparator();

	// connections
	JMenuItem connConfig = new JMenuItem("Connections...");
	connConfig.setIcon(Environment.CONNEXION_ICON);
	connConfig.setMnemonic(KeyEvent.VK_C);
	connConfig.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		openConfigDialog();
	    }
	});
	fileMenu.add(connConfig);

	fileMenu.addSeparator();

	// exit
	JMenuItem exit = new JMenuItem("Exit");
	exit.setIcon(Environment.EXIT_ICON);
	exit.setMnemonic(KeyEvent.VK_X);
	exit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		shutDown();
	    }
	});
	fileMenu.add(exit);

	menuBar.add(fileMenu);

	//
	// Tools Menu
	JMenu toolsMenu = new JMenu("Tools");
	toolsMenu.setMnemonic(KeyEvent.VK_T);

	// sql workbench
	JMenuItem sqlWB = new JMenuItem("MariusQL WorkBench");
	sqlWB.setIcon(Environment.WORK_ICON);
	sqlWB.setMnemonic(KeyEvent.VK_S);
	sqlWB.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent ae) {
		openMariusqlWorkBench();
	    }
	});
	toolsMenu.add(sqlWB);

	menuBar.add(toolsMenu);

	// Options Menu

	JMenu optionMenu = new JMenu("Options");
	optionMenu.setMnemonic(KeyEvent.VK_V);

	// Language
	JMenu submenu = new JMenu("Language");
	ButtonGroup groupeLanguage = new ButtonGroup();
	JRadioButtonMenuItem cbMenuItem;
	noLanguageAction = new NoLanguageAction(configUI.getSession());
	cbMenuItem = new JRadioButtonMenuItem(noLanguageAction);
	groupeLanguage.add(cbMenuItem);
	submenu.add(cbMenuItem);
	englishLanguageAction = new EnglishLanguageAction(configUI.getSession());
	cbMenuItem = new JRadioButtonMenuItem(englishLanguageAction);
	cbMenuItem.setSelected(true);
	groupeLanguage.add(cbMenuItem);
	submenu.add(cbMenuItem);
	frenchLanguageAction = new FrenchLanguageAction(configUI.getSession());
	cbMenuItem = new JRadioButtonMenuItem(frenchLanguageAction);
	groupeLanguage.add(cbMenuItem);
	submenu.add(cbMenuItem);
	optionMenu.add(submenu);

	// aliased fonts
	JCheckBoxMenuItem showAliasedFonts = new JCheckBoxMenuItem(
		"Anti-Aliased Fonts");
	showAliasedFonts.setMnemonic(KeyEvent.VK_A);
	showAliasedFonts.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent ie) {
		if (ie.getStateChange() == ItemEvent.SELECTED)
		    turnOnAliasedFonts();
		else
		    turnOffAliasedFonts();
	    }
	});
	showAliasedFonts.setSelected(false);
	optionMenu.add(showAliasedFonts);

	menuBar.add(optionMenu);

	return menuBar;
    }

    public void waitCursor(boolean on) {
	if (on) {
	    getGlassPane().setVisible(true);
	} else {
	    getGlassPane().setVisible(false);
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }

    public void setStatusBarText(String status) {
	statusBar.setText(status);
    }

    public void clearStatusBarText() {
	statusBar.clear();
    }

    public void openConfigDialog() {
	int confirm = gUtils
		.requestConfirm("Opening the session dialog will close "
			+ "currently open session and windows.\nDo you want to do this?");
	if (confirm == JOptionPane.NO_OPTION)
	    return;
	configUI.closeSession();
	disposeAllInternalFrames();
	configUI.setVisible(true);
    }

    public MariusQLWorkBench openMariusqlWorkBench() {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	MariusQLWorkBench swb = new MariusQLWorkBench(this,
		configUI.getSession());
	frenchLanguageAction.setSession(configUI.getSession());
	noLanguageAction.setSession(configUI.getSession());
	englishLanguageAction.setSession(configUI.getSession());
	initializeInternalFrame(swb);

	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	return swb;
    }

    public MariusQLWorkBench openMariusqlWorkBench(String text, File file) {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	MariusQLWorkBench swb = new MariusQLWorkBench(this,
		configUI.getSession(), text, file);
	swb.setTitleFileString(file.getAbsolutePath());
	swb.setModifiedShown(false);
	initializeInternalFrame(swb);

	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	return swb;
    }

    public boolean isWorkBenchInFocus() {
	if (desktop.getAllFrames().length == 0)
	    return false;
	CustomInternalFrame oif = (CustomInternalFrame) desktop
		.getSelectedFrame();
	return oif.getWindowType() == WindowType.MARIUSQL_WORKBENCH;
    }

    public MariusQLWorkBench getWorkBenchInFocus() {
	CustomInternalFrame cif = (CustomInternalFrame) desktop
		.getSelectedFrame();
	if (cif.getWindowType() == WindowType.MARIUSQL_WORKBENCH)
	    return (MariusQLWorkBench) cif;
	else
	    return null;
    }

    public void disposeAllInternalFrames() {
	JInternalFrame[] allFrames = desktop.getAllFrames();
	for (int i = 0; i < allFrames.length; i++)
	    allFrames[i].dispose();
    }

    private void initializePositionOnScreen() {
	this.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    public void updateAppMenuBar() {
	JMenuBar menuBar = getJMenuBar();
	JMenu optionMenu = menuBar.getMenu(2);

	// If this menu has already been initialized
	// we need to remove it to update it
	boolean isInitialization = optionMenu.getItemCount() == 2;
	if (!isInitialization) {
	    optionMenu.remove(0);
	}

	// Namespace
	// JMenu submenuNamespace = new JMenu("Namespace");
	// ButtonGroup groupeNamespace = new ButtonGroup();
	// String queryNamespaces = "select #namespace from #ontology";
	// MariusQLSession session = configUI.getSession();
	// MariusQLStatement stmt = session.createMariusQLStatement();
	// String defaultNamespace = session.getDefaultNameSpace();
	// try {
	// MariusQLResultSet resultset = stmt.executeQuery(queryNamespaces);
	// int i = 0;
	// JRadioButtonMenuItem cbMenuItem;
	// NamespaceListener namespaceAction = new NamespaceListener(session,
	// null);
	// cbMenuItem = new JRadioButtonMenuItem("None");
	// cbMenuItem.addItemListener(namespaceAction);
	// if this is not the initialization
	// we put the default namespace as none if there is no default
	// namespace
	// if (!isInitialization
	// && defaultNamespace == MariusQLHelper.NO_NAMESPACE) {
	// cbMenuItem.setSelected(true);
	// }
	// groupeNamespace.add(cbMenuItem);
	// submenuNamespace.add(cbMenuItem);
	// while (resultset.next()) {
	// String currentNamespace = resultset.getString(1);
	// namespaceAction = new NamespaceListener(session,
	// currentNamespace);
	// cbMenuItem = new JRadioButtonMenuItem(currentNamespace);
	// cbMenuItem.addItemListener(namespaceAction);
	// groupeNamespace.add(cbMenuItem);
	// submenuNamespace.add(cbMenuItem);
	// //if this is the initialization we set the default namespace
	// //as the first namespace
	// if (i == 0 && defaultNamespace == MariusQLHelper.NO_NAMESPACE
	// && isInitialization) {
	// session.setDefaultNameSpace(currentNamespace);
	// cbMenuItem.setSelected(true);
	// }
	// if (defaultNamespace != null
	// && currentNamespace.equals(defaultNamespace)) {
	// cbMenuItem.setSelected(true);
	// }
	// i++;
	// }
	// } catch (SQLException oExc) {
	//
	// }
	// optionMenu.insert(submenuNamespace, 0);

    }

    public void updateLanguageMenu() {
	JMenuBar menuBar = getJMenuBar();
	JMenu optionMenu = menuBar.getMenu(2);
	JMenu menuLanugage = (JMenu) optionMenu.getItem(1);
	MariusQLSession session = configUI.getSession();
	String language = session.getReferenceLanguage();

	if (language == MariusQLConstants.NO_LANGUAGE) {
	    ((JRadioButtonMenuItem) menuLanugage.getItem(0)).setSelected(true);
	} else if (language.equals(MariusQLHelper.FRENCH)) {
	    ((JRadioButtonMenuItem) menuLanugage.getItem(2)).setSelected(true);
	} else {
	    ((JRadioButtonMenuItem) menuLanugage.getItem(1)).setSelected(true);
	}
    }

    private void initializeInternalFrame(CustomInternalFrame cif) {
	int w = (int) (this.getSize().getWidth() / 1.25);
	int h = (int) (this.getSize().getHeight() / 1.25);
	cif.setSize(new Dimension(w, h));
	desktop.add(cif);
	cif.setVisible(true);
	cif.initialize();
	try {
	    if (cif.getOpenFrameCount() > 1) {
		for (JInternalFrame j : desktop.getAllFrames()) {

		    j.setMaximum(false);

		}
	    } else {
		cif.setMaximum(true);
	    }
	    cif.setSelected(true);

	} catch (PropertyVetoException pve) {
	    pve.printStackTrace();
	}

    }

    private void turnOnAliasedFonts() {
	try {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    SwingUtilities.updateComponentTreeUI(this);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }

    private void turnOffAliasedFonts() {
	try {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    UIManager.setLookAndFeel(UIManager
		    .getCrossPlatformLookAndFeelClassName());
	    SwingUtilities.updateComponentTreeUI(this);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    }

    private boolean createConfigDir() {
	File configDir = new File(Environment.CONFIG_DIR_PATH);
	if (configDir.exists())
	    return true;
	return configDir.mkdir();
    }

    private void installShutdownHook() {
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
		shutDown();
	    }
	});
    }

    private void shutDown() {
	final ActionDialog d = new ActionDialog(this, Environment.PLUG_ICON,
		null);

	if (configUI.sessionEstablished()) {
	    SwingWorker worker = new SwingWorker() {
		public Object construct() {
		    Runnable doNotifyOfConnectionClose1 = new Runnable() {
			public void run() {
			    d.setActionText("Closing Connections...");
			    d.setLocationRelativeTo(Main.this);
			    d.setVisible(true);
			}
		    };
		    SwingUtilities.invokeLater(doNotifyOfConnectionClose1);

		    configUI.closeSession();
		    try {
			Thread.sleep(500);
		    } catch (InterruptedException e) {
		    }

		    Runnable doNotifyOfConnectionClose2 = new Runnable() {
			public void run() {
			    d.setActionText("Connections successfully closed");
			}
		    };
		    SwingUtilities.invokeLater(doNotifyOfConnectionClose2);

		    try {
			Thread.sleep(400);
		    } catch (InterruptedException e) {
		    }
		    System.exit(0);
		    return null;
		}
	    };
	    worker.start();
	} else {
	    System.exit(0);
	}
    }

    public static void main(String[] args) {
	// TODO Auto-generated method stub
	try {
	    UIManager.installLookAndFeel("SmoothMetal",
		    "smoothmetal.SmoothLookAndFeel");
	} catch (Exception e) {
	    e.printStackTrace();
	}

	Main main = new Main();
	main.setVisible(true);
    }

}
