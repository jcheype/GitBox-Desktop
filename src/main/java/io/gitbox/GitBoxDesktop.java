package io.gitbox;

import com.jcheype.gitbox.AeSimpleSHA1;
import com.jcheype.gitbox.GitBox;
import com.jcheype.gitbox.GitBoxController;
import com.jcheype.gitbox.NotificationClient;
import io.gitbox.wizard.ConfigWizard;
import org.apache.commons.lang.SystemUtils;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jean-Baptiste Lemée
 */
public class GitBoxDesktop {
    private static final Logger LOG = LoggerFactory.getLogger(GitBoxDesktop.class);
    private static GitBoxController gitBoxController;

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

            final MenuItem mStart = new MenuItem(menu, SWT.PUSH);
            mStart.setText("Launch");
            mStart.setEnabled(Configuration.isValid());
            mStart.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {

                    try {
                        String remoteSha1 = AeSimpleSHA1.SHA1(Configuration.getGitRepository());
                        GitBox gitBox = new GitBox(Configuration.getDirectory());
                        NotificationClient notificationClient = new NotificationClient(Configuration.getNotificationServer() + remoteSha1);

                        gitBoxController = new GitBoxController(notificationClient, gitBox);
                        gitBoxController.getGitBox().start();
                        gitBoxController.getNotificationClient().start();
                        gitBoxController.getGitBoxFileListener().start();
                    } catch (NoSuchAlgorithmException e) {
                        LOG.error(e.getMessage(), e);
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    } catch (Exception e) {
                        LOG.error(e.getMessage(), e);
                    }

                }
            });

            final MenuItem mOpenDirectory = new MenuItem(menu, SWT.PUSH);
            mOpenDirectory.setText("Open");
            mOpenDirectory.setEnabled(Configuration.isValid()
                    && Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
            mOpenDirectory.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.open(new File(Configuration.getDirectory()));
                    } catch (IOException e) {
                        LOG.error("Unable to open the repository directory", e);
                    }
                }
            });

            final MenuItem mConfigWizard = new MenuItem(menu, SWT.PUSH);
            mConfigWizard.setText("Settings");
            mConfigWizard.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    WizardDialog dialog = new WizardDialog(shell, new ConfigWizard());
                    dialog.open();
                    mStart.setEnabled(Configuration.isValid());
                    mOpenDirectory.setEnabled(Configuration.isValid()
                            && Desktop.isDesktopSupported()
                            && Desktop.getDesktop().isSupported(Desktop.Action.OPEN));
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
                    gitBoxController.getGitBoxFileListener().stop();
                    gitBoxController.getNotificationClient().stop();
                    gitBoxController.getGitBox().stop();
                    System.exit(0);
                }
            });


            // make menu appear on right and left click
            final Listener displayMenu = new Listener() {
                public void handleEvent(Event event) {
                    menu.setVisible(true);
                }
            };
            item.addListener(SWT.MenuDetect, displayMenu);

            if (!SystemUtils.IS_OS_MAC) {
                item.addListener(SWT.Selection, displayMenu);
            }

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
