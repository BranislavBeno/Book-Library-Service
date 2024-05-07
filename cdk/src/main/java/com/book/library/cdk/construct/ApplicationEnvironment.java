package com.book.library.cdk.construct;

import software.amazon.awscdk.Tags;
import software.constructs.IConstruct;

/**
 * An application can be deployed into multiple environments (staging, production, ...).
 * An {@link ApplicationEnvironment} object serves as a descriptor in which environment
 * an application is deployed.
 * <p>
 * The constructs in this package will use this descriptor when naming AWS resources so that they
 * can be deployed into multiple environments at the same time without conflicts.
 *
 * @param applicationName the name of the application that you want to deploy.
 * @param environmentName the name of the environment the application shall be deployed into.
 */
public record ApplicationEnvironment(String applicationName, String environmentName) {

    /**
     * Strips non-alphanumeric characters from a String since some AWS resources don't cope with
     * them when using them in resource names.
     */
    private String sanitize(String fullName) {
        return fullName.replaceAll("[^a-zA-Z0-9-]", "");
    }

    @Override
    public String toString() {
        return sanitize(applicationName + "-" + environmentName);
    }

    /**
     * Prefixes a string with the application name and environment name.
     */
    public String prefix(String string) {
        return this + "-" + string;
    }

    public String prefix(String string, int characterLimit) {
        String name = this + "-" + string;
        return name.length() <= characterLimit ? name : name.substring(name.length() - characterLimit);
    }

    public void tag(IConstruct construct) {
        Tags.of(construct).add("application", applicationName);
        Tags.of(construct).add("environment", environmentName);
    }
}
