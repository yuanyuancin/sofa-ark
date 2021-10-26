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
package com.alipay.sofa.ark.test;

import com.alipay.sofa.ark.container.test.TestClassLoader;
import com.alipay.sofa.ark.spi.event.ArkEvent;
import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.ark.spi.service.event.EventAdminService;
import com.alipay.sofa.ark.spi.service.plugin.PluginManagerService;
import com.alipay.sofa.ark.springboot.runner.ArkBootRunner;
import com.alipay.sofa.ark.test.springboot.BaseSpringApplication;
import com.alipay.sofa.ark.test.springboot.TestValueHolder;
import com.alipay.sofa.ark.test.springboot.facade.SampleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
@RunWith(ArkBootRunner.class)
@SpringBootTest(classes = BaseSpringApplication.class)
public class ArkBootRunnerTest {

    @Autowired
    public SampleService        sampleService;

    @ArkInject
    public PluginManagerService pluginManagerService;

    @ArkInject
    public EventAdminService    eventAdminService;

    @Test
    public void test() {
        Assert.assertNotNull(sampleService);
        Assert.assertNotNull(pluginManagerService);
        Assert.assertTrue("SampleService".equals(sampleService.say()));

        ArkBootRunner runner = new ArkBootRunner(ArkBootRunnerTest.class);
        Field field = ReflectionUtils.findField(ArkBootRunner.class, "runner");
        Assert.assertNotNull(field);

        ReflectionUtils.makeAccessible(field);
        BlockJUnit4ClassRunner springRunner = (BlockJUnit4ClassRunner) ReflectionUtils.getField(
            field, runner);
        Assert.assertTrue(springRunner.getClass().getCanonicalName()
            .equals(SpringRunner.class.getCanonicalName()));

        ClassLoader loader = springRunner.getTestClass().getJavaClass().getClassLoader();
        Assert.assertTrue(loader.getClass().getCanonicalName()
            .equals(TestClassLoader.class.getCanonicalName()));

        Assert.assertEquals(0, TestValueHolder.getTestValue());
        eventAdminService.sendEvent(new ArkEvent() {
            @Override
            public String getTopic() {
                return "test-event-A";
            }
        });
        Assert.assertEquals(10, TestValueHolder.getTestValue());
        eventAdminService.sendEvent(new ArkEvent() {
            @Override
            public String getTopic() {
                return "test-event-B";
            }
        });
        Assert.assertEquals(20, TestValueHolder.getTestValue());
    }

    /**
     * issue#234
     */
    @Test
    public void testLogClassCastBug() {
        Throwable throwable = null;
        try {
            ILoggerFactory iLoggerFactory = (ILoggerFactory) this.getClass().getClassLoader()
                .loadClass("org.apache.logging.slf4j.Log4jLoggerFactory").newInstance();
        } catch (Throwable t) {
            throwable = t;
        }
        Assert.assertNull(throwable);
    }

}