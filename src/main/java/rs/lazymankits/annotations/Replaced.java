package rs.lazymankits.annotations;

import java.lang.annotation.*;

/*
An element annotated with @Replaced is the one that has been replaced by other element,
suggesting it should not be used any longer and may be removed in the future.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Replaced {
    Class<?> substitute() default Object.class;
    String method() default "None";
    String field() default "none";
}