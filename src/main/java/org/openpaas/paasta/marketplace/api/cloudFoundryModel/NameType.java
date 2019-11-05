package org.openpaas.paasta.marketplace.api.cloudFoundryModel;

import org.openpaas.paasta.marketplace.api.domain.Instance;

import java.util.UUID;

public enum NameType {
    Hash(""), Auto("app-");

    private String template;

    NameType(String template) {
        this.template = template;
    }

    public String generateName(Instance instance, String testPrefix) {
        if(this.equals(NameType.Auto)) {
            if(testPrefix != null) {
                template = testPrefix + "-";
            }

            return template + instance.getId();
        }
        return uuidGetter(instance);
    }

    private String uuidGetter(Instance instance) {
		int beginIndex = 20;
		String parentGuid = instance.getAppGuid().replaceAll("-", "").substring(beginIndex);
        String uuidStr = UUID.randomUUID().toString().replaceAll("-", "").substring(beginIndex);
		return parentGuid + "-" + uuidStr;
	}
}
