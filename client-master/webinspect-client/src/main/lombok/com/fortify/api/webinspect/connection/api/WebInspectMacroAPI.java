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
package com.fortify.api.webinspect.connection.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.Boundary;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;

import com.fortify.api.util.rest.json.JSONList;
import com.fortify.api.webinspect.connection.WebInspectAuthenticatingRestConnection;

public class WebInspectMacroAPI extends AbstractWebInspectAPI {
	public WebInspectMacroAPI(WebInspectAuthenticatingRestConnection conn) {
		super(conn);
	}

	public JSONList getMacros() {
		return conn().executeRequest(HttpMethod.GET, conn().getBaseResource().path("/scanner/macro"), JSONList.class);
	}
	
	public void uploadMacro(String name, byte[] data) {
		MultiPart multiPart = new FormDataMultiPart();
        try {
			multiPart.type(new MediaType("multipart", "form-data",
		    		Collections.singletonMap(Boundary.BOUNDARY_PARAMETER, Boundary.createBoundary())));
			FormDataContentDisposition.FormDataContentDispositionBuilder builder =
                    FormDataContentDisposition.name("macro").fileName(name).size(data.length);
			multiPart.bodyPart(new FormDataBodyPart(builder.build(), data, MediaType.APPLICATION_OCTET_STREAM_TYPE));
			conn().executeRequest(HttpMethod.POST, conn().getBaseResource().path("/scanner/macro"),
					Entity.entity(multiPart, multiPart.getMediaType()), null);
        } finally {
        	try {
				multiPart.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}
	
	public static void main(String[] args) throws IOException {
		WebInspectAuthenticatingRestConnection conn = WebInspectAuthenticatingRestConnection.builder().baseUrl("http://apiKey:test@rs-fortifywie.westeurope.cloudapp.azure.com:8088/webinspect;readTimeout=80000").build();
		System.out.println(conn.api().macro().getMacros());
		//conn.api().macro().deleteMacro("test");
		Path path = Paths.get("C:/Users/sendenr/Downloads/test.webmacro");
		byte[] data = Files.readAllBytes(path);
		conn.api().macro().uploadMacro("MyTest", data);
	}
}