class LocalPreferences {

    private val internalPreferences by lazy { java.util.prefs.Preferences.userRoot().node("KMR") }

    fun <T : Any> put(key: String, value: T) {
        when (value) {
            is String -> internalPreferences.put(key, value)
            is Long -> internalPreferences.putLong(key, value)
            is Int -> internalPreferences.putInt(key, value)
            else -> error("Type not supported")
        }
        internalPreferences.flush()
    }

    fun <T : Any> get(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> internalPreferences.get(key, defaultValue) as T
            is Long -> internalPreferences.getLong(key, defaultValue) as T
            is Int -> internalPreferences.getInt(key, defaultValue) as T
            else -> error("Type not supported")
        }
    }

    fun remove(key: String) {
        internalPreferences.remove(key)
        internalPreferences.flush()
    }

    fun clear() {
        internalPreferences.clear()
        internalPreferences.flush()
    }

}