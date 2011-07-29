package io.gitbox.wizard;

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

/**
 * @author Jean-Baptiste lemée
 */
public class ConfigWizard extends Wizard {

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
            System.out.println("Using directory: " + choosePage.getDirectory());
        }
        return true;
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

    private Text text;

    public ChooseDirectoryPage() {
        super(PAGE_NAME, "Choose Directory Page", null);
    }

    public void createControl(final Composite parent) {
        final Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));

        Label l = new Label(topLevel, SWT.CENTER);
        l.setText("Enter the directory to use:");

        text = new Text(topLevel, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button button = new Button(topLevel, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DirectoryDialog dlg = new DirectoryDialog(topLevel.getShell());

                // Set the initial filter path according
                // to anything they've selected or typed in
                dlg.setFilterPath(text.getText());

                // Change the title bar text
                dlg.setText("SWT's DirectoryDialog");

                // Customizable message displayed in the dialog
                dlg.setMessage("Select a directory");

                // Calling open() will open and run the dialog.
                // It will return the selected directory, or
                // null if user cancels
                String dir = dlg.open();
                if (dir != null) {
                    // Set the text box to the new selection
                    text.setText(dir);
                }
            }
        });

        setControl(topLevel);

        setPageComplete(true);
    }

    public String getDirectory() {
        return text.getText();
    }
}
