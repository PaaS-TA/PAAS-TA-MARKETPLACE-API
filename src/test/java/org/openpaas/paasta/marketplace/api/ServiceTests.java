package org.openpaas.paasta.marketplace.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openpaas.paasta.marketplace.api.service.CategoryServiceTest;
import org.openpaas.paasta.marketplace.api.service.InstanceCartServiceTest;
import org.openpaas.paasta.marketplace.api.service.InstanceServiceTest;
import org.openpaas.paasta.marketplace.api.service.ProfileServiceTest;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanServiceTest;
import org.openpaas.paasta.marketplace.api.service.SoftwareServiceTest;
import org.openpaas.paasta.marketplace.api.service.TestSoftwareInfoServiceTest;
import org.openpaas.paasta.marketplace.api.service.UserServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
        // @formatter:off
        CategoryServiceTest.class,
        InstanceCartServiceTest.class,
        InstanceServiceTest.class,
        ProfileServiceTest.class,
        SoftwarePlanServiceTest.class,
        SoftwareServiceTest.class,
        TestSoftwareInfoServiceTest.class,
        UserServiceTest.class,
        // @formatter:on
})
public class ServiceTests {
}
