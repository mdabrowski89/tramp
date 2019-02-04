package pl.mobite.tramp.data.local.repositories

import pl.mobite.tramp.data.local.db.dao.TramDao
import pl.mobite.tramp.data.local.db.entities.toTramLineEntity
import pl.mobite.tramp.data.local.db.entities.toTramStop
import pl.mobite.tramp.data.local.db.entities.toTramStopEntity
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop


class TramLineLocalRepositoryImpl(
    private val jsonDataProvider: JsonDataProvider,
    private val tramDao: TramDao
): TramLineLocalRepository {

    override fun getTramLineFromDb(tramLineDesc: TramLineDesc): TramLine? {
        val tramLineEntity = tramDao.getTramLine(tramLineDesc.name, tramLineDesc.direction).firstOrNull()

        return if (tramLineEntity != null) {
            val tramStops = tramDao.getTramStops(tramLineEntity.id).map { it.toTramStop() }
            TramLine(tramStops, false)
        } else {
            null
        }
    }

    override fun getTramLineFromJson(tramLineDesc: TramLineDesc): TramLine? {
        val tramStopsOrdered = jsonDataProvider.getStopsJson(tramLineDesc)?.stops?.mapNotNull {stop ->
            if (stop != null && stop.line == tramLineDesc.name && stop.direction == tramLineDesc.direction
                && stop.lat != null && stop.lon != null && stop.name != null && stop.stopId != null && stop.order != null) {
                TramStopOrdered(TramStop(stop.stopId, stop.name, stop.lat, stop.lon), stop.order)
            } else {
                null
            }
        }

        return if (tramStopsOrdered != null && !tramStopsOrdered.isEmpty()) {
            TramLine(tramStopsOrdered.sortedBy { it.order }.map { it.tramStop }, false)
        } else {
            null
        }
    }

    override fun storeTramLineInDb(tramLineDesc: TramLineDesc, tramLine: TramLine) {
        val tramLineId = tramDao.insert(tramLineDesc.toTramLineEntity())
        val tramStopEntities = tramLine.stops.map { it.toTramStopEntity(tramLineId) }
        tramDao.insert(tramStopEntities)
    }

    private data class TramStopOrdered(val tramStop: TramStop, val order: Int)
}