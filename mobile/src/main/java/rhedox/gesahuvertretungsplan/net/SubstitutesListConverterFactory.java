package rhedox.gesahuvertretungsplan.net;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.model.ShortNameResolver;
import rhedox.gesahuvertretungsplan.model.Student;
import rhedox.gesahuvertretungsplan.model.SubstitutesList;

/**
 * Created by Robin on 17.02.2016.
 */
public class SubstitutesListConverterFactory extends Converter.Factory {

    private Student student;
    private ShortNameResolver shortNameResolver;

    public SubstitutesListConverterFactory(ShortNameResolver resolver, Student student) {
        this.student = student;
        this.shortNameResolver = resolver;
    }


    @Override
    public Converter<ResponseBody, SubstitutesList> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new SubstitutesListConverter(shortNameResolver, student);
    }
}
