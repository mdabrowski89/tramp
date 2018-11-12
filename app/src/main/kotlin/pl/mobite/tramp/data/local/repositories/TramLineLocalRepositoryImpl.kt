package pl.mobite.tramp.data.local.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import pl.mobite.tramp.data.local.db.TrampAppDatabase
import pl.mobite.tramp.data.local.db.entities.toTramLineEntity
import pl.mobite.tramp.data.local.db.entities.toTramStop
import pl.mobite.tramp.data.local.db.entities.toTramStopEntity
import pl.mobite.tramp.data.local.json.JsonDataProvider
import pl.mobite.tramp.data.repositories.models.TramLine
import pl.mobite.tramp.data.repositories.models.TramLineDesc
import pl.mobite.tramp.data.repositories.models.TramStop


class TramLineLocalRepositoryImpl(
    private val jsonDataProvider: JsonDataProvider,
    private val database: TrampAppDatabase
): TramLineLocalRepository {

    override fun getTramLineFromDb(tramLineDesc: TramLineDesc): Maybe<TramLine> {
        return Maybe.fromCallable {
            val tramDao = database.tramDao()
            val tramLineEntity = tramDao.getTramLine(tramLineDesc.name, tramLineDesc.direction).firstOrNull()
            if (tramLineEntity != null) {
                val tramStops = tramDao.getTramStops(tramLineEntity.id).map { it.toTramStop() }
                TramLine(tramLineDesc, tramStops)
            } else {
                null
            }
        }
    }

    override fun getTramLineFromJson(tramLineDesc: TramLineDesc): Maybe<TramLine> {
        return Maybe.fromCallable {
            val tramStops = jsonDataProvider.getStopsJson(tramLineDesc)?.stops?.mapNotNull {stop ->
                if (stop != null && stop.line == tramLineDesc.name && stop.direction == tramLineDesc.direction
                    && stop.lat != null && stop.lon != null && stop.name != null && stop.stopId != null) {
                    TramStop(stop.stopId, stop.name, stop.lat, stop.lon)
                } else {
                    null
                }
            }
            if (tramStops != null && !tramStops.isEmpty()) {
                TramLine(tramLineDesc, tramStops)
            } else {
                null
            }
        }
    }

    override fun storeTramLineInDb(tramLine: TramLine): Completable {
        return Completable.fromCallable {
            val tramDao = database.tramDao()
            val tramLineId = tramDao.insert(tramLine.toTramLineEntity())
            val tramStopEntities = tramLine.stops.map { it.toTramStopEntity(tramLineId) }
            tramDao.insert(tramStopEntities)
            Unit
        }
    }
}