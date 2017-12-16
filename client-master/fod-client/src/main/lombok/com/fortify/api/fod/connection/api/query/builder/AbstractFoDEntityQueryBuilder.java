/*******************************************************************************
 * (c) Copyright 2017 EntIT Software LLC
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
package com.fortify.api.fod.connection.api.query.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.fortify.api.fod.connection.FoDAuthenticatingRestConnection;
import com.fortify.api.fod.connection.api.query.FoDEntityQuery;
import com.fortify.api.util.rest.query.AbstractRestConnectionQueryConfig;
import com.fortify.api.util.rest.query.IRestConnectionQuery;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdater;
import com.fortify.api.util.rest.webtarget.IWebTargetUpdaterBuilder;
import com.fortify.api.util.rest.webtarget.WebTargetQueryParamUpdater;

/**
 * <p>This abstract base class is used to build {@link FoDEntityQuery} instances. Concrete implementations
 * will need to provide the actual FoD REST API endpoint by implementing the {@link #getTargetPath()}
 * method, and provide any builder methods for configuring the query request.</p>
 * 
 * <p>This class provides various protected methods for configuring common FoD request parameters,
 * like 'filter' and 'orderBy'. Depending on the whether the target FoD endpoint supports these parameters,
 * concrete implementations can override these methods as 'public' to make the generic method available, 
 * and/or provide more specialized builder methods that call these generic methods, for example to support
 * specific fields to be added to the 'q' parameter.</p>  
 *  
 * @author Ruud Senden
 *
 * @param <T> Concrete builder type
 */
public abstract class AbstractFoDEntityQueryBuilder<T> extends AbstractRestConnectionQueryConfig<FoDAuthenticatingRestConnection, T> {
	private FoDParamFilter paramFilter = add(new FoDParamFilter());
	
	/**
	 * Create new instance for given {@link FoDAuthenticatingRestConnection} and indicator whether paging is supported.
	 * @param conn
	 * @param pagingSupported
	 */
	protected AbstractFoDEntityQueryBuilder(FoDAuthenticatingRestConnection conn, boolean pagingSupported) {
		super(conn, pagingSupported);
	}
	
	/**
	 * Build an {@link IRestConnectionQuery} instance from the current
	 * configuration.
	 * 
	 * @return
	 */
	public IRestConnectionQuery build() {
		return new FoDEntityQuery(this);
	}
	
	/**
	 * Add the 'orderBy' query parameter to the request configuration
	 * 
	 * @param orderBy
	 * @return
	 */
	protected T paramOrderBy(String orderBy) {
		return queryParam("orderBy", orderBy);
	}
	
	/**
	 * Add the 'orderByDirection' query parameter to the request configuration
	 * 
	 * @param orderByDirection
	 * @return
	 */
	protected T paramOrderByDirection(OrderByDirection orderByDirection) {
		return queryParam("orderByDirection", orderByDirection.name());
	}
	
	protected T paramOrderBy(String orderBy, OrderByDirection orderByDirection) {
		paramOrderBy(orderBy);
		return paramOrderByDirection(orderByDirection);
	}
	
	/**
	 * Add the 'fields' query parameter to the request configuration
	 * 
	 * @param fields
	 * @return
	 */
	protected T paramFields(String... fields) {
		return queryParam("fields", StringUtils.join(fields, ","));
	}
	
	/**
	 * Add the 'filter' query parameter to the request configuration. 
	 * If already set, the new field and value will be 'and-ed' to the
	 * current query parameter value.
	 * 
	 * TODO Add support for ORed values (multiple values separated by pipe symbol)
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	protected T paramFilterAnd(String field, String value) {
		paramFilter.paramFilterAnd(field, value); return _this();
	}
	
	/**
	 * {@link IWebTargetUpdaterBuilder} implementation for adding the
	 * FoD 'filter' request parameter.
	 *  
	 * @author Ruud Senden
	 *
	 */
	private static class FoDParamFilter implements IWebTargetUpdaterBuilder {
		private final Map<String, String> paramFilterAnds = new HashMap<>();
		
		public final FoDParamFilter paramFilterAnd(String field, String value) {
			paramFilterAnds.put(field, value);
			return this;
		}

		@Override
		public IWebTargetUpdater build() {
			String filter = null;
			if ( MapUtils.isNotEmpty(paramFilterAnds) ) {
				StringBuffer sb = new StringBuffer();
				for ( Map.Entry<String, String> entry : paramFilterAnds.entrySet() ) {
					String qAppend = entry.getKey()+":"+entry.getValue()+"";
					if ( sb.length() == 0 ) {
						sb.append(qAppend);
					} else {
						sb.append("+"+qAppend);
					}
				}
				filter = sb.toString();
			}
			return new WebTargetQueryParamUpdater("q", filter);
		}

	} 
}
