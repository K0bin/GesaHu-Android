package rhedox.gesahuvertretungsplan.dependencyInjection;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by robin on 10.03.2018.
 */

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PresenterScope {
}
