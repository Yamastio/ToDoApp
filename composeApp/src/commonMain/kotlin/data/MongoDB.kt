package data

import domain.ToDoTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class MongoDB {
    var realm: Realm? = null

    private fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(ToDoTask::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

}