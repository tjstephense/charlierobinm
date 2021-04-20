/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.test.baggage;

import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import com.alipay.sofa.rpc.context.RpcInvokeContext;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * @author zhanggeng
 */
public class BFutureSampleServiceImpl implements SampleService {

    private SampleService sampleServiceC;

    private SampleService sampleServiceD;

    private String        reqBaggage;

    public BFutureSampleServiceImpl(SampleService sampleServiceC, SampleService sampleServiceD) {
        this.sampleServiceC = sampleServiceC;
        this.sampleServiceD = sampleServiceD;
    }

    @Override
    public String hello() {
        RpcInvokeContext context = RpcInvokeContext.getContext();
        System.out.println("--b1-----:" + context);
        reqBaggage = context.getRequestBaggage("reqBaggageB");
        if (reqBaggage != null) {
            context.putResponseBaggage("respBaggageB", "b2aaa");
        } else {
            context.putResponseBaggage("respBaggageB_force", "b2aaaff");
        }
        String s1 = null;
        String s2 = null;
        try {
            s1 = sampleServiceC.hello();
            Future futureC = SofaResponseFuture.getFuture(true);
            s2 = sampleServiceD.hello();
            Future futureD = SofaResponseFuture.getFuture();
            s1 = (String) futureC.get();
            s2 = (String) futureD.get(2000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s1 + s2;
    }

    public String getReqBaggage() {
        return reqBaggage;
    }
}
