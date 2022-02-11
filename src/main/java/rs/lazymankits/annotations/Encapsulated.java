package rs.lazymankits.annotations;

import java.lang.annotation.*;

/*
An encapsulated element (method, class and etc.) suggests it's long-time-support, 
meaning it will not be frequently rewriten or changed.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Encapsulated {
}