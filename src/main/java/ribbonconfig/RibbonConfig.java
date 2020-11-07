package ribbonconfig;

import com.erudev.contentcenter.config.NacosSameClusterWeightedRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengfei.zhao
 * @date 2020/11/7 14:24
 */
@Configuration
public class RibbonConfig {

    @Bean
    public IRule ribbonRule() {
        return new NacosSameClusterWeightedRule();
    }
}
