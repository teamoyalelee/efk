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

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagInterceptor implements Interceptor {

    private static final Logger logger = LoggerFactory.getLogger(TagInterceptor.class);

    private static Pattern DEFAULT_TAG_PATTERN = Pattern.compile(Constants.DEFAULT_TAG_REGEX);

    private final boolean preserveTag;
    private final String header;
    private final String defaultTag;

    private TagInterceptor(boolean preserveTag, String header, String defaultTag) {
        this.preserveTag = preserveTag;
        this.header = header;
        this.defaultTag = defaultTag;
    }

    @Override
    public void initialize() {

    }

    @Override
    public Event intercept(Event event)
    {
        Map<String, String> headers = event.getHeaders();

        String eventTag = headers.get(header);
        if(eventTag == null || eventTag.trim().length() == 0){
            eventTag = defaultTag;
            headers.put(header,eventTag);
        }

        Matcher matcher = DEFAULT_TAG_PATTERN.matcher(eventTag);
        if(matcher.matches()){
            headers.put("tag",matcher.group());
            headers.put("event-source",matcher.group(1).toLowerCase());
            headers.put("application",matcher.group(2).toLowerCase());
            headers.put("category",matcher.group(3).toLowerCase());
            headers.put("environment",matcher.group(4).toLowerCase());
        }else{
            headers.put(header,defaultTag);
            headers.put("tag",defaultTag);
            headers.put("event-source","default");
            headers.put("application","application");
            headers.put("category","all");
            headers.put("environment","debug");
        }

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events)
    {
        for (Event event : events){
            intercept(event);
        }

        return events;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder
    {
        private boolean preserveTag;
        private String header;
        private String defaultTag;

        @Override
        public void configure(Context context) {
            preserveTag = context.getBoolean(Constants.PRESERVE, Constants.PRESERVE_DEFAULT);
            header = context.getString(Constants.HEADER, Constants.HEADER_DEFAULT);
            defaultTag = context.getString(Constants.DEFAULT_TAG,
                    Constants.DEFAULT_TAG_DEFAULT);
        }

        @Override
        public Interceptor build() {
            logger.info(String.format(
                    "Creating AppTagInterceptor: preserveTag=%s,header=%s,defaultTag=%s",
                    preserveTag, header, defaultTag));

            return new TagInterceptor(preserveTag, header, defaultTag);
        }
    }

    public static class Constants {
        public static final String HEADER = "headerName";
        public static final String HEADER_DEFAULT = "event-tag";

        public static final String DEFAULT_TAG = "defaultTag";
        public static final String DEFAULT_TAG_DEFAULT = "default.application.all.debug";

        public static final String PRESERVE = "preserveTag";
        public static final boolean PRESERVE_DEFAULT = true;

        public static final String DEFAULT_TAG_REGEX =
                "^([^.]*)\\.([^.]*)\\.([^.]*)\\.([^.]*)$";
    }
}