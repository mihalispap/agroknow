/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.File;
import java.io.InputStream;
import java.net.URL;



public final class Client {

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        // Sent HTTP GET request to query all customer info
        /*
         * URL url = new URL("http://localhost:9000/customers");
         * System.out.println("Invoking server through HTTP GET to query all
         * customer info"); InputStream in = url.openStream(); StreamSource
         * source = new StreamSource(in); printSource(source);
         */
    	
    	if (args.length == 0)
        {
          System.out.println("please specify url , directory and prefix");
          System.exit(1);
        }
    	
    	
    	 //URL url = new URL(args[0]);
    	 String url = args[0];
    	 String dir =  args[1];
    	 String prefix = args[2];
    	 String enable = args[3];
    	

    	 Process ps = Runtime.getRuntime().exec(new String[]{"java","-jar","harvester.jar",url,dir,enable});
    	 ps.waitFor();
    	 java.io.InputStream is = ps.getInputStream();
    	 byte b[] = new byte[is.available()];
    	 is.read(b, 0,b.length);
    	 
    	 
    	

    

        System.out.println("\n");
        System.exit(0);
    }

 

}
