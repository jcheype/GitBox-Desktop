package io.gitbox.wizard;

import com.jcheype.gitbox.GitBox;
import io.gitbox.Configuration;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author Jean-Baptiste Lemée
 */
public class ConfigWizard extends Wizard {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigWizard.class);

    public ConfigWizard() {
        super();
    }

    public void addPages() {
        addPage(new DirectoryPage());
        addPage(new ChooseDirectoryPage());
        addPage(new SummaryPage());
    }

    public boolean performFinish() {
        DirectoryPage dirPage = getDirectoryPage();
        if (dirPage.useDefaultDirectory()) {
            System.out.println("Using default directory");
        } else {
            ChooseDirectoryPage choosePage = getChoosePage();
            Configuration.setDirectory(choosePage.getDirectory());
            Configuration.setNotificationServer(choosePage.getNotificationServer());
            Configuration.setGitRepository(choosePage.getGitRepository());

            if(StringUtils.isNotBlank(choosePage.getDirectory())
                    && StringUtils.isNotBlank(choosePage.getGitRepository())
                    &&!containsDirectory(".git", choosePage.getDirectory())){
                GitBox.cloneGit(choosePage.getGitRepository(), new File(choosePage.getDirectory()));
            }
        }
        return true;
    }

     private static boolean containsDirectory(String subDirectory, String directory) {
        return new File(directory + subDirectory).exists();
    }

    private ChooseDirectoryPage getChoosePage() {
        return (ChooseDirectoryPage) getPage(ChooseDirectoryPage.PAGE_NAME);
    }

    private DirectoryPage getDirectoryPage() {
        return (DirectoryPage) getPage(DirectoryPage.PAGE_NAME);
    }

    public boolean performCancel() {
        System.out.println("Perform Cancel called");
        return true;
    }

    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof DirectoryPage) {
            DirectoryPage dirPage = (DirectoryPage) page;
            if (dirPage.useDefaultDirectory()) {
                SummaryPage summaryPage = (SummaryPage) getPage(SummaryPage.PAGE_NAME);
                summaryPage.updateText("Using default directory");
                return summaryPage;
            }
        }

        IWizardPage nextPage = super.getNextPage(page);
        if (nextPage instanceof SummaryPage) {
            SummaryPage summary = (SummaryPage) nextPage;
            DirectoryPage dirPage = getDirectoryPage();
            summary
                    .updateText(dirPage.useDefaultDirectory() ? "Using default directory"
                            : "Using directory:"
                            + getChoosePage().getDirectory());
        }
        return nextPage;
    }
}

class SummaryPage extends WizardPage {
    public static final String PAGE_NAME = "Summary";

    private Label textLabel;

    public SummaryPage() {
        super(PAGE_NAME, "Summary Page", null);
    }

    public void createControl(Composite parent) {
        Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new FillLayout());

        textLabel = new Label(topLevel, SWT.CENTER);
        textLabel.setText("");

        setControl(topLevel);
        setPageComplete(true);
    }

    public void updateText(String newText) {
        textLabel.setText(newText);
    }
}

class DirectoryPage extends WizardPage {
    public static final String PAGE_NAME = "Directory";

    private Button button;

    public DirectoryPage() {
        super(PAGE_NAME, "Directory Page", null);
    }

    public void createControl(Composite parent) {
        Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout(2, false));

        Label l = new Label(topLevel, SWT.CENTER);
        l.setText("Use default directory?");

        button = new Button(topLevel, SWT.CHECK);

        setControl(topLevel);
        setPageComplete(true);
    }

    public boolean useDefaultDirectory() {
        return button.getSelection();
    }
}

class ChooseDirectoryPage extends WizardPage {
    public static final String PAGE_NAME = "Choose Directory";

    private Text directoryText;
    private Text notificationServerText;
    private Text gitRepositoryURLText;

    public ChooseDirectoryPage() {
        super(PAGE_NAME, "Choose Directory Page", null);
    }

    public void createControl(final Composite parent) {
        final Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));

        Label l = new Label(topLevel, SWT.CENTER);
        l.setText("Enter the directory to use:");

        directoryText = new Text(topLevel, SWT.BORDER);
        directoryText.setText(Configuration.getDirectory());
        directoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button button = new Button(topLevel, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DirectoryDialog dlg = new DirectoryDialog(topLevel.getShell());

                // Set the initial filter path according
                // to anything they've selected or typed in
                dlg.setFilterPath(directoryText.getText());

                // Change the title bar directoryText
                dlg.setText("SWT's DirectoryDialog");

                // Customizable message displayed in the dialog
                dlg.setMessage("Select a directory");

                // Calling open() will open and run the dialog.
                // It will return the selected directory, or
                // null if user cancels
                String dir = dlg.open();
                if (dir != null) {
                    // Set the directoryText box to the new selection
                    directoryText.setText(dir);
                }
            }
        });

        Label l2 = new Label(topLevel, SWT.CENTER);
        l2.setText("Enter the notification server to use:");

        notificationServerText = new Text(topLevel, SWT.BORDER);
        notificationServerText.setText(Configuration.getNotificationServer());
        notificationServerText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        Label l3 = new Label(topLevel, SWT.CENTER);
        l3.setText("Enter the git repository to use:");

        gitRepositoryURLText = new Text(topLevel, SWT.BORDER);
        gitRepositoryURLText.setText(Configuration.getGitRepository());
        gitRepositoryURLText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));

        setControl(topLevel);

        setPageComplete(true);
    }

    public String getDirectory() {
        return directoryText.getText();
    }

    public String getGitRepository() {
        return gitRepositoryURLText.getText();
    }

    public String getNotificationServer() {
        return notificationServerText.getText();
    }
}
