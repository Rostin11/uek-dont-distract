package ch.ergon.rs.dontdistract.model

import kotlinx.serialization.Serializable

@Serializable
class WorkingSession(val remainingMilliSeconds: Int, val endDate: SessionDate, val name: String) {

}