package ovh.geoffrey_druelle.nantestoilettes.data.local.converter

import androidx.room.TypeConverter

class BooleanConverter {
    @TypeConverter
    fun toIntFromBoolean(boolean: Boolean): Int {
        return if (boolean) 1
        else 0
    }

    @TypeConverter
    fun toBooleanFromInt(int: Int): Boolean {
        return int == 1
    }
}