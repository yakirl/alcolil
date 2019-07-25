package org.yakirl.alcolil.marketdata;

import pl.zankowski.iextrading4j.client.*;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;
import pl.zankowski.iextrading4j.api.stocks.*;

/****
 * @author yakir
 * 
 *  IEX offer free realtime and historical data via simple REST API
 *  
 *  example:
 *  https://cloud.iexapis.com/stable/stock/aapl/quote?token=pk_ba20dd55232d4b4a89057c2d710ce4fd
 *
 * results is JSON
 * 
 *  Here we use the unofficial, standard java library for this API.
 *  
 *   NOTE: IEX API has been deprecated recently, and IEX CLOUD is used instead.
 *    So there are lots of deprecated APIs and out of date documentation in their site / github.
 *    Watch carefully and skip broken APIs.  
 */
public class IEXFetcher {
	public void printQuote() {
		final IEXCloudClient cloudClient = IEXTradingClient.create(IEXTradingApiVersion.IEX_CLOUD_BETA_SANDBOX,
		              new IEXCloudTokenBuilder()
		                      .withPublishableToken("Tpk_18dfe6cebb4f41ffb219b9680f9acaf2")
		                      .withSecretToken("Tsk_3eedff6f5c284e1a8b9bc16c54dd1af3")		              
		                      .build());
	
		
		final Quote quote = cloudClient.executeRequest(new QuoteRequestBuilder()
		    .withSymbol("AAPL")
		    .build());
		System.out.println(quote);
	}
}
