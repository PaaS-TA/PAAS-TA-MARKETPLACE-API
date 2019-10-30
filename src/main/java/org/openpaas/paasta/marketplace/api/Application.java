package org.openpaas.paasta.marketplace.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.woozooha.adonistrack.filter.AdonistrackFilter;

@SpringBootApplication
//@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean<AdonistrackFilter> profileFilter() {
        FilterRegistrationBean<AdonistrackFilter> registrationBean = new FilterRegistrationBean<AdonistrackFilter>();
        registrationBean.setFilter(new AdonistrackFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

}
