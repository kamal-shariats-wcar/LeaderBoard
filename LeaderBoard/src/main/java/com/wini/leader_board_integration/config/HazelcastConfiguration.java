package com.wini.leader_board_integration.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.wini.leader_board_integration.data.model.Profile;
import com.wini.leader_board_integration.data.model.security.User;
import com.wini.leader_board_integration.util.WiniUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
public class HazelcastConfiguration {
    private final String groupName;
    private final String instanceName;
    private final String hazelInterface;
    private final String hazelMember;
    public HazelcastConfiguration(@Value("${HAZELCAST_GROUP_NAME}") final String groupName, @Value("${HAZELCAST_INSTANCE_NAME}") final String instanceName,
                                  @Value("${HAZELCAST_INTERFACE}") final String hazelInterface, @Value("${HAZELCAST_MEMBER}") final String hazelMember) {
        this.groupName = groupName;
        this.instanceName = instanceName;
        this.hazelInterface = hazelInterface;
        this.hazelMember = hazelMember;
    }

    @Bean(name = "winiCache")
    public HazelcastInstance hazelCastConfig() {
        Config config = new Config();
        final MulticastConfig multicastConfig = new MulticastConfig();
        multicastConfig.setEnabled(false);
        config.setInstanceName(instanceName)
                .setClusterName(groupName);
        if (!hazelInterface.isEmpty() && !hazelMember.isEmpty()) {
            NetworkConfig networkConfig = new NetworkConfig();
            ArrayList<String> inteface = new ArrayList<>();
            inteface.add(hazelInterface);
            networkConfig.setInterfaces(new InterfacesConfig().setInterfaces(inteface).setEnabled(true));
            JoinConfig joinConfig = new JoinConfig();
            joinConfig.setMulticastConfig(multicastConfig);
            TcpIpConfig tcpIpConfig = new TcpIpConfig();
            tcpIpConfig.addMember(hazelMember);
            tcpIpConfig.setEnabled(true);
            joinConfig.setTcpIpConfig(tcpIpConfig);
            joinConfig.setMulticastConfig(new MulticastConfig().setEnabled(false));
            networkConfig.setJoin(joinConfig);
            config.setNetworkConfig(networkConfig);
        }


        return Hazelcast.getOrCreateHazelcastInstance(config);
    }

    @Bean
    CacheManager cacheManager() {
        return new HazelcastCacheManager(hazelCastConfig());
    }

    @Bean
    KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }


    @Scheduled(fixedDelay = 10 * 24 * 60 * 1000, initialDelay = 500)
    public void cacheEvictProfile() {
        IMap<String, Object> profilesMap = hazelCastConfig().getMap("profile");
        Collection<Object> profiles = profilesMap.values();
        profiles.stream()
                .map(profile -> (Profile) profile)
                .filter(profile -> Duration.between(WiniUtil.toDateTime(profile.getUpdatedAt()), LocalDateTime.now()).toDays() > 10)
                .forEach(profile -> profilesMap.evict(profile.getId()));
    }


    @Scheduled(fixedDelay = 10 * 24 * 60 * 1000, initialDelay = 500)
    public void cacheEvictUser() {
        IMap<String, Object> userMap = hazelCastConfig().getMap("user");
        Collection<Object> users = userMap.values();
        users.stream()
                .map(user -> (User) user)
                .filter(user -> Duration.between(WiniUtil.toDateTime(user.getUpdatedAt()), LocalDateTime.now()).toDays() > 10)
                .forEach(user -> userMap.evict(user.getUserId()));
    }
}
