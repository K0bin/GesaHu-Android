package rhedox.gesahuvertretungsplan.net;

import org.joda.time.LocalDate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Query;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.StudentInformation;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;

/**
 * Created by Robin on 17.02.2016.
 */
public class SubstitutesListConverterFactory extends Converter.Factory {

    private StudentInformation studentInformation;
    private ShortNameResolver shortNameResolver;

    public SubstitutesListConverterFactory(ShortNameResolver resolver, StudentInformation studentInformation) {
        this.studentInformation = studentInformation;
        this.shortNameResolver = resolver;
    }


    @Override
    public Converter<ResponseBody, SubstitutesList> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new SubstitutesListConverter(shortNameResolver, studentInformation);
    }
}
