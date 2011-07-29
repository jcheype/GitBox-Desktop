package io.gitbox;

import io.gitbox.wizard.ConfigWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jean-Baptiste lemée
 */
public class GitBoxDesktop {
    private static final Logger LOG = LoggerFactory.getLogger(GitBoxDesktop.class);

    public static void main(String... args) {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        // Initialize System Tray
        final Image image = new Image(display, Configuration.getIcon());
        final Tray tray = display.getSystemTray();
        if (tray == null) {
            LOG.warn("The system tray is not available");
        } else {
            final TrayItem item = new TrayItem(tray, SWT.NONE);
            item.setToolTipText("GitBox");

            // Add menu
            final Menu menu = new Menu(shell, SWT.POP_UP);

            // Add menu entries
            final MenuItem mu = new MenuItem(menu, SWT.PUSH);
            mu.setText("Test");
            mu.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    LOG.info("Menu Click !");
                }
            });

            final MenuItem mWizard = new MenuItem(menu, SWT.PUSH);
            mWizard.setText("Settings");
            mWizard.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    WizardDialog dialog = new WizardDialog(shell, new ConfigWizard());
                    dialog.open();
                }
            });

            // Separator before exist
            new MenuItem(menu, SWT.SEPARATOR);

            // Exit entry
            final MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Exit");
            mi.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    shell.dispose();
                }
            });


            // make menu appear on right and left click
            final Listener displayMenu = new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            };
            item.addListener(SWT.MenuDetect, displayMenu);

            /*if(OsType.current != OsType.Mac) {
              item.addListener(SWT.Selection, displayMenu );
           } */

            item.setImage(image);
        }

        // Main loop
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        image.dispose();
        display.dispose();
    }
}
