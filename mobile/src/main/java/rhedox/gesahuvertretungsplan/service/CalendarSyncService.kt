package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.CalendarContract
import android.support.annotation.ColorInt
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.crash.FirebaseCrash
import org.jetbrains.anko.accountManager
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import retrofit2.Response
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.api.Event
import rhedox.gesahuvertretungsplan.model.api.Exam
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.api.Test
import rhedox.gesahuvertretungsplan.util.PermissionManager
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Created by robin on 27.12.2016.
 */
class CalendarSyncService : Service() {
    companion object {
        private var syncAdapter: SyncAdapter? = null;

        fun setIsSyncEnabled(account: Account, isEnabled: Boolean) {
            if(isEnabled) {
                ContentResolver.setIsSyncable(account, CalendarContract.AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, CalendarContract.AUTHORITY, true);
                ContentResolver.addPeriodicSync(account, CalendarContract.AUTHORITY, Bundle.EMPTY, 2 * 24 * 60 * 60)
            } else {
                ContentResolver.setIsSyncable(account, CalendarContract.AUTHORITY, 0)
                ContentResolver.setSyncAutomatically(account, CalendarContract.AUTHORITY, false);
                ContentResolver.removePeriodicSync(account,  CalendarContract.AUTHORITY, Bundle.EMPTY)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        synchronized(Companion) {
            if (syncAdapter == null)
                syncAdapter = SyncAdapter(applicationContext, true)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return syncAdapter!!.syncAdapterBinder;
    }

    class SyncAdapter(context: Context, autoInitialize: Boolean): AbstractThreadedSyncAdapter(context, autoInitialize, false) {
        companion object {
            const val examCalendarName = "gesaHuExams";
            const val testCalendarName = "gesaHuTests";
            const val eventCalendarName = "gesaHuEvents";
        }

        private val gesaHu = GesaHu(context)
        private val address = context.getString(R.string.school_address)

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            //android.os.Debug.waitForDebugger();

            if(Thread.interrupted()) {
                return;
            }
            val permissionManager = PermissionManager(context)
            if (!permissionManager.isCalendarReadingPermissionGranted || !permissionManager.isCalendarWritingPermissionGranted) {
                setIsSyncEnabled(account, false)
                return;
            }

            val existingCalendars = getCalendarIds(account)
            val calendars = mutableMapOf<String, Long>()
            calendars.putAll(existingCalendars)

            if(!calendars.containsKey(examCalendarName)) {
                calendars.put(examCalendarName, createCalendar(examCalendarName, account))
            }
            if(!calendars.containsKey(testCalendarName)) {
                calendars.put(testCalendarName, createCalendar(testCalendarName, account))
            }
            if(!calendars.containsKey(eventCalendarName)) {
                calendars.put(eventCalendarName, createCalendar(eventCalendarName, account))
            }

            val start = DateTime.now()
            val end = start.withFieldAdded(DurationFieldType.days(), 60).withTime(23,59,59,999)

            val password = context.accountManager.getPassword(account) ?: "";

            val testCall = gesaHu.tests(account.name, start)
            var testResponse: Response<List<Test>>? = null
            try {
                testResponse = testCall.execute()
            } catch (e: Exception) {
                if (e !is IOException && !BuildConfig.DEBUG) {
                    FirebaseCrash.report(e)
                } else {
                    Log.e("CalendarSync", e.message)
                }
            }
            if(Thread.interrupted()) {
                return;
            }
            if(testResponse != null && testResponse.isSuccessful) {
                if(calendars[testCalendarName] != null) {
                    clearExistingCalendars(calendars[testCalendarName]!!, start)
                }

                val tests = testResponse.body()
                tests?.forEach {
                    insert(it, calendars[testCalendarName]!!)
                }
            } else if (testResponse != null && testResponse.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return;
            }

            val eventCall = gesaHu.events(account.name, password, start, end)
            var eventResponse: Response<List<Event>>? = null
            try {
                eventResponse = eventCall.execute()
            } catch (e: Exception) {
                if (e !is IOException && !BuildConfig.DEBUG) {
                    FirebaseCrash.report(e)
                } else {
                    Log.e("CalendarSync", e.message)
                }
            }
            if(Thread.interrupted()) {
                return;
            }
            if(eventResponse != null && eventResponse.isSuccessful) {
                if(calendars[eventCalendarName] != null) {
                    clearExistingCalendars(calendars[eventCalendarName]!!, start)
                }

                val events = eventResponse.body()
                events?.forEach {
                    insert(it, calendars[eventCalendarName]!!)
                }
            } else if (eventResponse != null && eventResponse.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return;
            }

            val examCall = gesaHu.exams(account.name, start)
            var examResponse: Response<List<Exam>>? = null
            try {
                examResponse = examCall.execute()
            } catch (e: Exception) {
                if (e !is IOException && e !is SocketTimeoutException && !BuildConfig.DEBUG) {
                    FirebaseCrash.report(e)
                } else {
                    Log.e("CalendarSync", e.message)
                }
            }
            if(Thread.interrupted()) {
                return;
            }
            if(examResponse != null && examResponse.isSuccessful) {
                if(calendars[examCalendarName] != null) {
                    clearExistingCalendars(calendars[examCalendarName]!!, start)
                }

                val exams = examResponse.body()
                exams?.forEach {
                    insert(it, calendars[examCalendarName]!!)
                }
            } else if (examResponse != null && examResponse.code() == 403) {
                BoardsSyncService.setIsSyncEnabled(account, false)
                CalendarSyncService.setIsSyncEnabled(account, false)
                SubstitutesSyncService.setIsSyncEnabled(account, false)

                GesaHuAccountService.GesaHuAuthenticator.askForLogin(context)
                return;
            }
        }

        private fun getCalendarIds(account: Account): Map<String, Long> {
            val cursor = context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI,
                    arrayOf(CalendarContract.Calendars.NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars._ID),
                    "${CalendarContract.Calendars.ACCOUNT_TYPE} = '${account.type}' AND ${CalendarContract.Calendars.ACCOUNT_NAME} = '${account.name}'",
                    null, null);

            val calendars = mutableMapOf<String, Long>()

            if(cursor.count > 0 && !cursor.isClosed) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast && !cursor.isClosed) {
                    calendars.put(cursor.getString(0), cursor.getLong(2))
                    cursor.moveToNext()
                }
            }

            cursor.close()

            return calendars;
        }

        private fun clearExistingCalendars(calendarId: Long, start: DateTime) {
            val uri = CalendarContract.Events.CONTENT_URI
            context.contentResolver.delete(uri, "${CalendarContract.Events.CALENDAR_ID} = $calendarId AND (${CalendarContract.Events.DTSTART} >= ${start.millis} OR ${CalendarContract.Events.DTEND} >= ${start.millis})", null)
        }

        private fun createCalendar(name: String, account: Account): Long {
            val values = ContentValues()
            values.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
            values.put(CalendarContract.Calendars.ACCOUNT_TYPE, account.type)
            values.put(CalendarContract.Calendars.NAME, name)
            values.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
            values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ)
            values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "GesaHu")
            values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "Europe/Berlin")

            @ColorInt val color: Int;
            val displayName: String;
            when(name) {
                examCalendarName -> {
                    color = ContextCompat.getColor(context, R.color.calendar_exam_color)
                    displayName = context.getString(R.string.calendar_exam_name)
                }
                testCalendarName -> {
                    color = ContextCompat.getColor(context, R.color.calendar_test_color)
                    displayName = context.getString(R.string.calendar_test_name)
                }
                eventCalendarName -> {
                    color = ContextCompat.getColor(context, R.color.calendar_event_color)
                    displayName = context.getString(R.string.calendar_event_name)
                }
                else -> {
                    color = 0;
                    displayName = "GesaHu"
                }
            }
            values.put(CalendarContract.Calendars.CALENDAR_COLOR, color)
            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, displayName)

            val uri = buildUri(CalendarContract.Calendars.CONTENT_URI, account)
            val calendarUri = context.contentResolver.insert(uri, values)
            Log.d("CalendarSync", "$calendarUri")
            return calendarUri.lastPathSegment.toLong();
        }

        @Suppress("ReplaceArrayOfWithLiteral")
        @RequiresPermission(allOf = arrayOf(android.Manifest.permission.WRITE_CALENDAR))
        private fun insert(event: Event, calendarId: Long) {
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, event.begin.millis)

            if(event.end != null) {
                values.put(CalendarContract.Events.DTEND, event.end.millis)
            } else {
                values.put(CalendarContract.Events.DTEND, event.begin.millis)
            }
            values.put(CalendarContract.Events.ALL_DAY, if (event.isWholeDay) 1 else 0)

            values.put(CalendarContract.Events.TITLE, event.description)
            var description = context.getString(R.string.calendar_event_description, event.category);
            if(event.location.isNotBlank()) {
                description += System.getProperty("line.separator") + context.getString(R.string.calendar_location, event.location)
            }
            if(event.author.isNotBlank()) {
                description += System.getProperty("line.separator") + context.getString(R.string.calendar_author, event.author)
            }
            values.put(CalendarContract.Events.DESCRIPTION, description)
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
            values.put(CalendarContract.Events.EVENT_LOCATION, address);

            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

        @Suppress("ReplaceArrayOfWithLiteral")
        @RequiresPermission(allOf = arrayOf(android.Manifest.permission.WRITE_CALENDAR))
        private fun insert(test: Test, calendarId: Long) {
            val values = ContentValues()
            if (test.lessonStart != null && test.duration != null) {
                values.put(CalendarContract.Events.DTSTART, test.date.toDateTime(SchoolWeek.lessonStart(test.lessonStart)).millis)
                values.put(CalendarContract.Events.DTEND, test.date.toDateTime(SchoolWeek.lessonEnd(if (test.duration >= 1) test.lessonStart + test.duration - 1 else test.lessonStart)).millis)
                values.put(CalendarContract.Events.ALL_DAY, 0);
            } else {
                values.put(CalendarContract.Events.ALL_DAY, 1);
            }
            values.put(CalendarContract.Events.TITLE, context.getString(R.string.calendar_test_title, test.subject, test.course, test.year.toString(), test.teacher))
            values.put(CalendarContract.Events.DESCRIPTION, test.remark)
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
            values.put(CalendarContract.Events.EVENT_LOCATION, address)

            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

        @Suppress("ReplaceArrayOfWithLiteral")
        @RequiresPermission(allOf = arrayOf(android.Manifest.permission.WRITE_CALENDAR))
        private fun insert(exam: Exam, calendarId: Long) {
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, exam.date.toDateTime(exam.time).millis)
            values.put(CalendarContract.Events.DTEND, exam.date.toDateTime(exam.time).millis + (exam.duration?.millis ?: 90L * 60L * 1000L))
            values.put(CalendarContract.Events.TITLE, context.getString(R.string.calendar_exam_title, exam.subject, exam.course, exam.examiner))
            values.put(CalendarContract.Events.DESCRIPTION, context.getString(R.string.calendar_exam_description, exam.examinee, exam.examiner, exam.chair, exam.recorder, exam.room, if(exam.allowAudience) context.getString(R.string.bool_true_lower) else context.getString(R.string.bool_false_lower)))
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
            values.put(CalendarContract.Events.EVENT_LOCATION, address);

            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

        private fun buildUri(baseUri: Uri, account: Account): Uri {
            return baseUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type)
                    .build();
        }
    }
}