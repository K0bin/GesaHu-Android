package rhedox.gesahuvertretungsplan.model.old;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import rhedox.gesahuvertretungsplan.model.AbbreviationResolver;
import rhedox.gesahuvertretungsplan.model.Student;

/**
 * Created by Robin on 17.02.2016.
 */
public class SubstitutesListConverterFactory extends Converter.Factory {

    private Student student;
    private AbbreviationResolver abbreviationResolver;

    public SubstitutesListConverterFactory(AbbreviationResolver resolver, Student student) {
        this.student = student;
        this.abbreviationResolver = resolver;
    }


    @Override
    public Converter<ResponseBody, SubstitutesList_old> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new SubstitutesListConverter(abbreviationResolver, student);
    }
}
