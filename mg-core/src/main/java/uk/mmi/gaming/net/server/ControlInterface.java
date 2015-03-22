package uk.mmi.gaming.net.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class ControlInterface implements ServerCommListener {
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YY HH:mm:ss");

	Display display;
	private Image greenLight;
	private Image redLight;
	Label lblGameCount;
	Label lblGamesStarted;
	Label lblPlayerOnline;
	ServerController serverComm;
	private Label lblStatusLight;
	private LedRunnable greenLedExec;
	private LedRunnable redLedExec;
	List logList;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ControlInterface window = new ControlInterface();
			Shell shell = window.createUiContent();
			window.createBackendContent();
			window.open(shell);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	private Shell createUiContent() {
		display = Display.getDefault();
		Shell shell = new Shell();
		shell.setText("Net Pong Control");
		shell.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginRight = 10;
		gl_composite.verticalSpacing = 10;
		gl_composite.horizontalSpacing = 10;
		gl_composite.marginLeft = 10;
		gl_composite.marginTop = 10;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite statusComposite = new Composite(composite, SWT.NONE);
		statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		statusComposite.setLayout(new GridLayout(4, false));

		Label lblRunning = new Label(statusComposite, SWT.NONE);
		lblRunning.setSize(57, 20);
		lblRunning.setText("Running:");

		redLight = SWTResourceManager.getImage(ControlInterface.class, "/led_red.png");
		greenLight = SWTResourceManager.getImage(ControlInterface.class, "/led_green.png");
		lblStatusLight = new Label(statusComposite, SWT.CENTER);
		GridData gd_lblStatusLight = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
		gd_lblStatusLight.widthHint = 50;
		lblStatusLight.setLayoutData(gd_lblStatusLight);
		lblStatusLight.setSize(32, 32);
		lblStatusLight.setImage(redLight);

		final Button btnStart = new Button(statusComposite, SWT.NONE);
		GridData gd_btnStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnStart.widthHint = 80;
		btnStart.setLayoutData(gd_btnStart);
		btnStart.setSize(70, 30);
		btnStart.setText("Start...");

		final Button btnStop = new Button(statusComposite, SWT.NONE);
		GridData gd_btnStop = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnStop.widthHint = 80;
		btnStop.setLayoutData(gd_btnStop);
		btnStop.setSize(70, 30);
		btnStop.setText("Stop...");
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (serverComm.isRunning()) {
					serverComm.shutdown();
					btnStop.setEnabled(false);
					btnStart.setEnabled(true);
				}
			}
		});
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!serverComm.isRunning()) {
					serverComm.startup();
					btnStop.setEnabled(true);
					btnStart.setEnabled(false);
				}
			}
		});
		btnStop.setEnabled(false);

		Composite infoComposite = new Composite(composite, SWT.NONE);
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		infoComposite.setLayout(new GridLayout(2, false));

		Label lblGamesCreated = new Label(infoComposite, SWT.NONE);
		lblGamesCreated.setSize(102, 20);
		lblGamesCreated.setText("Games created:");

		lblGameCount = new Label(infoComposite, SWT.NONE);
		lblGameCount.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblGameCount.setSize(8, 20);
		lblGameCount.setText("0");

		Label lblStarted = new Label(infoComposite, SWT.NONE);
		lblStarted.setSize(98, 20);
		lblStarted.setText("Games started:");

		lblGamesStarted = new Label(infoComposite, SWT.NONE);
		lblGamesStarted.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGamesStarted.setSize(8, 20);
		lblGamesStarted.setText("0");

		Label lblPlayersOnline = new Label(infoComposite, SWT.NONE);
		lblPlayersOnline.setSize(94, 20);
		lblPlayersOnline.setText("Players online:");

		lblPlayerOnline = new Label(infoComposite, SWT.NONE);
		lblPlayerOnline.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPlayerOnline.setSize(8, 20);
		lblPlayerOnline.setText("0");

		logList = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_logList = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_logList.heightHint = 100;
		gd_logList.minimumHeight = 100;
		logList.setLayoutData(gd_logList);

		shell.layout();
		shell.pack(true);
		shell.setSize(400, 400);
		return shell;
	}

	private void createBackendContent() {
		serverComm = new ServerController(this);

		greenLedExec = new LedRunnable(lblStatusLight, greenLight);
		redLedExec = new LedRunnable(lblStatusLight, redLight);
	}

	public void open(Shell shell) {
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		serverComm.shutdown();
	}

	@Override
	public void serverStarted() {
		display.asyncExec(greenLedExec);
		updateData(0, 0, 0);
	}

	@Override
	public void serverStopped() {
		display.asyncExec(redLedExec);
	}

	@Override
	public void log(String msg, Object... params) {
		final String dateMessage = sdf.format(new Date()) + " - " + String.format(msg, params);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				logList.add(dateMessage);
				int index = logList.getItemCount() - 1;
				logList.select(index);
				logList.showSelection();
				logList.deselect(index);
			}
		});
	}

	private void updateData(final int gameCount, final int gameStarted, final int players) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				lblGameCount.setText(String.valueOf(gameCount));
				lblGamesStarted.setText(String.valueOf(gameStarted));
				lblPlayerOnline.setText(String.valueOf(players));
			}
		});
	}

	class LedRunnable implements Runnable {
		private Image img;
		private Label lbl;

		public LedRunnable(Label lbl, Image img) {
			this.lbl = lbl;
			this.img = img;
		}

		@Override
		public void run() {
			lbl.setImage(img);
		}
	}
}