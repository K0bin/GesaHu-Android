package rhedox.gesahuvertretungsplan.service

import android.accounts.Account
import android.app.Service
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.CalendarContract
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.Log
import org.jetbrains.anko.accountManager
import org.joda.time.DateTime
import org.joda.time.DurationFieldType
import org.joda.time.LocalTime
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.api.json.Event
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.api.json.Exam
import rhedox.gesahuvertretungsplan.model.api.json.Test
import java.util.*

/**
 * Created by robin on 27.12.2016.
 */
class CalendarSyncService : Service() {
    companion object {
        private var syncAdapter: SyncAdapter? = null;
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

        override fun onPerformSync(account: Account, extras: Bundle?, authority: String, provider: ContentProviderClient, syncResult: SyncResult?) {
            //debug
            //android.os.Debug.waitForDebugger();

            /*if(Thread.interrupted()) {
                return;
            }*/
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

            val testCall = gesaHu.tests(account.name, password, start)
            val testResponse = testCall.execute()
            if(testResponse != null && testResponse.isSuccessful) {
                if(calendars[testCalendarName] != null) {
                    clearExistingCalendars(calendars[testCalendarName]!!, start)
                }

                val tests = testResponse.body()
                tests.forEach {
                    insert(it, calendars[testCalendarName]!!)
                }
            }

            val eventCall = gesaHu.events(account.name, password, start, end)
            val eventResponse = eventCall.execute()
            if(eventResponse != null && eventResponse.isSuccessful) {
                if(calendars[eventCalendarName] != null) {
                    clearExistingCalendars(calendars[eventCalendarName]!!, start)
                }

                val events = eventResponse.body()
                events.forEach {
                    insert(it, calendars[eventCalendarName]!!)
                }
            }

            val examCall = gesaHu.exams(account.name, password, start)
            val examResponse = examCall.execute()
            if(examResponse != null && examResponse.isSuccessful) {
                if(calendars[examCalendarName] != null) {
                    clearExistingCalendars(calendars[examCalendarName]!!, start)
                }

                val exams = examResponse.body()
                exams.forEach {
                    insert(it, calendars[examCalendarName]!!)
                }
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
            context.contentResolver.delete(uri, "${CalendarContract.Events.CALENDAR_ID} = $calendarId AND ${CalendarContract.Events.DTSTART} > ${start.millis}", null)
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

        private fun insert(event: Event, calendarId: Long) {
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, event.begin.millis)

            if(event.end != null && !event.isWholeDay) {
                values.put(CalendarContract.Events.DTEND, event.end.millis)
            } else {
                values.put(CalendarContract.Events.DTEND, event.begin.millis)
                values.put(CalendarContract.Events.ALL_DAY, event.isWholeDay)
            }

            values.put(CalendarContract.Events.TITLE, event.description)
            values.put(CalendarContract.Events.DESCRIPTION, context.getString(R.string.calendar_event_description, event.category))
            values.put(CalendarContract.Events.EVENT_LOCATION, event.location)
            values.put(CalendarContract.Events.ORGANIZER, event.author)
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");

            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

        private fun insert(test: Test, calendarId: Long) {
            val values = ContentValues()
            values.put(CalendarContract.Events.DTSTART, test.date.toDateTime(SchoolWeek.lessonStart(test.lessonStart)).millis)
            values.put(CalendarContract.Events.DTEND, test.date.toDateTime(SchoolWeek.lessonEnd(if(test.duration >= 1) test.lessonStart + test.duration - 1 else test.lessonStart)).millis)
            values.put(CalendarContract.Events.TITLE, context.getString(R.string.calendar_test_title, test.year.toString(), test.subject, test.course, test.teacher))
            values.put(CalendarContract.Events.DESCRIPTION, test.remark)
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");

            context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        }

        private fun insert(exam: Exam, calendarId: Long) {

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