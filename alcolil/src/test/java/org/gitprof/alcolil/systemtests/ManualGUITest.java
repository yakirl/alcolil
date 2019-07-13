package org.gitprof.alcolil.systemtests;


import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.gitprof.alcolil.common.Quote;
import org.gitprof.alcolil.common.QuoteObserver;
import org.gitprof.alcolil.core.Command;
import org.gitprof.alcolil.core.Core;
import org.gitprof.alcolil.core.Executor;
// import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.systemtests.BaseTestScenario;
import org.gitprof.alcolil.systemtests.mocks.*;
import org.gitprof.alcolil.ui.GController;
import org.gitprof.alcolil.ui.GUI;
import org.gitprof.alcolil.ui.GUIHandlers;
import org.gitprof.alcolil.ui.UserInterface;
import org.junit.Test;


/****
 * @author yakir
 *
 * This test uses mocked GController and Core.
 * GController is mocked.
 * In order to see full GUI functionality run system test that spawn GUI
 */

public class ManualGUITest extends BaseTestScenario {
	
	// @Test
	public void testBasic() throws Exception {
		// MockedCore core = new MockedCore();
		
		// UserInterface.startInterface(core);
		// Thread.sleep(100000);
	}
	
	// @Test
	public void testBacktestBasic() {
		MockedCore core = new MockedCore();
		MockedGController gcontroller = new MockedGController(core);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				(new GUI(gcontroller)).showGUI();
			}
		});		
	}
	
	@Test
	public void testBacktest() throws Exception {
		Thread t = new Thread() {
			public void run() {
				String arr[] = {"s"};
				Executor.main(arr);
			}
		};
		t.start();
		t.join();		
	}
}
