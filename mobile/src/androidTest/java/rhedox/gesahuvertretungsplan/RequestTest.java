package rhedox.gesahuvertretungsplan;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.mock.MockContext;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.joda.time.LocalDate;

import java.lang.Override;
import java.lang.Throwable;

import rhedox.gesahuvertretungsplan.model.SubstitutesList;
import rhedox.gesahuvertretungsplan.net.SubstituteJauntRequest;
import rhedox.gesahuvertretungsplan.net.VolleySingleton;

/**
 * Created by Robin on 12.10.2015.
 */
public class RequestTest extends TestCase {

    @Override
    public void runBare() throws Throwable {
        run();
    }

    @Override
    public void run(TestResult result) {
        run();
    }

    @Override
    public TestResult run() {
        MockContext context = new MockContext();
        RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
        queue.add(new SubstituteJauntRequest(context, new LocalDate(24, 06, 2014), null, new Response.Listener<SubstitutesList>() {
            @Override
            public void onResponse(SubstitutesList response) {

            }
        }, null));

        TestResult result = new TestResult();
        result.

        return null;
    }
}
