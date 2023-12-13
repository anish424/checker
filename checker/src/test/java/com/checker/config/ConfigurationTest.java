package com.checker.config;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.checker.configuration.CoreApplicationConfig;
import com.checker.configuration.EmailConfig;
import com.checker.configuration.NoticeConfig;
import com.checker.configuration.SwaggerConfig;
import com.checker.dto.NoticeDto;

import springfox.documentation.spring.web.plugins.Docket;

class ConfigurationTest {

	@Test
	void shouldCheckPresenceOfNoticeDtoBean() {
		ApplicationContextRunner context = new ApplicationContextRunner().withUserConfiguration(NoticeConfig.class);
		context.run(it -> {
			(it).getBean(NoticeDto.class);
		});
		assertNotNull(context);
	}

	@Test
	void shouldCheckSwaggerConfiguration() {
		ApplicationContextRunner context = new ApplicationContextRunner()
				.withUserConfiguration(SwaggerConfig.class);
		context.run(it -> {
			(it).getBean(Docket.class);
		});
		assertNotNull(context);
	}
	
	@Test
	void shouldCheckEmailConfiguration() {
		ApplicationContextRunner context = new ApplicationContextRunner()
				.withUserConfiguration(EmailConfig.class);
		context.run(it -> {
			(it).getBean(JavaMailSender.class);
		});
		assertNotNull(context);
	}

	@Test
	void shouldCoreApplicationConfiguration() {

		ApplicationContextRunner context = new ApplicationContextRunner().withAllowBeanDefinitionOverriding(true)
				.withPropertyValues("jasypt.encryptor.password:hello", "jwt.validity:18000","otp.validity:2400",
						"jwt.secret:ENC(Zx5j6mxFStJU4u5zUbSLxq/KzZpzWCCAW0urEtxmn5CBU7/qa+p/vkWt8kvTjAvt)")
				.withUserConfiguration(CoreApplicationConfig.class)
				.withBean("handlerExceptionResolver", HandlerExceptionResolverComposite.class);
		context.run(it -> {
			(it).getBean(ModelMapper.class);
			(it).getBean(SessionLocaleResolver.class);
			(it).getBean(ReloadableResourceBundleMessageSource.class);
			(it).getBean("messages");
		});

		assertNotNull(context);
	}

}
