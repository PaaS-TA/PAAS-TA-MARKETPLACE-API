package org.openpaas.paasta.marketplace.api.controller;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.service.InstanceCartService;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/instances/cart")
@RequiredArgsConstructor
public class InstanceCartController {

	private final InstanceCartService instanceCartService;
	
    @PostMapping
    public InstanceCart create(@NotNull @Validated @RequestBody InstanceCart instanceCart, BindingResult bindingResult) throws BindException {
        Software software = instanceCart.getSoftware();
        if (software.getId() == null) {
            bindingResult.rejectValue("software.id", "Required");
        }
        
        if (StringUtils.isBlank(instanceCart.getSoftwarePlanId())) {
        	bindingResult.rejectValue("softwarePlanId", "Required");
        }
        
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return instanceCartService.create(instanceCart);
    }

}
