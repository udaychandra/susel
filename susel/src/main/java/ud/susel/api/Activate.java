package ud.susel.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a public method in the referer class that will
 * be used by Susel to activate a concrete service implementation.
 *
 * Useful in use-cases where a service implementation wants to initialize key aspects like configuration.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Activate {}
