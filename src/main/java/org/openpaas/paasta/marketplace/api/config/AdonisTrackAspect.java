package org.openpaas.paasta.marketplace.api.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.woozooha.adonistrack.ProfileAspect;

@Component
@Aspect
public class AdonisTrackAspect extends ProfileAspect {

    @Pointcut("execution(* org.openpaas.paasta.marketplace.api.controller.*Controller.*(..))")
    protected void profilePointcut() {
    }

    @Pointcut("execution(* *(..)) && (within(org.openpaas.paasta.marketplace.api..*) || within(org.openpaas.paasta.marketplace.api..*+)) && !within(org.openpaas.paasta.marketplace.api.config..*)")
    protected void executionPointcut() {
    }

}
