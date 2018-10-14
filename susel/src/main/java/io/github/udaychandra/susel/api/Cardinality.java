package io.github.udaychandra.susel.api;

/**
 * Specifies a service cardinality being requested by the referer.
 * This allows Susel to find and load the appropriate services.
 */
public enum Cardinality {
    /**
     * Indicates that one and only one service implementation is required.
     */
    ONE,

    /**
     * Indicates that at least one service implementation is required.
     * The more the merrier.
     */
    ONE_OR_MORE,

    /**
     * Indicates that the referenced service is optional.
     * But it would be nice to pass all available service providers.
     */
    ZERO_OR_MORE,

    /**
     * Indicates that the referenced service is optional.
     * But it would be nice to pass only one of the available service providers.
     */
    ZERO_OR_ONE
}
