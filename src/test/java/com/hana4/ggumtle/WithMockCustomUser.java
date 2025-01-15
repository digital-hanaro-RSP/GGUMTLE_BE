package com.hana4.ggumtle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.hana4.ggumtle.model.entity.user.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
	String user() default "01012341234";

	UserRole role() default UserRole.USER;
}
