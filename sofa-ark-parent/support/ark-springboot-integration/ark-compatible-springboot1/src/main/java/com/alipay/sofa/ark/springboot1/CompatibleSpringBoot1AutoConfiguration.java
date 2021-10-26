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
package com.alipay.sofa.ark.springboot1;

import com.alipay.sofa.ark.springboot.condition.ConditionalOnArkEnabled;
import com.alipay.sofa.ark.springboot.condition.ConditionalOnSpringBootVersion;
import com.alipay.sofa.ark.springboot1.endpoint.IntrospectBizEndpoint;
import com.alipay.sofa.ark.springboot1.endpoint.IntrospectBizEndpointMvcAdapter;
import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qilong.zql
 * @since 0.6.0
 */
@Configuration
@ConditionalOnSpringBootVersion(version = ConditionalOnSpringBootVersion.Version.OneX)
@ConditionalOnArkEnabled
public class CompatibleSpringBoot1AutoConfiguration {

    @ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.Endpoint")
    public static class ConditionIntrospectEndpointConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint("bizState")
        public IntrospectBizEndpoint introspectBizEndpoint() {
            return new IntrospectBizEndpoint();
        }
    }

    @Bean
    @ConditionalOnBean(IntrospectBizEndpoint.class)
    @ConditionalOnWebApplication
    public IntrospectBizEndpointMvcAdapter introspectBizEndpointMvcAdapter(IntrospectBizEndpoint introspectBizEndpoint) {
        return new IntrospectBizEndpointMvcAdapter(introspectBizEndpoint);
    }
}