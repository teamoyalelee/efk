/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.interceptor;

import com.google.common.collect.Maps;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagTest extends TestCase{
    private static final Map<String, Pattern> cache = Maps.newHashMap();

    @Test
    public void testTagParse(){
        String source = "gent.application.all.debug-105";
        Pattern pattern = Pattern.compile("^([^.]*)\\.([^.]*)\\.([^.]*)\\.([^.]*)$");
        Matcher matcher = pattern.matcher(source);
        if(matcher.matches()){
            System.out.println("Matched: "+matcher.group());
            System.out.println("Matched: "+matcher.group(1));
            System.out.println("Matched: "+matcher.group(2));
            System.out.println("Matched: "+matcher.group(3));
            System.out.println("Matched: "+matcher.group(4));
        }else{
            System.out.println("Unmatched");
        }
    }

    @Test
    public void testMsgParse(){
//        String source = "{key=value,key2=value2}";
//        Pattern pattern = Pattern.compile("\\{(.*)\\}");
        String source = "2017-11-24 10:23:18  [ DubboServerHandler-192.168.19.105:22883-thread-200:950117277 ][com.westone.cm2.csmp.mbls.component.AuthComponent][authenticate] - [ INFO ] MBls authenticate account aa\n\n";
        cache.put("\\{(.*)\\}",Pattern.compile("\\{(.*)\\}"));
        List<String> values = parseRegex(source,"(.*)",null);
        System.out.println("Values = "+values.toString());
//        Pattern pattern = Pattern.compile("(.*)");
//        Matcher matcher = pattern.matcher(source);
//        if(matcher.matches()){
//            System.out.println("Matched: "+matcher.group());
//            System.out.println("Matched: "+matcher.group(1));
//        }else{
//            System.out.println("Unmatched");
//        }
    }

    private List<String> parseRegex(String line, String regex, String split) {

        List<String> vals = new ArrayList<String>();


        if (line == null || regex == null || line.trim().length() < 1) {
            return vals;
        }

        if (split != null) {
            for (String str : line.split(split, -1)) {
                vals.add(str);
            }
            return vals;
        }

        Pattern p = cache.get(regex);
        if (p == null) {
            p = Pattern.compile(regex,Pattern.DOTALL);
            cache.put(regex, p);
        }
        Matcher m = p.matcher(line);
        if (m.matches()) {
            int count = m.groupCount();
            for (int i = 1; i <= count; i++) {
                vals.add(m.group(i));
            }
        }
        return vals;
    }
}
