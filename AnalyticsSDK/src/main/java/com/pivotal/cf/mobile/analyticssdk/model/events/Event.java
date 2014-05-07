package com.pivotal.cf.mobile.analyticssdk.model.events;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.pivotal.cf.mobile.analyticssdk.database.Database;
import com.pivotal.cf.mobile.common.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Event implements Parcelable {

    public static class Columns {
        public static final String EVENT_UUID = "id";
        public static final String TYPE = "type";
        public static final String TIME = "time";
        public static final String STATUS = "status";
        public static final String DATA = "data";
    }

    public static class Status {
        public static final int NOT_POSTED = 0;
        public static final int POSTING = 1;
        public static final int POSTED = 2;
        public static final int POSTING_ERROR = 3;
    }

    public static String statusString(int status) {
        switch (status) {
            case Status.NOT_POSTED:
                return "Not posted";
            case Status.POSTING:
                return "Posting";
            case Status.POSTED:
                return "Posted";
            case Status.POSTING_ERROR:
                return "Error";
        }
        return "?";
    }

    private transient int status;
    private transient int id;

    @SerializedName(Columns.EVENT_UUID)
    private String eventId;

    @SerializedName(Columns.TYPE)
    private String eventType;

    @SerializedName(Columns.TIME)
    private String time;

    @SerializedName(Columns.DATA)
    private HashMap<String, Object> data;

    public Event() {
    }

    // Construct from cursor
    public Event(Cursor cursor) {
        int columnIndex;

        columnIndex = cursor.getColumnIndex(BaseColumns._ID);
        if (columnIndex >= 0) {
            setId(cursor.getInt(columnIndex));
        } else {
            setId(0);
        }

        columnIndex = cursor.getColumnIndex(Columns.STATUS);
        if (columnIndex >= 0) {
            setStatus(cursor.getInt(columnIndex));
        } else {
            setStatus(Status.NOT_POSTED);
        }

        columnIndex = cursor.getColumnIndex(Columns.TYPE);
        if (columnIndex >= 0) {
            setEventType(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(Columns.EVENT_UUID);
        if (columnIndex >= 0) {
            setEventId(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(Columns.TIME);
        if (columnIndex >= 0) {
            setTime(cursor.getString(columnIndex));
        }

        columnIndex = cursor.getColumnIndex(Columns.DATA);
        if (columnIndex >= 0) {
            if (cursor.isNull(columnIndex)) {
                setData(null);
            } else {
                final byte[] bytes = cursor.getBlob(columnIndex);
                final Serializable deserializedBytes = deserialize(bytes);
                setData(deserializedBytes);
            }
        }
    }

    // Copy constructor
    public Event(Event source) {
        this.status = source.status;
        this.id = source.id;
        this.eventId = source.eventId;
        this.eventType = source.eventType;
        this.time = source.time;
        if (source.data != null) {
            this.data = new HashMap<String, Object>(source.data);
        } else {
            this.data = null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (status != Status.NOT_POSTED && status != Status.POSTING && status != Status.POSTED && status != Status.POSTING_ERROR) {
            throw new IllegalArgumentException("Illegal event status: " + status);
        }
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String id) {
        this.eventId = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTime(Date time) {
        if (time != null) {
            this.time = String.format("%d", time.getTime() / 1000L);
        } else {
            this.time = null;
        }
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    private void setData(Serializable serializedData) {
        if (serializedData == null) {
            this.data = null;
        } else {
            try {
                this.data = (HashMap<String, Object>) serializedData;
            } catch (ClassCastException e) {
                Logger.w("Warning: attempted to deserialize invalid event data field");
                this.data = null;
            }
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (!(o instanceof Event)) {
            return false;
        }

        final Event other = (Event) o;

        if (other.status != status) {
            return false;
        }

        if (other.eventId == null && eventId != null) {
            return false;
        }
        if (other.eventId != null && eventId == null) {
            return false;
        }
        if (other.eventId != null && eventId != null && !(other.eventId.equals(eventId))) {
            return false;
        }

        if (other.eventType == null && eventType != null) {
            return false;
        }
        if (other.eventType != null && eventType == null) {
            return false;
        }
        if (other.eventType != null && eventType != null && !(other.eventType.equals(eventType))) {
            return false;
        }

        if (other.time == null && time != null) {
            return false;
        }
        if (other.time != null && time == null) {
            return false;
        }
        if (other.time != null && time != null && !(other.time.equals(time))) {
            return false;
        }

        if (other.data == null && data != null) {
            return false;
        }
        if (other.data != null && data == null) {
            return false;
        }
        if (other.data != null && data != null && !(other.data.equals(data))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = (result * 31) + (eventId == null ? 0 : eventId.hashCode());
        result = (result * 31) + (eventType == null ? 0 : eventType.hashCode());
        result = (result * 31) + (time == null ? 0 : time.hashCode());
        result = (result * 31) + (data == null ? 0 : data.hashCode());
        return result;
    }

    // JSON helpers

    public static List<Event> jsonStringToList(String str) {
        final Gson gson = new Gson();
        final Type type = getTypeToken();
        final List list = gson.fromJson(str, type);
        return list;
    }

    public static String listToJsonString(List<Event> list) {
        if (list == null) {
            return null;
        } else {
            final Gson gson = new Gson();
            final Type type = getTypeToken();
            final String str = gson.toJson(list, type);
            return str;
        }
    }

    private static Type getTypeToken() {
        return new TypeToken<List<Event>>() {
        }.getType();
    }

    // Database helpers

    public ContentValues getContentValues() {
        // NOTE - do not save the 'id' field to the ContentValues. Let the database
        // figure out the 'id' itself.
        final ContentValues cv = new ContentValues();
        cv.put(Columns.EVENT_UUID, getEventId());
        cv.put(Columns.TIME, getTime());
        cv.put(Columns.STATUS, getStatus());
        cv.put(Columns.TYPE, getEventType());

        if (data != null) {
            final byte[] bytes = serialize(data);
            cv.put(Columns.DATA, bytes);
        } else {
            cv.putNull(Columns.DATA);
        }

        return cv;
    }

    public static String getCreateTableSqlStatement() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append('\'');
        sb.append(Database.EVENTS_TABLE_NAME);
        sb.append("\' ('");
        sb.append(BaseColumns._ID);
        sb.append("' INTEGER PRIMARY KEY AUTOINCREMENT, '");
        sb.append(Columns.TYPE);
        sb.append("' TEXT, '");
        sb.append(Columns.EVENT_UUID);
        sb.append("' TEXT, '");
        sb.append(Columns.TIME);
        sb.append("' INT, '");
        sb.append(Columns.STATUS);
        sb.append("' INT, '");
        sb.append(Columns.DATA);
        sb.append("' BLOB);");
        return sb.toString();
    }

    public static String getDropTableSqlStatement() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DROP TABLE IF EXISTS '");
        sb.append(Database.EVENTS_TABLE_NAME);
        sb.append("';");
        return sb.toString();
    }

    public static int getRowIdFromCursor(final Cursor cursor) {
        final int idColumn = cursor.getColumnIndex(BaseColumns._ID);
        if (idColumn < 0) {
            throw new IllegalArgumentException("No " + BaseColumns._ID + " in cursor");
        }
        final int id = cursor.getInt(idColumn);
        return id;
    }

    // Serializable helpers

    public static Serializable deserialize(byte[] bytes) {

        ByteArrayInputStream byteStream = null;
        ObjectInputStream in = null;

        try {
            byteStream = new ByteArrayInputStream(bytes);
            in = new ObjectInputStream(byteStream);
            return (Serializable) in.readObject();

        } catch (IOException i) {
            Logger.ex("Error deserializing data: ", i);
        } catch (ClassNotFoundException c) {
            Logger.ex("Error deserializing data: ", c);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (byteStream != null) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    public static byte[] serialize(Serializable data) {

        ByteArrayOutputStream byteStream = null;
        ObjectOutputStream out = null;

        try {
            byteStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteStream);
            out.writeObject(data);
            return byteStream.toByteArray();

        } catch (IOException i) {
            Logger.w("Warning: Serializable object didn't serialize.");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (byteStream != null) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    // Parcelable stuff

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    private Event(Parcel in) {
        id = in.readInt();
        status = in.readInt();
        eventType = in.readString();
        eventId = in.readString();
        time = in.readString();
        setData(in.readSerializable());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(id);
        out.writeInt(status);
        out.writeString(eventType);
        out.writeString(eventId);
        out.writeString(time);
        out.writeSerializable(data);
    }
}
