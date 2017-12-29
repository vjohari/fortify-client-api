/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC, a Micro Focus company
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.client.wie.api.query.builder;

import com.fortify.client.wie.api.query.WIEEntityQuery;
import com.fortify.client.wie.connection.WIEAuthenticatingRestConnection;
import com.fortify.util.rest.json.preprocessor.JSONMapFilterRegEx;
import com.fortify.util.rest.json.preprocessor.AbstractJSONMapFilter.MatchMode;

/**
 * This class allows for building an {@link WIEEntityQuery} instance that allows for
 * querying WIE macro's.
 * 
 * @author Ruud Senden
 *
 */
public class WIEMacrosQueryBuilder extends AbstractWIEEntityQueryBuilder<WIEMacrosQueryBuilder> {
	public WIEMacrosQueryBuilder(WIEAuthenticatingRestConnection conn) {
		super(conn, true);
		appendPath("/api/v1/macros");
	}
	
	public WIEMacrosQueryBuilder names(String... names) {
		return preProcessor(new JSONMapFilterRegEx(MatchMode.INCLUDE, "name", "\\Q"+String.join("\\E|\\Q", names)+"\\E"));
	}
}