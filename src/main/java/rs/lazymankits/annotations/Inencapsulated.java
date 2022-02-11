package rs.lazymankits.annotations;

import java.lang.annotation.*;

/*
An encapsulated element (method, class and etc.) suggests it's not fully encapsulated, but you may use it. 
Be advised that the element may be annotated with @Replaced and be abandoned in the future
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Inencapsulated {
}