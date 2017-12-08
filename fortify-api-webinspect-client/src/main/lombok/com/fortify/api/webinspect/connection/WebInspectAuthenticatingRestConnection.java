/*******************************************************************************
 * (c) Copyright 2017 Hewlett Packard Enterprise Development LP
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions:
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
package com.fortify.api.webinspect.connection;

import javax.ws.rs.client.WebTarget;

import org.apache.http.auth.Credentials;

import com.fortify.api.util.rest.connection.IRestConnectionBuilder;
import com.fortify.api.util.rest.connection.RestConnection;
import com.fortify.api.webinspect.connection.api.WebInspectAPI;

/**
 * This {@link RestConnection} implementation provides various
 * methods for working with the WebInspect Enterprise API.
 * 
 * @author Ruud Senden
 *
 */
public class WebInspectAuthenticatingRestConnection extends WebInspectBasicRestConnection {
	private final Credentials credentials;
	private final WebInspectAPI api = new WebInspectAPI(this);
	
	public WebInspectAuthenticatingRestConnection(RestConnectionConfig<?> config) {
		super(config);
		this.credentials = config.getCredentials();
	}
	
	public final WebInspectAPI api() {
		return api;
	}
	
	@Override
	protected WebTarget updateWebTarget(WebTarget webTarget) {
		webTarget = super.updateWebTarget(webTarget);
		if ( credentials != null ) {
			if ( "apiKey".equalsIgnoreCase(credentials.getUserPrincipal().getName()) ) {
				webTarget = webTarget.queryParam("api_key", credentials.getPassword());
			}
		}
		return webTarget;
	}
	
	public static final WebInspectAuthenticatingRestConnectionBuilder builder() {
		return new WebInspectAuthenticatingRestConnectionBuilder();
	}
	
	public static final class WebInspectAuthenticatingRestConnectionBuilder extends RestConnectionConfigWithoutCredentialsProvider<WebInspectAuthenticatingRestConnectionBuilder> implements IRestConnectionBuilder<WebInspectAuthenticatingRestConnection> {
		@Override
		public WebInspectAuthenticatingRestConnection build() {
			return new WebInspectAuthenticatingRestConnection(this);
		}
	}
}