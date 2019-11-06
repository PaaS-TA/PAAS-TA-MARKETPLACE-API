package org.openpaas.paasta.marketplace.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openpaas.paasta.marketplace.api.controller.AdminCategoryControllerTest;
import org.openpaas.paasta.marketplace.api.controller.AdminSellerProfileControllerTest;
import org.openpaas.paasta.marketplace.api.controller.AdminSoftwareControllerTest;
import org.openpaas.paasta.marketplace.api.controller.CategoryControllerTest;
import org.openpaas.paasta.marketplace.api.controller.InstanceControllerTest;
import org.openpaas.paasta.marketplace.api.controller.SellerProfileControllerTest;
import org.openpaas.paasta.marketplace.api.controller.SoftwareControllerTest;

@RunWith(Suite.class)
@SuiteClasses({
        // @formatter:off
        AdminCategoryControllerTest.class, 
        AdminSellerProfileControllerTest.class, 
        AdminSoftwareControllerTest.class, 
        CategoryControllerTest.class, 
        InstanceControllerTest.class, 
        SellerProfileControllerTest.class, 
        SoftwareControllerTest.class
        // @formatter:on
    })
public class RestdocTests {
}
