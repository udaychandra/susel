package io.github.udaychandra.susel.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a public method in the referer class that will
 * be used by Susel to set a service reference.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceReference {
    /**
     * Specifies the service cardinality being requested by the referer.
     * Default value is {@link Cardinality#ONE}
     *
     * @return the service cardinality being requested by the referer.
     */
    Cardinality cardinality() default Cardinality.ONE;
}
