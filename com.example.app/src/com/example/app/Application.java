package com.example.app;

import java.io.IOException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
 

public class Application implements IApplication {

	@Override
	public Object start(final IApplicationContext context) throws Exception {

		final var display = PlatformUI.createDisplay();

		final Location instanceLoc = Platform.getInstanceLocation();
		try {
			if (instanceLoc == null) {
				// Need a workspace
				return IApplication.EXIT_OK;
			}
			if (instanceLoc.isSet()) {
				// Attempt to lock workspace
				if (!instanceLoc.lock()) {
					display.dispose();
					return IApplication.EXIT_OK;
				}

				final int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor( ));
				if (returnCode == PlatformUI.RETURN_RESTART) {
					return IApplication.EXIT_RESTART;
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (instanceLoc != null) {
				instanceLoc.release();
			}
			// clean exit
			display.dispose();
		}
		return IApplication.EXIT_OK;
	}
   
	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning()) {
			return;
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final var display = workbench.getDisplay();
		display.syncExec(() -> {
			if (!display.isDisposed()) {
				workbench.close();
			}
		});
	}
 
}
