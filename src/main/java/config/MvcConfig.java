package config;

import java.util.Properties;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// 환경설정 클래스 (xml을 대체하는 클래스임을 의미함)
@Configuration
// Component가 가능한 패키지 범위 설정
@ComponentScan(basePackages = {"controller","logic","dao","aop","websocket"})
// AOP설정
@EnableAspectJAutoProxy
// 유효성 검증
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

	/*
	 * @Bean : <bean id="handlerMapping" class="..HandlerMapping" />
	 * 			=> HandlerMapping 클래스의 객체를 handlerMapping 이름으로 컨테이너에 저장
	 */	
	
	
	// 요청정보와 Controller를 mapping
	// Controller에서 보면, user밑에 list로 접근할 수 있게 만들어 준 것
	@Bean
	public HandlerMapping handlerMapping() {
		RequestMappingHandlerMapping hm = new RequestMappingHandlerMapping();
		hm.setOrder(0);
		return hm;
	}
	
	// view 위치설정
	// ViewResolver의 property : Prefix, Suffix
	@Bean
	public ViewResolver veiwResolver() {
		InternalResourceViewResolver vr = new InternalResourceViewResolver();
		vr.setPrefix("/WEB-INF/view/");
		vr.setSuffix(".jsp");
		return vr;
	}
	
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("messages");
		return ms;
	}
	
	// 파일 업로드 객체
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver mr = new CommonsMultipartResolver();
		mr.setMaxInMemorySize(10485760);
		mr.setMaxUploadSize(104857600);
		return mr;
	}
	
	// 예외처리 객체
	// WEB-INF.view.exception.jsp로 이동하게 됨
	@Bean
	public SimpleMappingExceptionResolver exceptionHandler() {
		SimpleMappingExceptionResolver ser = new SimpleMappingExceptionResolver();
		Properties pr = new Properties();
		pr.put("exception.CartEmptyException", "exception");
		pr.put("exception.LoginException", "exception");
		pr.put("exception.BoardException", "exception");
		ser.setExceptionMappings(pr);
		return ser;
	}
}
