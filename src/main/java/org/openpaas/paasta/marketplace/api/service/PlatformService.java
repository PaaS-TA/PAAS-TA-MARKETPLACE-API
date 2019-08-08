package org.openpaas.paasta.marketplace.api.service;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PlatformService {

    public void provision(Instance instance) throws PlatformException {
        // TODO: implements
    }

    public void deprovision(Instance instance) throws PlatformException {
        // TODO: implements
    }

}
