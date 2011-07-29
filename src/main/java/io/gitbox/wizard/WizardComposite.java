package io.gitbox.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jean-Baptiste Lemée
 */
public class WizardComposite extends Composite {
    public WizardComposite(Composite parent) {
        super(parent, SWT.NONE);
        buildControls();
    }

    protected void buildControls() {
        final Composite parent = this;
        FillLayout layout = new FillLayout();
        parent.setLayout(layout);

        Button dialogBtn = new Button(parent, SWT.PUSH);
        dialogBtn.setText("Wizard Dialog...");
        dialogBtn.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                WizardDialog dialog = new WizardDialog(parent.getShell(),
                        new ProjectWizard());
                dialog.open();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

}

class ProjectWizard extends Wizard {

    public ProjectWizard() {
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

    public void createControl(Composite parent) {
        Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout(2, false));

        Label l = new Label(topLevel, SWT.CENTER);
        l.setText("Enter the directory to use:");

        text = new Text(topLevel, SWT.SINGLE);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        setControl(topLevel);
        setPageComplete(true);
    }

    public String getDirectory() {
        return text.getText();
    }
}
