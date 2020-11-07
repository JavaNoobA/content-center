package com.erudev.contentcenter.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.alibaba.nacos.NacosDiscoveryProperties;
import org.springframework.cloud.alibaba.nacos.ribbon.NacosServer;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 集群基于权重的负载均衡算法
 *
 * @author pengfei.zhao
 * @date 2020/11/7 15:03
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的服务名称
            String name = loadBalancer.getName();
            // 配置中集群名称
            String clusterName = nacosDiscoveryProperties.getClusterName();

            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // 1. 找到指定服务的所有实例 A
            List<Instance> instances = namingService.getAllInstances(name, true);

            // 2. 过滤出相同集群下的所有实例 B
            List<Instance> instanceList = instances.stream().
                    filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());

            // 3. 如果 B 为空, 就用A
            List<Instance> result = null;
            if (CollectionUtils.isEmpty(instanceList)) {
                log.warn("发生跨集群间调用, name = {}, clusterName = {}, instances = {}", name, clusterName, instances);
                result = instances;
            } else {
                result = instanceList;
            }
            // 4. 基于权重的负载均衡算法, 返回1个实例
            Instance instance = ExtendedBalancer.getHostByRandomWeight2(result);
            return new NacosServer(instance);
        } catch (NacosException e) {
            return null;
        }
    }
}

class ExtendedBalancer extends Balancer {
    public static Instance getHostByRandomWeight2(List<Instance> hosts){
        return getHostByRandomWeight(hosts);
    }
}
