package org.openpaas.paasta.marketplace.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//	@Bean
//	public CommonsMultipartResolver multipartResolver(){
//		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
//		commonsMultipartResolver.setDefaultEncoding("UTF-8");
//		commonsMultipartResolver.setMaxUploadSizePerFile(5 * 1024 * 1024); //5 * 1024 * 1024 (5mb)
//		return commonsMultipartResolver;
//	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry){
		registry.addInterceptor(new LoggerInterceptor());
	}

}
