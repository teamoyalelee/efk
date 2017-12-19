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
package org.apache.flume.sink.elasticsearch;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liz on 15/11/10.
 */
public class EventThreadPoolExecutor extends ThreadPoolExecutor {

    public boolean hasFinish = false;

    public AtomicInteger finishNum = new AtomicInteger(0);

    public EventThreadPoolExecutor(int i, int i1, long l, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue) {
        super(i, i1, l, timeUnit, blockingQueue);
    }

    public EventThreadPoolExecutor(int i, int i1, long l, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
        super(i, i1, l, timeUnit, blockingQueue, threadFactory);
    }

    public EventThreadPoolExecutor(int i, int i1, long l, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, RejectedExecutionHandler rejectedExecutionHandler) {
        super(i, i1, l, timeUnit, blockingQueue, rejectedExecutionHandler);
    }

    public EventThreadPoolExecutor(int i, int i1, long l, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
        super(i, i1, l, timeUnit, blockingQueue, threadFactory, rejectedExecutionHandler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t){
        super.afterExecute(r, t);
        synchronized(this){
            if(this.getActiveCount() == 1 )
            {
                this.hasFinish=true;
                this.notify();
            }
        }
    }

    public void isEndTask() throws InterruptedException {
        synchronized(this){
            while (this.hasFinish == false) {
                    this.wait();
            }
        }
    }

    public void runOver(){
        finishNum.getAndIncrement();
    }

    public void reset(){
        finishNum.set(0);
        hasFinish = false;
    }

    public int getFinishNum(){
        return finishNum.get();
    }
}
