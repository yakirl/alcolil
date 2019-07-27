package org.yakirl.alcolil.systemtests;


import java.util.Arrays;

import javax.swing.SwingUtilities;

import org.junit.Test;
import org.yakirl.alcolil.common.Quote;
import org.yakirl.alcolil.common.QuoteObserver;
import org.yakirl.alcolil.core.Command;
import org.yakirl.alcolil.core.Core;
import org.yakirl.alcolil.core.Executor;
import org.yakirl.alcolil.systemtests.BaseTestScenario;
import org.yakirl.alcolil.systemtests.mocks.*;
import org.yakirl.alcolil.ui.GController;
import org.yakirl.alcolil.ui.GUI;
import org.yakirl.alcolil.ui.GUIHandlers;
import org.yakirl.alcolil.ui.UserInterface;


/****
 * @author yakir
 *
 * This test uses mocked GController and Core.
 * GController is mocked.
 * In order to see full GUI functionality run system test that spawn GUI
 */

public class ManualGUITest extends BaseTestScenario {
	
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
