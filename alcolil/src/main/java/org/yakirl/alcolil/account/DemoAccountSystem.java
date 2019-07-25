package org.yakirl.alcolil.account;

import org.yakirl.alcolil.common.*;

public class DemoAccountSystem extends BaseAccountSystem {

	@Override
	public int makeOrder(AOrder order) {
		return 0;
	}

	@Override
	public void cancelOrder(int orderID) {
		// TODO Auto-generated method stub
		
	}
	
	/* since this is not real ordering system, we need to listen to quote streaming,
	 * to see when an order is executed
	 * TODO: make it runnable or something...
	 * if we cant decide if or which order is executed after receiving a quote - cancel the order and notify user
	 * 
	 * BTW: we can pull ticks from IB.
	 */
	public void listenToQuoteStreaming() {
		
	}

}
