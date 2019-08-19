package com.devtides.animalsapp.di;

import javax.inject.Qualifier;

@Qualifier
public @interface TypeOfContext {
    public static final String CONTEXT_APP = "Application context";
    public static final String CONTEXT_ACTIVITY = "Activity context";

    String value() default "";
}
